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

import java.util.concurrent.TimeUnit;


import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;

/**
 * Adapter to check the encoding progress of an IMediaWriter object
 * 
 * @author Andrew Kallmeyer
 */
public class ProgressListener extends MediaListenerAdapter {

    //This is just a constant for when you're replacing audio so the percent completion is approximately correct before switching to the audio replacing part
    private static final double percentForVideo = 0.70;
    
	private long finalDuration;
	private long audioDuration;
	private double progress;
	private ProgressChangedListener listener;
	private boolean started;
	private boolean isSeperatelyAddingAudio;
	
	public ProgressListener(long finalDuration, ProgressChangedListener listener) {
		this.finalDuration = finalDuration;
		this.listener = listener;
		this.isSeperatelyAddingAudio = false;
		this.audioDuration = -1;
	}
	
	public ProgressListener(long finalDuration, ProgressChangedListener listener, boolean isSeperatelyAddingAudio, long audioDuration){
	    this.finalDuration = finalDuration;
        this.listener = listener;
        
        this.isSeperatelyAddingAudio = isSeperatelyAddingAudio;
        this.audioDuration = audioDuration;
	}
	
	public void onVideoPicture(IVideoPictureEvent event) {
	    if(!started){
	        started = true;
	        listener.onTaskStarted();
	    }
	    
		long currentTime = event.getTimeStamp(TimeUnit.MILLISECONDS);
		
		if(!isSeperatelyAddingAudio){
		    progress = 1 - ((finalDuration - currentTime)/(double)finalDuration);
		}else{
		    progress = percentForVideo - ((finalDuration - currentTime)/(double)finalDuration)*percentForVideo;
		}
		
		listener.onProgressChanged(progress);
	}
	
	public void onAudioSamples(IAudioSamplesEvent event){
	    if(isSeperatelyAddingAudio && (int)(progress * 100.0) >= (int)(percentForVideo*100.0) - 4){
            long currentTime = event.getTimeStamp(TimeUnit.MILLISECONDS);
            
            progress = 1 - ((audioDuration - currentTime)/(double)audioDuration)*(1-percentForVideo);
            
            listener.onProgressChanged(progress);
	    }
	}
	
	public void onClose(ICloseEvent event) {
	    if((int) (progress*1000) >= 980){
	        listener.onTaskEnded();
	    }
	}
}
