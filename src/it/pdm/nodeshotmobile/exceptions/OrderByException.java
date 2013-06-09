package it.pdm.nodeshotmobile.exceptions;

public class OrderByException extends Exception {

private static final long serialVersionUID = 22L;
	
	private String message;
	
	public void ConnectionException() {
		message="The value for the condition 'order-by' inserted is not correct.";
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
}
