package com.nks.whatsapp.marketing.app;

import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class SVGImages {

	private static String[] defaultImagesIds=new String[]{"x-alt-light","media-download","audio","status-dblcheck-ack","star","media-disabled","logo","ptt","status-ptt-gray","refresh-light","emoji-travel","ptt-out-green","audio-play","status-location","status-audio","platform-wp","audio-cancel-noborder","back-light","ptt-chat","alert-update","minus","ptt-out-blue","forward-light","audio-download","checkmark-light-l","menu","contact","media-cancel","audio-cancel","search","ptt-out-gray","emoji-input","x-light","msg-dblcheck","emoji-recent","chevron-right","star-btn","emoji-people","status-check","alert-notification","x","status-vcard","platform-iphone","x-alt-small","x-alt","send-light","emoji-activity","back-blue","msg-dblcheck-ack","add-alt","msg-dblcheck-light","msg-time","status-time","user","send","search-light","dnd","status-video","round-send-inv","media-upload","audio-upload","status-dblcheck","x-viewer","camera-light","status-ptt-green","ptt-in-blue","emoji-flags","emoji-objects","clip","x-input","platform-bb","pencil","ptt-in-gray","status-document","platform-s60","msg-video-light","hide","msg-audio-light","refresh-l-light","forward","connection-l-light","emoji-symbols","msg-check","unstar-btn","gps-light","smiley","round-x","msg-time-light","error","image","media-upload-noborder","checkmark-light","download","media-play","refresh","emoji-nature","alert-computer","star-light","camera","platform-bb10","media-download-noborder","round-x-inv","delete","broadcast","status-image","audio-pause","ptt-in-green","msg-dblcheck-ack-light","platform-android","back","checkmark","emoji-food","round-send","broadcast-light","alert-battery","plus","platform-s40","location","muted","share","status-ptt-blue","chat","msg-check-light","chevron-left","alert-phone","add-user","group-default"};
	private static Hashtable<String,SVGImage> images=new Hashtable<String,SVGImage>();	
	
public static void loadImages() throws SAXException, IOException, ParserConfigurationException
{
Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(SVGImages.class.getResourceAsStream("systemImages.svg"));	
loadSingleNode(doc.getChildNodes().item(0));
}

private static void loadSingleNode(Node svgNode)
{
	try{
	NodeList childNodes=svgNode.getChildNodes();
		for(int i=0;i<childNodes.getLength();i++)
			loadSingleImage(childNodes.item(i));
	}catch(Exception arrEx) {return;}
}

private static void loadSingleImage(Node node)
{
((Element)node).setAttribute("xmlns", "http://www.w3.org/2000/svg");
((Element)node).setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
try{
SVGImage svgImage=new SVGImage(node);
if(!images.contains(svgImage.getId()))
	images.put(svgImage.getId(), svgImage);
}catch(Exception ex){ex.printStackTrace();}
}

public static SVGImage getImage(String id)
{
return images.get(id);	
}

public static void checkDefaultLoaded() throws Exception
{
for(String id:defaultImagesIds)
	if(!images.containsKey(id))
		throw new Exception("System image :"+id+" couldn't be loaded");
}
}
