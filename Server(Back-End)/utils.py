from rest_framework import serializers


class GenderField(serializers.ChoiceField):
    choices = ((True, "Male"), (False, "Female"), )

    def __init__(self, **kwargs):
        super(GenderField, self).__init__(choices=self.choices, **kwargs)

    def to_representation(self, value):
        return "Male" if value else "Female"
