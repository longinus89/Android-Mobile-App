package it.pdm.nodeshotmobile.exceptions;

public class AcceptException extends Exception {

private static final long serialVersionUID = 25L;
	
	private String message;
	
	public void ConnectionException() {
		message="The value for the condition 'Accept' inserted is not correct.";
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
}
