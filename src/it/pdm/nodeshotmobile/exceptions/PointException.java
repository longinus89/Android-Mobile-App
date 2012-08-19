package it.pdm.nodeshotmobile.exceptions;

public class PointException extends NullPointerException {
	private static final long serialVersionUID = 10L;
	
	private String message;
	
	public PointException() {
		message="L'oggetto MapPoint in input è NULL";
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}

}
