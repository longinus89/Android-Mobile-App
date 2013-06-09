package it.pdm.nodeshotmobile.exceptions;

public class ConnectionException extends Exception {

private static final long serialVersionUID = 20L;
	
	private String message;
	
public ConnectionException() {
		message="Sorry, this application needs Internet to work properly. Active Internet and try again.";
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}
	
}
