from django.conf.urls import url, include
from rest_framework import routers

from patients.views import (
    HealthRecordViewSet, PatientViewSet, DoctorViewSet, RelativeViewSet, BloodPressureViewSet, CommentViewSet,
    HeartRateViewSet, DoctorRelationshipViewSet, RelativeRelationshipViewSet
)

# Routers provide an easy way of automatically determining the URL conf.
router = routers.DefaultRouter()
router.register(r'health-records', HealthRecordViewSet, 'health-records')
router.register(r'patients', PatientViewSet, base_name='patients')
router.register(r'doctors', DoctorViewSet, base_name='doctors')
router.register(r'relatives', RelativeViewSet, base_name='relatives')
router.register(r'blood-pressures', BloodPressureViewSet, 'blood-pressures')
router.register(r'comments', CommentViewSet, 'comments')
router.register(r'heart-rates', HeartRateViewSet, 'heart-rates')
router.register(r'relative-relationships', RelativeRelationshipViewSet, 'relative-relationships')
router.register(r'doctor-relationships', DoctorRelationshipViewSet, 'doctor-relationships')

urlpatterns = [
    url(r'^', include(router.urls)),
]
