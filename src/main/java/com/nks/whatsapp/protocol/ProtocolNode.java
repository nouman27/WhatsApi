package com.nks.whatsapp.protocol;

import java.util.ArrayList;
import java.util.List;

public class ProtocolNode {

	//private static String nonPrintable = "#[^"+Util.hexToString("20")+"-"+Util.hexToString("7E")+"]#";
	private String tag;
	private Attributes<String> attributeHash;
	private String data;
	private List<ProtocolNode> children;
	
	
	public ProtocolNode(String tag,Attributes<String> attributes,ProtocolNode[] children,String data)
	{
		this.tag=tag;
		this.attributeHash=attributes;
		setChildren(children);
		this.data=data;
		
	}
	
	private void setChildren(ProtocolNode[] children)
	{
		if(children==null)
			return;
		for(ProtocolNode child:children)
			addChild(child);
	}
	
	
	public String getTag() {
		return tag;
	}



	public Attributes<String> getAttributes()
    {
        return attributeHash;
    }

	public Object getAttribute(String attribute)
    {
        if(this.attributeHash.containsKey(attribute))
        	return attributeHash.get(attribute);
        return "";
    }

	

	public ProtocolNode[] getChildren() {
		return children==null?null:children.toArray(new ProtocolNode[0]);
	}
	
	public String getData() {
		return data;
	}

	public void addChild(ProtocolNode node)
	{
		if(children==null)
			children=new ArrayList<ProtocolNode>();
		children.add(node);
	}
	

	public void removeChild(int tag)
	{
	if(this.children!=null && children.size()>tag)
		children.remove(tag);
	}

	public void removeChild(String tag)
	{
		removeChild(tag,null);
	}
	
	public void removeChild(String tag,Attributes<String> attributes)
	{
	if(this.children==null)
			return;
	ProtocolNode toCompare=new ProtocolNode(tag,attributes,null,null);
	for(int i=0;i<children.size();i++)
		if(children.get(i).equals(toCompare, true))
			{children.remove(i);return;}
		}


	public ProtocolNode getChild(int tag)
	{
	if(this.children!=null && children.size()>tag)
		return children.get(tag);
	return null;
	}

	public boolean hasChild(String tag)
	{
		return getChild(tag,null)!=null?true:false;
	}
	
	public ProtocolNode getChild(String tag)
	{
		return getChild(tag,null);
	}
	
	public ProtocolNode getChild(String tag,Attributes<String> attributes)
	{
		if(this.children==null)
			return null;
	ProtocolNode toCompare=new ProtocolNode(tag,attributes,null,null);
		for(int i=0;i<children.size();i++)
			if(children.get(i).equals(toCompare, attributes!=null))
				{return children.get(i);}
			else{ProtocolNode fromChild=children.get(i).getChild(tag,attributes);if(fromChild!=null)return fromChild;}
	return null;
	}

	
	
	public String nodeString(String indent)
	{
		return nodeString(indent,false);
	}
	
	public String nodeString()
	{
		return nodeString(false);
	}
	
	public String nodeString(boolean isChild)
	{
		return nodeString("",isChild);
	}
	
	public String nodeString(String indent , boolean isChild )
    {
		char lt = '<';
        char gt = '>';
        String nl = "\n";
        
		//TODO
        /*
		  if (!self::isCli()) {
            $lt = '&lt;';
            $gt = '&gt;';
            $nl = '<br />';
            $indent = str_replace(' ', '&nbsp;', $indent);
        }*/
        StringBuilder ret = new StringBuilder();
        ret.append(indent);ret.append(lt);ret.append(tag);
        if (attributeHash != null) {
            for (String key:attributeHash.keys()) {
                ret .append(" ");ret.append(key);ret.append("=\"");ret.append(attributeHash.get(key));ret.append("\"");
            }
        }
        ret .append(gt);
        if (data!=null && data.length() > 0) {
            if (data.length() <= 1024) {
              //if (data.matches(nonPrintable)) {
                if(!Util.isAsciiPrintable(data)){
                	ret.append(Util.bin2hex(data));
                } else {
                    ret.append(data);
                }
            } else {
                //raw data
                ret .append(" ");ret.append(data.length());ret.append(" byte data");
            }
        }
        if (children!=null) {
            ret.append(nl);
            List<String> foo = new ArrayList<String>();
            for(ProtocolNode child:children) {
               foo.add(child.nodeString(indent+"  ",true));
            }
            ret.append(Util.implode(new String(nl), foo.toArray(new String[0])));
            ret.append(nl);ret.append(indent);
        }
        ret.append(lt);ret.append('/');ret.append(tag);ret.append(gt);
        if (!isChild) {
            ret.append(nl);
            //TODO
            /*if (!self::isCli()) {
                $ret .= $nl;
            }*/
        }
        return ret.toString();
    }	
	
	@Override
	public String toString(){
		return nodeString(true);
	}
	
public boolean equals(ProtocolNode other,boolean includeAttributes)
{
if(!tag.equals(other.tag)) return false;
if(!includeAttributes) return true;	
if(other.attributeHash==null){return attributeHash==null?true:false;}
if(attributeHash==null) return false;
for(String key:other.attributeHash.keys())
	{
	Object val=other.attributeHash.get(key);
	Object childVal=attributeHash.get(key);
	if(childVal==null) return false;
	if(!val.equals(childVal)) return false;
	}
return true;
}
}


