package it.pdm.nodeshotmobile.managers;


import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.entities.POI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ListManager {
    
    private String[] from={"id","name"}; //dai valori contenuti in queste chiavi
    private int[] to={R.id.id,R.id.name};//agli id delle view
	
    private SimpleAdapter nodesAdapter;
    private ArrayList<HashMap<String, Object>> items;
    private ArrayList<HashMap<String, Object>> items_result;

    private ListView listaView;
    private Context generic;
    private int layout;
    
    public ListManager(Context generic, ListView l,String[] from,int[] to,int layout) {
    	this.generic=generic;
    	listaView=l;
    	items=new ArrayList<HashMap<String, Object>>();
    	items_result=new ArrayList<HashMap<String, Object>>();
    	this.from=from.clone();
    	this.to=to.clone();
    	this.layout=layout;
    	refresh();
    	
    }
    
    public ArrayList<HashMap<String, Object>> getMap(){
    		return items_result;
    	
    }
    
    public void append(ArrayList<HashMap<String, Object>> arraymap,Integer id,String name){
    	HashMap<String, Object> map=new HashMap<String, Object>();
    	map.put("id", id);
    	map.put("name", name);
    	arraymap.add(0,map);
    }
    
    public void append(Integer id,String name){
    	append(items,id,name);
    }
    
    
    
    public void load(ArrayList<HashMap<String, Object>> arraymap,ArrayList list){
    	arraymap.clear();
    	
		for(int i=0; i<list.size();i++){

    		String name=((POI)list.get(i)).getName();
    		Integer id=((POI)list.get(i)).getId();
    		
    		append(arraymap,id,name);
		}
    }
    
    public void sortByName(ArrayList<HashMap<String, Object>> obj){
    	Comparator<HashMap<String, Object>> comparator = new Comparator<HashMap<String, Object>>() {

			@Override
			public int compare(HashMap<String, Object> obj1,
					HashMap<String, Object> obj2) {
   	         return ((String)obj1.get("name")).compareToIgnoreCase((String)obj2.get("name"));
			}
    	   };
    	   Collections.sort(obj,comparator);
    }
    
    public void sortByNameReverse(ArrayList<HashMap<String, Object>> obj){
    	Comparator<HashMap<String, Object>> comparator = new Comparator<HashMap<String, Object>>() {

			@Override
			public int compare(HashMap<String, Object> obj1,
					HashMap<String, Object> obj2) {
   	         return ((String)obj2.get("name")).compareToIgnoreCase((String)obj1.get("name"));
			}
    	   };
    	   Collections.sort(obj,comparator);
    }
    
    public void sortById(ArrayList<HashMap<String, Object>> obj){
    	Comparator<HashMap<String, Object>> comparator = new Comparator<HashMap<String, Object>>() {

			@Override
			public int compare(HashMap<String, Object> obj1,
					HashMap<String, Object> obj2) {
				int difference=0;
				difference= (Integer)obj2.get("id")-(Integer)obj1.get("id");
				
				return difference;
			}
    	   };
    	   Collections.sort(obj,comparator);
    }
    
    
    public void load(ArrayList list){
    	load(items,list);
    	sortByName(items);
    	load(items_result,list);
    	sortByName(items_result);
    	refresh();
    }
    
    public void loadNews(ArrayList list){
    	load(items,list);
    	sortById(items);
    	load(items_result,list);
    	sortById(items_result);
    	refresh();
    }
    
    
    public void refresh(ArrayList<HashMap<String, Object>> items){
    	
    	nodesAdapter=new SimpleAdapter(generic,items,layout,from,to);
    	listaView.setAdapter(nodesAdapter);
    	listaView.setItemsCanFocus(false);

    	
    }
    
    public void refresh(){
    	refresh(this.items);
    }
    
    public void filter(String s){

    	sortByNameReverse(items);
    	Iterator<HashMap<String, Object>> handle=items.iterator();
    	items_result.clear();
    	do{
    		
    		HashMap<String, Object> item=handle.next();
    		
    		
    		String name=(String)item.get("name");
    		
    		boolean match=Pattern.compile(Pattern.quote(s), Pattern.CASE_INSENSITIVE).matcher(name).find();
    		
    		if(match){
    			append(items_result, (Integer)item.get("id"), (String)item.get("name"));
    		}
    		
    	}while(handle.hasNext());
    	
    	
    	refresh(items_result);
    	
    }
    
    public void delete(Integer id){
    	
    	items.remove(id.intValue());
    	
    	refresh();
    	
    }
    
    public void deleteAll(){
    	items.clear();
    	
    	refresh();
    	
    }
	
}
