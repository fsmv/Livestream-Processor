call mvn package -P win32,!win64,!linux32,!linux64,!macosx32,!macosx64,exe

call mvn package -P !win32,win64,!linux32,!linux64,!macosx32,!macosx64,exe

call mvn package -P !win32,!win64,linux32,!linux64,!macosx32,!macosx64

call mvn package -P !win32,!win64,!linux32,linux64,!macosx32,!macosx64

call mvn package -P !win32,!win64,!linux32,!linux64,macosx32,!macosx64

call mvn package -P !win32,!win64,!linux32,!linux64,!macosx32,macosx64
