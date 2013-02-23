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

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.AudioSamplesEvent;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseCoderEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.mediatool.event.IOpenEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.IVideoPicture;

public class MediaTimelapser extends MediaToolAdapter {
    public static final int AUDIO_SPEED_UP = 1;
    public static final int AUDIO_REMOVE = 2;
    public static final int AUDIO_REPLACE = 3;

    private long videoSampleCounter;
    private int audioStreamIndex;
    private double speedupFactor;
    private int audioOption;
    private boolean onAudioFile = false;
    private long lastTimeStamp;

    public MediaTimelapser(double speedup, int audioOption) {
        this.speedupFactor = speedup;
        videoSampleCounter = 0;
        lastTimeStamp = 0;
        this.audioOption = audioOption;
    }

    @Override
    public synchronized void onVideoPicture(IVideoPictureEvent event) {
        if (speedupFactor * (videoSampleCounter / speedupFactor - Math.floor(videoSampleCounter / speedupFactor)) < 1) {
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
        if (audioOption == AUDIO_SPEED_UP) {
            event.getAudioSamples().setTimeStamp((long) (event.getAudioSamples().getTimeStamp() / speedupFactor));
            int sampleCount = 0;
            for (int i = 0; i < event.getAudioSamples().getNumSamples(); i++) {
                if (speedupFactor * (i / speedupFactor - Math.floor(i / speedupFactor)) < 1) {
                    sampleCount++;
                    int numChannels = event.getAudioSamples().getChannels();
                    Format format = event.getAudioSamples().getFormat();
                    for(int channel = 0; channel<numChannels; channel++){
                        int sample = event.getAudioSamples().getSample(i, channel, format);
                        event.getAudioSamples().setSample((long) (i / speedupFactor), channel, format, sample);
                    }
                }
            }
            
            event.getAudioSamples().setComplete(true, sampleCount, event.getAudioSamples().getSampleRate(), event.getAudioSamples().getChannels(),
                    event.getAudioSamples().getFormat(), (long) (event.getAudioSamples().getPts()/speedupFactor));
            super.onAudioSamples(event);
        } else if (audioOption == AUDIO_REPLACE && onAudioFile) {
            if (event.getAudioSamples().getTimeStamp() <= lastTimeStamp) {
                IAudioSamplesEvent newEvent = new AudioSamplesEvent(this, event.getAudioSamples(), audioStreamIndex);
                super.onAudioSamples(newEvent);
            }
        }else if(!onAudioFile){
            audioStreamIndex = event.getStreamIndex();
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
