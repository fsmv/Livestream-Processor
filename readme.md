Downloads
---------
v0.8:
 - [Linux 64 bit](http://code.google.com/p/livestream-processor/downloads/detail?name=Livestream-Processor-v0.8-linux64.jar)
 - [Linux 32 bit](http://code.google.com/p/livestream-processor/downloads/detail?name=Livestream-Processor-v0.8-linux32.jar)
 - [Windows 64 bit](http://code.google.com/p/livestream-processor/downloads/detail?name=Livestream-Processor-v0.8-win64.jar)
 - [Windows 32 bit](http://code.google.com/p/livestream-processor/downloads/detail?name=Livestream-Processor-v0.8-win32.jar)
 - [Mac 64 bit](http://code.google.com/p/livestream-processor/downloads/detail?name=Livestream-Processor-v0.8-mac64.jar)
 - [Mac 32 bit](http://code.google.com/p/livestream-processor/downloads/detail?name=Livestream-Processor-v0.8-mac32.jar)

What Works
----------

 - Concatenating files
  - Concatenates arbitrary numbers of files
  - Manual ordering of files to concatenate
  - Selection of which files in the list to concatenate
  - Progress bar updating
  - Run from command line
 - Timelapsing files
  - Speeds up a video by an arbitrary amount
  - Run from command line
  - Final length estimator
  - Choose amount to speed up by end video length or by multiplier
  - Displays progress
  - Add your own audio
  - Speed up in-place audio
 - Multi-platform building
 - Time remaining predictor
 - Tabs with an independent progress bar, start button, and status output

What Doesn't (yet)
------------------

 - Concatenation:
  - Output codec selection? (at least keep it from using the AAC audio codec because xuggler can't handle reading it)
  - CPU Limiting
  - Show progress by drawing a background on completed files in the list
 - Timelapse Creator:
  - It all works!
 - Twitch.tv downloader:
  - Everything
 - Run more than one task consecutively without prompting the user
  - Download -> Concatenate -> Timelapse
  - Concatenate -> Timelapse
 - Program Icon
 - About dialog

Building
--------

To build for your platform run `mvn package`.

To build for all platforms at once run either `buildall.sh` or `buildall.bat` depending on your operating system.

To build for a specific platform run `mvn package -P win32,!win64,!linux32,!linux64,!macosx32,!macosx64` where the platform without the exclamation point is the one you want to build for.

Credits
-------

 - Class loader (MultiPlatformSwtHelper.java) and parts of the Maven configuration file: [Multiplatform SWT](https://github.com/jendap/multiplatform-swt)
 - Video Library: [Xuggler](http://www.xuggle.com/xuggler/)
 - GUI: [SWT](http://www.eclipse.org/swt/)
 - Ideas about loading SWT and a snippet on using jar-in-jar-loader: [SWTJar](http://mchr3k.github.com/swtjar/)
 - Loading internal jars: jar-in-jar-loader.jar
 - Application icon: Antonio Lassandro
