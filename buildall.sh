#!/bin/sh

mvn package -P win32,!win64,!linux32,!linux64,!macosx32,!macosx64,exe
mvn package -P !win32,win64,!linux32,!linux64,!macosx32,!macosx64,exe
mvn package -P !win32,!win64,linux32,!linux64,!macosx32,!macosx64
mvn package -P !win32,!win64,!linux32,linux64,!macosx32,!macosx64
mvn package -P !win32,!win64,!linux32,!linux64,macosx32,!macosx64
mvn package -P !win32,!win64,!linux32,!linux64,!macosx32,macosx64
