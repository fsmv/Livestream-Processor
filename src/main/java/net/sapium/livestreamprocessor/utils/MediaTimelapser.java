package net.sapium.livestreamprocessor.utils;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseCoderEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.mediatool.event.IOpenEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IVideoPicture;

public class MediaTimelapser extends MediaToolAdapter {
    private long videoSampleCounter;
    private long frameCounter = 0;
    private double speedupFactor;
    
    public MediaTimelapser(double speedup){
        this.speedupFactor = speedup;
        videoSampleCounter = 0;
    }
    
    @Override
    public synchronized void onVideoPicture(IVideoPictureEvent event) {
        if(videoSampleCounter == 0){
            frameCounter++;
            IVideoPicture picture = event.getMediaData();
            picture.setTimeStamp((long) (picture.getTimeStamp()/speedupFactor));
            super.onVideoPicture(new VideoPictureEvent(this, picture, event.getStreamIndex()));
        }
        
        videoSampleCounter = (videoSampleCounter + 1) % (int)speedupFactor;
    }

    public void onAudioSamples(IAudioSamplesEvent event) {
        //TODO: Allow putting in your own audio track, for now we'll just remove any audio though
    }
    
    @Override
    public void onClose(ICloseEvent event) {
    }
    
    @Override
    public void onAddStream(IAddStreamEvent event) {
    }

    @Override
    public void onOpen(IOpenEvent event) {
    }

    @Override
    public void onOpenCoder(IOpenCoderEvent event) {
    }

    @Override
    public void onCloseCoder(ICloseCoderEvent event) {
    }
}
