package it.pdm.nodeshotmobile.entities;

import java.util.ArrayList;

public class MapPoint extends POI{
    private String status;
    private String slug;
	private Integer lat; 
	private Integer lng; 
	private String jslug;
    
	static public ArrayList<MapPoint> points; //lista dei punti presi tramite Jsonparser
	
    public MapPoint(){
    	super();
    	status="";
    	slug= ""; 
    	lat= 0; 
    	lng= 0; 
    	jslug= "";
		
	}
    

    public Integer getLatitude() {
            return lat;
    }

    public void setLatitude(Integer lat) {
            this.lat = lat;
    }

    public Integer getLongitude() {
            return lng;
    }

    public void setLongitude(Integer lon) {
            this.lng = lon;
    }

    public String getStatus() {
            return status;
    }

    public void setStatus(String stat) {
            this.status = stat;
    }

    public String getSlug() {
            return slug;
    }

    public void setSlug(String slug) {
    		this.slug=slug;
    }
    
    public String getJslug() {
        return jslug;
    }

    public void setJslug(String jslug) {
    	this.jslug=jslug;
    }

    @Override
    public String toString() {
            return "MapPoint [id=" + getId() +"\n, name=" + getName() + "\n, latitude=" + getLatitude() + "\n, longitude=" + getLongitude() +"\n, " +
            		"status="+ getStatus() + "\n, slug=" + getSlug()+ "\n, jslug=" + getJslug()+"]";
    }
}
