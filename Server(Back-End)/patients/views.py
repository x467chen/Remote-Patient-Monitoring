from rest_framework import viewsets
from rest_framework.authtoken import views
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User
from rest_framework.response import Response

from patients.models import (
    PersonalInformation, HealthRecord, BloodPressure, Comment, HeartRate, RelativeRelationship, DoctorRelationship
)
from patients.serializers import (
    PatientSerializer, RelativeSerializer, DoctorSerializer, HealthRecordSerializer, BloodPressureSerializer,
    CommentSerializer, HeartRateSerializer, RelativeRelationshipSerializer, DoctorRelationshipSerializer
)


class PatientViewSet(viewsets.ModelViewSet):
    queryset = PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_PATIENT)
    serializer_class = PatientSerializer

    def destroy(self, request, pk=None):
        instance = PersonalInformation.objects.get(pid=pk)
        user_instance = User.objects.get(username=instance.email)
        user_instance.delete()
        instance.delete()
        return Response()


class DoctorViewSet(viewsets.ModelViewSet):
    queryset = PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_DOCTOR)
    serializer_class = DoctorSerializer


    def destroy(self, request, pk=None):
        instance = PersonalInformation.objects.get(pid=pk)
        user_instance = User.objects.get(username=instance.email)
        user_instance.delete()
        instance.delete()
        return Response()


class RelativeViewSet(viewsets.ModelViewSet):
    queryset = PersonalInformation.objects.filter(user_type=PersonalInformation.USER_TYPE_RELATIVE)
    serializer_class = RelativeSerializer

    def destroy(self, request, pk=None):
        instance = PersonalInformation.objects.get(pid=pk)
        user_instance = User.objects.get(username=instance.email)
        user_instance.delete()
        instance.delete()
        return Response()


class HealthRecordViewSet(viewsets.ModelViewSet):
    queryset = HealthRecord.objects
    serializer_class = HealthRecordSerializer


class BloodPressureViewSet(viewsets.ModelViewSet):
    queryset = BloodPressure.objects
    serializer_class = BloodPressureSerializer


class CommentViewSet(viewsets.ModelViewSet):
    queryset = Comment.objects
    serializer_class = CommentSerializer


class HeartRateViewSet(viewsets.ModelViewSet):
    queryset = HeartRate.objects
    serializer_class = HeartRateSerializer


class RelativeRelationshipViewSet(viewsets.ModelViewSet):
    queryset = RelativeRelationship.objects
    serializer_class = RelativeRelationshipSerializer


class DoctorRelationshipViewSet(viewsets.ModelViewSet):
    queryset = DoctorRelationship.objects
    serializer_class = DoctorRelationshipSerializer


class ObtainTokenView(views.ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        token, created = Token.objects.get_or_create(user=user)
        try:
            person = PersonalInformation.objects.get(email=user.email)
            personal_information = {
                "email": person.email,
                "type_readable": person.READABLE_USER_TYPES.get(person.user_type),
                "type": person.user_type,
                "pid": person.pid,
            }
        except PersonalInformation.DoesNotExist:
            personal_information = {}

        return Response({'token': token.key, 'personal_information': personal_information})
