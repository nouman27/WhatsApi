package com.nks.whatsapp.protocol;

import java.util.ArrayList;
import java.util.List;



public class BinTreeNodeReader {

private String input;
private KeyStream key;

public BinTreeNodeReader()
{
this(null);
}

public BinTreeNodeReader(String input)
{
this.input=input;	
}


public void resetKey()
{
    key = null;
}
public void setKey(KeyStream key)
{
    this.key = key;
}

public static void main(String[] args)
	{
	System.out.println(Util.hex2bin("48656c6c6f20576f726c6421"));
	}
	
	

private int readInt8()
{
int ret=IOUtil.peekInt8(input);
if(input.length()>0)
	input=input.substring(1);
return ret;
}

private int readInt16()
{
int ret=IOUtil.peekInt16(input);
if(ret>0)
	input=input.substring(2);
return ret;
}

private int readInt20()
{
int ret=IOUtil.peekInt20(input);
if(input.length()>2)
	input=input.substring(3);
return ret;
}

private int readInt24()
{
int ret=IOUtil.peekInt24(input);
if(input.length()>2)
	input=input.substring(3);
return ret;
}

private int readInt31()
{
int ret=IOUtil.peekInt31(input);
if(input.length()>3)
	input=input.substring(4);
return ret;
}

private String fillArray(int length)
{
    if (input.length() >= length) {
    	String ret=input.substring(0, length);
    	input=input.substring(length);
    	return ret;
    }	
    return "";
}


private String getToken(int token) 
{
    String result = TokenMap.GetToken(token);
    if (result==null) {
        result=TokenMap.GetToken(readInt8());
    	if(result==null)
    		throw new RuntimeException("BinTreeNodeReader->getToken: Invalid token/length in getToken "+token);
    }
    return result;
}

private String readPacked8(int n) 
{
    int len = readInt8();
    int remove = 0;
    if ((len & 0x80) != 0 && n == 251) {
        remove = 1;
    }
    len = len & 0x7F;
    String text = input.substring(0,len);
    input = input.substring(len);
    String data = Util.bin2hex(text);
    len = data.length();
    StringBuffer out = new StringBuffer();
    for (int i = 0; i < len; ++i) {
        int val = Util.ord(Util.hex2bin("0"+data.charAt(i)));
        if (i == (len - 1) && val > 11 && n != 251) {
            continue;
        }
        out.append((char)IOUtil.unpackByte(n, val));
    }
    String result=out.toString();
    return result.substring(0, result.length() - remove);
}

private String readString(int token) 
{
    String ret="";
    if (token == -1) {
        throw new RuntimeException("BinTreeNodeReader->readString: Invalid -1 token in readString "+token);
    }
    if ((token > 2) && (token < 236)) {
        return getToken(token);
    } else {
        switch (token) {
            case 0:
                ret = "";
                break;
            case 236:
            case 237:
            case 238:
            case 239:return IOUtil.getTokenDouble(token - 236, readInt8());
          case 250: {
                String readString = readString(readInt8());
                String s = readString(readInt8());
                if (readString != null && s != null) {
                    return readString+'@'+s;
                }
                if (s == null) {
                    return "";
                }
                break;
            }
            case 251:
            case 255:return readPacked8(token); //maybe utf8 decode
            case 252:return fillArray(readInt8()); //maybe ut8 decode
            case 253:return fillArray(readInt20()); //maybe ut8 decode
            case 254:return fillArray(readInt31()); //maybe ut8 decode
            default:throw new RuntimeException("readString couldn't match token "+token);
        }
       return ret;
    }
}


private Attributes<String> readAttributes(int size) 
{
	Attributes<String> attributes=new Attributes<String>();
	
	int attribCount = (size - 2 + size % 2) / 2;
    for (int i = 0; i < attribCount; i++) {
        String key = readString(readInt8());
        String value=readString(readInt8());
        attributes.put(key, value);
   }
    return attributes;
}

/*private String readNibble() 
{
	int byte_=readInt8();
    boolean ignoreLastNibble = (byte_ & 0x80)>0?true:false;
    int size = (byte_ & 0x7f);
    int nrOfNibbles = size * 2 - (ignoreLastNibble?1:0);
    String data = fillArray(size);
    String ret = "";
    for (int i = 0; i < nrOfNibbles; i++) {
        char c = data.charAt((int)Math.floor(i/2));
        int ord = Util.ord(c);
        int shift = 4 * (1 - i % 2);
        int decimal = (ord & (15 << shift)) >> shift;
        switch (decimal) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                ret+=decimal;
                break;
            case 10:
            case 11:
                ret+= (char)(decimal - 10 + 45);
                break;
            default:
                throw new RuntimeException("Bad nibble: "+decimal);
        }
    }
    return ret;
}
*/
private int readListSize(int token)
{
    if (token == 0) {
        return 0;
    }
    if (token == 0xf8) {
        return readInt8();
    } else if (token == 0xf9) {
        return readInt16();
    }
    throw new RuntimeException("BinTreeNodeReader->readListSize: invalid list size in readListSize: token "+token);
}

private ProtocolNode[] readList(int token) 
{
   List<ProtocolNode> nodes=new ArrayList<ProtocolNode>();
    int count=readListSize(token);
	for (int i = 0; i < count; i++) {
        nodes.add(nextTreeInternal());
    }
    return nodes.toArray(new ProtocolNode[0]);
}


private ProtocolNode nextTreeInternal() {
 
	int size = readListSize(readInt8());
    if (size == 0) {
        throw new RuntimeException("nextTree sees 0 list or null tag");
    }
   
    int token = readInt8();
    if (token == 1) {
        token = readInt8();
    }
    if (token == 2) {
        return null;
    }
    String tag = readString(token);
    Attributes<String> attributes = readAttributes(size);
    if (size % 2 == 1) {
        return new ProtocolNode(tag, attributes, null, "");
    }
   
    int read2 = readInt8();
    if (IOUtil.isListTag(read2)) {
        return new ProtocolNode(tag, attributes, readList(read2), "");
    }
    switch (read2) {
        case 252:return new ProtocolNode(tag, attributes, null, fillArray(readInt8()));
        case 253:return new ProtocolNode(tag, attributes, null, fillArray(readInt20()));
        case 254:return new ProtocolNode(tag, attributes, null, fillArray(readInt31()));
        case 255:
        case 251:return new ProtocolNode(tag, attributes, null, readPacked8(read2));
        default:return new ProtocolNode(tag, attributes, null, readString(read2));
     }
}

public ProtocolNode nextTree() 
{
return nextTree(null);
}
public ProtocolNode nextTree(String in) {
    if (in != null) {
        this.input = in;
    }
    int firstByte = IOUtil.peekInt8(input);
    int stanzaFlag = (firstByte & 0xF0) >> 4; //ENCRYPTED
    
    int stanzaSize = IOUtil.peekInt16(input,1) | ((firstByte & 0x0F) << 16);
    if (stanzaSize > this.input.length()) {
        throw new RuntimeException("Incomplete message "+stanzaSize+" != "+input.length());
    }
    
    readInt24();
    
    if ((stanzaFlag & 8) > 0) { 
    if (key!=null) {
            int realSize = stanzaSize - 4;
            input = key.decodeMessage(input, realSize, 0, realSize); // . $remainingData;
            if ((stanzaFlag & 4) > 0) { 
            	input = Util.gzuncompress(input); // done
            }
        } else {
            throw new RuntimeException("Encountered encrypted message, missing key");
        }
    }
    if (stanzaSize > 0) {
        return nextTreeInternal();
    }
    else //TODO added for return complete
    	return null;
 }

}
