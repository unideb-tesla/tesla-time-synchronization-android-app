# TESLA Time Synchronization Android Application
An Android loose time synchronization application for the TESLA protocol.

With this Android application, you can pertform loose time synchronization with a dedicated time synchronization server. The app uses TCP protocoll to communicate with the server. At the end of the successful time synchronization, you will get an upper bound to the time difference in milliseconds between your mobile phone and the server. Upon getting this value, the app also broadcasts a message to other apps, that can use this value for whatever they want (like using it in a TESLA communication).

To perform the loose time synchronization, you must provide a public key file for the program (you can place it in an SD card or in the internal memory of your phone), and you should also know the IP address and the port of the synchronization server.

Currently there is not any release APK file that you can install directly on your Android phone, so the best option is to connect your phone to your development PC, and then install the app with the Android Studio IDE.