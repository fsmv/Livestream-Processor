package net.sapium.livetolapse.Livestream_To_Timelapse;

import java.io.File;

import com.xuggle.mediatool.IMediaDebugListener.Event;
import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class App implements ProgressChangedListener {
	public App(String name){
		concatenateFiles(getFileList(name), "C:\\Users\\Andrew\\Downloads\\ld245\\out.mp4");
	}
	
	public static void main(String[] args) {
		if (args.length == 2) {
			throw new IllegalArgumentException("Need to pass a folder");
		}
		new App(args[0]);
		System.out.println("done");
	}

	public void concatenateFiles(File[] files, String output) {
		MediaConcatenator concatenator = new MediaConcatenator(0, 1);
		System.out.println(files[0].getAbsolutePath() + " " + files[1].getAbsolutePath());
		
		IMediaReader[] readers = new IMediaReader[files.length];
		VideoData data = null;
		long duration = 0;
		for(int i=0; i<files.length; i++){
			IMediaReader reader = ToolFactory.makeReader(files[i].getAbsolutePath());
			reader.addListener(concatenator);
			data = new VideoData(files[i]);
			duration += data.getDuration();
			System.out.println(duration);
			readers[i] = reader;
		}
		IMediaListener debug = ToolFactory.makeDebugListener(Event.META_DATA);
		IMediaWriter writer = ToolFactory.makeWriter(output);
		System.out.println(duration);
		ProgressListener progress = new ProgressListener(duration, this);
		writer.addListener(progress);
		//writer.addListener(debug);
		concatenator.addListener(writer);

		writer.addVideoStream(0, 1, data.getWidth(), data.getHeight());
		writer.addAudioStream(1, 0, data.getAudioChannels(), data.getAudioSampleRate());

		for(int i=0; i<readers.length; i++){
			while (readers[i].readPacket() == null)
				;
		}
		
		writer.close();
	}

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

	public void onProgressChanged(double progress) {
		System.out.println(progress);
	}
}
