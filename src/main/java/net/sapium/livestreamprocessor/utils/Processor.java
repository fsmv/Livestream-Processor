package net.sapium.livestreamprocessor.utils;

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

    public Processor(ProgressChangedListener listener) {
        this.listener = listener;
        validTasks = new ArrayList<Integer>();
        registerTask(TASK_NONE);
        setCurrentTask(TASK_NONE);
        logger = LoggerFactory.getLogger(Processor.class);
    }

    /**
     * @return the ProgessChangedListener to send progress updates to
     */
    public ProgressChangedListener getListener() {
        return listener;
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
    public void registerTask(int task) {
        if (validTasks.contains(task)) {
            logger.warn("Task already registered: " + task);
        } else {
            validTasks.add(task);
            logger.debug("Registered new task: " + task);
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

    @Override
    public void run() {
        if (currentTask == TASK_NONE) {
            logger.error("No task set");
        } else {
            if (validate(currentTask)) {
                logger.debug("Processing task: " + currentTask);
                process(currentTask);
            } else {
                logger.error("Could not validate data for task: " + currentTask);
            }
        }
    }
}
