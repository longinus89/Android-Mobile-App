package it.pdm.nodeshotmobile.managers;

import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.entities.Group;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.entities.News;
import it.pdm.nodeshotmobile.exceptions.DBOpenException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JsonManager {
	
	private JSONObject jObject;
	private FileManager fmng;
	private ArrayList<MapPoint> parsedDataMapPoints;
	private ArrayList<Group> parsedDataMapGroups;
	private ArrayList<News> parsedDataNews;
	private Context current;
	private ArrayList<String> optvalues;
	
	public JsonManager(Context current) {
		parsedDataMapPoints=new ArrayList<MapPoint>();
		parsedDataMapGroups=new ArrayList<Group>();
		parsedDataNews=new ArrayList<News>();
		fmng=new FileManager();
		this.current=current;
	}
	
	/**
     * A debug method that write on LogCat a vDebug given string
     * @param		debugString the string to be written
     */
	static void vDebug(String debugString){         //metodi di convenienza
        Log.v("DomParsing", debugString+"\n");
	}
	
	/**
     * A debug method that write on LogCat an eDebug given string
     * @param		debugString the string to be written
     */
    static void eDebug(String debugString){
            Log.e("DomParsing", debugString+"\n");
    }
	
	public ArrayList<MapPoint> getParsedDataMapPoints() {  //metodo di accesso alla struttura dati
        return parsedDataMapPoints;
	}
	
	public ArrayList<Group> getParsedDataGroups() {  //metodo di accesso alla struttura dati
        return parsedDataMapGroups;
	}
	
	public ArrayList<News> getParsedDataNews() {  //metodo di accesso alla struttura dati
        return parsedDataNews;
	}
	
	public void clearParsedDataMapPoints() { 
        parsedDataMapPoints.clear();
	}
	
	/**
     * This method obtains all nodes from json file using json parsing JSON Parser. 
     * @param	jString string who contains a JSON Document
     */
	public void parseJsonNodes(String jString){
		
			JSONArray mapObject;
		
			try {
				optvalues = visitTreeJson(jString);
				mapObject = jObject.getJSONArray("objects");
				parseTreeJsonNodes(mapObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				eDebug(e.toString());
			}
		
	}
	
	/**
     * This method obtains all nodes from json file using json parsing JSON Parser. 
     * @param	jString string who contains a JSON Document
	 * @throws JSONException 
     */
	public void parseJsonGroups(String jString) throws JSONException{
		
		
			jObject = new JSONObject(jString);	
			JSONArray mapObject;
			
			try {
				//Log.v("root", jObject.toString(2));
				mapObject = jObject.getJSONArray("objects");
				parseTreeJsonGroups(mapObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				eDebug(e.toString());
			}
		
	}
	
	public ArrayList<String> visitTreeJson(String jString) throws JSONException{
		
		jObject = new JSONObject(jString);	
		JSONArray jobj = jObject.getJSONArray("objects");
		JSONObject jpoi = jobj.getJSONObject(0);
		
		//recupero tutti i nomi dei campi(a loro volta sono JSONObject oppure JSONArray) dell'oggetto 
		//radice dell'albero
		Iterator<String> labels = jpoi.keys();
	    ArrayList<String> names = new ArrayList<String>();
	    for(int i=0;i<jpoi.length();i++){		    
		    names.add((String)labels.next());
	    }
	    
	    String[] values= current.getResources().getStringArray(R.array.type_node_json_model);
	    
	    for(int i=0;i<values.length;i++){		    
		    names.remove(values[i]);
	    }
	    
	    return names;
		
		
	}
	
	public void parseTreeJsonGroups(JSONArray obj) throws JSONException{
		//recupero tutti i nomi dei campi(a loro volta sono JSONObject oppure JSONArray) dell'oggetto 
		//radice dell'albero
		
		Log.v("Grandezza lista gruppi", ""+obj.length());
		
	    for (int i = 0; i < obj.length(); ++i) {
	        JSONObject poi = obj.getJSONObject(i);
	        Group gr = new Group();
	        
	        String[] values= current.getResources().getStringArray(R.array.type_group_json_model);
    		
	    	
    		try{
    		String url=poi.getString(values[1]);
    	   	gr.setUri(url);
    	   	
	        //String organization=poi.getString(values[0]);
	        String name=poi.getString(values[2]);
    		gr.setName(name);
    		
    	   	parsedDataMapGroups.add(gr);
    		}catch(JSONException e){
    			//non fa nulla
    		}
    	   	
	    }
	    //Log.v("Grandezza lista gruppi DOPO", ""+parsedDataMapGroups.size());
	    //Log.v("list of groups", parsedDataMapGroups.toString());

	}
	
	public void parseTreeJsonNodes(JSONArray obj) throws JSONException{
		//recupero tutti i nomi dei campi(a loro volta sono JSONObject oppure JSONArray) dell'oggetto 
		//radice dell'albero
	    
	    for (int i = 0; i < obj.length(); ++i) {
	        JSONObject poi = obj.getJSONObject(i);
	        MapPoint point = new MapPoint();
	        
	        String[] values= current.getResources().getStringArray(R.array.type_node_json_model);
    		
    		String status=poi.getString(values[0]);
    	   	
    		//Log.v("Status", status);
    		
    		if(status.equals("0")){
    			continue;
    		}
    		
    		point.setType(status);
    	
    	   	String name=poi.getString(values[1]);
    	   	point.setName(name);
    	
    	   	double lat=poi.getDouble(values[2]);
    	   	point.setLatitude((int)(lat*1000000));
    	
    	   	double lng=poi.getDouble(values[3]);
    	   	point.setLongitude((int)(lng*1000000));
    	   	
    	   	try{
    	   		double alt=poi.getDouble(values[4]);
    	   	   	point.setAltitude((int)(alt));
    	   	}catch(JSONException e){
    	   	   	point.setAltitude(60);
    	   	}
    	   	for(int j=0;j<optvalues.size();j++){
    	   		String label = optvalues.get(j);
    	   		point.addOptValue(label, poi.getString(label));
    	   	}
    	
    	   	parsedDataMapPoints.add(point);
    	   	
	    }

	}
	

	
	
	/**
     * This method obtains all news from json file using json parsing JSON Parser. 
     * @param	jString string who contains a JSON Document
     */
	public void parseJsonNews(String jString){
		
		JSONArray mapObject;
		int num=0;
		
		try {
			
			String[] values0= current.getResources().getStringArray(R.array.type_news_json_values);
			String[] values1= current.getResources().getStringArray(R.array.type_news_json_model);
			
			jObject = new JSONObject(jString);	
			num=jObject.getInt(values0[0]);
			mapObject = jObject.getJSONArray(values0[1]);
			
			for (int i = num; i >= 0; i--) {
			    JSONObject rec = mapObject.getJSONObject(i);
			    News mess=new News();
			    
			    int id = rec.getInt(values1[0]);
			    String title = rec.getString(values1[1]);
			    String content = rec.getString(values1[2]);
			
			    mess.setId(id);
			    mess.setTitle(title);
			    mess.setContent(content);
			    
			    parsedDataNews.add(mess);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			eDebug(e.toString());
		}
		
		

	}
	
	//created mainly for mixare plugin
	/**
     * This method is used to creare a JSON Document that will be used by Mixare plug-in (Augmented Reality).
     * @param		map an ArrayList that contains MapPoint nodes
     * @return		a result String
     */
	public String createJson(ArrayList<MapPoint> map){
		
		String comma=",";
		int count=map.size();
		StringBuilder builder = new StringBuilder();

		builder.append("{" +
				"\"status\": \"OK\"," +
				"\"num_results\": "+count+"," +
				"\"results\": [");
		for(MapPoint point : map){
			count--;
			builder.append("{" +
					"\"id\":\""+point.getId()+"\","+
					"\"lat\":\""+((double)point.getLatitude())/1E6+"\","+
					"\"lng\":\""+((double)point.getLongitude())/1E6+"\","+
					"\"elevation\":\""+0+"\","+
					"\"title\":\""+point.getName()+"\","+
					"\"distance\":\"\","+
					"\"has_detail_page\":\""+0+"\","+
					"\"webpage\":\"\"}");
			if(count>0){
				builder.append(comma);
			}
		}
		builder.append("]}");
		
		String result=builder.toString();
		
		/*try {
			//fmng.writeFile("/sdcard/download/nma_mixare.json", result);
		} catch (FileNotFoundException e) {
			eDebug(e.toString());
		}*/
		
		
		
		vDebug(result);
		return result; 
	}
	
	/**
     * this method is used to fill an ArrayList of MapPoint used by the main activity with the nodes parsed from the given
     * JSON Document.
     * @param		json_list a String that contains a JSON well-formatted Document
     */
	public void getNodes(String json_list){
    	   clearParsedDataMapPoints();
		   parseJsonNodes(json_list);//usiamo il parser per scandire il contenuto fornito
	       MapPoint.points=getParsedDataMapPoints(); //preleva la lista dei nodi parsati e popola quella della activity.
	}
	
	/**
     * this method is used to fill an ArrayList of Groups used by the main activity, with the groups parsed from the given
     * JSON Document.
     * @param		json_list a String that contains a JSON well-formatted Document
	 * @throws JSONException 
     */
	public void getGroups(String json_list) throws JSONException{
	       parseJsonGroups(json_list);//usiamo il parser per scandire il contenuto fornito
	       Group.groups=getParsedDataGroups(); //preleva la lista dei nodi parsati e popola quella della activity.
	}
	
	/**
     * this method is used to fill an ArrayList of News used by the main activity, with the news parsed from the given
     * JSON Document.
     * @param		json_list a String that contains a JSON well-formatted Document
     */
	public void getNews(String json_list){
	       parseJsonNews(json_list);//usiamo il parser per scandire il contenuto fornito
	       News.news=getParsedDataNews(); //preleva la lista dei nodi parsati e popola quella della activity.
	}
	
}
