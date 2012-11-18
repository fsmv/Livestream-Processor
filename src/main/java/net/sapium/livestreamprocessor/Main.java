package net.sapium.livestreamprocessor;

import net.sapium.livestreamprocessor.gui.MainWindow;

public class Main {
    public static void main(String[] args) {
        final MultiPlatformSwtHelper multiPlatformSwtHelper = new MultiPlatformSwtHelper();
        multiPlatformSwtHelper.addSwtPlatformDependentJarURLToSystemClassLoader();

        MainWindow.main(args);
    }
}
