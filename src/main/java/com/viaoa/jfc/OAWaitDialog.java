/*  Copyright 1999 Vince Via vvia@viaoa.com
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.viaoa.jfc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.viaoa.hub.Hub;
import com.viaoa.jfc.console.Console;
import com.viaoa.object.OAThreadLocalDelegate;
import com.viaoa.process.OAProcess;
import com.viaoa.util.OAString;


public class OAWaitDialog extends JDialog implements ActionListener {

	private JLabel lblStatus;
    private JLabel lblProcess;
    private JButton cmdCancel;
    private JButton cmdBackground;
    private JProgressBar progressBar;
    private boolean bUsesProcess;
    private boolean bAllowRunInBackground;
    private Window parent;
    private OAConsole console;

    private boolean bRunningInBackground;
    private boolean bCancelled;
    private boolean bDone;
    private volatile OAProcess process;
    
    public OAWaitDialog(Window parent) {
        this(parent, true);
    }    
    public OAWaitDialog(Window parent, boolean bAllowBackground) {
        this(parent, bAllowBackground, false);
    }
    public OAWaitDialog(Window parent, boolean bAllowBackground, boolean bUsesProcess) {
    	super(parent, "", ModalityType.APPLICATION_MODAL);
    	this.parent = parent;
    	this.bAllowRunInBackground = bAllowBackground;
        this.bUsesProcess = bUsesProcess;
        this.setResizable(false);
        
        // create window without decoration
        setUndecorated(true);
        // use the root pan decoration
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        if (bAllowBackground) {
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    getRunInBackgroundButton().doClick();
                }
            });
        }
        getContentPane().setLayout(new BorderLayout(2,2));
        getContentPane().add(getPanel(), BorderLayout.NORTH);
    }    
    
    
    public void clearProcess() {
        this.process = null;
        refreshProcess();
    }
    public OAProcess getProcess() {
        if (process != null) return process;
        process = new OAProcess() {
            @Override
            public void run() {
            }  
            @Override
            public void setSteps(String... steps) {
                super.setSteps(steps);
                refreshProcess();
            }
            @Override
            public void setCurrentStep(int x) {
                super.setCurrentStep(x);
                refreshProcess();
            }
            @Override
            public void setName(String s) {
                super.setName(s);
                refreshProcess();
            }
            @Override
            public void setDescription(String s) {
                super.setDescription(s);
                refreshProcess();
            }

            @Override
            public void setAllowCancel(boolean b) {
                super.setAllowCancel(b);
                refreshProcess();
            }
            
            @Override
            public void requestCancel(String reason) {
                super.requestCancel(reason);
                refreshProcess();
            }
            
            @Override
            public void setWasCancelled(boolean b) {
                super.setWasCancelled(b);
                OAWaitDialog.this.bCancelled = b;
                refreshProcess();
            }
            
            @Override
            public void setDone() {
                super.setDone();
                refreshProcess();
            }
        };
        refreshProcess();
        return process;
    }
    
    
    public void setStatus(String msg) {
        getStatusLabel().setText(msg);
    }
    
	public JLabel getStatusLabel() {
		if (lblStatus == null) {
			lblStatus = new JLabel("   Please wait ... processing request ....   ");
			lblStatus.setHorizontalAlignment(JLabel.CENTER);
		}
		return lblStatus;
	}

    public JLabel getProcessLabel() {
        if (lblProcess == null) {
            lblProcess = new JLabel("");
            lblProcess.setHorizontalAlignment(JLabel.CENTER);
        }
        return lblProcess;
    }
	
    public JProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 20);
        }
        return progressBar;
    }
	
    public JButton getRunInBackgroundButton() {
    	if (cmdBackground == null) {
    	    cmdBackground = new JButton("Background");
            cmdBackground.setActionCommand("background");
    	    cmdBackground.addActionListener(this);
    	}
    	return cmdBackground;
    }

    public JButton getCancelButton() {
        if (cmdCancel == null) {
            cmdCancel = new JButton("Cancel") {
                @Override
                public void setEnabled(boolean b) {
                    super.setEnabled(b);
                }  
            };
            cmdCancel.registerKeyboardAction(this, "cancel", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
            cmdCancel.setActionCommand("cancel");
            cmdCancel.addActionListener(this);
        }
        return cmdCancel;
    }
    
    protected JPanel getPanel() {
    	JPanel panel = new JPanel(new BorderLayout(2,2));

        Border border = new EmptyBorder(5,5,5,5);
        panel.setBorder(border);

        JPanel panel2 = new JPanel(new BorderLayout(2,2));
        
        JPanel pan2 = new JPanel(new BorderLayout(2,2));
        border = new EmptyBorder(25,5,5,5);
        pan2.setBorder(border);
        pan2.add(getStatusLabel(), BorderLayout.CENTER);
        panel2.add(pan2, BorderLayout.NORTH);
        
        
        pan2 = new JPanel(new BorderLayout(2,2));
        border = new EmptyBorder(5,5,25,5);
        pan2.setBorder(border);
        pan2.add(getProcessLabel(), BorderLayout.CENTER);
        panel2.add(pan2, BorderLayout.SOUTH);

        panel.add(panel2, BorderLayout.NORTH);
        
        if (bAllowRunInBackground || bUsesProcess) {
            if (bAllowRunInBackground && bUsesProcess) {
                pan2 = new JPanel(new GridLayout(1, 2, 14, 1));
            }
            else {
                pan2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            }
            border = new EmptyBorder(1,1,15,1);
            pan2.setBorder(border);
            if (bAllowRunInBackground) {
                pan2.add(getRunInBackgroundButton());
            }
            if (bUsesProcess) {
                pan2.add(getCancelButton());
            }
            
            JPanel pan3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            pan3.add(pan2);
            
            panel.add(pan3, BorderLayout.CENTER);
        }
        
        JPanel pan = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(1, 3, 1, 3);
        gc.anchor = gc.CENTER;
        
        gc.fill = gc.NONE;
        gc.weightx = .25f;
        pan.add(new JLabel(""), gc);
        
        gc.fill = gc.HORIZONTAL;
        gc.weightx = .50f;
        pan.add(getProgressBar(), gc);
        
        gc.gridwidth = gc.REMAINDER;
        gc.fill = gc.NONE;
        gc.weightx = .25f;
        pan.add(new JLabel(""), gc);

        panel.add(pan, BorderLayout.SOUTH);
        return panel;
    }
    
    public boolean getAllowRunInBackground() {
        return bAllowRunInBackground;
    }
    public boolean getUsesProcess() {
        return bUsesProcess;
    }
    
    public boolean wasCancelled() {
        return bCancelled;
    }
    public boolean getCancelled() {
        return bCancelled;
    }
    
    public boolean getRunningInBackground() {
        return bRunningInBackground;
    }
    public boolean isRunningInBackground() {
        return bRunningInBackground;
    }

    
    
    @Override
    public void setVisible(boolean b) {
        setVisible(b, b);
    }
    private boolean bPack;
    public void setVisible(boolean b, boolean bShowProcessing) {
        if (b) {
            bDone = false;
            if (!bPack) {
                bPack = true;
                pack();
                this.setLocationRelativeTo(parent);
            }
            bRunningInBackground = false;
            bCancelled = false;

            if (getAllowRunInBackground() && cmdCancel != null) {
                cmdCancel.setEnabled(true);
            }
        }
        setCursor(Cursor.getPredefinedCursor(bShowProcessing?Cursor.WAIT_CURSOR:Cursor.DEFAULT_CURSOR));
        getProgressBar().setIndeterminate(bShowProcessing);
        refreshProcess();
        try {
            super.setVisible(b);  // this will put it in blocking mode
        }
        catch (Exception e) {
            // no-op
        }
    }
    
    public void actionPerformed(ActionEvent e) {
    	if (e == null) return;
        String cmd = e.getActionCommand();
    	if (cmd == null) return;
        if (cmd.equalsIgnoreCase("background")) {
            bRunningInBackground = true;
            setVisible(false);
            onRunInBackground();
        }
        if (cmd.equalsIgnoreCase("cancel")) {
            if (process != null) {
                process.requestCancel("Cancelled by user");
            }
            else bCancelled = true;
            onCancel();
        }
    }
    
    protected void onRunInBackground() {
    }
    protected void onCancel() {
    }
    
    
    private JSplitPane splitPane;
    private JScrollPane scrollPane;
    
    public void setConsole(OAConsole con) {
        if (console == con) return;

        if (scrollPane != null) {
            scrollPane.remove(console);
            if (splitPane == null) getContentPane().remove(scrollPane);
        }
        if (splitPane != null) {
            if (console != null) splitPane.remove(scrollPane);
            if (compDisplay != null) splitPane.remove(compDisplay);
            getContentPane().remove(splitPane);
        }
        scrollPane = null;
        splitPane = null;
        
        this.console = con;
        
        if (console != null) {
            this.scrollPane = new JScrollPane(con);
            scrollPane.setBorder(new TitledBorder("Console"));

            if (compDisplay != null) {
                this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, compDisplay, scrollPane);
                getContentPane().add(splitPane, BorderLayout.CENTER);
            }
            else {
                getContentPane().add(scrollPane, BorderLayout.CENTER);
            }
            setResizable(true);
        }
    }
    public OAConsole getConsole() {
        return this.console;
    }

    private JComponent compDisplay;
    
    public void setDisplayComponent(JComponent pan) {
        if (this.compDisplay == pan) return;
        
        if (scrollPane != null) {
            scrollPane.remove(console);
            if (splitPane == null) getContentPane().remove(scrollPane);
        }
        if (splitPane != null) {
            if (console != null) splitPane.remove(scrollPane);
            if (compDisplay != null) splitPane.remove(compDisplay);
            getContentPane().remove(splitPane);
        }
        scrollPane = null;
        splitPane = null;
       
        
        
        this.compDisplay = pan;
        if (pan != null) {
            if (console != null) {
                this.scrollPane = new JScrollPane(console);
                this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, compDisplay, this.scrollPane);
                getContentPane().add(splitPane, BorderLayout.CENTER);
            }
            else {
                getContentPane().add(compDisplay, BorderLayout.CENTER);
            }
            setResizable(true);
        }
    }
    public JComponent getDisplayComponent() {
        return compDisplay;
    }
    
    
    public void doneRunning() {
        bDone = true;
        if (cmdCancel != null) {
            cmdCancel.setEnabled(false);
        }
    }
    
    @Override
    public void setCursor(Cursor cursor) {
        if (bDone && cursor.equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))) cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        super.setCursor(cursor);
    }
    
    public void refreshProcess() {
        String msg;
        OAProcess p = this.process;
        if (p != null) {
            boolean b = p.getAllowCancel();
            b = b && !p.getDone() && !p.getWasCancelled() && !p.getRequestedToCancel();
            getCancelButton().setEnabled(b);
            
            if (p.getDone() || p.getWasCancelled()) {
                getProgressBar().setIndeterminate(false);
                getProgressBar().setMaximum(100);
                getProgressBar().setValue(100);
            }
            
            if (p.getWasCancelled()) {
                msg = "Process was cancelled";
            }
            else if (p.getDone()) {
                msg = "Process completed";
                if (p.getRequestedToCancel()) {
                    msg += " (request to cancel was ignored)";
                }
            }
            else if (p.getRequestedToCancel()) {
                msg = "Process requested to be cancelled";
            }
            else {
                msg = "Process is running";
            }
            int x = p.getCurrentStep();
            String[] ss = p.getSteps();
            if (ss != null && ss.length > 0 && x < ss.length) {
                msg += String.format(" - step %d of %d - %s",  x+1, ss.length, ss[x]);
            }
        }
        else msg = "";
        getProcessLabel().setText(msg);
    }
    
    public static void main(String[] args) {
        final OAWaitDialog dlg = new OAWaitDialog(null, true, true);
        dlg.setTitle("Wait for me");
        dlg.getStatusLabel().setText("this is a wait dialog");

        Hub<Console> h = new Hub(Console.class);
        final Console updateObject = new Console();
        
        updateObject.setText("");
        h.add(updateObject);
        h.setAO(updateObject);
        
        OAConsole oac = new OAConsole(h, "text", 45);
        dlg.setConsole(oac);
        
        Thread t = new Thread() {
            @Override
            public void run() {
                OAThreadLocalDelegate.setProcess(dlg.getProcess());
                dlg.refreshProcess();
                dlg.getProcess().setAllowCancel(true);
                dlg.getProcess().setSteps("step 1", "step 2", "step 3");
                int currentStep = 0;
                try {
                    int iRequestToCancel = -1;
                    for (int i=0; i<80;i++) {
                        if (iRequestToCancel < 0 && dlg.process.getRequestedToCancel()) {
                            if (iRequestToCancel < 0) iRequestToCancel = i;
                            updateObject.setText("Command was requested to cancel by user");
                        }
                        if (iRequestToCancel > 0 && i == iRequestToCancel + 10) {
                            dlg.process.setWasCancelled(true);
                        }
                        if (dlg.process.getWasCancelled()) {
                            updateObject.setText("Command was cancelled by process");
                            break;
                        }

                        if (i % 10 == 0 && currentStep < dlg.getProcess().getTotalSteps()) {
                            dlg.getProcess().setCurrentStep(currentStep++); 
                        }
                        updateObject.setText(i+" "+OAString.getRandomString(5, 75, true, true, true));
                        Thread.sleep(350);
                    }
                    dlg.getProcess().setDone();
                    if (!dlg.getProcess().getWasCancelled()) {
                        dlg.getProcess().setDoneMessage("All done");
                        updateObject.setText("done");
                    }
                    dlg.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
            }
        };
        t.start();
        
        dlg.setVisible(true);
        System.exit(0);
    }
}


