package com.nks.whatsapp.marketing.app;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ActionableJPanel extends JPanel implements MouseListener,ContainerListener{

	private static final long serialVersionUID = 1783792313909065595L;
	private static long multiClickThreshhold = 2000;
	private final static int PRESSED = 1 << 2;

	private String actionCommand;
	private List<ActionListener> listeners=new ArrayList<ActionListener>();
	private long lastPressedTimestamp = -1;
	private boolean shouldDiscardRelease = false;
	protected int stateMask = 0;

    
	public ActionableJPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout,isDoubleBuffered);
		 addListenersToChilds(this);

	}

    public ActionableJPanel(LayoutManager layout) {
        super(layout);
        addListenersToChilds(this);

    }

    public ActionableJPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        addListenersToChilds(this);

    }

    public ActionableJPanel() {
        super();
        addListenersToChilds(this);

    }


	
    
	
	public String getActionCommand() {
		return actionCommand;
	}

	public void setActionCommand(String actionCommand) {
		this.actionCommand = actionCommand;
	}

	public void addActionListener(ActionListener listener)
	{
		this.listeners.add(listener);
	}
	
	 @Override
	public void add(Component comp, Object constraints) {
		 super.add(comp, constraints);
		 addListenersToChilds(comp);
	}
	
	private void addListenersToChilds(Component comp)
	{
		comp.addMouseListener(this);
		if(comp instanceof Container)
			{
			Container cont=((Container)comp);
			cont.addContainerListener(this);
			Component[] childs=cont.getComponents();
			
			for(Component child:childs)
				addListenersToChilds(child);
			}
	}
	
	private void actionPerformed(int modifiers) {
	for(ActionListener listener:listeners)
		listener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,actionCommand,EventQueue.getMostRecentEventTime(),modifiers));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		long lastTime = lastPressedTimestamp;
         long currentTime = lastPressedTimestamp = e.getWhen();
         if (lastTime != -1 && currentTime - lastTime < multiClickThreshhold) {
             shouldDiscardRelease = true;
             return;
         }
		setPressed(true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
            // Support for multiClickThreshhold
            if (shouldDiscardRelease) {
                shouldDiscardRelease = false;
                return;
            }
            setPressed(false);
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}


	private void setPressed(boolean b)
	{
        if((isPressed() == b)) {
            return;
        }

        if (b) {
            stateMask |= PRESSED;
        } else {
            stateMask &= ~PRESSED;
        }

        if(!isPressed()) {
            int modifiers = 0;
            AWTEvent currentEvent = EventQueue.getCurrentEvent();
            if (currentEvent instanceof InputEvent) {
                modifiers = ((InputEvent)currentEvent).getModifiers();
            } else if (currentEvent instanceof ActionEvent) {
                modifiers = ((ActionEvent)currentEvent).getModifiers();
            }
           actionPerformed(modifiers);
        }		
		
	}
	
	private boolean isPressed() {
        return (stateMask & PRESSED) != 0;
    }

	@Override
	public void componentAdded(ContainerEvent e) {
	addListenersToChilds(e.getComponent());
	}

	@Override
	public void componentRemoved(ContainerEvent e) {

	}
}
