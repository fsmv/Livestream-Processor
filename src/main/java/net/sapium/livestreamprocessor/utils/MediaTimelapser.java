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
    public static final int AUDIO_SPEED_UP = 1;
    public static final int AUDIO_REMOVE = 2;
    public static final int AUDIO_REPLACE = 3;

    private long videoSampleCounter;
    private long audioSampleCounter;
    private double speedupFactor;
    private int audioOption;
    private boolean onAudioFile = false;
    private long lastTimeStamp;

    public MediaTimelapser(double speedup, int audioOption) {
        this.speedupFactor = speedup;
        videoSampleCounter = 0;
        audioSampleCounter = 0;
        lastTimeStamp = 0;
        this.audioOption = audioOption;
    }

    @Override
    public synchronized void onVideoPicture(IVideoPictureEvent event) {
        if (speedupFactor*(videoSampleCounter/speedupFactor - Math.floor(videoSampleCounter/speedupFactor)) < 1) {
            IVideoPicture picture = event.getMediaData();
            picture.setTimeStamp((long) (picture.getTimeStamp() / speedupFactor));
            if (!onAudioFile) {
                lastTimeStamp = picture.getTimeStamp();
            }
            super.onVideoPicture(new VideoPictureEvent(this, picture, event.getStreamIndex()));
        }

        videoSampleCounter++;
    }

    public void onAudioSamples(IAudioSamplesEvent event) {
        if(audioOption == AUDIO_SPEED_UP){
            if(speedupFactor*(audioSampleCounter/speedupFactor - Math.floor(audioSampleCounter/speedupFactor)) < 1){
                event.getAudioSamples().setTimeStamp((long) (event.getAudioSamples().getTimeStamp() / speedupFactor));
                super.onAudioSamples(event);
            }
            
            audioSampleCounter++;
        }else if(audioOption == AUDIO_REPLACE && onAudioFile){
            if(event.getAudioSamples().getTimeStamp() <= lastTimeStamp){
                super.onAudioSamples(event);
            }
        }
    }

    @Override
    public void onClose(ICloseEvent event) {
        onAudioFile = true;
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
