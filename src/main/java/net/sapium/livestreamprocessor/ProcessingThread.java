package net.sapium.livestreamprocessor;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class ProcessingThread implements Runnable {

	private ProgressChangedListener listener;
	private File[] files;
	private String outFile;
	public static final String TASK_CONCATENATING = "concatenating";
	public static final String TASK_SPEEDING_UP = "speeding up";
	

	public ProcessingThread(ProgressChangedListener listener, File[] files, String outFile) {
		this.listener = listener;
		this.files = files;
		this.outFile = outFile;
	}

	public void run() {
		concatenateFiles(listener, files, outFile);
	}

	/**
	 * Concatenates a list of video files all must have the same frame size, audio rates, number of channels, and filetype
	 * 
	 * @param files
	 *            array of files to concatenate together in the order of this array
	 * @param output
	 *            location of the output file
	 */
	// TODO: Error handling for when the files array has files of different types
	// TODO: Error handling for when the output file already exists
	public static void concatenateFiles(ProgressChangedListener listener, File[] files, String output) {
	    Logger logger = LoggerFactory.getLogger(ProcessingThread.class);
	    logger.info("Concatenating files and saving as " + output);
	    
		MediaConcatenator concatenator = new MediaConcatenator(0, 1);

		IMediaReader[] readers = new IMediaReader[files.length];
		VideoData data = null;
		long duration = 0;
		for (int i = 0; i < files.length; i++) {
		    data = new VideoData(files[i]);
			IMediaReader reader = data.getReader();
			reader.addListener(concatenator);
			readers[i] = reader;
			
			duration += data.getDuration();
			logger.info(files[i].getAbsolutePath());
		}
		
		IMediaWriter writer = ToolFactory.makeWriter(output);
		if (listener != null) {
			ProgressListener progress = new ProgressListener(duration, listener);
			writer.addListener(progress);
		}
		concatenator.addListener(writer);

		writer.addVideoStream(0, 1, data.getWidth(), data.getHeight());
		writer.addAudioStream(1, 0, data.getAudioChannels(), data.getAudioSampleRate());

		for (int i = 0; i < readers.length; i++) {
			while (readers[i].readPacket() == null)
				;
		}
		
		writer.close();
		
		logger.info("Done");
	}

	/**
	 * Gets a list of files from a directory containing the files to concatenate
	 * 
	 * Folder must contain only video files of the same type and parameters
	 * 
	 * @param folder
	 *            folder to search through
	 * @return an array of files from the folder
	 */
	public static File[] getFileList(String folder) {
		File sourceFolder = new File(folder);
		File[] result = null;

		if (sourceFolder.exists() && sourceFolder.isDirectory()) {
			File[] files = sourceFolder.listFiles();

			String extension = "";
			for (int i = 0; i < files.length; i++) {
				String name = files[i].getAbsolutePath();
				int index = name.lastIndexOf('.');
				if (extension == "") {
					extension = name.substring(index);
				} else if (!extension.equals(name.substring(index))) {
					throw new IllegalArgumentException("Folder contains multiple filetypes.");
				}
			}

			result = files;
		}

		return result;
	}
}
