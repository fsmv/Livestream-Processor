package net.sapium.livestreamprocessor;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
import org.eclipse.swt.widgets.TaskBar;
import org.eclipse.swt.widgets.TaskItem;
import org.eclipse.swt.widgets.Text;

public class App implements ProgressChangedListener {
    protected Shell shell;
    private Text folderTextBox;
    private Text outputTextBox;
    private Button outputButton;
    private Label outputLabel;
    private ProgressBar progressBar;
    private Display display;
    private TaskItem taskBarItem;
    private Label statusLabel;

    public static void main(String[] args) {
        try {
            App window = new App();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open() {
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }

    protected void createContents() {
        display = new Display();
        shell = new Shell(display);
        shell.setSize(450, 300);
        shell.setText("Livestream Processor");
        shell.setLayout(new FormLayout());
        
        taskBarItem = getTaskBarItem();
        
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
                if (result != null) {
                    outputTextBox.setText(result);
                }
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
        progressBar.setMaximum(1000);
        FormData fd_progressBar = new FormData();
        fd_progressBar.right = new FormAttachment(outputButton, 0, SWT.RIGHT);
        fd_progressBar.bottom = new FormAttachment(100, -10);
        fd_progressBar.left = new FormAttachment(0, 10);
        progressBar.setLayoutData(fd_progressBar);

        final List fileListView = new List(shell, SWT.BORDER | SWT.V_SCROLL);
        fileListView.setItems(new String[] {});
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
                if (result != null) {
                    folderTextBox.setText(result);

                    File folder = new File(result);
                    fileListView.setItems(folder.list());
                }
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
                        if (size > 0) {
                            File[] fileList = new File[size];
                            for (int i = 0; i < size; i++) {
                                fileList[i] = new File(new File(folderTextBox.getText()).getAbsolutePath() + "\\" + fileListView.getItems()[i]);
                            }

                            Thread concatenateThread = new Thread(new ProcessingThread(App.this, fileList, outputFile.getAbsolutePath()));
                            concatenateThread.start();
                        }
                    }
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        
        statusLabel = new Label(shell, SWT.NONE);
        statusLabel.setAlignment(SWT.RIGHT);
        FormData fd_statusLabel = new FormData();
        fd_statusLabel.left = new FormAttachment(outputButton, -28);
        fd_statusLabel.top = new FormAttachment(progressBar, -26, SWT.TOP);
        fd_statusLabel.right = new FormAttachment(outputButton, 0, SWT.RIGHT);
        fd_statusLabel.bottom = new FormAttachment(progressBar, -11);
        statusLabel.setLayoutData(fd_statusLabel);
    }

    /**
     * For windows task bar progress
     */
    public TaskItem getTaskBarItem() {
        TaskBar bar = display.getSystemTaskBar();
        if (bar == null)
            return null;
        TaskItem item = bar.getItem(shell);
        if (item == null)
            item = bar.getItem(null);
        return item;
    }
    
    public void onProgressChanged(double progress) {
        final Double finalProg = new Double(progress);
        display.asyncExec(new Runnable() {
            public void run() {
                if(taskBarItem != null){
                    if(taskBarItem.getProgressState() == SWT.DEFAULT){
                        taskBarItem.setProgressState(SWT.NORMAL);
                    }else if(taskBarItem.getProgressState() == SWT.NORMAL){
                        taskBarItem.setProgress((int) (finalProg.doubleValue() * 100));
                        if(finalProg.doubleValue() == 1){
                            taskBarItem.setProgressState(SWT.DEFAULT);
                        }
                    }
                }
                int percent = (int) (finalProg.doubleValue() * 1000);
                statusLabel.setText((percent/10) + "%");
                if(percent < 10){
                    percent = 10;
                }else if(percent == 1000){
                    percent = 0;
                    statusLabel.setText("Done");
                }
                progressBar.setSelection(percent);
            }
        });
    }
}
