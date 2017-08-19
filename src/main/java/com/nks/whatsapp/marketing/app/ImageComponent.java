package com.nks.whatsapp.marketing.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class ImageComponent extends ActionableJPanel
{
 static final long serialVersionUID = 2653495759946954043L;

 Image image;
 private int width;
 private int height;
 private Color imageBackground;
 private boolean circle;
 
 public ImageComponent(boolean circle)
 {
	 this(null,circle); 
 }
 
 public ImageComponent(Color imageBackground,boolean circle)
 {
	 this(imageBackground,0,0,circle); 
 }

 public ImageComponent(int width,int height,boolean circle) {
this(null,width,height,circle);	  
 }
 
 public ImageComponent(Color imageBackground,int width,int height,boolean circle) {
this.imageBackground=imageBackground;
this.setOpaque(false);	
this.width=width;
this.height=height;
this.circle=circle; 
}

 
 
 public void setImageBackground(Color imageBackground) {
	this.imageBackground = imageBackground;
}

public void updateImage(Image image)
 {
	 this.image=image;
	 if(width==0)
	 	 width=image.getWidth(null);
	 if(height==0)
	 	height=image.getHeight(null);
	 this.repaint();
 }

 public void updateImage(Color imageBackground,Image image)
 {
	 this.imageBackground=imageBackground;
	 updateImage(image);
 }

 
 @Override public Dimension getPreferredSize() {
	    return new Dimension(width,height);
	  }

 @Override public void paintComponent(Graphics g)
 {
	super.paintComponent(g);
	int imageWidth=image.getWidth(null);
		int imageHeight=image.getHeight(null);

		Graphics2D g2 = (Graphics2D) g;
	 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	 g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);	 
	 if(this.imageBackground!=null)
	 {
		g2.setColor(imageBackground);
		if(circle)
		g2.fillOval(0, 0, width, height);
		else
		g2.fillRect(0, 0, width, height);
	 }
		else
	{
		g2.setColor(this.getBackground());
		if(circle)
		g2.drawOval((width-imageWidth)/2, (height-imageHeight)/2, imageWidth, imageHeight);
	} 
	Shape shape=null;
	 if(circle)
		shape=new Ellipse2D.Double((width-imageWidth)/2, (height-imageHeight)/2, imageWidth, imageHeight);
	 else
		shape=new Rectangle2D.Double((width-imageWidth)/2, (height-imageHeight)/2, imageWidth, imageHeight);
	g2.setClip(shape);
	 g2.drawImage(image, (width-imageWidth)/2, (height-imageHeight)/2, null);
 }
 
}
