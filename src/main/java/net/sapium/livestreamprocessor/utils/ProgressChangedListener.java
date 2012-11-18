package net.sapium.livestreamprocessor.utils;

/**
 * Implement this class to be able to recieve progress data from the ProgressListenerClass
 * 
 * @author Andrew Kallmeyer
 */
public interface ProgressChangedListener {
	/**
	 * Called when the writer object the ProgressListener is attached to reports that a new video frame
	 * has been written to the file.
	 * 
	 * @param progress current encoding progress as a decimal percentage
	 */
	public void onProgressChanged(double progress);
	
	/**
	 * Called when the writer gets its first video packet
	 */
	public void onTaskStarted();
	
	/**
	 * Called when a file is closed and progress is higher than 98.0%
	 */
	public void onTaskEnded();
}
