What Works
----------

 - Concatenating files
 - Multi-platform building
 - Time remaining predictor

What Doesn't (yet)
------------------

- Concatenation:
  - Manual ordering of files to concatenate
  - Selection of which files in the list to concatenate
  - Codec selection?
  - CPU Limiting
- Time Lapse Creator:
  - Everything
- Twitch.tv downloader:
  - Everything

Building
--------

To build for your platform run `mvn package`.

To build for all platforms at once run either `buildall.sh` or `buildall.bat` depending on your operating system.

To build for a specific platform run `mvn package -P win32,!win64,!linux32,!linux64,!macosx32,!macosx64` where the platform without the exclamation point is the one you want to build for.

Credits
-------

 - Maven configuration and class loader: [Multiplatform SWT](https://github.com/jendap/multiplatform-swt)
 - Video Library: [Xuggler](http://www.xuggle.com/xuggler/)
 - GUI: [SWT](http://www.eclipse.org/swt/)
 - Ideas about loading SWT and a snippet on using jar-in-jar-loader: [SWTJar](http://mchr3k.github.com/swtjar/)
 - Loading interal jars: jar-in-jar-loader.jar
