package com.nks.whatsapp.marketing.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.AbstractBorder;

import com.nks.whatsapp.Chat;
import com.nks.whatsapp.Member;
import com.nks.whatsapp.Message;
import com.nks.whatsapp.MessageStatus;
import com.nks.whatsapp.TextMessage;
import com.nks.whatsapp.Util;
import com.nks.whatsapp.WhatsApp;
import com.nks.whatsapp.event.ChatListener;
import com.nks.whatsapp.event.EventUtility;
import com.nks.whatsapp.event.MessageListener;

public class ChatPanel extends JPanel implements PanelUnitListener,ComponentListener,ChatListener{

	private static final long serialVersionUID = 2647735107932557683L;
	private PanelUnit upperPanel;
	private List<MemberListPanelListener> listeners=new ArrayList<MemberListPanelListener>();
	private JLayeredPane mainPanel;
	private ImageComponent backgroundPanel;
	private JPanel chatPanel; 
	private JPanel messagePanel;
	private JScrollPane chatScroll;
	private JPanel chatMain;
	private long listenerId=EventUtility.getUniqueListenerId();
	private WhatsApp whatsApp;
	private Chat lastChat;
	
	public ChatPanel()
	{
		super(new BorderLayout(0,0));
		jbInit();
	}

	private void jbInit(){
	upperPanel=new PanelUnit(PanelUnit.CHAT);
	upperPanel.addPanelUnitListener(this);
	upperPanel.setBackground(Constants.getNormalApplicationBackgroudColor());
	add(upperPanel,BorderLayout.NORTH);
	mainPanel=new JLayeredPane();
	add(mainPanel,BorderLayout.SOUTH);
	
	chatPanel=new JPanel(new BorderLayout(0,0));
	chatPanel.setOpaque(false);
	
	chatScroll=new JScrollPane();
	chatScroll.setBorder(null);
	chatScroll.getViewport().setBackground(new Color(250,250,220,240));
	chatScroll.setOpaque(false);
	
	chatMain=new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP));
	chatMain.setOpaque(false);
	chatScroll.setViewportView(chatMain);
	chatPanel.add(chatScroll,BorderLayout.CENTER);
	messagePanel=new JPanel();
	messagePanel.setBackground(new Color(250,250,220,240));
	messagePanel.add(new ChatMessage(ChatMessage.SEND,null,true));
	chatPanel.add(messagePanel,BorderLayout.SOUTH);
	addComponentListener(this);
	setBackground(Constants.getNormalApplicationBackgroudColor());
	}

public void updateChat(WhatsApp whatsApp,Chat chat) 
{
this.whatsApp=whatsApp;
upperPanel.updateMember(whatsApp,chat.getMember());
removeAllMessages(false);
if(chat!=null)
{	
	List<Message> messages=chat.getMessages();
	for(int i=0;i<messages.size();i++)
	{
		Message message=messages.get(i);
		if(i==0)
		{
			if(message.getStatuses().size()>0)
				addMessage(new TextMessage("0","system",Util.getFriendlyDate(message.getStatuses().get(0).getTime())),false);
			addMessage(message,true,false);
		}
		else
		{
			boolean showComment=!message.getSenderId().equals(messages.get(i-1).getSenderId());
			if(message.getStatuses().size()>0 && messages.get(i-1).getStatuses().size()>0)
				{
				int days=Util.daysBetween(message.getStatuses().get(0).getTime(), messages.get(i-1).getStatuses().get(0).getTime());
				if(days>0)
					{showComment=true;addMessage(new TextMessage("0","system",Util.getFriendlyDate(message.getStatuses().get(0).getTime())),true,false);}
				}
			addMessage(message,showComment,false);
		}	
	}
chatMain.validate();
if(this.lastChat!=null)
	lastChat.removeListener(this);
this.lastChat=chat;
this.lastChat.addChatListener(this);
}


}



public void setBackGround(Color color)
{
	setComponentHirearchyBackgroundColor(upperPanel,color);
}

private static void setComponentHirearchyBackgroundColor(Component comp,Color backGround)
{
if(comp!=null)
	{
	comp.setBackground(backGround);
	if(comp instanceof Container)
		{
		Component childs[]=((Container)comp).getComponents();
		for(Component child:childs)
			setComponentHirearchyBackgroundColor(child,backGround);
		}
	}
}


private void removeAllMessages(boolean validate)
{
	chatMain.removeAll();
	if(validate)
		{chatMain.validate();}
}

public void addMessage(Message message,boolean showComment)
{
	addMessage(message,showComment,true);
}

private void addMessage(Message message,boolean showComment,boolean validate)
{
String uniqueId=message.getSenderId();
ChatMessage messagePanel;
if(whatsApp.getMyContact().getUniqueId().equals(uniqueId))
	messagePanel=new ChatMessage(ChatMessage.MINE,message,showComment);
else if (uniqueId.equals(Member.SYSTEM_MEMBER_ID))
	messagePanel=new ChatMessage(ChatMessage.SYSTEM,message,false);
else
	messagePanel=new ChatMessage(ChatMessage.THEIR,message,showComment);
chatMain.add(messagePanel);
if(validate)
	chatMain.validate();
}

@Override
public void detailPressed(PanelUnit source) {
for(MemberListPanelListener listener:listeners)
	listener.showDetail(this, source.getMember(), false);
}

@Override
public void imagePressed(PanelUnit source) {
	backPressed(source);
}

@Override
public void backPressed(PanelUnit source) {
	for(MemberListPanelListener listener:listeners)
		listener.showChatPanel();
	
}

@Override
public void attachmentPressed(PanelUnit source) {
	// TODO Auto-generated method stub
	
}

public void addChatListPanelListener(MemberListPanelListener listener)
{listeners.add(listener);}

@Override
public void componentResized(ComponentEvent e) {
}

@Override
public void componentMoved(ComponentEvent e) {
}

@Override
public void componentShown(ComponentEvent e) {
	if(backgroundPanel==null)
		{
		int width=this.getSize().width;
		int height=this.getSize().height-upperPanel.getHeight();
		backgroundPanel=new ImageComponent(width, height,false);
		backgroundPanel.updateImage(ImageUtil.getBackgroundImage(width, height));
		backgroundPanel.setBounds(0, 0, width, height);
		chatPanel.setBounds(0, 0, width, height);
		mainPanel.add(backgroundPanel, 0,0);
		mainPanel.add(chatPanel, new Integer(2),0);
		mainPanel.setPreferredSize(new Dimension(width,height));
		mainPanel.validate();
		validate();
		mainPanel.repaint();
		
		}
}

@Override
public void componentHidden(ComponentEvent e) {
}

private static class ChatMessage extends JPanel implements MessageListener
{
private static final long serialVersionUID = 7608228957353889045L;
private static final int MINE=0;
private static final int THEIR=1;
private static final int SYSTEM=2;
private static final int SEND=3;


private JPanel outerMessagePanel;
private JPanel internalMessagePanel;
private int thickness=10;
private ChatMessage(int type,Message message,boolean showComment)
{
jbInit(type,message,showComment);
if(type==MINE)
message.addMessageListener(this);
}



private void jbInit(final int type,Message message,boolean showComment)
{
this.setOpaque(false);	
FlowLayout flowLayout=new FlowLayout();

flowLayout.setVgap(0);
Color internalColor=Constants.getTheirChatBackColor();
int direction=MessageBorder.DIRECTION_NONE;
switch(type)
{
case SYSTEM:{internalColor=Constants.getSystemChatBackColor();flowLayout.setAlignment(FlowLayout.CENTER);break;}
case MINE:{direction=MessageBorder.DIRECTION_RIGHT;internalColor=Constants.getMyChatBackColor();flowLayout.setAlignment(FlowLayout.RIGHT);break;}
case THEIR:{direction=MessageBorder.DIRECTION_LEFT;flowLayout.setAlignment(FlowLayout.LEFT);break;}
case SEND:{direction=MessageBorder.DIRECTION_RIGHT;flowLayout.setAlignment(FlowLayout.RIGHT);break;}
}
setLayout(flowLayout);
outerMessagePanel=new JPanel(new BorderLayout(0,0));
outerMessagePanel.setOpaque(false);
outerMessagePanel.setBorder(new MessageBorder(internalColor,direction,thickness,showComment));
LayoutManager internalLayout;
if(type==SYSTEM || type==SEND)
	internalLayout=new BorderLayout(0,0);
else
	internalLayout=new VerticalFlowLayout(VerticalFlowLayout.TOP,5,0);

internalMessagePanel=new JPanel(internalLayout){
private static final long serialVersionUID = -5988560437421846679L;
@Override
public Dimension getPreferredSize() {
Dimension dim=super.getPreferredSize();
	if(type==SYSTEM)
		return new Dimension(Math.max(dim.width, 100),Math.max(dim.height, 40));
	else	
		return new Dimension(Math.max(dim.width, 100),Math.max(dim.height, 20));
}
};
internalMessagePanel.setBackground(internalColor);
outerMessagePanel.add(internalMessagePanel);
add(outerMessagePanel);

if(type==SYSTEM)
	{
	JLabel label=new JLabel(((TextMessage)message).getMessage(),JLabel.CENTER);
	label.setFont(Constants.getSystemChatFont());
	internalMessagePanel.add(label);
	}
else if(type==SEND)
{
	JTextArea area=new JTextArea();
	area.setPreferredSize(new Dimension(280,25));
	area.setFont(Constants.getMessageFont());
	area.setForeground(Constants.getMessageTextColor());
	area.setAutoscrolls(true);
	area.setLineWrap(true);
	area.setWrapStyleWord(true);
	area.setBackground(internalColor);
	internalMessagePanel.add(area,BorderLayout.CENTER);
	ImageComponent smiley=new ImageComponent(25,25,false);
	smiley.updateImage(ImageUtil.getSmileyImage(new Color(69,69,69),20));
	internalMessagePanel.add(smiley,BorderLayout.WEST);
	ImageComponent photo=new ImageComponent(25,25,false);
	photo.updateImage(ImageUtil.getPhotoAttachmentImage(new Color(69,69,69),30));
	internalMessagePanel.add(photo,BorderLayout.EAST);
	
}
else
{
final JPanel internalTopPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
final JPanel internalBottomPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0))
{
private static final long serialVersionUID = -5988560437421846679L;
@Override
public Dimension getPreferredSize() {
Dimension dim=super.getPreferredSize();
	return new Dimension(Math.max(dim.width,internalTopPanel.getWidth()),Math.max(dim.height, 20));
}

};
internalBottomPanel.setBackground(internalColor);
internalMessagePanel.add(internalTopPanel);

if(message instanceof TextMessage)
{
	if(message.getSenderId()==null || !message.getSenderId().equals(Member.SYSTEM_MEMBER_ID))
	{
		JTextArea area=new JTextArea(){
		private static final long serialVersionUID = 1027324839365567769L;
		@Override
		public Dimension getPreferredSize() {
		Dimension dim=super.getPreferredSize();
			return new Dimension(Math.max(Math.min(this.getFontMetrics(this.getFont()).stringWidth(this.getText()), 280),internalBottomPanel.getWidth()),Math.max(dim.height, 20));
		}
	};
	area.setFont(Constants.getMessageFont());
	area.setForeground(Constants.getMessageTextColor());
	area.setEditable(false);
	area.setBorder(null);
	area.setAutoscrolls(false);
	area.setLineWrap(true);
	area.setWrapStyleWord(true);
	area.setText(((TextMessage)message).getMessage());
	area.setBackground(internalColor);	
	internalTopPanel.add(area);
	
	}
	else
	{
		JLabel label=new JLabel(((TextMessage)message).getMessage(),JLabel.CENTER);
		label.setFont(Constants.getSystemChatFont());
		label.setForeground(Constants.getSystemChatColor());
		internalTopPanel.setBackground(internalColor);
		internalTopPanel.add(label);
	}
}
if(message!=null &&(message.getSenderId()==null || !message.getSenderId().equals(Member.SYSTEM_MEMBER_ID)))
	{
	JLabel lowerLabel=new JLabel(message.getStatuses().size()==0?"":Constants.getMessageTimeFormatter().format(message.getStatuses().get(0).getTime())){
	private static final long serialVersionUID = -8417398663185291858L;
	@Override
	public Dimension getPreferredSize() {
	Dimension dim=super.getPreferredSize();
		return new Dimension(dim.width,20);
	}
	};
	

	lowerLabel.setFont(Constants.getMessageTimeFont());
	lowerLabel.setForeground(Constants.getMessageTimeColor());
	internalBottomPanel.add(lowerLabel);
	if(type==ChatMessage.MINE)
		{
		ImageComponent status=new ImageComponent(false);
		status.updateImage(ImageUtil.getStatusImage(message.getLastStatus().getType()));
		internalBottomPanel.add(status);
		}
	internalMessagePanel.add(internalBottomPanel);
	
	}
}
}


@Override
public Dimension getPreferredSize()
{
	return new Dimension(346,outerMessagePanel.getPreferredSize().height); 
}



@Override
public long getId() {
	// TODO Auto-generated method stub
	return 0;
}



@Override
public void messageStatusChanged(MessageStatus newStatus) {
	// TODO Auto-generated method stub
	
}


}


private static class MessageBorder extends AbstractBorder
{

	private static final int DIRECTION_RIGHT=0;
	private static final int DIRECTION_LEFT=1;
	private static final int DIRECTION_NONE=2;
    
	private static final long serialVersionUID = -4993161532190038114L;
	protected int thickness;
    protected Color lineColor;
    private int direction;
    private boolean showComment=true;
    
    
    
    public MessageBorder(Color color, int direction,int thickness,boolean showComment)  {
        lineColor = color;
        this.thickness = thickness;
        this.direction=direction;
        this.showComment=showComment;
    }


        @Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if ((this.thickness > 0) && (g instanceof Graphics2D) && direction!=DIRECTION_NONE) {
            Graphics2D g2d = (Graphics2D) g;
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
       	 	g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);	 
       	 
            Color oldColor = g2d.getColor();
            g2d.setColor(lineColor);

            if(showComment)
            {
            	if(direction==DIRECTION_RIGHT)
            g2d.fillPolygon(new int[]{width-thickness,width,width-thickness}, new int[]{0,0,thickness}, 3);
            else
            g2d.fillPolygon(new int[]{thickness,thickness,0}, new int[]{0,thickness,0}, 3);
            }
            if(direction==DIRECTION_RIGHT)
            {
            	g2d.drawRoundRect(1, 1, width-thickness-1, height-3, 10, 10);
            	g2d.drawRoundRect(0, 0, width-thickness+1, height-1, 10, 10);
             	
            }
            else{
            	g2d.drawRoundRect(thickness-1, 1, width-thickness-1, height-3, 10, 10);
            	g2d.drawRoundRect(thickness-2, 0, width-thickness+1, height-1, 10, 10);
             }

          g2d.setColor(oldColor);
        }
    }

    @Override
	public Insets getBorderInsets(Component c, Insets insets) {
    	insets.set(2,direction==DIRECTION_LEFT?thickness:2,2,direction==DIRECTION_RIGHT?thickness:2);
    	return insets;
    }

      @Override
	public boolean isBorderOpaque() {
    	return true;
    }

}


@Override
public void menuPressed(PanelUnit source) {
	// TODO Auto-generated method stub
	
}

@Override
public long getId() {
	return this.listenerId;
}

@Override
public void messageAdded(Message message) {
	this.addMessage(message, true);
	
}


}
