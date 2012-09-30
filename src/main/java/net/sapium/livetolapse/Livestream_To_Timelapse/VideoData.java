package net.sapium.livetolapse.Livestream_To_Timelapse;

import java.io.File;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

public class VideoData {
	private int width;
	private int height;
	private int audioChannels;
	private int audioSampleRate;
	private long duration;
	
	public VideoData(File video){
		if (!video.exists()) {
			throw new IllegalArgumentException("File not found.");
		}

		IContainer container = IContainer.make();
		if (container.open(video.getAbsolutePath(), IContainer.Type.READ, null) < 0) {
			throw new IllegalArgumentException("File could not be opened.");
		}

		for (int i = 0; i < container.getNumStreams(); i++) {
			IStream stream = container.getStream(i);
			IStreamCoder coder = stream.getStreamCoder();

			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				IRational base = stream.getTimeBase();
				duration = container.getDuration()/base.getDenominator();
				width = coder.getWidth();
				height = coder.getHeight();
			} else if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				audioChannels = coder.getChannels();
				audioSampleRate = coder.getSampleRate();
			}
		}
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the audioChannels
	 */
	public int getAudioChannels() {
		return audioChannels;
	}

	/**
	 * @return the audioSampleRate
	 */
	public int getAudioSampleRate() {
		return audioSampleRate;
	}

	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}
}