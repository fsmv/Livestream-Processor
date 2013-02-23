/* This file is part of Livestream Processor.
 * Copyright (C) 2013  Andrew Kallmeyer
 * 
 * Livestream Processor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * Livestream Processor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Livestream Processor.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sapium.livestreamprocessor;

import java.io.File;

import net.sapium.livestreamprocessor.gui.MainWindow;
import net.sapium.livestreamprocessor.utils.Concatenator;
import net.sapium.livestreamprocessor.utils.ProgressChangedListener;
import net.sapium.livestreamprocessor.utils.Timelapser;

public class Main {
    public static String ARG_CONCAT = "concatenate";
    public static String ARG_TIMELAPSE = "timelapse";
    public static String ARG_DOWNLOAD = "download";
    public static String ARG_HELP = "help";

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals(ARG_CONCAT)) {
                argConcat(args);
            } else if (args[0].equals(ARG_TIMELAPSE)) {
                argTimelapse(args);
            } else if (args[0].equals(ARG_DOWNLOAD)) {
                argDownload(args);
            } else if (args[0].equals(ARG_HELP)) {
                argHelp(args);
            }
        } else {
            loadWindow(args);
        }
    }

    public static void argConcat(String[] args) {
        if (args.length > 3) {
            File[] fileList = new File[args.length - 3];
            for(int i=1; i<args.length-1; i++){
                int fi = i-2;
                fileList[fi] = new File(args[i]);
            }
            String outFile = args[args.length-1];
            
            Concatenator concat = new Concatenator(new TerminalProgress(), fileList, outFile);
            concat.run();
        } else {
            System.out.println("Usage: java -jar LiveProc.jar concatenate [file1] ... [fileN] [outFile]");
        }
    }

    public static void argTimelapse(String[] args) {
        if(args.length == 4){
            File inFile = new File(args[1]);
            File outFile = new File(args[2]);
            double speedupFactor = Double.parseDouble(args[3]);
            
            Timelapser timelapser = new Timelapser(new TerminalProgress(), inFile, outFile, speedupFactor);
            timelapser.run();
        }else{
            System.out.println("Usage: java -jar LiveProc.jar timelapse [inFile] [outFile] [speedup factor]");
        }
    }

    public static void argDownload(String[] args) {

    }

    public static void argHelp(String[] args) {

    }

    public static void loadWindow(String[] args) {
        final MultiPlatformSwtHelper multiPlatformSwtHelper = new MultiPlatformSwtHelper();
        multiPlatformSwtHelper.addSwtPlatformDependentJarURLToSystemClassLoader();

        MainWindow.main(args);
    }
    
    public static class TerminalProgress implements ProgressChangedListener {
        @Override
        public void onProgressChanged(double progress) {
            
        }

        @Override
        public void onTaskStarted() {
            
        }

        @Override
        public void onTaskEnded() {
            
        }
    }
}