package com.viaoa.jfc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.viaoa.hub.Hub;
import com.viaoa.object.OAFinder;
import com.viaoa.object.OALinkInfo;
import com.viaoa.object.OAObject;
import com.viaoa.object.OAObjectCacheDelegate;
import com.viaoa.object.OAObjectReflectDelegate;
import com.viaoa.object.OAObjectSchedulerDelegate;
import com.viaoa.scheduler.OAScheduler;
import com.viaoa.scheduler.OASchedulerController;
import com.viaoa.scheduler.OASchedulerPlan;
import com.viaoa.util.OADate;
import com.viaoa.util.OADateTime;
import com.viaoa.util.OAPropertyPath;
import com.viaoa.util.OATime;

/**
 * Used to update the scheduled datetimes of an active object.
 * Uses a OAMonthCacheCalendar to select an available datetime slot, and then update the date/time object properties.
 * 
 * @author vvia
 *
 * @param <F>
 */
public class OASchedulerPanel<F extends OAObject> extends JPanel {

    private OASchedulerController control;
    
    private String ppDisplay;

    // used to display UI calendar panel
    private OAMonthCacheCalendar<F> monthCalendar;
    
    
    /**
     * Create a new scheduler panel, that allows user to find an available datetime slot to schedule the active object in hubFrom.
     * @param hubFrom object that is being scheduled.
     * @param ppSchedule property path to the schedule reference (OAOne) link object. 
     * @param ppDisplay property from schedule to use for display/renderer. 
     * @param ppDateFrom property from schedule object
     * @param ppTimeFrom property from schedule object
     * @param ppDateTo property from schedule object
     * @param ppTimeTo property from schedule object
     */
    public OASchedulerPanel(OASchedulerController control, String ppDisplay) {
        this.control = control;
        this.ppDisplay = ppDisplay;
        setup();
    }

    protected void setup() {
        if (control == null) return;
        
        monthCalendar = new OAMonthCacheCalendar(control.getDetailHub(), ppDisplay, control.getFromDateProperty()) {
            private JButton cmdSelect;

            @Override
            protected void onSelected(OADateTime dt) {
                OASchedulerPanel.this.control.set(dt, dt);;
                OASchedulerPanel.this.onSelected(dt);
            }
            @Override
            public OAScheduler getScheduler(OADate date) {
                return OASchedulerPanel.this.control.getSchedulerCallback(date);
            }
            @Override
            protected JPanel getSelectDatePanel() {
                JPanel panx = super.getSelectDatePanel();
                
                //url = OAButton.class.getResource("icons/goto.gif");
                //Icon icon = new ImageIcon(url);
                //JButton cmdSelect = new JButton("Select", icon);
                cmdSelect = new JButton("Select");
                cmdSelect.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        onSelected(lastSelectedDate);
                    }
                });
                OAButton.setup(cmdSelect);
                cmdSelect.setFocusPainted(true);
                panx.add(cmdSelect);
                return panx;
            }
            
            @Override
            protected void onDaySelected(OADate date, Hub hub, Hub hubDetail) {
                updateSelectButton(date);
                super.onDaySelected(date, hub, hubDetail);
            }
            protected void updateSelectButton(OADate date) {
                if (cmdSelect == null) return;
                boolean b = false;
                if (date != null) {
                    OAScheduler schr = getScheduler(date);
                    if (schr == null) b = true;
                    else {
                        ArrayList<OASchedulerPlan> al = schr.getSchedulePlans();
                        b = schr.isAvailable(date);
                    }
                }
                cmdSelect.setEnabled(b);
            }
            
        };
        
        this.setLayout(new BorderLayout());
        this.add(monthCalendar);
    }

    protected void onSelected(OADateTime dt) {
    }
    
}
