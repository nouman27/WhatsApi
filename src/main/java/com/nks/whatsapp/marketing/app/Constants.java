package com.nks.whatsapp.marketing.app;

import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;

public class Constants {

private static final SimpleDateFormat lastSeenDateFormat=new SimpleDateFormat("d MMMM, h:mm aaa");
private static final SimpleDateFormat dateFormatter=new SimpleDateFormat("dd/MM/YYYY");
private static final SimpleDateFormat messageTimeFormatter=new SimpleDateFormat("h:mm aaa");
private static Font regularOpenSansFont;	
private static Font regularRobotoFont;	
	

public static SimpleDateFormat getDateFormatter()
{
return dateFormatter;
}

public static SimpleDateFormat getLastSeenDateFormatter()
{
return lastSeenDateFormat;	
}

public static Font getNormalFont()
{
	return 	getRegularRobotoFont().deriveFont(Font.PLAIN, 12f);
}

public static Font getBoldlFont()
{
	return 	getRegularRobotoFont().deriveFont(Font.PLAIN, 14f);
}

public static Font getLargeBoldlFont()
{
	return 	getRegularRobotoFont().deriveFont(Font.BOLD, 16f);
}

public static Font getSmallFont()
{
return new Font("Arial",Font.PLAIN,8);	
}

public static Color getNormalFontColor()
{
return Color.GRAY;
}

public static Color getBoldFontColor()
{
return Color.BLACK;
}

public static Color getLargeBoldFontColor()
{
return Color.WHITE;
}

public static Color getSmallFontColor()
{
return Color.GRAY;
}

public static Color getNormalApplicationBackgroudColor()
{
return Color.WHITE;
}

public static Color getTopPanelBackgroudColor()
{
return Color.GREEN.darker();
}

public static int getRowHeightForPanelUnit()
{
return 50;	
}

public static int getMaxUnits()
{
return 5;	
}

public static Color getMyChatBackColor()
{
return new Color(220,248,198);	
}

public static Color getTheirChatBackColor()
{
return new Color(255,255,255);	
}

public static Color getSystemChatBackColor()
{
return new Color(225,245,254,240);	
}



public static SimpleDateFormat getMessageTimeFormatter()
{
return messageTimeFormatter;	
}

public static Font getRegularOpenSansFont()
{
if(regularOpenSansFont==null)
	try{regularOpenSansFont=Font.createFont(Font.TRUETYPE_FONT, Constants.class.getResourceAsStream("OpenSans-Regular.ttf"));}catch(Exception ex){ex.printStackTrace();return new Font(Font.SANS_SERIF,0,12);}
return regularOpenSansFont;
}

public static Font getRegularRobotoFont()
{
if(regularRobotoFont==null)
	try{regularRobotoFont=Font.createFont(Font.TRUETYPE_FONT, Constants.class.getResourceAsStream("Roboto-Regular.ttf"));}catch(Exception ex){ex.printStackTrace();return new Font(Font.SANS_SERIF,0,12);}
return regularRobotoFont;
}


public static Font getMessageFont()
{
return 	getRegularOpenSansFont().deriveFont(Font.PLAIN, 13.6f);
}

public static Color getMessageTextColor()
{
return new Color(38,38,38);
}

public static Font getSystemChatFont()
{
return 	getRegularRobotoFont().deriveFont(Font.PLAIN, 12.5f);
}

public static Color getSystemChatColor()
{
return new Color(69,90,100,242);
}


public static Font getMessageTimeFont()
{
return 	getRegularRobotoFont().deriveFont(Font.PLAIN, 10f);
}

public static Color getMessageTimeColor()
{
return new Color(0,0,0,120);
}


}
