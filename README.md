# Headwind Remote: free and open source remote control of Android devices

This is the agent application for sharing the Android device screen, sending the screencast to the server, and playing gestures sent by a person who controls the device.

Headwind Remote website: https://headwind-remote.com

The Headwind Remote project is completely open source, both client and server modules are available. 

## Software safety

Unfortunately there was an issue when Headwind Remote and applications based on its source code were falsely detected as malware by Google Play Protect which damaged developer accounts. 

Headwind Remote has been thoroughly reviewed and tested by an independent cybersecurity company https://aegisbyte.com and they confirmed the absence of any malicious code. Additionally, they have found no high-risk vulnerabilities. The audit report is available [here](https://headwind-remote.com/hremote_security_assessment_aegisbytes_2307.pdf).

Information about the usage of sensitive permissions could be found [here](https://headwind-remote.com/privacy-policy/).

We believe it is now safe to use Headwind Remote as a base software for your projects. In the case of any problems with Google, please contact the [developer](https://h-mdm.com/contact-us/), we are ready to help.

## Mobile device management

The Headwind Remote project is sponsored by [Headwind MDM](https://h-mdm.com), the open source mobile device management system for Android. 

Since you're interested in the remote control of Android devices, consider using Headwind MDM in your company. It is easily installed and makes all your Android devices controllable from a single server.

The Headwind Remote Premium:

* is seamlessly integrated into Headwind MDM as a module;
* automatically starts by executing a command from the remote server;
* doesn't require user interaction and is suitable for kiosk devices;
* supports any HTTPS certificates.

## Building Headwind Remote

To build Headwind Remote, open the project in Android Studio, place the SDK location in the *local.properties* file, and build the project. 

Once the project built successfully, you can set up your Headwind Remote server URL and secret in the *app/build.gradle*. This will simplify the initial setup of the application by setting your server as default.

## Running the app

The application uses accessibility services to play the remote gestures. Please enable accessibility services when the application prompts.

While sharing the screen, the application displays a flashing green dot in the top left corner. This dot doesn't only display that your screen is casting to a remote peer, it generates a small video traffic stabilizing the picture and keeping the client alive. To enable the dot, allow the Headwind Remote agent to draw overlays (display on top of other apps).

## Compatibility

Headwind Remote is using native Android API and is therefore compatible with all Android devices and builds since Android 7 and above. AOSP and custom Android OS are also supported.

The server can be installed on any Linux system (tested on Ubuntu Linux 18.04 and above). The manager application is web-based. It works in any browser supporting WebRTC and doesn't require installation of any desktop software.

More details about the software and purchasing the premium version can be found at https://headwind-remote.com.
