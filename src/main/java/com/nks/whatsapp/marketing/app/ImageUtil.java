package com.nks.whatsapp.marketing.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import com.nks.whatsapp.Message;
import com.nks.whatsapp.MessageStatus;

public class ImageUtil {

private static Map<Dimension,Image> backgrounds=new Hashtable<Dimension,Image>();
private static Image baseBackground;
	

public static Image getSmileyImage() {
	try{
		return SVGImages.getImage("smiley").getImage();
	}catch(Exception ex){return null;}
}

public static Image getSmileyImage(Color color) {
	try{
		return SVGImages.getImage("smiley").getImage(color);
	}catch(Exception ex){return null;}
}
public static Image getSmileyImage(int height) {
	try{
		return SVGImages.getImage("smiley").getImage(height);
	}catch(Exception ex){return null;}
}

public static Image getSmileyImage(Color color,int height) {
	try{
		return SVGImages.getImage("smiley").getImage(color,height);
	}catch(Exception ex){return null;}
}


public static Image getSearchImage() {
		try{
			return SVGImages.getImage("search-light").getImage();
		}catch(Exception ex){return null;}
	}

	public static Image getSearchImage(Color color) {
		try{
			return SVGImages.getImage("search-light").getImage(color);
		}catch(Exception ex){return null;}
	}
	public static Image getSearchImage(int height) {
		try{
			return SVGImages.getImage("search-light").getImage(height);
		}catch(Exception ex){return null;}
	}

	public static Image getSearchImage(Color color,int height) {
		try{
			return SVGImages.getImage("search").getImage(color,height);
		}catch(Exception ex){return null;}
	}

	public static Image getChatImage() {
		try{
			return SVGImages.getImage("chat").getImage();
		}catch(Exception ex){return null;}
	}

	public static Image getChatImage(Color color) {
		try{
			return SVGImages.getImage("chat").getImage(color);
		}catch(Exception ex){return null;}
	}
	public static Image getChatImage(int height) {
		try{
			return SVGImages.getImage("chat").getImage(height);
		}catch(Exception ex){return null;}
	}

	public static Image getChatImage(Color color,int height) {
		try{
			return SVGImages.getImage("chat").getImage(color,height);
		}catch(Exception ex){return null;}
	}


	public static Image getInfoImage() {
		try{
			return SVGImages.getImage("alert-update").getImage();
		}catch(Exception ex){return null;}
	}

	public static Image getInfoImage(Color color) {
		try{
			return SVGImages.getImage("alert-update").getImage(color);
		}catch(Exception ex){return null;}
	}
	public static Image getInfoImage(int height) {
		try{
			return SVGImages.getImage("alert-update").getImage(height);
		}catch(Exception ex){return null;}
	}

	public static Image getInfoImage(Color color,int height) {
		try{
			return SVGImages.getImage("alert-update").getImage(color,height);
		}catch(Exception ex){return null;}
	}

	
	public static Image getGoBackImage() {
		try{
			return SVGImages.getImage("back").getImage();
		}catch(Exception ex){return null;}
	}

	public static Image getGoBackImage(Color color) {
		try{
			return SVGImages.getImage("back").getImage(color);
		}catch(Exception ex){return null;}
	}
	public static Image getGoBackImage(int height) {
		try{
			return SVGImages.getImage("back").getImage(height);
		}catch(Exception ex){return null;}
	}

	public static Image getGoBackImage(Color color,int height) {
		try{
			return SVGImages.getImage("back").getImage(color,height);
		}catch(Exception ex){return null;}
	}

	public static Image getMenuImage() {
		try{
			return SVGImages.getImage("menu").getImage();
		}catch(Exception ex){return null;}
	}

	public static Image getMenuImage(Color color) {
		try{
			return SVGImages.getImage("menu").getImage(color);
		}catch(Exception ex){return null;}
	}
	public static Image getMenuImage(int height) {
		try{
			return SVGImages.getImage("menu").getImage(height);
		}catch(Exception ex){return null;}
	}

	public static Image getMenuImage(Color color,int height) {
		try{
			return SVGImages.getImage("menu").getImage(color,height);
		}catch(Exception ex){return null;}
	}
	
	public static Image getAttachmentImage() {
		try{
			return SVGImages.getImage("clip").getImage();
		}catch(Exception ex){return null;}
	}

	public static Image getAttachmentImage(Color color) {
		try{
			return SVGImages.getImage("clip").getImage(color);
		}catch(Exception ex){return null;}
	}
	public static Image getAttachmentImage(int height) {
		try{
			return SVGImages.getImage("clip").getImage(height);
		}catch(Exception ex){return null;}
	}

	public static Image getAttachmentImage(Color color,int height) {
		try{
			return SVGImages.getImage("clip").getImage(color,height);
		}catch(Exception ex){return null;}
	}
	

	public static Image getPhotoAttachmentImage() {
		try{
			return SVGImages.getImage("status-image").getImage();
		}catch(Exception ex){return null;}
	}

	public static Image getPhotoAttachmentImage(Color color) {
		try{
			return SVGImages.getImage("status-image").getImage(color);
		}catch(Exception ex){return null;}
	}
	public static Image getPhotoAttachmentImage(int height) {
		try{
			return SVGImages.getImage("status-image").getImage(height);
		}catch(Exception ex){return null;}
	}

	public static Image getPhotoAttachmentImage(Color color,int height) {
		try{
			return SVGImages.getImage("status-image").getImage(color,height);
		}catch(Exception ex){return null;}
	}

	public static Image getAddUserImage() {
		try{
			return SVGImages.getImage("add-user").getImage();
		}catch(Exception ex){return null;}
	}

	public static Image getAddUserImage(Color color) {
		try{
			return SVGImages.getImage("add-user").getImage(color);
		}catch(Exception ex){return null;}
	}
	public static Image getAddUserImage(int height) {
		try{
			return SVGImages.getImage("add-user").getImage(height);
		}catch(Exception ex){return null;}
	}

	public static Image getAddUserImage(Color color,int height) {
		try{
			return SVGImages.getImage("add-user").getImage(color,height);
		}catch(Exception ex){return null;}
	}
	
	
	
public static Image getNoMemberPhotoImage() {
	try{
		return SVGImages.getImage("user").getImage();
	}catch(Exception ex){return null;}
}

public static Image getNoGroupPhotoImage() {
	try{
		return SVGImages.getImage("group-default").getImage();
	}catch(Exception ex){return null;}

}

public static Image getNoMemberPhotoImage(int height) {
try{
	return SVGImages.getImage("user").getImage(height);
}catch(Exception ex){return null;}
}

public static Image getNoMemberPhotoImage(Color backColor,int height) {
try{
	return SVGImages.getImage("user").getImage(backColor,height);
}catch(Exception ex){return null;}
}


public static Image getNoGroupPhotoImage(int height) {
try{
	return SVGImages.getImage("group-default").getImage(height);
}catch(Exception ex){return null;}
}

public static Image getNoGroupPhotoImage(Color backColor,int height) {
try{
	return SVGImages.getImage("group-default").getImage(backColor,height);
}catch(Exception ex){return null;}
}

public static Image statusSeenImage() {
	return null;
}


/*public static BufferedImage toBufferedImage(Image img)
{
    if (img instanceof BufferedImage)
    {
        return (BufferedImage) img;
    }

    // Create a buffered image with transparency
    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    // Draw the image on to the buffered image
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();

    // Return the buffered image
    return bimage;
}*/


public static Image getStatusImage(MessageStatus.Type type)
{
try{
	switch(type)
	{
	case PENDING:{return SVGImages.getImage("status-time").getImage();}
	case SENT:{return SVGImages.getImage("status-check").getImage();}
	case DELIVERED:{return SVGImages.getImage("status-dblcheck").getImage();}
	case SEEN:{return SVGImages.getImage("status-dblcheck-ack").getImage();}
	default:{return null;}
	}
	}catch(Exception ex){return null;}

}

public static Image getBackgroundImage(int width,int height)
{
Dimension dim=new Dimension(width,height);
if(backgrounds.containsKey(dim)) return backgrounds.get(dim);
Image img=createBackgroundImage(dim);
if(img!=null)
	backgrounds.put(dim, img);
return img;
}

private static Image createBackgroundImage(Dimension d)
{
try{
	if(baseBackground==null)
		{
		BufferedImage img=ImageIO.read(ImageUtil.class.getResourceAsStream("background.png"));
		baseBackground=img;
		if(img!=null && img.getWidth()==d.getWidth() && img.getHeight()==d.getHeight())
			return img;
		}
	if(baseBackground!=null)
		return baseBackground.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
return null;
}catch(Exception ex){return null;}	

}

public static Image getMessageTypeImage(int sendableType)
{
	try{
	switch(sendableType)
	{
	case Message.AUDIO:{return SVGImages.getImage("status-audio").getImage();}
	case Message.VIDEO:{return SVGImages.getImage("status-video").getImage();}
	case Message.PHOTO:{return SVGImages.getImage("status-image").getImage();}
	default:{return null;}
	}
	}catch(Exception ex){return null;}
}

public static Image getMessageTypeImage(Color color,int sendableType)
{
	try{
	switch(sendableType)
	{
	case Message.AUDIO:{return SVGImages.getImage("status-audio").getImage(color);}
	case Message.VIDEO:{return SVGImages.getImage("status-video").getImage(color);}
	case Message.PHOTO:{return SVGImages.getImage("status-image").getImage(color);}
	default:{return null;}
	}
	}catch(Exception ex){return null;}
}

}


