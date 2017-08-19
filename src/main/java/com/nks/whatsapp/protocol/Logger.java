package com.nks.whatsapp.protocol;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;


public class Logger {

	private static SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	java.util.logging.Logger logger=java.util.logging.Logger.getLogger("default");
	public Logger(String logfile){
	File file=new File(logfile);
	if(!file.exists())
		try{if(!file.createNewFile())
			throw new RuntimeException("Log file "+logfile+" cannot be created");
		}catch(IOException ioEx){throw new RuntimeException("Log file "+logfile+" cannot be created");}
	
	if(!file.canWrite())
		throw new RuntimeException("Log file "+logfile+" is not writeable");
	try{logger.addHandler(new FileHandler(logfile));}catch(Exception ex){throw new RuntimeException("Log file "+logfile+" cannot be created because "+ex.getMessage());}
	}

	public void log(String level,String message)
	{
		log(level,message,new HashMap<String,String>());
	}
	public void log(String level,String message, Map<String,String> context)
    {
        String logline = "["+dateFormatter.format(new Date())+"] "+"["+level.toUpperCase()+"]: "+interpolate(message, context)+"\n";
        logger.info(logline);
    }

	public String interpolate(String message)
	{
		return interpolate(message,new HashMap<String,String>());
	}
	public String interpolate(String message, Map<String,String> context)
	   {
	    return message;  
		//TODO implement
	    /* String[] replace = [context.size()];
	       int index=0;
	       message.re
	       for (String key:context.keySet()) {
	           {replace['{'.$key.'}'] = $val;index++;}
	       }
	       return strtr($message, $replace);*/
	   }

}
