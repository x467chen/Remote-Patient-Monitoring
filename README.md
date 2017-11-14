## Synopsis

This project developed an Android application to collect and store data from sensor(MOTO 360 Watch) with MVP framework. Designed Server architecture with Django REST framework. Invoke Smart Phone Local Database to manage data and interacted Android and Serve with HTTP protocol. Used Material Design to improve UI.

## Motivation

There is a current trend to develop IOT devices in medical industry, which can provide online and continuous information for remote monitoring of the health parameters, such as: body temperature, blood glucose, heartbeat rate and blood pressure. The goal of our project is building an efficient health care monitoring system that includes mobile service, server side and data collection (from sensors or from simulation). In the system, users are classified into five groups: seniors/patients, their relatives, family doctors, system operators, emergency services.<br>
The data collection devices (the sensors) collect health data from seniors/patients. The procedure of detecting the data from patients happens automatically which can save many times from patients than they use traditional methods to detect their healthy situation.The health condition data will share with their relatives and family doctors. If the sensors detect the seniors/patients is under unusual health condition, alert will sent to them, their relatives and family doctors. After confirmation, emergency service will be called with detailed location as well as patient’s basic condition. The system also provide detailed health data to the family doctors. By analysing the history data of the particular seniors/patients, doctors can know more about their condition and have better solution and advice to the seniors/patients. The system also provides an access for the government to observe and analyze the trend of different disease. The government can adjust their policy and health service toward particular problems.<br>
According to the statistic result, Over 30% of the time seniors experiencing a fall can’t call for emergency help potentially due to unconsciousness or confusion. Our purpose is to avoid such situation. The seniors’ relatives or their loved ones can also call emergency service. <br> 
To improve the utility and operability of our system, we will build up an android client for the patients/seniors and their relatives while create a desktop environment for the family doctors, system operators and emergency service. 
Our design will consider two iterations for the development. First, for each group the basic and prior functions will be developed as a course project to guarantee the satisfaction. Then, to meet the optional requirements, new demands of users or to improve nonfunctional properties the next iteration of development will be documented.<br>


## Installation
A.Client<br>
1.Install Andriod  Studio and Python 2.7<br>
2.Open the Andriod Studio and utilize the Android Virtual Device（AVD）to test.

B.Server<br>
1.Install Postman to mirrors the Existing API Development Workflow<br>
2.Install DB Browsers for SQLite to manage the data on server<br>
3.Creates isolated Python environments for Python libraries<br>
Install virtualenv via pip by cmd line:$ pip install virtualenv<br>
Create virtualenv space: $ virtualenv venv<br>
Activate virtualenv:$ source ./venv/bin/activate<br>




