package net.sapium.livestreamprocessor;

import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;

/**
 * Adapter to check the encoding progress of an IMediaWriter object
 * 
 * @author Andrew Kallmeyer
 */
public class ProgressListener extends MediaListenerAdapter {

	private long finalDuration;
	private double progress;
	private ProgressChangedListener listener;
	
	public ProgressListener(long finalDuration, ProgressChangedListener listener) {
		this.finalDuration = finalDuration;
		this.listener = listener;
	}
	
	public void onVideoPicture(IVideoPictureEvent event) {
		long currentTime = event.getTimeStamp(TimeUnit.MILLISECONDS);
		
		progress = 1 - ((finalDuration - currentTime)/(double)finalDuration);
		listener.onProgressChanged(progress);
	}
	
	public void onClose(ICloseEvent event) {
	    if((int) (progress*1000) >= 980){
	        listener.onProgressChanged(1);
	    }
	}
}
