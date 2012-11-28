package net.sapium.livestreamprocessor;

import java.io.File;

import net.sapium.livestreamprocessor.utils.Concatenator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConcatenatorTest extends TestCase {
    public ConcatenatorTest(String testName){
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(ConcatenatorTest.class);
    }
    
    public void testConcatenate() {
        File file1 = new File("concat1.flv");
        file1.delete();
        File file2 = new File("concat2.flv");
        file2.delete();
        File file3 = new File("concat3.flv");
        file3.delete();
        
        File[] fileList = {file1, file2, file3};
        
        File outFile = new File("concat.flv");
        outFile.delete();
        
        TestcaseGenerator.generateSplitVideoWithSound(file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath());
        Concatenator concat = new Concatenator(null, fileList, outFile.getAbsolutePath());
        concat.setCurrentTask(Concatenator.TASK_CONCATENATE);
        concat.run();
        
        assertTrue(true); //TODO: Some sort of checking to see if the files concatenated correctly
        //Right now you have to check manually, the gaps in audio are expected (~25ms between videos) becuase my generator leaves empty space at the beginning and end for some reason.
    }
}
