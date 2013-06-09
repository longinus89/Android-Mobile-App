package it.pdm.nodeshotmobile.entities;

import it.pdm.nodeshotmobile.NodeShotActivity;
import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.SettingsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;

public class MapPoint extends POI{
    private String type;
    private Integer id_group;
	private Integer lat; 
	private Integer lng; 
	private Integer alt;
	private HashMap<String, String> optvalues;
    
	static public ArrayList<MapPoint> points; //lista dei punti presi tramite Jsonparser
	
    public MapPoint(){
    	super();
    	type="";
    	id_group=0;
    	lat= 0; 
    	lng= 0;
    	alt= 0;
    	optvalues = new HashMap<String, String>();
		
	}
    
    public Integer getGroupId() {
        return id_group;
    }

    public void setGroupId(Integer id_group) {
        this.id_group = id_group;
    }
    
    public String getType() {
            return type;
    }

    public void setType(String type) {
            this.type = type;
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
    
    public Integer getAltitude() {
        return alt;
    }

    public void setAltitude(Integer alt) {
        this.alt = alt;
    }
    
    public void addOptValue(String optvalue,String value) {
        this.optvalues.put(optvalue, value);
    }
    
    public HashMap<String, String> getOptValue() {
        return this.optvalues;
    }

    
    public String toString(Context cntx) {
    	
    	String[] translates = cntx.getResources().getStringArray(R.array.source_labels_group_ninux);
    	
    	String svalue;
    	
    	Integer itype = Integer.parseInt(getType());
    	String stype = translates[itype];
    	
    	svalue = "Name: "+getName()+"\nLat: "+(double)getLatitude()/SettingsActivity.FACTOR+"\n" +
					"Long: "+(double)getLongitude()/SettingsActivity.FACTOR+"\nType: "+stype+"\nAlt: "+(double)getAltitude()+"\n";
    	
    	Iterator<String> current = getOptValue().keySet().iterator();
    	
    	while (current.hasNext()) {
    		
            String name = current.next();
            if(!name.equals("details")){
            	if(name.equals("is_hotspot")){
            		if(getOptValue().get(name).equals("true")){
                    	svalue+="Properties"+": Hotspot\n";
            		}
            	}else{
            		String uppercase_name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                	svalue+=uppercase_name+": "+getOptValue().get(name)+"\n";
            	}
            }
            
        }
    	
    	return svalue;
    }
}
