package com.nks.whatsapp.marketing.app;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.nks.whatsapp.Group;
import com.nks.whatsapp.Member;
import com.nks.whatsapp.WhatsApp;

public class ListPanel extends JScrollPane implements PanelUnitListener{
	private static final long serialVersionUID = 2647735107932557683L;
	private JPanel mainPanel=new JPanel();
	private List<ListPanelListener> listeners=new ArrayList<ListPanelListener>();
	private int type;
	private ArrayList<PanelUnit> panelsList=new ArrayList<PanelUnit>();
	
	
	public ListPanel(int type)
	{
		super();
		this.type=type;
		jbInit();
	}
	
	private void jbInit()
	{
		setViewportView(mainPanel);
	}

	@Override
	public void setBackground(Color color)
	{
	super.setBackground(color);
	if(mainPanel!=null)
	{
	mainPanel.setBackground(color);
	for(Component comp:mainPanel.getComponents())
		((PanelUnit)comp).setBackground(color);
	}
	}

	public void setMembers(WhatsApp whatsApp,Collection<Member> members)
	{
	mainPanel.removeAll();
	int size=this.panelsList.size();
	for(int i=size-1;i>=0;i--)
		panelsList.remove(i);
	mainPanel.setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
	for(Member member:members)
	addMember(whatsApp,member,false);	
	mainPanel.validate();
	}

	public void addMember(WhatsApp whatsApp,Member member,boolean validate)
	{
	PanelUnit unit=new PanelUnit(type);
	unit.updateMember(whatsApp, member);
	panelsList.add(unit);
	unit.setBackground(super.getBackground());
	mainPanel.add(unit);
	unit.addPanelUnitListener(this);
	for(ListPanelListener listener:listeners)
		listener.panelUnitAdded(unit);
	if(validate)
		mainPanel.validate();
	}

	
		
	@Override
	public void detailPressed(PanelUnit source) {
	for(ListPanelListener listener:listeners)
			listener.panelDetailPressed(source);
	}

	@Override
	public void imagePressed(PanelUnit source) {
		for(ListPanelListener listener:listeners)
			listener.panelImagePressed(source);
	}

	public void addListPanelListener(ListPanelListener listener)
	{
	this.listeners.add(listener);
	}

	public void search(String text)
	{
		mainPanel.removeAll();
		List<PanelUnit> searched=getPanelsListForSearch(text);
		for(PanelUnit unit:searched)
			this.mainPanel.add(unit);
		mainPanel.revalidate();
		mainPanel.repaint();
		
		
	}
	
	public void filter(boolean showWhatsApp,boolean showNonWhatsApp,boolean showContacts,boolean showGroups)
	{
		mainPanel.removeAll();
		List<PanelUnit> searched=getPanelsListForFilters(showWhatsApp,showNonWhatsApp,showContacts,showGroups);
		for(PanelUnit unit:searched)
			this.mainPanel.add(unit);
		mainPanel.revalidate();
		mainPanel.repaint();
	}
	
	public void clearSearch()
		{
		this.removeAll();
		for(PanelUnit unit:this.panelsList)
			this.mainPanel.add(unit);
		mainPanel.revalidate();
		repaint();
		}


	private List<PanelUnit> getPanelsListForSearch(String searchText)
		{
		List<PanelUnit> list=new ArrayList<PanelUnit>();
		for(PanelUnit unit:this.panelsList)
			if(unit.qualifyForSearch(searchText))
				list.add(unit);
		return list;
		}

	private List<PanelUnit> getPanelsListForFilters(boolean showWhatsApp,boolean showNonWhatsApp,boolean showContacts,boolean showGroups)
	{
		List<PanelUnit> list=new ArrayList<PanelUnit>();
		for(PanelUnit unit:this.panelsList)
		{
			Member member=unit.getMember();
			if(member instanceof Group && showGroups)
				{
				if(member.isWhatsApp() && showWhatsApp)
					list.add(unit);
				if(!member.isWhatsApp() && showNonWhatsApp)
					list.add(unit);
				}
			else if(showContacts)
				{
				if(member.isWhatsApp() && showWhatsApp)
					list.add(unit);
				if(!member.isWhatsApp() && showNonWhatsApp)
					list.add(unit);
				}
		}
		return list;
		}

	@Override
	public void backPressed(PanelUnit source) {
	}

	@Override
	public void attachmentPressed(PanelUnit source) {
	}

	@Override
	public void menuPressed(PanelUnit source) {
		// TODO Auto-generated method stub
		
	}

	
	
}

