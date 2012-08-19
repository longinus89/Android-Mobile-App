package it.pdm.nodeshotmobile.exceptions;

public class DBOpenException extends Throwable {
private static final long serialVersionUID = 21L;
	
	private String message;
	
	public DBOpenException() {
		message="Bad connection to the Database. Please try again.";
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}
	
}

