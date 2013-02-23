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
        timelapseTestVideo.delete();
        File timelapseOutVideo = new File("timelapse_out.flv");
        timelapseOutVideo.delete();
        double speedupFactor = 3.14;
        
        timelapseTestVideo.delete();
        TestcaseGenerator.generateVideoWithoutSound("timelapse.flv");
        
        Timelapser timelapser = new Timelapser(null, timelapseTestVideo, timelapseOutVideo, speedupFactor);
        System.out.println(timelapser.getOutFile().getAbsolutePath());
        
        timelapser.run(); //Not in a thread because the method will end and the test won't get to run
        
        VideoData testData = new VideoData(timelapseTestVideo);
        VideoData testData2 = new VideoData(timelapseOutVideo);
        
        double ratio = testData.getDuration()/(double)testData2.getDuration();
        
        testData.getReader().close();
        testData2.getReader().close();
        
        if(Math.abs(ratio - speedupFactor) < 0.1){
            timelapseTestVideo.delete();
            timelapseOutVideo.delete();
            assertTrue(true);
        }else{
            assertEquals(speedupFactor, ratio);
        }
    }
}
