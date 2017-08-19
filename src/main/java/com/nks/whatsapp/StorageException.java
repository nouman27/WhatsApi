package com.nks.whatsapp;

public class StorageException extends Exception{

	private static final long serialVersionUID = -3037421444211827941L;

	public StorageException() {
	        super();
	    }

	    public StorageException(String message) {
	        super(message);
	    }
	    public StorageException(String message, Throwable cause) {
	        super(message, cause);
	    }
	    public StorageException(Throwable cause) {
	        super(cause);
	    }

	   protected StorageException(String message, Throwable cause,
	                        boolean enableSuppression,
	                        boolean writableStackTrace) {
	        super(message, cause, enableSuppression, writableStackTrace);
	    }

}
