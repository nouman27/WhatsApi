package com.nks.whatsapp.protocol;

import java.util.Arrays;




public class BinTreeNodeWriter {

	 private String output="";
	 private KeyStream key;

	 public void resetKey()
	    {
	        key = null;
	    }
	    public void setKey(KeyStream key)
	    {
	        this.key = key;
	    }
	
	    
	    private void writeListStart(int len)
	    {
	        if (len == 0) {
	            output += Util.hexToString("00");
	        } else if (len < 256) {
	        	output += Util.hexToString("f8")+(char)len;
	        } else {
	            output += Util.hexToString("f9")+writeInt16(len);
	        }
	    }
	    private void writeBytes(String bytes)
	    {
	    	writeBytes(bytes,false);
	    }
	    
	    private String tryPackAndWriteHeader(int v, String data)
	    {
	        int length = data.length();
	        if (length >= 128) {
	            return "";
	        }
	        int[] array2=new int[(int)(Math.floor((length + 1) / 2))];
	        Arrays.fill(array2, 0);
	        for (int i = 0; i < length; i++) {
	            int packByte = packByte(v, Util.ord(data.charAt(i)));
	            if (packByte == -1) {
	                array2 = new int[0];
	                break;
	            }
	            int n2 =(int) Math.floor(i / 2);
	            array2[n2] |= (packByte << 4 * (1 - i % 2));
	        }
	        if (array2.length > 0) {
	        	if (length % 2 == 1) {
	                array2[array2.length - 1] |= 0xF;
	            }
	        	char[] charArray=new char[array2.length]; 
	        	for(int i=0;i<charArray.length;i++)
	        		charArray[i]=(char)array2[i];
	        	String string=new String(charArray);
	        	output += (char)v;
	            output += writeInt8(length % 2 << 7 | string.length());
	            return string;
	    
	        }
	        return "";
	    }
	    
	    
	    private void writeBytes(String bytes, boolean b)
	    {
	        int len = bytes.length();
	        String toWrite = bytes;
	        if (len >= 0x100000) {
	            output += Util.hexToString("fe");
	            output += writeInt31(len);
	        } else if (len >= 0x100) {
	            output += Util.hexToString("fd");
	            output += writeInt20(len);
	        } else {
	            String r = "";
	            if (b) {
	                if (len < 128) {
	                    r = tryPackAndWriteHeader(255, bytes);
	                    if (r.equals("")) {
	                        r = tryPackAndWriteHeader(251, bytes);
	                    }
	                }
	            }
	            if (r.equals("")) {
	                output += Util.hexToString("fc");
	                output += writeInt8(len);
	            } else {
	                toWrite = r;
	            }
	        }
	        output += toWrite;
	    }
	    
	    private void writeToken(int token)
	    {
	        if (token <= 255 && token >= 0) {
	            output += (char)token;
	        } else {
	            throw new java.lang.IllegalArgumentException("Invalid token.");
	        }
	     }
	    
	  
	    
	    private void writeJid(String user, String server)
	    {
	    	output+=Util.hexToString("fa");
	        if (user.length() > 0) {
	            writeString(user, true);
	        } else {
	            writeToken(0);
	        }
	        writeString(server);
	    }
	    
	    private void writeString(String tag )
	    {
	    	writeString(tag, false );
		}
	    
	    private void writeString(String tag, boolean packed )
	    {
	        int intVal = -1;
	        boolean subdict = false;
	        Object[] returned=TokenMap.tryGetToken(tag, subdict, intVal);
	        if((Boolean)returned[2]){
	            if ((Boolean)returned[1]) {
	                writeToken(236);
	            }
	            writeToken((Integer)returned[0]);
	            return;
	        }
	        int index =tag.indexOf('@');
	        		
	        if (index>0) {
	            String server =tag.substring(index+1); 
	            String user =tag.substring(0, index); 
	            writeJid(user, server);
	        } else {
	            if (packed) {
	                writeBytes(tag, true);
	            } else {
	                writeBytes(tag);
	            }
	        }
	    }
	    
	    private void writeAttributes(Attributes<String> attributes)
	    {
	        if (attributes!=null) {
	            for(String key:attributes.keys())
	            {
	            	writeString(key);
	            	writeString(attributes.get(key).toString());
	            }
	        }
	    }
	    
	    private String flushBuffer()
	    {
	    	return flushBuffer(true);
		}
	    
	    private String flushBuffer(boolean encrypt)
	    {
	        int size = output.length();
	        String data = output;
	        if (key != null && encrypt) {
	            StringBuilder bsize = new StringBuilder(getInt24(size));
	            data = key.encodeMessage(data, size, 0, size);
	            int len = data.length();
	            bsize.setCharAt(0, (char)((8 << 4) | ((len & 16711680) >> 16)));
	            bsize.setCharAt(1,(char)((len & 65280) >> 8));
	            bsize.setCharAt(2,(char)(len & 255));
		        size = parseInt24(bsize.toString());
	        }
	        String ret = writeInt24(size)+data;
	        output = "";
	        return ret;
	    }
	    
	    public String startStream(String domain, String resource)
	    {
	    	Attributes<String> attributes=new Attributes<String>();
	    	attributes.put("to", domain);
	    	attributes.put("resource", resource);
	    	writeListStart(attributes.size() * 2 + 1);
	        output += Util.hexToString("01");
	        writeAttributes(attributes);
	        return "WA"+writeInt8(1)+writeInt8(6)+flushBuffer();
	    }

	    private void writeInternal(ProtocolNode protocolTreeNode)
	    {
	        int len = 1;
	        Attributes<String> attributes=protocolTreeNode.getAttributes();
	        ProtocolNode[] children=protocolTreeNode.getChildren();
	        String data=protocolTreeNode.getData();
	        
	        if ( attributes!= null) {
	            len += attributes.size() * 2;
	        }
	        if (children!=null && children.length>0) {
	            len += 1;
	        }
	        if (data!=null && data.length()>0) {
	            len += 1;
	        }
	        
	        writeListStart(len);
	        writeString(protocolTreeNode.getTag());
	        writeAttributes(attributes);
	        if (data!=null && data.length()>0) {
		            writeBytes(data);
	        }
	        if (children!=null) {
	            writeListStart(children.length);
	            for(ProtocolNode child:children) {
	                writeInternal(child);
	            }
	        }
	    }    
	 
	 public String write(ProtocolNode node)
	 {
		 return write(node,true);
		 
	 }
	    
	 public String write(ProtocolNode node, boolean encrypt)
	    {
	        if (node == null) {
	            output+=Util.hexToString("00");
	        } else {
	            writeInternal(node);
	        }
	        return flushBuffer(encrypt);
	    }    
	    
	private static String writeInt8(int v)
    {
        return new String(new char[]{(char)(v & 0xff)});
    }
    
	private static String writeInt16(int v)
    {
        StringBuffer ret = new StringBuffer(new String(new char[]{(char)((v & 0xff00) >> 8)}));
        ret.append((char)((v & 0x00ff) >> 0));
        return ret.toString();
    }
	
	private static String writeInt20(int v)
    {
		StringBuffer ret = new StringBuffer(new String(new char[]{(char)((0xF0000 & v) >> 16)}));
		ret.append((char)((0xFF00 & v) >> 8));
		ret.append((char)((v & 0xFF) >> 0));
		return ret.toString();
	}
	
	private static String writeInt31(int v)
    {
		StringBuffer ret = new StringBuffer(new String(new char[]{(char)((0x7F000000 & v) >> 24)}));
		ret.append((char)((0xFF0000 & v) >> 16));
		ret.append((char)((0xFF00 & v) >> 8));
		ret.append((char)((v & 0xFF) >> 0));
		return ret.toString();
    }
    
	private static String writeInt24(int v)
    {
		StringBuffer ret = new StringBuffer(new String(new char[]{(char)((v & 0xff0000) >> 16)}));
		ret.append((char)((v & 0x00ff00) >> 8));
		ret.append((char)((v & 0x0000ff) >> 0));
		return ret.toString();
	}	

	 private static String getInt24(int length)
	    {
	        StringBuilder ret = new StringBuilder("");
	        ret.append((char)(((length & 0xf0000) >> 16)));
	        ret.append((char)(((length & 0xff00) >> 8)));
	        ret.append((char)((length & 0xff)));
	        return ret.toString();
	    }

	 private static int packHex(int n)
	    {
	        switch (n) {
	            case 48:
	            case 49:
	            case 50:
	            case 51:
	            case 52:
	            case 53:
	            case 54:
	            case 55:
	            case 56:
	            case 57:
	                return n - 48;
	            case 65:
	            case 66:
	            case 67:
	            case 68:
	            case 69:
	            case 70:
	                return 10 + (n - 65);
	            default:
	                return -1;
	        }
	    }
	    
	 	private static int packNibble(int n)
	    {
	        switch (n) {
	            case 45:
	            case 46:
	                return 10 + (n - 45);
	            case 48:
	            case 49:
	            case 50:
	            case 51:
	            case 52:
	            case 53:
	            case 54:
	            case 55:
	            case 56:
	            case 57:
	                return n - 48;
	            default:
	                return -1;
	        }
	    }

	 	private static int packByte(int v, int n2)
	    {
	        switch (v) {
	            case 251:
	                return packHex(n2);
	            case 255:
	                return packNibble(n2);
	            default:
	                return -1;
	        }
	    }

	 	private static int parseInt24(String data)
	    {
	        int ret = Util.ord(data.substring(0, 1)) << 16;
	        ret |= Util.ord(data.substring(1, 2)) << 8;
	        ret |= Util.ord(data.substring(2, 3)) << 0;
	        return ret;
	    }
	 	
	 	
}
