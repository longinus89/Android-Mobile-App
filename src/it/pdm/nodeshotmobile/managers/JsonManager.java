package it.pdm.nodeshotmobile.managers;

import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.entities.News;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class JsonManager {
	
	private JSONObject jObject;
	private FileManager fmng;
	private ArrayList<MapPoint> parsedDataMapPoints;
	private ArrayList<News> parsedDataNews;
	private Context current;
	
	public JsonManager(Context current) {
		parsedDataMapPoints=new ArrayList<MapPoint>();
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
	
	public ArrayList<News> getParsedDataNews() {  //metodo di accesso alla struttura dati
        return parsedDataNews;
	}
	
	/**
     * This method obtains all nodes from json file using json parsing JSON Parser. 
     * @param	jString string who contains a JSON Document
     */
	public void parseJsonNodes(String jString){
			JSONObject mapObject;
			try {
				
				String[] values= current.getResources().getStringArray(R.array.type_node_json_values);
				
				jObject = new JSONObject(jString);	
				mapObject = jObject.getJSONObject(values[0]);
				parseTreeJsonNodes(mapObject);
				
				jObject = new JSONObject(jString);
				mapObject = jObject.getJSONObject(values[1]);
				parseTreeJsonNodes(mapObject);
				
				jObject = new JSONObject(jString);
				mapObject = jObject.getJSONObject(values[2]);
				parseTreeJsonNodes(mapObject);
				
				
			
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				eDebug(e.toString());
			}
		
	}
	
	public void parseTreeJsonNodes(JSONObject obj){
		//recupero tutti i nomi dei campi(a loro volta sono JSONObject oppure JSONArray) dell'oggetto 
		//radice dell'albero
	    Iterator<String> myIter = obj.keys();
	    List<String> names = new ArrayList<String>();

	    while(myIter.hasNext()){
	        names.add(myIter.next());
	    }
	    
	    for(int i=0;i<names.size();i++){
	    	JSONObject entryObject = obj.optJSONObject(names.get(i));
	    	MapPoint point=new MapPoint();
	    	
	    	try{
	    		
	    		String[] values= current.getResources().getStringArray(R.array.type_node_json_model);
	    		
	    		String status=entryObject.getString(values[0]);
	    	   	point.setStatus(status);
	    	
	    	   	int id=entryObject.getInt(values[1]);
	    	   	point.setId(id);
	    	
	    	   	String name=entryObject.getString(values[2]);
	    	   	point.setName(name);
	    	
	    	   	String slug=entryObject.getString(values[3]);
	    	   	point.setSlug(slug); 
	    	
	    	   	String jslug=entryObject.getString(values[4]);
	    	   	point.setJslug(jslug); 
	    	
	    	   	double lat=entryObject.getDouble(values[5]);
	    	   	point.setLatitude((int)(lat*1000000));
	    	
	    	   	double lng=entryObject.getDouble(values[6]);
	    	   	point.setLongitude((int)(lng*1000000));
	    	
	    	   	parsedDataMapPoints.add(point);
	    	
	    	}catch(NullPointerException e){} catch (JSONException e) {
				Log.e("JsonManager", e.toString());
			}
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
			
			for (int i = 0; i < num; ++i) {
			    JSONObject rec = mapObject.getJSONObject(i);
			    News mess=new News();
			    
			    int id = rec.getInt(values1[0]);
			    String title = rec.getString(values1[1]);
			    String content = rec.getString(values1[2]);
			
			    mess.setId(id);
			    mess.setName(title);
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
     * This method is used to creare a JSON Document that will be used by Mixare plug-in (AUgmented Reality).
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
		
		try {
			fmng.writeFile("/sdcard/download/nma_mixare.json", result);
		} catch (FileNotFoundException e) {
			eDebug(e.toString());
		}
		
		
		
		vDebug(result);
		return result; 
	}
	
	/**
     * this method is used to fill an ArrayList of MapPoint used by the main activity with the nodes parsed from the given
     * JSON Document.
     * @param		json_list a String that contains a JSON well-formatted Document
     */
	public void getNodes(String json_list){
	       parseJsonNodes(json_list);//usiamo il parser per scandire il contenuto fornito
	       MapPoint.points=getParsedDataMapPoints(); //preleva la lista dei nodi parsati e popola quella della activity.
	}
	
	public void getNews(String json_list){
	       parseJsonNews(json_list);//usiamo il parser per scandire il contenuto fornito
	       News.news=getParsedDataNews(); //preleva la lista dei nodi parsati e popola quella della activity.
	}
	
}
