package net.sapium.livestreamprocessor.utils;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class which serves as a template for a class that performs some set of actions
 * 
 * @author Andrew Kallmeyer
 */
public abstract class Processor implements Runnable {
    public static final int TASK_NONE = 0;
    private int currentTask;
    private ProgressChangedListener listener;
    private ArrayList<Integer> validTasks;
    private static Logger logger;
    
    private File outFile;
    private boolean overwrite;

    public Processor(ProgressChangedListener listener) {
        this.listener = listener;
        validTasks = new ArrayList<Integer>();
        logger = LoggerFactory.getLogger(this.getClass());
        registerTask(TASK_NONE);
        setCurrentTask(TASK_NONE);
    }

    /**
     * @return the ProgessChangedListener to send progress updates to
     */
    public ProgressChangedListener getListener() {
        return listener;
    }
    
    protected Logger getLogger(){
        return logger;
    }

    /**
     * Sets the task which will be completed in run() with {@code process(currentTask)}
     * 
     * @param currentTask
     * @throws IllegalArgumentException
     *             when the task is not registered
     */
    public void setCurrentTask(int currentTask) {
        if (validTasks.contains(currentTask)) {
            this.currentTask = currentTask;
        } else {
            throw new IllegalArgumentException("Task is not registered: " + currentTask);
        }
    }

    /**
     * Get the task which will be completed in run() with {@code process(currentTask)}
     * 
     * @return the current task
     */
    public int getCurrentTask() {
        return currentTask;
    }

    /**
     * Register a task for validation before {@code process(currentTask} is called
     * 
     * @param task
     */
    protected void registerTask(int task) {
        if (validTasks.contains(task)) {
            logger.warn("Task already registered: " + task);
        } else {
            validTasks.add(task);
            logger.debug("Registered new task: " + task);
        }
    }
    
    /**
     * @return the current out file location
     */
    public File getOutFile(){
        return outFile;
    }
    
    /**
     * @param outFile the new out file location
     */
    public void setOutFile(File outFile){
        this.outFile = outFile;
    }
    
    /**
     * @return whether the current out file is set to be overwritten or not
     */
    public boolean shouldOverwrite(){
        return overwrite;
    }
    
    /**
     * Change whether the current out file is should be overwritten or not
     */
    public void setOverwrite(boolean overwrite){
        this.overwrite = overwrite;
    }
    
    /**
     * Changes a file name by appending a number to the path until the new file name no longer exists
     * 
     * Stops and returns null after trying 1 through 100 to avoid infinite loops
     * 
     * @param file file name to change (the actual object goes unchanged)
     * @return a new file object with the new name or null if 1 through 100 didn't work
     */
    public static File appendNumberToFileName(File file){
        int i = 1;
        File newFile = new File(file.getAbsolutePath());
        int dotLoc = newFile.getAbsolutePath().lastIndexOf('.');
        String fileNamePref = newFile.getAbsolutePath().substring(0, dotLoc);
        String fileNameSufx = newFile.getAbsolutePath().substring(dotLoc-1);
        while(newFile.exists()){
            newFile = new File(fileNamePref + i + fileNameSufx);
            i++;
            if(i > 100){
                logger.error("Can't find an unused file name (tried appending 1 through 100 to the end of the name): " + fileNamePref + fileNameSufx);
                return null;
            }
        }
        
        return newFile;
    }
    
    /**
     * If the out file is set to be overwritten, deletes the old one
     * If the out file is set not to be overwritten, renames the file to include a number at the end
     * 
     * @return new out file (also changes the stored out file) null if the out file starts out null
     */
    public File validateOutFile(){
        if(outFile != null){
            if(outFile.exists()){
                if(!overwrite && !outFile.delete()){ //Just deletes the file if overwrite is set 

                    outFile = Processor.appendNumberToFileName(outFile);
                    if(outFile != null){
                        logger.warn("Original outfile existed and cannot overwrite, changed out file to " + outFile.getAbsolutePath());
                    }
                }
            }
            
            return outFile;
        }else{
            return null;
        }
    }

    /**
     * Called immediately before {@code process(task)}
     * 
     * Check whether the information needed in {@code process(task)}
     * are valid or not
     * 
     * @param task task to validate information for
     * @return whether required information is valid or not
     */
    public abstract boolean validate(int task);

    /**
     * Perform the task requested
     * 
     * Will be called in its own thread
     * 
     * @param task the task to preform
     */
    public abstract void process(int task);
    
    public void start(){
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        if (currentTask == TASK_NONE) {
            logger.error("No task set");
        } else {
            if (validate(currentTask)) {
                logger.debug("Processing task: " + currentTask);
                process(currentTask);
                logger.debug("Task complete!");
            } else {
                logger.error("Could not validate data for task: " + currentTask);
            }
        }
    }
}