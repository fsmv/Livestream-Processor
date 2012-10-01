package net.sapium.livetolapse.Livestream_To_Timelapse;

import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.event.IVideoPictureEvent;

/**
 * Adapter to check the encoding progress of an IMediaWriter object
 * 
 * @author Andrew Kallmeyer
 */
public class ProgressListener extends MediaListenerAdapter {

	private long finalDuration;
	private ProgressChangedListener listener;
	
	public ProgressListener(long finalDuration, ProgressChangedListener listener) {
		this.finalDuration = finalDuration;
		this.listener = listener;
	}
	
	public void onVideoPicture(IVideoPictureEvent event) {
		long currentTime = event.getTimeStamp(TimeUnit.MILLISECONDS);
		
		double progress = 1 - ((finalDuration - currentTime)/(double)finalDuration);
		listener.onProgressChanged(progress);
	}
}
