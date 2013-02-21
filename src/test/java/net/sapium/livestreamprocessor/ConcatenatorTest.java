package net.sapium.livestreamprocessor;

import java.io.File;

import net.sapium.livestreamprocessor.utils.Concatenator;
import net.sapium.livestreamprocessor.utils.VideoData;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConcatenatorTest extends TestCase {
    public ConcatenatorTest(String testName) {
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

        File[] fileList = { file1, file2, file3 };

        File outFile = new File("concat.flv");
        outFile.delete();

        TestcaseGenerator.generateSplitVideoWithSound(file1.getAbsolutePath(), file2.getAbsolutePath(), file3.getAbsolutePath());
        Concatenator concat = new Concatenator(null, fileList, outFile.getAbsolutePath());
        concat.run();

        VideoData outData = new VideoData(outFile);
        VideoData data1 = new VideoData(file1);
        VideoData data2 = new VideoData(file2);
        VideoData data3 = new VideoData(file3);

        long left = outData.getDuration() / 100;
        long right = (data1.getDuration() + data2.getDuration() + data3.getDuration()) / 100 + 50;
        boolean aboutEqual = left <= right;

        outData.getReader().close();
        data1.getReader().close();
        data2.getReader().close();
        data3.getReader().close();

        if (aboutEqual) {
            outFile.delete();
            file1.delete();
            file2.delete();
            file3.delete();
            assertTrue(true);
        }else{
            assertEquals(left, right);
        }
    }
}
