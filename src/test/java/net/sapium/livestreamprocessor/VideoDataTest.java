package net.sapium.livestreamprocessor;

import java.io.File;

import com.xuggle.xuggler.IRational;

import net.sapium.livestreamprocessor.utils.VideoData;

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
	    File testFile = new File("videoDataTest.flv");
	    testFile.delete();
	    TestcaseGenerator.generateVideoWithSound(testFile.getAbsolutePath());
		VideoData test = new VideoData(testFile);
		
		boolean result = true;
		if(test.getAudioChannels() != 1){
			result = false;
			System.out.println("Num audio channels failure; expected: " + 1 + " actual: " + test.getAudioChannels());
		}else if(test.getAudioSampleRate() != 44100){
			result = false;
			System.out.println("Audio sample-rate failure; expected: " + 44100 + " actual: " + test.getAudioSampleRate());
		}else if(test.getDuration() != 5015){
			result = false;
			System.out.println("Duration failure; expected: " + 5015 + " actual: " + test.getDuration());
		}else if(test.getHeight() != 600){
			result = false;
			System.out.println("Frame height failure; expected: " + 600 + " actual: " + test.getHeight());
		}else if(test.getWidth() != 800){
			result = false;
			System.out.println("Frame width failure; expected: " + 800 + " actual: " + test.getWidth());
		}else if(test.getFrameRate().equals(IRational.make(36, 1))){
		    result = false;
		    System.out.println("Frame rate failure; expected: " + "36/1" + " actual: " + test.getFrameRate().toString());
		}
		
		test.getReader().close();
		
		if(result){
		    testFile.delete();
		}
		assertTrue(result);
	}
}
