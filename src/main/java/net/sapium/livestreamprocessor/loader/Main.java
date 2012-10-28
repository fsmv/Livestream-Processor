package net.sapium.livestreamprocessor.loader;

import net.sapium.livestreamprocessor.App;

public class Main {
    public static void main(String[] args) {
        final MultiPlatformSwtHelper multiPlatformSwtHelper = new MultiPlatformSwtHelper();
        multiPlatformSwtHelper.addSwtPlatformDependentJarURLToSystemClassLoader();
        
        App.main(args);
    }
}
