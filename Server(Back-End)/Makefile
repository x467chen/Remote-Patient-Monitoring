help:
	@echo '                                                                                     		'
	@echo 'Makefile for the patients portal.                                              		        '
	@echo '                                                                                     		'
	@echo 'Usage:                                                                               		'
	@echo '    make requirements                 install requirements for the project     		        '
	@echo '    make migrate                      apply migrations                               		'
	@echo '    make serve                        start the dev server at 127.0.0.1:8000         		'
	@echo '                                                                                     		'

requirements:
	pip install -r requirements.txt

migrate:
	python manage.py migrate


#port
serve:
	python manage.py runserver 0.0.0.0:8000
