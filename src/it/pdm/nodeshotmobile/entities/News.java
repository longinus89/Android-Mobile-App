package it.pdm.nodeshotmobile.entities;

import java.util.ArrayList;

public class News extends POI{
    private String content;
    private String date;
    
	static public ArrayList<News> news; //lista cdelle news prese tramite Jsonparser
	
    public News() {
    	super();
    	content = ""; 
    	date = "";     	
	}

    public String getContent() {
            return content;
    }

    public void setContent(String content) {
            this.content = content;
    }

    public String getDate() {
            return date;
    }

    public void setDate(String date) {
            this.date = date;
    }

    @Override
    public String toString() {
            return "News [id=" + getId() +"\n, title=" + getName() + "\n, content=" + getContent() + "\n, date=" + getDate() +"]";
    }
}
