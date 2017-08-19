package com.nks.whatsapp.marketing.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGImage {

private Hashtable<ComparableColor,Hashtable<Dimension,Image>> images=new Hashtable<ComparableColor,Hashtable<Dimension,Image>>();	
	
private Node node;
private String id;
private int width;
private int height;

public SVGImage(Node node) throws Exception
{
try{
this.node=node;
this.id=node.getAttributes().getNamedItem("id").getNodeValue();
this.width=Integer.parseInt(node.getAttributes().getNamedItem("width").getNodeValue());
this.height=Integer.parseInt(node.getAttributes().getNamedItem("height").getNodeValue());
}catch(Exception ex){System.out.println("exception");throw ex;}
}

public String getId() {
	return id;
}

public Image getImage() throws Exception
{
return getImage(width,height);
}

public Image getImage(Color backGround) throws Exception
{
return getImage(new ComparableColor(backGround),width,height);
}

public Image getImage(int height) throws Exception
{
double ratio= (double) this.width/this.height;
int width=(int)(height*ratio);
return getImage(width,height);
}

public Image getImage(Color backGround,int height) throws Exception
{
double ratio= (double) this.width/this.height;
int width=(int)(height*ratio);
return getImage(new ComparableColor(backGround),width,height);
}


public Image getImage(int width,int height) throws Exception
{
	return getImage(ComparableColor.defaultColor,width,height);
}

public Image getImage(ComparableColor color,int width,int height) throws Exception
{
	Hashtable<Dimension,Image> comparableImages=getImages(color);
	Dimension dimension=new Dimension(width,height);
	if(comparableImages.containsKey(dimension)) return comparableImages.get(dimension);
	Image createdImage=createImage(color,dimension);
	comparableImages.put(dimension, createdImage);
	
	return createdImage;
}


public Hashtable<Dimension,Image> getImages(ComparableColor color) throws Exception
{
	if(images.containsKey(color)) return images.get(color);
	Hashtable<Dimension,Image> dimensionImages=new Hashtable<Dimension,Image>();
	images.put(color, dimensionImages);
	return dimensionImages;
}


private Image createImage(ComparableColor backgroundColor,Dimension dimension) throws Exception
{
	PNGTranscoder pngTranscode=new PNGTranscoder();
	pngTranscode.addTranscodingHint(PNGTranscoder.KEY_WIDTH,(float) dimension.width);
	pngTranscode.addTranscodingHint(PNGTranscoder.KEY_HEIGHT,(float) dimension.height);
	TranscoderInput input=new TranscoderInput(new StringReader(getOuterXml(getNodeForBackground(backgroundColor,node))));
	ByteArrayOutputStream byteArray=new ByteArrayOutputStream();
	TranscoderOutput output=new TranscoderOutput(byteArray);
	pngTranscode.transcode(input, output);
	return ImageIO.read(new ByteArrayInputStream(byteArray.toByteArray()));
}


private static String getOuterXml(Node node)
	    throws TransformerConfigurationException, TransformerException
	{
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty("omit-xml-declaration", "yes");

	    StringWriter writer = new StringWriter();
	    transformer.transform(new DOMSource(node), new StreamResult(writer));
	    return writer.toString();         
	}

private static Node getNodeForBackground(ComparableColor color,Node node)
{
if(!color.hasColor) return node;
Node newNode=node.cloneNode(true);
setColorToNode(newNode,color);	
return newNode;
}

private static void setColorToNode(Node node,ComparableColor color)
{
if(node.getNodeName().equals("path"))
	{
	String c=String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	String o=Float.toString((float)color.getAlpha()/255);
	Node fillNode=node.getAttributes().getNamedItem("fill");
	Node opacityNode=node.getAttributes().getNamedItem("opacity");
	Node fillOpacityNode=node.getAttributes().getNamedItem("fill-opacity");
	
	if(fillNode!=null)
		fillNode.setNodeValue(c);
	else
		((Element)node).setAttribute("fill", c);
	
	if(opacityNode!=null)
		opacityNode.setNodeValue(o);
	else
		if(fillOpacityNode!=null)
			if(color.getAlpha()<255)
				fillOpacityNode.setNodeValue(o);
		else
		{
			if(color.getAlpha()<255)
				((Element)node).setAttribute("opacity", o);
		}
	}

NodeList n=node.getChildNodes();
if(n==null || n.getLength()<1) return;
for(int index=0;index<n.getLength();index++)
	setColorToNode(n.item(index),color);
}


static class ComparableColor extends Color 
{
	private static final long serialVersionUID = -3866558657387233429L;
	private boolean hasColor;
	private static ComparableColor defaultColor=new ComparableColor();
	
	public ComparableColor(Color baseColor)
	{
	super(baseColor.getRed(),baseColor.getGreen(),baseColor.getBlue(),baseColor.getAlpha());	
	hasColor=true;	
	}

	public ComparableColor()
	{
	super(0,0,0);
	hasColor=false;
	}
	
	@Override
	public boolean equals(Object obj) {
	if(obj instanceof ComparableColor)
	{
	ComparableColor other=((ComparableColor)obj);
		return (hasColor==other.hasColor && this.getRGB()==other.getRGB() && this.getAlpha()==other.getAlpha());
	}
		return false;
	}
	

}

}
