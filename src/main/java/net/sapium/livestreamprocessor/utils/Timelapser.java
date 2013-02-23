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

import java.io.File;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class Timelapser extends Processor {
    private File inFile;
    private double speedupFactor;
    private int audioOption;
    private File audioFile;

    public Timelapser(ProgressChangedListener listener) {
        super(listener);
    }

    public Timelapser(ProgressChangedListener listener, File inFile, File outFile, double speedupFactor) {
        this(listener);
        this.inFile = inFile;
        this.setOutFile(outFile);
        this.speedupFactor = speedupFactor;
        this.audioOption = MediaTimelapser.AUDIO_REMOVE;
    }

    public Timelapser(ProgressChangedListener listener, File inFile, File outFile, double speedupFactor, int audioOption, File audioFile) {
        this(listener, inFile, outFile, speedupFactor);
        this.audioOption = audioOption;
        this.audioFile = audioFile;
    }

    public void setInFile(File inFile) {
        this.inFile = inFile;
    }

    public void setSpeedupFactor(double speedupFactor) {
        this.speedupFactor = speedupFactor;
    }

    @Override
    public boolean validate() {
        if (speedupFactor <= 0) {
            getLogger().error("Speed up factor is invalid: " + speedupFactor);
            return false;
        }

        if (!inFile.isFile() || !inFile.exists()) {
            getLogger().error("In file could not be read: " + inFile.getAbsolutePath());
            return false;
        }

        if (validateOutFile() == null) {
            getLogger().error("Out file could not be validated: " + getOutFile().getAbsolutePath());
            return false;
        }

        if (audioOption == MediaTimelapser.AUDIO_REPLACE) {
            if (!audioFile.isFile() || !audioFile.exists()) {
                getLogger().error("Audio file could not be read: " + audioFile.getAbsolutePath());
                return false;
            }
        }

        if (audioOption != MediaTimelapser.AUDIO_REMOVE && audioOption != MediaTimelapser.AUDIO_REPLACE && audioOption != MediaTimelapser.AUDIO_SPEED_UP) {
            getLogger().error("Invalid audio option: " + audioOption);
            return false;
        }

        return true;
    }

    @Override
    public void process() {
        MediaTimelapser timelapseAdapter = new MediaTimelapser(speedupFactor, audioOption);
        VideoData inVid = new VideoData(inFile);
        IMediaReader reader = inVid.getReader();

        reader.addListener(timelapseAdapter);

        IMediaWriter writer = ToolFactory.makeWriter(getOutFile().getAbsolutePath());
        ProgressChangedListener listener = this.getListener();
        VideoData audioData = null;
        if (listener != null) {
            long duration = (long) (inVid.getDuration() / speedupFactor);
            ProgressListener progress;
            if(audioOption == MediaTimelapser.AUDIO_REPLACE){
                audioData = new VideoData(audioFile);
                long audioLength = inVid.getDuration() < audioData.getDuration() ? inVid.getDuration() : audioData.getDuration(); 
                progress = new ProgressListener(duration, listener, true, audioLength);
            }else{
                progress = new ProgressListener(duration, listener);
            }
            writer.addListener(progress);
        }
        writer.addVideoStream(0, 1, inVid.getWidth(), inVid.getHeight());
        if (audioOption == MediaTimelapser.AUDIO_SPEED_UP) {
            writer.addAudioStream(1, 1, inVid.getAudioChannels(), inVid.getAudioSampleRate());
        } else if (audioOption == MediaTimelapser.AUDIO_REPLACE) {
            writer.addAudioStream(1, 1, audioData.getAudioChannels(), audioData.getAudioSampleRate());
        }
        timelapseAdapter.addListener(writer);

        while (reader.readPacket() == null && this.shouldContinue())
            ;

        if (this.shouldContinue()) {
            if (audioOption == MediaTimelapser.AUDIO_REPLACE) {
                IMediaReader audioReader = audioData.getReader();

                audioReader.addListener(timelapseAdapter);

                while (audioReader.readPacket() == null && this.shouldContinue())
                    ;
            }
        }

        writer.close();
        
        if(audioData != null){
            audioData.getReader().close();
        }

        if(inVid != null){
            inVid.getReader().close();
        }
        
        if (!this.shouldContinue()) {
            getOutFile().delete();
            listener.onTaskEnded();
        }
    }
}
