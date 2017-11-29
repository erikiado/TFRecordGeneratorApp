# TFRecordGeneratorApp
In this repository, all the code needed to run the Object Localization Dataset Generator System is being hosted. 
This system consist of 2 applications a mobile android application which is in charge of capturing the images, labeling them and uploading to the server; and a web server written in python using flask which main purpose is to compile, organize and make all of the datasets available.

## Android Application Setup
Install Android Studio from:
https://developer.android.com/studio/install.html


Open the TFRecords project which already contains all the dependencies needed to run the application.
Change the server string in the strings file under the resources directory with your current ip.
You can get your current ip by running the commands `ifconfig` on linux or `ipconfig` on windows.
Run the project.

If you want to simply start a project which uses the latest opencv library and can run the camera on full screen, a small example application is also provided.

## Flask Server Setup
First follow the instructions in this tensorflow guide in order to install all the dependecies for that module.
https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/installation.md

After the installation of the tensorflow libraries you can proceed to install the dependencies for the server by running the following command inside the tfd_server directory.

`pip install -r requirements.txt`

After installing all the dependecies you can run the server by running the following command:
`flask run`
