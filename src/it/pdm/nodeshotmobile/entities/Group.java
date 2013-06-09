package it.pdm.nodeshotmobile.entities;

import java.util.ArrayList;

public class Group{
	    private String dateC;
	    private Integer id;
		private String name; 
		private String uri;
		static private Integer staticId=0;
		
		static public ArrayList<Group> groups; //lista dei punti presi tramite Jsonparser
		
	    public Group() {
			super();
	    	dateC="";
	    	name="";
	    	uri="";
	    	staticId++;
	    	id=staticId;
			
		}
	    
	    public Integer getId() {
	        return id;
	    }

	    public void setId(Integer id) {
	        this.id = id;
	    }

	    public String getName() {
            return name;
	    }

	    public void setName(String name) {
	    	this.name = name;
	    }
	    
	    public String getUri() {
            return uri;
	    }

	    public void setUri(String uri) {
	    	this.uri = uri;
	    }
	    
	    @Override
	    public String toString() {
	    	String svalue;
	    	svalue = "Group [id=" + getId() + "\n, name="+ getName() + "\n, uri=" + getUri() +"\n]";
	    	
	    	return svalue;
	    }
	

	
}
