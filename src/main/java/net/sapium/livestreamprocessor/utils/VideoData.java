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
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * A data class for finding and holding data about a video file
 * 
 * @author Andrew Kallmeyer
 */
public class VideoData {
	private int width;
	private int height;
	private int audioChannels;
	private int audioSampleRate;
	private long duration;
	private IMediaReader reader;
	private IRational frameRate;
	
	public VideoData(File video){
		if (!video.exists()) {
			throw new IllegalArgumentException("File not found.");
		}
		reader = ToolFactory.makeReader(video.getAbsolutePath());
		reader.open();
		IContainer container = reader.getContainer();

		for (int i = 0; i < container.getNumStreams(); i++) {
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();

			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				width = coder.getWidth();
				height = coder.getHeight();
				frameRate = coder.getFrameRate();
			} else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				audioChannels = coder.getChannels();
				audioSampleRate = coder.getSampleRate();
			}
		}
		duration = container.getDuration()/1000;
	}

	/**
	 * @return the frame width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the frame height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the number of audio channels
	 */
	public int getAudioChannels() {
		return audioChannels;
	}

	/**
	 * @return the the sample rate
	 */
	public int getAudioSampleRate() {
		return audioSampleRate;
	}
	
	public IRational getFrameRate(){
	    return frameRate;
	}

	/**
	 * @return the duration in ms
	 */
	public long getDuration() {
		return duration;
	}
	
	public IMediaReader getReader(){
	    return reader;
	}
	
	@Override
	public String toString(){
	    return "Duration: " + this.getDuration() + 
	            "\nFrame Width: " + this.getWidth() +
	            "\nFrame Height: " + this.getHeight() + 
	            "\nFrame Rate: " + this.getFrameRate() +
	            "\nAudio Channels: " + this.getAudioChannels() + 
	            "\nAudio Sample Rate: " + this.getAudioSampleRate(); 
	}
}