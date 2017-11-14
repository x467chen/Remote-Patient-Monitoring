from __future__ import unicode_literals

from django.db import models
from django.db.models.signals import post_save
from django.dispatch import receiver
from django.contrib.auth.models import User
from rest_framework.authtoken.models import Token


class PersonalInformation(models.Model):
    pid = models.AutoField(primary_key=True)
    email = models.CharField(max_length=512)
    password = models.CharField(max_length=512)
    cell_phone = models.CharField(max_length=20)
    name = models.CharField(max_length=512)
    user_type = models.IntegerField()

    @property
    def relatives(self):
        if self.user_type != self.USER_TYPE_PATIENT:
            return []

        relatives = PersonalInformation.objects.filter(
            pid__in=RelativeRelationship.objects.filter(patient=self).values_list('relative__pid', flat=True)
        ).all()
        return relatives

    @property
    def doctors(self):
        if self.user_type != self.USER_TYPE_PATIENT:
            return []

        doctors = PersonalInformation.objects.filter(
            pid__in=DoctorRelationship.objects.filter(patient=self).values_list('doctor__pid', flat=True)
        ).all()
        return doctors

    USER_TYPE_DOCTOR = 1
    USER_TYPE_PATIENT = 2
    USER_TYPE_RELATIVE = 3

    READABLE_USER_TYPES = {
        USER_TYPE_DOCTOR: "Doctor",
        USER_TYPE_PATIENT: "Patient",
        USER_TYPE_RELATIVE: "Relative",
    }

    class Meta(object):
        app_label = "patients"

    def __str__(self):
        return "<{user_type}: {pid}>  {name} - {email}".format(
            pid=self.pid,
            user_type=self.READABLE_USER_TYPES.get(self.user_type),
            name=self.name,
            email=self.email,
        )


class RelativeRelationship(models.Model):
    patient = models.ForeignKey(PersonalInformation)
    relative = models.ForeignKey(PersonalInformation, related_name="relatives_relationships")

    class Meta(object):
        app_label = "patients"

    def __str__(self):
        return "Relative: {relative},  Patient: {patient}".format(
            relative=self.relative,
            patient=self.patient,
        )

    @classmethod
    def create_relative_relationships(cls, patient, relatives):
        for relative in relatives:
            cls.objects.create(patient=patient, relative=relative)

    @classmethod
    def update_relative_relationships(cls, patient, relatives):
        present_relatives = patient.relatives.all()

        for relative in relatives:
            if not(relative in present_relatives):
                cls.objects.create(patient=patient, relative=relative)

        cls.objects.filter(relative__in=set(present_relatives) - set(relatives)).delete()


class DoctorRelationship(models.Model):
    patient = models.ForeignKey(PersonalInformation)
    doctor = models.ForeignKey(PersonalInformation, related_name="doctors_relationships")

    class Meta(object):
        app_label = "patients"

    def __str__(self):
        return "Doctor: {doctor},  Patient: {patient}".format(
            doctor=self.doctor,
            patient=self.patient,
        )

    @classmethod
    def create_doctor_relationships(cls, patient, doctors):
        for doctor in doctors:
            cls.objects.create(patient=patient, doctor=doctor)

    @classmethod
    def update_doctor_relationships(cls, patient, doctors):
        present_doctors = patient.doctors.all()

        for doctor in doctors:
            if not(doctor in present_doctors):
                cls.objects.create(patient=patient, doctor=doctor)

        cls.objects.filter(doctor__in=set(present_doctors) - set(doctors)).delete()


class HealthRecord(models.Model):
    age = models.IntegerField()
    sex = models.BooleanField()
    height = models.IntegerField()
    weight = models.IntegerField()

    patient = models.ForeignKey(PersonalInformation, related_name='health_records', on_delete=models.CASCADE)

    SEX_MALE = True
    SEX_FEMALE = False

    class Meta(object):
        app_label = "patients"

    def __str__(self):
        return "HealthRecord of {patient}".format(
            patient=self.patient,
        )


class BloodPressure(models.Model):
    time = models.DateTimeField()
    high = models.IntegerField()
    low = models.IntegerField()

    health_record = models.ForeignKey(HealthRecord, related_name='blood_pressure_records', on_delete=models.CASCADE)

    class Meta(object):
        app_label = "patients"

    def __str__(self):
        return "BloodPressure: {high}-{low} of {patient}".format(
            high=self.high,
            low=self.low,
            patient=self.health_record.patient,
        )


class Comment(models.Model):
    comment = models.TextField()
    time = models.DateTimeField()

    health_record = models.ForeignKey(HealthRecord, related_name='comments', on_delete=models.CASCADE)

    class Meta(object):
        app_label = "patients"

    def __str__(self):
        return "Comment by {patient}".format(
            patient=self.health_record.patient,
        )


class HeartRate(models.Model):
    time = models.DateTimeField()
    rate = models.IntegerField()

    health_record = models.ForeignKey(HealthRecord, related_name='heart_rates', on_delete=models.CASCADE)

    class Meta(object):
        app_label = "patients"

    def __str__(self):
        return "HeartRate {rate} of {patient}".format(
            rate=self.rate,
            patient=self.health_record.patient,
        )


@receiver(post_save, sender=User)
def create_auth_token(sender, instance=None, created=False, **kwargs):
    if created:
        Token.objects.create(user=instance)
