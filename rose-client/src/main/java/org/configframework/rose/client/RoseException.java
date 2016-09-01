package org.configframework.rose.client;

public class RoseException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2951641555731793104L;

	public RoseException() {
        super();
    }

    public RoseException(String message) {
        super(message);
    }

    public RoseException(Throwable t) {
        super(t);
    }

    public RoseException(String message, Throwable cause) {
        super(message, cause);
    }

}
