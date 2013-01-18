package net.sapium.livestreamprocessor.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class which serves as a template for a class that performs some set of actions
 * 
 * @author Andrew Kallmeyer
 */
public abstract class Processor implements Runnable {
    private ProgressChangedListener listener;
    private static Logger logger;

    private File outFile;
    private boolean overwrite;
    private boolean shouldContinue;
    private boolean isRunning;

    public Processor(ProgressChangedListener listener) {
        this.listener = listener;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * @return the ProgessChangedListener to send progress updates to
     */
    public ProgressChangedListener getListener() {
        return listener;
    }

    protected Logger getLogger() {
        return logger;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void cancel() {
        shouldContinue = false;
        logger.info("Operation canceled");
    }

    protected boolean shouldContinue() {
        return shouldContinue;
    }

    /**
     * @return the current out file location
     */
    public File getOutFile() {
        return outFile;
    }

    /**
     * @param outFile
     *            the new out file location
     */
    public void setOutFile(File outFile) {
        this.outFile = outFile;
    }

    /**
     * @return whether the current out file is set to be overwritten or not
     */
    public boolean shouldOverwrite() {
        return overwrite;
    }

    /**
     * Change whether the current out file is should be overwritten or not
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * Changes a file name by appending a number to the path until the new file name no longer exists
     * 
     * Stops and returns null after trying 1 through 100 to avoid infinite loops
     * 
     * @param file
     *            file name to change (the actual object goes unchanged)
     * @return a new file object with the new name or null if 1 through 100 didn't work
     */
    public static File appendNumberToFileName(File file) {
        int i = 1;
        File newFile = new File(file.getAbsolutePath());
        int dotLoc = newFile.getAbsolutePath().lastIndexOf('.');
        String fileNamePref = newFile.getAbsolutePath().substring(0, dotLoc);
        String fileNameSufx = newFile.getAbsolutePath().substring(dotLoc - 1);
        while (newFile.exists()) {
            newFile = new File(fileNamePref + i + fileNameSufx);
            i++;
            if (i > 100) {
                logger.error("Can't find an unused file name (tried appending 1 through 100 to the end of the name): " + fileNamePref + fileNameSufx);
                return null;
            }
        }

        return newFile;
    }

    /**
     * If the out file is set to be overwritten, deletes the old one If the out file is set not to be overwritten, renames the file to include a number at the end
     * 
     * @return new out file (also changes the stored out file) null if the out file starts out null
     */
    public File validateOutFile() {
        if (outFile != null) {
            if (outFile.exists()) {
                if (!overwrite && !outFile.delete()) { // Just deletes the file if overwrite is set

                    outFile = Processor.appendNumberToFileName(outFile);
                    if (outFile != null) {
                        logger.warn("Original outfile existed and cannot overwrite, changed out file to " + outFile.getAbsolutePath());
                    }
                }
            }

            return outFile;
        } else {
            return null;
        }
    }

    /**
     * Called immediately before {@code process()}
     * 
     * Check whether the information needed in is valid or not
     * 
     * @return whether required information is valid or not
     */
    public abstract boolean validate();

    /**
     * Perform the task requested
     * 
     * To implement the cancel function, stop running this method when the this.shouldContinue() becomes false
     * 
     * Will be called in its own thread
     */
    public abstract void process();

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        if (validate()) {
            logger.debug("Processing task");
            shouldContinue = true;
            isRunning = true;
            process();
            isRunning = false;
            logger.debug("Task complete!");
        } else {
            logger.error("Could not validate requered data");
        }
    }
}
