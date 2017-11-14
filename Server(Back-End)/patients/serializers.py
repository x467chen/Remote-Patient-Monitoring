from django.contrib.auth.models import User
from rest_framework import serializers

from patients.models import (
    PersonalInformation, HealthRecord, BloodPressure, Comment, HeartRate, RelativeRelationship, DoctorRelationship,
)
from utils import GenderField


class PatientSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = PersonalInformation
        fields = (
            'pid', 'email', 'cell_phone', 'name', 'password', 'health_records', 'relatives', 'doctors',
        )
    password = serializers.CharField(style={'input_type': 'password'}, write_only=True)
    health_records = serializers.HyperlinkedRelatedField(
        view_name="health-records-detail",
        read_only=False,
        many=True,
        queryset=PersonalInformation.objects,
    )
    doctors = serializers.HyperlinkedRelatedField(
        view_name="doctors-detail",
        read_only=False,
        many=True,
        queryset=PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_DOCTOR),
    )
    relatives = serializers.HyperlinkedRelatedField(
        view_name="relatives-detail",
        read_only=False,
        many=True,
        queryset=PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_RELATIVE),
    )

    def create(self, validated_data):
        relatives = validated_data.pop("relatives", [])
        doctors = validated_data.pop("doctors", [])

        validated_data.update({
            "user_type": PersonalInformation.USER_TYPE_PATIENT
        })

        patient = super(PatientSerializer, self).create(validated_data)

        RelativeRelationship.create_relative_relationships(patient, relatives)
        DoctorRelationship.create_doctor_relationships(patient, doctors)

        User.objects.create_user(
            username=validated_data.get("email"),
            email=validated_data.get("email"),
            password=validated_data.get("password"),
        )

        return patient

    def update(self, instance, validated_data):
        relatives = validated_data.pop("relatives", [])
        doctors = validated_data.pop("doctors", [])

        patient = super(PatientSerializer, self).update(instance, validated_data)

        RelativeRelationship.update_relative_relationships(patient, relatives)
        DoctorRelationship.update_doctor_relationships(patient, doctors)

        user = User.objects.get(username=validated_data.get("email"))
        user.set_password(validated_data.get("password"))
        user.save()

        return patient


class DoctorSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = PersonalInformation
        fields = ('pid', 'email', 'cell_phone', 'name', 'password')
        extra_kwargs = {'password': {'write_only': True}}

    password = serializers.CharField(style={'input_type': 'password'}, write_only=True)

    def create(self, validated_data):
        validated_data.update({
            "user_type": PersonalInformation.USER_TYPE_DOCTOR
        })

        User.objects.create_user(
            username=validated_data.get("email"),
            email=validated_data.get("email"),
            password=validated_data.get("password"),
        )

        return super(DoctorSerializer, self).create(validated_data)

    def update(self, instance, validated_data):
        user = User.objects.get(username=validated_data.get("email"))
        user.set_password(validated_data.get("password"))
        user.save()
        return super(DoctorSerializer, self).update(instance, validated_data)


class RelativeSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = PersonalInformation
        fields = ('pid', 'email', 'cell_phone', 'name', 'password')

    password = serializers.CharField(style={'input_type': 'password'}, write_only=True)

    def create(self, validated_data):
        validated_data.update({
            "user_type": PersonalInformation.USER_TYPE_RELATIVE
        })

        User.objects.create_user(
            username=validated_data.get("email"),
            email=validated_data.get("email"),
            password=validated_data.get("password"),
        )

        return super(RelativeSerializer, self).create(validated_data)

    def update(self, instance, validated_data):
        user = User.objects.get(username=validated_data.get("email"))
        user.set_password(validated_data.get("password"))
        user.save()
        return super(RelativeSerializer, self).update(instance, validated_data)


class HealthRecordSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = HealthRecord
        fields = ('id', 'age', 'sex', 'height', 'weight', 'patient', 'blood_pressure_records', 'comments', 'heart_rates')

    patient = serializers.HyperlinkedRelatedField(
        view_name="patients-detail",
        read_only=False,
        queryset=PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_PATIENT),
    )

    blood_pressure_records = serializers.HyperlinkedRelatedField(
        view_name="blood-pressures-detail",
        read_only=True,
        many=True,
    )
    comments = serializers.HyperlinkedRelatedField(
        view_name="comments-detail",
        read_only=True,
        many=True,
    )
    heart_rates = serializers.HyperlinkedRelatedField(
        view_name="heart-rates-detail",
        read_only=True,
        many=True,
    )
    sex = GenderField()


class BloodPressureSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = BloodPressure
        fields = ('id', 'time', 'high', 'low', 'health_record')

    health_record = serializers.HyperlinkedRelatedField(
        view_name="health-records-detail",
        read_only=False,
        queryset=HealthRecord.objects,
    )


class CommentSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Comment
        fields = ('id', 'comment', 'time', 'health_record')

    health_record = serializers.HyperlinkedRelatedField(
        view_name="health-records-detail",
        read_only=False,
        queryset=HealthRecord.objects,
    )


class HeartRateSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = HeartRate
        fields = ('id', 'time', 'rate', 'health_record')

    health_record = serializers.HyperlinkedRelatedField(
        view_name="health-records-detail",
        read_only=False,
        queryset=HealthRecord.objects,
    )


class RelativeRelationshipSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = RelativeRelationship
        fields = ('patient', 'relative')

    patient = serializers.HyperlinkedRelatedField(
        view_name="patients-detail",
        read_only=False,
        queryset=PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_PATIENT),
    )
    relative = serializers.HyperlinkedRelatedField(
        view_name="relatives-detail",
        read_only=False,
        queryset=PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_RELATIVE),
    )


class DoctorRelationshipSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = DoctorRelationship
        fields = ('patient', 'doctor')

    patient = serializers.HyperlinkedRelatedField(
        view_name="patients-detail",
        read_only=False,
        queryset=PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_PATIENT),
    )
    doctor = serializers.HyperlinkedRelatedField(
        view_name="doctors-detail",
        read_only=False,
        queryset=PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_DOCTOR),
    )
