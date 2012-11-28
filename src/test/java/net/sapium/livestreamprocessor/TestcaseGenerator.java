package net.sapium.livestreamprocessor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IRational;

public class TestcaseGenerator {
    public static final IRational FRAME_RATE = IRational.make(36, 1); // FPS has to be a factor of SAMPLE_RATE for the audio to mesh correctly
    public static final int SAMPLE_RATE = 44100;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static void generateVideoWithoutSound(String outFile) {
        int lengthInSeconds = 10;

        final IMediaWriter writer = ToolFactory.makeWriter(outFile);

        writer.addVideoStream(0, 0, FRAME_RATE, WIDTH, HEIGHT);

        for (int i = 0; i < lengthInSeconds * FRAME_RATE.getDouble(); i++) {
            writer.encodeVideo(0, generateVideoFrame(i), (long) (1000 * i / FRAME_RATE.getDouble()), TimeUnit.MILLISECONDS);
        }

        writer.close();
    }

    public static void generateVideoWithSound(String outFile) {
        int lengthInSeconds = 5;

        final IMediaWriter writer = ToolFactory.makeWriter(outFile);

        writer.addVideoStream(0, 0, FRAME_RATE, WIDTH, HEIGHT);
        writer.addAudioStream(1, 0, 1, SAMPLE_RATE);

        int offset = 0;
        for (int i = 0; i < lengthInSeconds * FRAME_RATE.getDouble(); i++) {
            short[] samples = generateAudioSamples(offset);
            writer.encodeVideo(0, generateVideoFrame(i), (long) (1000 * i / FRAME_RATE.getDouble()), TimeUnit.MILLISECONDS);
            writer.encodeAudio(1, samples, (long) (offset / SAMPLE_RATE) * 1000, TimeUnit.MILLISECONDS);
            offset += samples.length;
        }

        writer.close();
    }

    public static void generateSplitVideoWithSound(String file1, String file2, String file3) {
        int totalLengthInSeconds = 6; // Should be a multiple of 3
        int seperateVideoLength = totalLengthInSeconds / 3; // Makes 3 videos

        final IMediaWriter writer1 = ToolFactory.makeWriter(file1);
        writer1.addVideoStream(0, 0, FRAME_RATE, WIDTH, HEIGHT);
        writer1.addAudioStream(1, 1, 1, SAMPLE_RATE);
        final IMediaWriter writer2 = ToolFactory.makeWriter(file2);
        writer2.addVideoStream(0, 0, FRAME_RATE, WIDTH, HEIGHT);
        writer2.addAudioStream(1, 1, 1, SAMPLE_RATE);
        final IMediaWriter writer3 = ToolFactory.makeWriter(file3);
        writer3.addVideoStream(0, 0, FRAME_RATE, WIDTH, HEIGHT);
        writer3.addAudioStream(1, 1, 1, SAMPLE_RATE);

        int soundOffset = 0;
        int vidOffset = 0;
        IMediaWriter writer = null;
        for (int i = 0; i < totalLengthInSeconds * FRAME_RATE.getDouble(); i++) {
            if (i < seperateVideoLength * FRAME_RATE.getDouble()) {
                writer = writer1;
                vidOffset = 0;
            } else if (i < 2 * seperateVideoLength * FRAME_RATE.getDouble()) {
                if (writer != writer2) {
                    vidOffset = i;
                }
                writer = writer2;
            } else {
                if (writer != writer3) {
                    vidOffset = i;
                }
                writer = writer3;
            }

            short[] samples = generateAudioSamples(soundOffset);
            writer.encodeVideo(0, generateVideoFrame(i), (long) (1000 * (i - vidOffset) / FRAME_RATE.getDouble()), TimeUnit.MILLISECONDS);
            writer.encodeAudio(1, samples, (long) (((soundOffset) / SAMPLE_RATE) - vidOffset/FRAME_RATE.getDouble()) * 1000, TimeUnit.MILLISECONDS);
            soundOffset += samples.length;
        }

        writer1.close();
        writer2.close();
        writer3.close();
    }

    public static BufferedImage generateVideoFrame(int frameNum) {
        BufferedImage frame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        int displayNumber = (int) (frameNum / FRAME_RATE.getDouble());

        Graphics2D g = (Graphics2D) frame.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.black);
        g.drawString(displayNumber + "", WIDTH / 2, HEIGHT / 2);
        g.dispose();

        return frame;
    }

    public static short[] generateAudioSamples(int offset) {
        short[] samples = new short[(int) (SAMPLE_RATE / FRAME_RATE.getDouble())];
        double startFrequency = 440;

        for (int i = 0; i < samples.length; i++) {
            double newFreq = startFrequency + (i + offset) / 600.0;
            samples[i] = (short) (10000 * Math.sin(newFreq * (2 * Math.PI) * (i + offset) / SAMPLE_RATE));
        }

        return samples;
    }
}
