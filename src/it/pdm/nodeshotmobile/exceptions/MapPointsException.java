package it.pdm.nodeshotmobile.exceptions;

public class MapPointsException extends NullPointerException {
	private static final long serialVersionUID = 22L;
	
	private String message;
	
	public MapPointsException() {
		message="Null Pointer arraylist in input";
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}

}
