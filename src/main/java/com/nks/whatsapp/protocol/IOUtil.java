package com.nks.whatsapp.protocol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;

public class IOUtil {
	public static boolean isListTag(int token)
	{
	    return token == 248 || token == 0 || token == 249;
	}

	public  static int peekInt8(String input)
	{
	return peekInt8(input,0);	 
	}

	public static int peekInt8(String input,int offset)
	{
	if(input.length()>=(offset+1))
		return Util.ord(input.substring(offset,offset+1));
	return 0;
	}

	public static int peekInt16(String input)
	{
	return peekInt16(input,0);
	}

	public static int peekInt16(String input,int offset)
	{
	    if (input.length() >= (2 + offset)) {
	        return peekInt8(input,offset) << 8 | peekInt8(input,offset+1) << 0;
	    }
	    return 0;
	}

	public static int peekInt20(String input)
	{
	return peekInt20(input,0);
	}

	public static int peekInt20(String input,int offset)
	{
	    if (input.length() >= (3 + offset)) {
	        return peekInt8(input,offset) << 16 | peekInt8(input,offset+1) << 8 | peekInt8(input,offset+2);
	    }
	    return 0;
	}

	public static int peekInt24(String input)
	{
	return peekInt24(input,0);
	}

	public static int peekInt24(String input,int offset)
	{
	    if (input.length() >= (3 + offset)) {
	        return peekInt8(input,offset) << 16 | peekInt8(input,offset+1) << 8 | peekInt8(input,offset+2) << 0;
	    }
	    return 0;
	}


	public static int peekInt31(String input)
	{
	return peekInt31(input,0);
	}

	public static int peekInt31(String input,int offset)
	{
	    if (input.length() >= (4 + offset)) {
	        return peekInt8(input,offset) << 24 | peekInt8(input,offset+1) << 16 | peekInt8(input,offset+2) << 8 | peekInt8(input,offset+3);
	    }
	    return 0;
	}

	public static int readHeader(String input)
	{
	return readHeader(input,0);
	}

	public static int readHeader(String input,int offset)
	{
	    if (input.length() >= (3 + offset)) {
	        return peekInt8(input,offset) + ((peekInt8(input,offset+1) << 16) + (peekInt8(input,offset+2) << 8));
	    }
	    return 0;
	}

	public static int unpackHex(int n1) 
	{
	    switch (n1) {
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
	            return n1 + 48;
	        case 10:
	        case 11:
	        case 12:
	        case 13:
	        case 14:
	        case 15:
	            return 65 + (n1 - 10);
	        default:
	            throw new RuntimeException("bad hex "+n1);
	    }
	}

	public static int unpackNibble(int n1) 
	{
	    switch (n1) {
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
	            return n1 + 48;
	        case 10:
	        case 11:
	            return 45 + (n1 - 10);
	        default:
	            throw new RuntimeException("bad nibble "+n1);
	    }
	}

	public static int unpackByte(int n1, int n2) 
	{
	    switch (n1) {
	        case 251:
	            return unpackHex(n2);
	        case 255:
	            return unpackNibble(n2);
	        default:
	            throw new RuntimeException("bad packed type " + n1);
	    }
	}

	public static String getTokenDouble(int n1, int n2) 
	{
	    int pos = n2 + n1 * 256;
	    String result = TokenMap.GetToken(pos);
	    if (result==null) {
	        throw new RuntimeException("BinTreeNodeReader->getToken: Invalid token "+pos);
	    }
	    return result;
	}

	public static void socketWrite(Socket socket,String data,int length) throws IOException{
			socket.getOutputStream().write(Util.stringToByte(data),0,length);
	
	}
	
	public static String socketRead(Socket socket,int length) throws IOException {
			byte[] bt=new byte[length];
			int read=socket.getInputStream().read(bt);
			if(read==-1)
				return new String("");
			return Util.byteToString(bt);
		
	}
	
	public static int file_put_contents(String fileName,String content){
		File file=new File(fileName);
		if(!file.exists())
			try{if(!file.createNewFile())return 0;}catch(IOException ioEx){return 0;}
		BufferedWriter buffered=null;
		try{
			buffered=new BufferedWriter(new FileWriter(file)); 
			buffered.write(content);
			return Util.stringToByte(content).length;
		}catch(IOException ioEx){return 0;}
		finally{try{buffered.close();}catch(Exception ex){}}
	}
	

}
