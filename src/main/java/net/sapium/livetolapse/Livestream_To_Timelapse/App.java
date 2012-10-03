package net.sapium.livetolapse.Livestream_To_Timelapse;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class App implements ProgressChangedListener {
	protected Shell shell;
	private Text folderTextBox;
	private Text outputTextBox;
	private Button outputButton;
	private Label outputLabel;
	private ProgressBar progressBar;
	private Display display;

	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new FormLayout());

		folderTextBox = new Text(shell, SWT.BORDER);
		folderTextBox.setTouchEnabled(true);
		FormData fd_folderTextBox = new FormData();
		fd_folderTextBox.top = new FormAttachment(0, 7);
		folderTextBox.setLayoutData(fd_folderTextBox);

		Label folderLabel = new Label(shell, SWT.NONE);
		fd_folderTextBox.left = new FormAttachment(folderLabel, 6);
		FormData fd_folderLabel = new FormData();
		fd_folderLabel.top = new FormAttachment(0, 10);
		fd_folderLabel.left = new FormAttachment(0, 10);
		folderLabel.setLayoutData(fd_folderLabel);
		folderLabel.setText("Source folder:");

		outputTextBox = new Text(shell, SWT.BORDER);
		outputTextBox.setTouchEnabled(true);
		FormData fd_outputTextBox = new FormData();
		fd_outputTextBox.left = new FormAttachment(folderTextBox, 0, SWT.LEFT);
		fd_outputTextBox.right = new FormAttachment(folderTextBox, 0, SWT.RIGHT);
		outputTextBox.setLayoutData(fd_outputTextBox);

		final FileDialog outputDialog = new FileDialog(shell, SWT.SAVE);
		String[] filter = { "*.mp4" };
		outputDialog.setFilterNames(filter);
		outputDialog.setOverwrite(true);

		outputButton = new Button(shell, SWT.NONE);
		fd_outputTextBox.top = new FormAttachment(outputButton, 2, SWT.TOP);
		outputButton.setText("Browse");
		FormData fd_outputButton = new FormData();
		fd_outputButton.top = new FormAttachment(folderTextBox, 6);
		fd_outputButton.right = new FormAttachment(100, -10);
		outputButton.setLayoutData(fd_outputButton);
		outputButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (!folderTextBox.getText().equals("")) {
					outputDialog.setFilterPath(folderTextBox.getText());
				}
				String result = outputDialog.open();
				outputTextBox.setText(result);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		outputLabel = new Label(shell, SWT.NONE);
		outputLabel.setText("Output file:");
		FormData fd_outputLabel = new FormData();
		fd_outputLabel.top = new FormAttachment(outputTextBox, 3, SWT.TOP);
		fd_outputLabel.left = new FormAttachment(folderLabel, 0, SWT.LEFT);
		outputLabel.setLayoutData(fd_outputLabel);

		progressBar = new ProgressBar(shell, SWT.SMOOTH);
		FormData fd_progressBar = new FormData();
		fd_progressBar.right = new FormAttachment(outputButton, 0, SWT.RIGHT);
		fd_progressBar.bottom = new FormAttachment(100, -10);
		fd_progressBar.left = new FormAttachment(0, 10);
		progressBar.setLayoutData(fd_progressBar);

		final List fileListView = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		FormData fd_fileListView = new FormData();
		fd_fileListView.top = new FormAttachment(outputButton, 6);
		fd_fileListView.right = new FormAttachment(outputButton, 0, SWT.RIGHT);
		fd_fileListView.left = new FormAttachment(0, 10);
		fileListView.setLayoutData(fd_fileListView);

		final DirectoryDialog folderDialog = new DirectoryDialog(shell, SWT.OPEN);
		folderDialog.setMessage("Choose a video source folder");

		Button folderButton = new Button(shell, SWT.NONE);
		fd_folderTextBox.right = new FormAttachment(folderButton, -6);
		FormData fd_folderButton = new FormData();
		fd_folderButton.top = new FormAttachment(0, 5);
		fd_folderButton.right = new FormAttachment(100, -10);
		folderButton.setLayoutData(fd_folderButton);
		folderButton.setText("Browse");
		folderButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				String result = folderDialog.open();
				folderTextBox.setText(result);

				File folder = new File(result);
				fileListView.setItems(folder.list());
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Button concatVideosButton = new Button(shell, SWT.NONE);
		fd_fileListView.bottom = new FormAttachment(concatVideosButton, -6);
		FormData fd_concatVideosButton = new FormData();
		fd_concatVideosButton.bottom = new FormAttachment(100, -33);
		fd_concatVideosButton.left = new FormAttachment(0, 10);
		concatVideosButton.setLayoutData(fd_concatVideosButton);
		concatVideosButton.setText("Concatenate Videos");
		concatVideosButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (!outputTextBox.getText().equals("") && !folderTextBox.getText().equals("")) {
					File outputFile = new File(outputTextBox.getText());

					if (!outputFile.exists()) {
						int size = fileListView.getItems().length;
						File[] fileList = new File[size];
						for (int i = 0; i < size; i++) {
							fileList[i] = new File(new File(folderTextBox.getText()).getAbsolutePath() + "\\" + fileListView.getItems()[i]);
							System.out.println(fileList[i]);
						}

						System.out.println("concatenating");
						Thread concatenateThread = new Thread(new ProcessingThread(App.this, fileList, outputFile.getAbsolutePath()));
						concatenateThread.start();
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public static void main(String[] args) {
		try {
			App window = new App();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void onProgressChanged(double progress) {
		final Double finalProg = new Double(progress);
		display.asyncExec(new Runnable() {
			public void run() {
				int percent = (int) (finalProg.doubleValue() * 100);
				progressBar.setSelection(percent);
				System.out.println(finalProg.doubleValue());
			}
		});
	}
}
