package com.nks.whatsapp.marketing.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import com.nks.whatsapp.Message;
import com.nks.whatsapp.MessageStatus;

public class GenericMessagePanel extends ActionableJPanel{

public static final int TYPE_NORMAL=0;	
public static final int TYPE_SMALL=1;	
public static final int TYPE_HEADING=2;
public static final int TYPE_DATE=3;
public static final int TYPE_LINE=4;
public static final int TYPE_LARGE_HEADING=5;

private static final long serialVersionUID = 7314883563703500839L;
private int type;

public 	GenericMessagePanel(int type)
	{
	this.type=type;
	init();
	}


private void init()
{
this.setOpaque(false);
if(type==TYPE_LINE)
{
	this.setLayout(new BorderLayout());
	this.add(new JSeparator(),BorderLayout.SOUTH);
}
else
	this.setLayout(new FlowLayout(FlowLayout.LEFT));
}

public void updateMessage(String message)
	{
	updateMessage(message,null);
	}

public void updateMessage(String message,Color color)
{
if(this.type==TYPE_LINE) return;
this.removeAll();
addString(message,color);
}

public void updateMessage(String memberText,MessageStatus lastStatus,int messageType,String messageText)
{
this.removeAll();
if(lastStatus==null) return;
String msgTxt=null;
Image statusImage=null;
Image typeImage=null;

switch(messageType)
	{
case Message.TEXT:{statusImage=ImageUtil.getStatusImage(lastStatus.getType());msgTxt=messageText;break;}
case Message.AUDIO:{typeImage=ImageUtil.getMessageTypeImage(Constants.getSmallFontColor(),Message.AUDIO);statusImage=ImageUtil.getStatusImage(lastStatus.getType());msgTxt="Audio";break;}
case Message.VIDEO:{typeImage=ImageUtil.getMessageTypeImage(Constants.getSmallFontColor(),Message.VIDEO);statusImage=ImageUtil.getStatusImage(lastStatus.getType());msgTxt="Video";break;}
case Message.PHOTO:{typeImage=ImageUtil.getMessageTypeImage(Constants.getSmallFontColor(),Message.PHOTO);statusImage=ImageUtil.getStatusImage(lastStatus.getType());msgTxt="Photo";break;}
}

if(memberText!=null)
	addString(memberText,null);

if(statusImage!=null)
	this.add(new JLabel(new ImageIcon(statusImage)));
if(typeImage!=null)
	this.add(new JLabel(new ImageIcon(typeImage)));

addString(msgTxt,null);
}
 


private void addString(String string,Color color)
{
JLabel jlabel=new JLabel(string);
Color foregroundColor;
switch(type)
	{
	case TYPE_HEADING:{jlabel.setFont(Constants.getBoldlFont());foregroundColor=Constants.getBoldFontColor();break;}
	case TYPE_LARGE_HEADING:{jlabel.setFont(Constants.getLargeBoldlFont());foregroundColor=Constants.getLargeBoldFontColor();break;}
	case TYPE_SMALL:{jlabel.setFont(Constants.getSmallFont());foregroundColor=Constants.getSmallFontColor();break;}
	case TYPE_DATE:{jlabel.setFont(Constants.getMessageTimeFont());foregroundColor=Constants.getMessageTimeColor();break;}
	default:{jlabel.setFont(Constants.getNormalFont());foregroundColor=Constants.getNormalFontColor();break;}
	}
if(color==null)
	jlabel.setForeground(foregroundColor);
else
	jlabel.setForeground(color);
this.add(jlabel);		
}

}
