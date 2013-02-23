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

package net.sapium.livestreamprocessor.gui;

import net.sapium.livestreamprocessor.utils.Processor;
import net.sapium.livestreamprocessor.utils.ProgressChangedListener;

import org.eclipse.swt.widgets.Composite;

/**
 * Tab superclass for organizing the GUI elements
 * 
 * @author Andrew Kallmeyer
 */
public abstract class TabContent extends Composite {
    public static final String[] filterNames = { "Video files", "All files" };
    public static final String[] filterExtensions = {"*.flv;*.mp4;*.ogg;*.avi;*.mov", "*.*"};
    
    protected Processor processor;
    
    public TabContent(Composite arg0, int arg1) {
        super(arg0, arg1);
        createContents();
        addListeners();
    }
    
    /**
     * Cancel the running operation
     */
    public void cancel() {
        if(processor != null && processor.isRunning()) {
            processor.cancel();
        }
    }
    
    /**
     * Create GUI elements here
     */
    protected abstract void createContents();
    
    /**
     * Add listeners to the GUI elements here (Called after createContents())
     */
    protected abstract void addListeners();
    
    /**
     * Called when the start button is pressed. Each tab should implement its
     * functionality here.
     * 
     * @param listener ProgressChangedListener to send progress events to
     */
    protected abstract void start(ProgressChangedListener listener);
}
