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

package net.sapium.livestreamprocessor.gui;

import java.util.ArrayList;

import net.sapium.livestreamprocessor.utils.ProgressChangedListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TaskBar;
import org.eclipse.swt.widgets.TaskItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindow implements ProgressChangedListener {
    protected Shell shell;

    private ProgressBar progressBar;
    private Display display;
    private TaskItem taskBarItem;
    private Label statusLabel;

    private long taskStartTime;
    private ArrayList<Long> remainingTimeList;
    private int remainingTimeIndex;
    private final int remainingTimeHistory = 100;
    private TabFolder tabFolder;
    private TabContent concatTab;
    private TabContent timelapseTab;
    private static Button startButton;
    private static Button cancelButton;
    private static Logger logger;
    
    public static final String CONCAT_NAME = "Concatenate";
    public static final String TIMELAPSE_NAME = "Timelapse";
    public static final String DOWNLOADER_NAME = "Twitch.tv Downloader";
    
    private static final String icon16 = "/icon16.png";
    private static final String icon24 = "/icon24.png";
    private static final String icon32 = "/icon32.png";
    private static final String icon48 = "/icon48.png";
    private static final String icon64 = "/icon64.png";
    private static final String icon128 = "/icon128.png";
    private static final String icon256 = "/icon256.png";
    private static final String icon512 = "/icon512.png";
    
    public static void main(String[] args) {
        logger = LoggerFactory.getLogger(MainWindow.class);
        try {
            MainWindow window = new MainWindow();
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

        // TODO: tell threads to stop and delete half-finished files

        display.dispose();
    }

    protected void createContents() {
        display = new Display();
        shell = new Shell(display);
        shell.setSize(450, 400);
        shell.setText("Livestream Processor");
        Image[] images = new Image[] { 
                new Image(display, MainWindow.class.getResourceAsStream(icon16)),
                new Image(display, MainWindow.class.getResourceAsStream(icon24)), 
                new Image(display, MainWindow.class.getResourceAsStream(icon32)), 
                new Image(display, MainWindow.class.getResourceAsStream(icon48)), 
                new Image(display, MainWindow.class.getResourceAsStream(icon64)), 
                new Image(display, MainWindow.class.getResourceAsStream(icon128)), 
                new Image(display, MainWindow.class.getResourceAsStream(icon256)), 
                new Image(display, MainWindow.class.getResourceAsStream(icon512))};
        shell.setImages(images);
        shell.setLayout(new FormLayout());

        taskBarItem = getTaskBarItem();

        progressBar = new ProgressBar(shell, SWT.SMOOTH);
        progressBar.setMaximum(1000);
        FormData fd_progressBar = new FormData();
        fd_progressBar.left = new FormAttachment(0, 10);
        fd_progressBar.right = new FormAttachment(100, -10);
        fd_progressBar.bottom = new FormAttachment(100, -10);
        progressBar.setLayoutData(fd_progressBar);
        
        tabFolder = new TabFolder(shell, SWT.NONE);
        FormData fd_tabFolder = new FormData();
        fd_tabFolder.top = new FormAttachment(0, 10);
        fd_tabFolder.left = new FormAttachment(0, 10);
        fd_tabFolder.right = new FormAttachment(100, -10);
        tabFolder.setLayoutData(fd_tabFolder);
   
        startButton = new Button(shell, SWT.FLAT);
        
        TabItem tbtmConcatenate = new TabItem(tabFolder, SWT.NONE);
        tbtmConcatenate.setText(CONCAT_NAME);

        concatTab = new ConcatenateTab(tabFolder, SWT.NONE);
        tbtmConcatenate.setControl(concatTab);
        
        TabItem tbtmTimelapse = new TabItem(tabFolder, SWT.NONE);
        tbtmTimelapse.setText(TIMELAPSE_NAME);
        
        timelapseTab = new TimelapseTab(tabFolder, SWT.NONE);
        tbtmTimelapse.setControl(timelapseTab);
        
        fd_tabFolder.bottom = new FormAttachment(startButton, -2);
        startButton.setText("Start");
        FormData fd_startButton = new FormData();
        fd_startButton.bottom = new FormAttachment(progressBar, -2);
        fd_startButton.left = new FormAttachment(0, 10);
        startButton.setLayoutData(fd_startButton);
        startButton.addSelectionListener(new SelectionListener(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                getActiveTab().start(MainWindow.this);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        statusLabel = new Label(shell, SWT.NONE);
        statusLabel.setAlignment(SWT.RIGHT);
        FormData fd_statusLabel = new FormData();
        fd_statusLabel.right = new FormAttachment(100, -10);
        fd_statusLabel.top = new FormAttachment(startButton, 5, SWT.TOP);
        statusLabel.setLayoutData(fd_statusLabel);
        
        cancelButton = new Button(shell, SWT.NONE);
        fd_statusLabel.left = new FormAttachment(cancelButton, 6);
        FormData fd_cancelButton = new FormData();
        fd_cancelButton.top = new FormAttachment(startButton, 0, SWT.TOP);
        fd_cancelButton.left = new FormAttachment(startButton, 6);
        cancelButton.setLayoutData(fd_cancelButton);
        cancelButton.setText("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getActiveTab().cancel();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }
    
    public TabContent getActiveTab() {
        TabItem[] selectedTabs = tabFolder.getSelection();
        if(selectedTabs.length > 0){
            switch(selectedTabs[0].getText()){
            case CONCAT_NAME:
                return concatTab;
            case TIMELAPSE_NAME:
                return timelapseTab;
            case DOWNLOADER_NAME:
                //TODO: return downloader tab
                break;
            default:
                MainWindow.logger.warn("Tab name not found: " + selectedTabs[0].getText());
            }
        }
        
        return null;
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
    
    public static Button getStartButton() {
        return startButton;
    }
    
    public static Button getCancelButton() {
        return cancelButton;
    }

    public void onProgressChanged(double progress) {
        final Double finalProg = new Double(progress);
        final long elapsedTime = System.nanoTime() - getTaskStartTime();
        if (elapsedTime > 60000000) { // Only update every 1/60 seconds (the status label was flickering)
            display.asyncExec(new Runnable() {
                public void run() {
                    if (taskBarItem != null) {
                        if (taskBarItem.getProgressState() == SWT.NORMAL) {
                            taskBarItem.setProgress((int) (finalProg.doubleValue() * 100));
                        }
                    }

                    int percent = (int) (finalProg.doubleValue() * 1000);

                    long remainingTime = (long) (elapsedTime / finalProg.doubleValue()) - elapsedTime;
                    if (remainingTimeList.size() > remainingTimeIndex) {
                        remainingTimeList.set(remainingTimeIndex, remainingTime);
                    } else {
                        remainingTimeList.add(remainingTime);
                    }

                    remainingTimeIndex++;
                    if (remainingTimeIndex >= remainingTimeHistory) {
                        remainingTimeIndex = 0;
                    }

                    long remainingTimeAve = 0;
                    for (int i = 0; i < remainingTimeList.size(); i++) {
                        remainingTimeAve += remainingTimeList.get(i);
                    }

                    remainingTimeAve /= remainingTimeList.size();

                    statusLabel.setText((percent / 10) + "% " + MainWindow.nanoTimeToString(elapsedTime) + " remaining: " + MainWindow.nanoTimeToString(remainingTimeAve));
                    if (percent < 10) {
                        percent = 10;
                    }
                    progressBar.setSelection(percent);
                }
            });
        }
    }

    @Override
    public void onTaskStarted() {
        setTaskStartTime(System.nanoTime());
        remainingTimeList = new ArrayList<Long>(remainingTimeHistory);
        remainingTimeIndex = 0;
        
        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                MainWindow.getStartButton().setEnabled(false);
                MainWindow.getCancelButton().setEnabled(true);
                
                if (taskBarItem != null) {
                    taskBarItem.setProgressState(SWT.NORMAL);
                }
            }
        });
    }

    @Override
    public void onTaskEnded() {
        setTaskStartTime(0);
        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                if (taskBarItem != null) {
                    taskBarItem.setProgressState(SWT.DEFAULT);
                    taskBarItem.setProgress(0);
                }
                progressBar.setSelection(0);
                statusLabel.setText("Done");
                
                MainWindow.getStartButton().setEnabled(true);
                MainWindow.getCancelButton().setEnabled(false);
            }
        });
    }

    private synchronized long getTaskStartTime() {
        return taskStartTime;
    }

    private synchronized void setTaskStartTime(long time) {
        taskStartTime = time;
    }

    public static String nanoTimeToString(long time) {
        String result = "";

        int h = (int) (((time) / 1000000000 / 60 / 60) % 24);
        int m = (int) (((time) / 1000000000 / 60) % 60);
        int s = (int) ((time / 1000000000) % 60);

        if (h < 10) {
            result += "0" + h;
        } else {
            result += h;
        }

        result += ":";

        if (m < 10) {
            result += "0" + m;
        } else {
            result += m;
        }

        result += ":";

        if (s < 10) {
            result += "0" + s;
        } else {
            result += s;
        }

        return result;
    }
}
