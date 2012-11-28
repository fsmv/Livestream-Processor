package net.sapium.livestreamprocessor.utils;

import java.io.File;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class Timelapser extends Processor {

    public static final int TASK_TIMELAPSE = 1;
    private File inFile;
    private double speedupFactor;

    public Timelapser(ProgressChangedListener listener) {
        super(listener);

        registerTask(TASK_TIMELAPSE);
    }

    public Timelapser(ProgressChangedListener listener, File inFile, File outFile, double speedupFactor) {
        this(listener);
        this.inFile = inFile;
        this.setOutFile(outFile);
        this.speedupFactor = speedupFactor;
    }
    
    public void setInFile(File inFile){
        this.inFile = inFile;
    }
    
    public void setSpeedupFactor(double speedupFactor){
        this.speedupFactor = speedupFactor;
    }

    @Override
    public boolean validate(int task) {
        if(speedupFactor <= 0){
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

        return true;
    }

    @Override
    public void process(int task) {
        if (task == TASK_TIMELAPSE) {
            MediaTimelapser timelapseAdapter = new MediaTimelapser(speedupFactor); // do not hard code this value
            VideoData inVid = new VideoData(inFile);
            IMediaReader reader = inVid.getReader();

            reader.addListener(timelapseAdapter);

            IMediaWriter writer = ToolFactory.makeWriter(getOutFile().getAbsolutePath());
            writer.addVideoStream(0, 1, inVid.getWidth(), inVid.getHeight());
            timelapseAdapter.addListener(writer);

            while (reader.readPacket() == null)
                ;

            writer.close();
        }
    }
}
