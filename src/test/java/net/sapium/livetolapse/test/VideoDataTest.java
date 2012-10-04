package net.sapium.livetolapse.test;

import java.io.File;

import net.sapium.livetolapse.ProcessingThread;
import net.sapium.livetolapse.VideoData;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class VideoDataTest extends TestCase {
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public VideoDataTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(VideoDataTest.class);
	}
	
	public void testData(){
		VideoData test = new VideoData(new File("C:\\Users\\Andrew\\workspace\\java\\Livestream-To-Timelapse\\testcase\\1.mp4"));
		System.out.println(test.getAudioChannels() + " " + test.getAudioSampleRate() + " " + test.getDuration() + " " + test.getHeight() + " " + test.getWidth());
		assertTrue(true);
		boolean result = true;
		if(test.getAudioChannels() != 2){
			result = false;
		}else if(test.getAudioSampleRate() != 44100){
			result = false;
		}else if(test.getDuration() != 70560){
			result = false;
		}else if(test.getHeight() != 720){
			result = false;
		}else if(test.getWidth() != 1280){
			result = false;
		}
		
		assertTrue(result);
	}
	
	public void testConcatenation() {
		File[] files = {new File("C:\\Users\\Andrew\\workspace\\java\\Livestream-To-Timelapse\\testcase\\1.mp4"), new File("C:\\Users\\Andrew\\workspace\\java\\Livestream-To-Timelapse\\testcase\\2.mp4")};
		String output = "C:\\Users\\Andrew\\workspace\\java\\Livestream-To-Timelapse\\testcase\\out.mp4";
		File out = new File(output);
		if(out.exists()){
			out.delete();
		}
		
		ProcessingThread.concatenateFiles(null, files, output);
		assertTrue(true);
	}
}
