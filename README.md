# logger-passive-sensing

THIS PROJECT IS NOT UNDER ACTIVE DEVELOPMENT

This Android application was used to enable mental health research at the University of Toronto. The application passively senses a variety of sensor data and also actively asks users (i.e., study participants) to complete self-report measures of depression, anxiety, and general impairment. The data is transmitted to a Firebase backend, which is not included as part of this repository. Downstream analysis of the collected data included the engineering of features which were hypothesized to capture the mental health state of participants. Associations between these extracted features and mental health state (as measured by the self-report measures embedded in the app) were then tested.

To be able to build this app:

- create a Firebase project and the add "google-services.json" file associated with your Firebase project to the "app" directory
- get a Google Awareness API key and insert the key into the "AndroidManifest.xml" file
- sign up for BugFender: Get an app key at bugfender.com and specify the key in the "LoggerApplication.java" file
- might need to update for newer versions of Android (update build setting and dependencies, remove deprecated code, etc)
