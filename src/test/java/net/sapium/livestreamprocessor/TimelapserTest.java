package net.sapium.livestreamprocessor;

import java.io.File;

import net.sapium.livestreamprocessor.utils.Timelapser;
import net.sapium.livestreamprocessor.utils.VideoData;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TimelapserTest extends TestCase {
    public TimelapserTest(String testName){
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(TimelapserTest.class);
    }
    
    public void testTimelapse(){
        File timelapseTestVideo = new File("timelapse.flv");
        File timelapseOutVideo = new File("timelapse_out.flv");
        double speedupFactor = 10.0;
        
        timelapseTestVideo.delete();
        TestcaseGenerator.generateVideoWithoutSound("timelapse.flv");
        
        Timelapser timelapser = new Timelapser(null, timelapseTestVideo, timelapseOutVideo, speedupFactor);
        System.out.println(timelapser.getOutFile().getAbsolutePath());
        timelapser.setCurrentTask(Timelapser.TASK_TIMELAPSE);
        
        timelapser.run(); //Not in a thread because the method will end and the thread won't get to run
        
        VideoData testData = new VideoData(timelapseTestVideo);
        VideoData testData2 = new VideoData(timelapseOutVideo);
        
        double ratio = testData.getDuration()/(double)testData2.getDuration();
        
        if((int)ratio == (int)speedupFactor){
            timelapseTestVideo.delete();
            timelapseOutVideo.delete();
            assertTrue(true);
        }else{
            assertEquals(speedupFactor, ratio);
        }
    }
}
