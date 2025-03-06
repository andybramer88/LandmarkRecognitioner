Readme File and Explanations for this Android App
=================================================

1) Sources
==========
This App is build on a tutorial by the android software developer Philipp Lackner
https://www.youtube.com/watch?v=ViRfnLAR_Uc
The AI Model is downloaded from here:
https://www.kaggle.com/models/google/landmarks/tfLite/classifier-europe-v1/1?tfhub-redirect=true
https://github.com/tensorflow/tfhub.dev/blob/master/assets/docs/google/collections/landmarks/1.md

2) Purpose
==========
This app is for demonstrating how AI could be use in image analysis and object recognition and
is the practical part of the bachelor thesis 1.

3) Technical Background
=======================
This app divides its functionality into 3 parts:
a - use CameraX für captioning pictures
b - preprocessing pic data before use of AI model (e.g. crop)
c - use AI model and present output on GUI

4) Setting up a Test
====================
This app is tested with an emulated device:
Google Pixel 3a; API 31
Emulated Camera: Webcam
Pre-Trained model: /assets/landmarks.tflite
Testfiles: /resources
Once the app is started, you can use your webcam for testing purposes with the images provided under /resources.

4) Conclusio
============
The complicated thing in using AI is the training part to get good results. The usage of pretrained models does not
require much of code. But you can even fine tune it to get better results, e.g. in terms of image pre processing, resolution,
used cropped field, using other probability scores etc.
