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
	 * Called when the operation is canceled or is finished
	 */
	public void onTaskEnded();
}
