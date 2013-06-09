package it.pdm.nodeshotmobile.managers;

import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.SettingsActivity;
import it.pdm.nodeshotmobile.entities.DbHelper;
import it.pdm.nodeshotmobile.entities.Group;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.entities.POI;
import it.pdm.nodeshotmobile.exceptions.DBOpenException;
import it.pdm.nodeshotmobile.exceptions.MapPointsException;
import it.pdm.nodeshotmobile.exceptions.PointException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

/** A class used to perform operations and queries into the DB */
public class DbManager extends AsyncTask<Object,String,Object> {
    
    private ArrayList<MapPoint> map;
    private DbHelper dbHelp;
    private Context current;
    private ProgressDialog progressDialog;
    private Integer current_op;
       
    public DbManager(DbHelper helper,Context context) throws DBOpenException{
        this.map=null;
        this.progressDialog=null;
        this.dbHelp=helper;
        this.current=context;

    }
    
    
  //----------------GESTIONE DB ------------------------------------------//

    /** it checks the first argument passed to the function and performs a specific operation<br><br>
       0: getNodes();<br>
       1: getNodeByPosition(lat, lng, grade_lat, grade_lng);<br>
       2: getNodeByPosition(lat, lng);<br>
       3: getNodeByName(name);<br>
       4: insertNodes();<br>
       5: insertNode();<br>
       6: getNodeById(id);<br>
       7: removeNodes();<br>
       8: removeNode(id);<br>
       9: getNodesByType(type);<br>
       10: getGroups();<br>
    */

    @Override
    protected Object doInBackground(Object... params) throws MapPointsException,PointException {
        // TODO Auto-generated method stub
    	
    	Integer id_group;
    	Integer lat;
    	Integer lng;
    	Integer grade_lat;
    	Integer grade_lng;
    	
    	current_op=(Integer)params[0];
    	id_group = (Integer)params[1];
    	switch(current_op){

    	case 0:
    		
			
			try {
				ArrayList<MapPoint> m;
				m = getNodes(id_group);
				return m;
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
    	
    	case 1:
    		
    		lat=(Integer)params[2];
    		lng=(Integer)params[3];
    		grade_lat=(Integer)params[4];
    		grade_lng=(Integer)params[5];
			try {
				ArrayList<MapPoint> m;
				m = getNodeByPosition(id_group,lat, lng, grade_lat, grade_lng);
				return m;
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
    			
    		
    		
    	case 2:
    		
    		lat=(Integer)params[2];
    		lng=(Integer)params[3];
    		
    		try {
				return getNodeByPosition(id_group, lat, lng);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
    		
    	case 3:

    		String tosearch=(String)params[2];
    		
    		try {
				return getNodeByName(id_group,tosearch);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}	
    		
    	case 4:
    		
    		
    		if(map.equals(null)){
    		
    			throw new MapPointsException();
    		
    		}else{
    			
        		try {
        	    	
					insertNodes(id_group,map);
					
				} catch (DBOpenException e) {
					Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
				} catch (SQLiteConstraintException e) {
					Log.e("DB_ERROR - SQLITE COSTRAINT",e.getMessage());
					return -2;
			    }
    		}    		
    	break;
    	
    	
    	case 5:
    		
    		MapPoint mp1=(MapPoint)params[2];
    		
    		if(mp1 == null){
    	
    			throw new PointException();
    		
    		}else{
    		
    			try {
					insertNode(mp1);
					return 0;
				} catch (DBOpenException e) {
					Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
					return -1;
				} catch (SQLiteConstraintException e) {
					Log.e("DB_ERROR - SQLITE COSTRAINT",e.getMessage());
					return -2;
				}
    		
    		}
    	case 6:
    			
    		
    			try {
    				Integer id=(Integer)params[2];
    				return getNodesById(id);
    			} catch (DBOpenException e) {
    				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
    			}
    			
    	    	break;	
    	    	
    	case 7:
			
    		
			try {
		    	deleteNodesGroup(id_group);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
			
	    	break;	
	    	
    	case 8:
			
    		
			try {
				Integer id=(Integer)params[2];
		    	deleteNode(id);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
			
	    	break;	
    	
    	case 9:

    		String tosearch_type=(String)params[2];
    		
    		try {
				return getNodeByType(id_group,tosearch_type);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}	
    		break;
    		
    	case 10:
			
    		
			try {
				return getGroups();
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
			
	    	break;	
	    	
    	default:
    		return null;
    	}
    	
    	
		return null;
    	
        
        
       
    }
    
    
    public ArrayList<Group> getGroups() throws DBOpenException {
    	
    	Cursor cursor;
    	SQLiteDatabase db=null;
        db=openDBR();
    	
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME3;
        
        cursor= db.rawQuery(sql,null); //ottengo un cursore che punta alle entry ottenute dalla query
        cursor.moveToFirst();
        
        ArrayList<Group> result = new ArrayList<Group>();
        
        
        
        do{
        	if(cursor!=null && cursor.getCount()>0){
        		
        		Group current = new Group();
        		
        		current.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        		current.setName(cursor.getString(cursor.getColumnIndex("name")));
        		current.setUri(cursor.getString(cursor.getColumnIndex("uri")));
        		
        		result.add(current);
        	}

       	cursor.moveToNext();
        }while(!cursor.isAfterLast());
        
        db.close();
        
        return result;
	}


	@Override
    protected void onPreExecute() {
		//showProgressDialog();

    }
	
	public void showProgressDialog(){
        progressDialog = new ProgressDialog(current);
        progressDialog.setMessage(current.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
	}
	
	public void dismissProgressDialog(){
		progressDialog.dismiss();
	}
	
    
    @Override
    protected void onPostExecute(Object result) {
    	//dismissProgressDialog();
    }

    /**
     * It opens the database of the app in writing mode.
     * @return      the writable database.
     */
    public SQLiteDatabase openDBW() throws DBOpenException{
    	
    	SQLiteDatabase db;
    	try{
    		db=dbHelp.getWritableDatabase();
    		return db;
    	}catch (SQLiteException e) {
    		Log.v("Errore in OpenDB","NON SO DOVE");
        	throw new DBOpenException();
        }
    }
    
    /**
     * It opens the database of the app in reading mode.
     * @return      the readable database.
     */
    public SQLiteDatabase openDBR() throws DBOpenException{
    	
    	SQLiteDatabase db;
        
    	try{
    		db=dbHelp.getReadableDatabase();
    		return db;
    	}catch (SQLiteException e) {
        	throw new DBOpenException();
        }
    }
    
    /**
     * It deletes the content of the given Table.
     * @param 		table the table which content will be erased.
     */
    public void truncateTable(String table) throws DBOpenException{
    	SQLiteDatabase db=openDBW();
    	db.execSQL("DELETE FROM "+table);    //cancello il contenuto della tabella <table>    	
        db.close();
    }
    
    /**
     * It deletes the given table.
     * @param		table the table to erase.
     * @return      the writable database.
     */
    public void deleteTable(String table) throws DBOpenException{
    	SQLiteDatabase db=null;
    	db=openDBW();
    	db.execSQL("DROP TABLE "+table);    //cancello la tabella <table>
        db.close();
    }
    
    public void deleteNode(Integer id) throws DBOpenException{
    	SQLiteDatabase db=null;
    	db=openDBW();
    	db.execSQL("DELETE FROM "+DbHelper.TABLE_NAME1+" WHERE _id="+id);    //cancello il record dalla tabella Nodes
        db.close();
    }
    
    public void setListPoint(ArrayList<MapPoint> m){
    	this.map=m;
    }
    

	
	public String getDateTimeSystem(){
		return CalendarManager.formatDateTimeForDatabase(CalendarManager.getCurrentDateTime());
	}
    
    /**
     * It inserts given nodes in the table "Nodes". Old are deleted.
     * @param id_group	id of the given group. 
     * @param map the arraylist of MapPoint that contains the nodes.
     */
    public void insertNodes(Integer id_group,ArrayList<MapPoint> map) throws DBOpenException,SQLiteConstraintException{
    	this.map=map;
    	
    	deleteNodesGroup(id_group);
    	deleteOptvaluesGroup(id_group);
    	deleteMarkersGroup(id_group);
    	
		//Log.v("TOTALE NODI IN MAPPA", ""+map.size());
    	//showProgressDialog();
    	Log.v("GRANDEZZA MAPPA", ""+map.size());
		for(int i=0; i<map.size();i++){
			
			
			
			MapPoint mp=(MapPoint)map.get(i);
			mp.setGroupId(id_group);
			insertNode(mp);
		}
		
		insertMarkers(id_group);
		insertUpdate(id_group);
		
		//dismissProgressDialog();
    }
    
    /**
     * @return all nodes of the DB
     */
    public ArrayList<MapPoint> getNodes(Integer id_group) throws DBOpenException{
    	
    	String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE id_group="+ id_group;
    	Log.i("getNodes", "Dentro a getNodes(id_group)");
    	//select * from Nodes
    	return getNodesBySql(id_group,sql);
    	
    	
    	
    }
    
    /**
     * It searches nodes using latitude/longitude with a given tollerance(in nanodegree). If grade_lat and grade_lng are not used (0)
     * you should use instead getNodeByPosition(Integer lat, Integer lng).
     * @param		lat the latitude of the node to search
     * @param		lng the longitude of the node to search
     * @param		grade_lat tollerance of latitude
     * @param		grade_lng tollerance of longitude
     * @return      nodes of db according to the params
     */
    private ArrayList<MapPoint> getNodeByPosition(Integer id_group,Integer lat, Integer lng, Integer grade_lat, Integer grade_lng) throws DBOpenException{

        Integer min_lat=lat-grade_lat;
        Integer max_lat=lat+grade_lat;
        Integer min_lng=lng-grade_lng;
        Integer max_lng=lng+grade_lng;
        
           
        //select * from Nodes where (lat between [min_lat] AND [max_lat]) and (lng between [min_lng] AND [max_lng]));
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE id_group="+ id_group +" AND (lat BETWEEN " +min_lat.toString()+ " AND "+max_lat.toString()+") " +
        		"AND (lng BETWEEN "+min_lng.toString()+ " AND "+max_lng.toString()+")";
        return getNodesBySql(id_group,sql);
       
    }
    
   /**
    * Used to search nodes in a specific latitude/longitude. 
    * @param		lat the latitude of the node to search
    * @param		lng the longitude of the node to search
    * @return       nodes of db according to the params
    */
    
    private ArrayList<MapPoint> getNodeByPosition(Integer id_group,Integer lat, Integer lng) throws DBOpenException{
    	
        //select * from Nodes where ....
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE id_group="+ id_group +" AND lat= " +lat+ " AND lng="+lng+")";

        return getNodesBySql(id_group,sql);
    }
    
    /**
     * Used to search nodes using the name.
     * @param		name the name of the node to search
     * @return      nodes of db according to the params
     */
    private ArrayList<MapPoint> getNodeByName(Integer id_group,String name) throws DBOpenException{
       
           
        //select * from Nodes where ....
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE id_group = "+id_group+" AND name like '%"+name+"%'";
        
        return getNodesBySql(id_group,sql);
       
    }
    
    private ArrayList<MapPoint> getNodesBySql(Integer id_group,String sql) throws DBOpenException{
    	
    	ArrayList<MapPoint> recordset=new ArrayList<MapPoint>();
    	Cursor cursor = null;
    	SQLiteDatabase db=null;
        db=openDBR();
        String where;
    	
    	cursor= db.rawQuery(sql,null); //ottengo un cursore che punta alle entry ottenute dalla query
        cursor.moveToFirst();
        
        do{
        	//Log.i("getNodes", "Dentro a getNodesBySql(id_group,sql)");
        	if(cursor!=null && cursor.getCount()>0){
        		//Log.i("getNodes", "Dentro a ciclo 1");
            	MapPoint node=new MapPoint();
            	int id_node = cursor.getInt(cursor.getColumnIndex("_id"));
                node.setId(id_node);
                node.setGroupId(cursor.getInt(cursor.getColumnIndex("id_group")));
            	node.setName(cursor.getString(cursor.getColumnIndex("name")));
            	node.setType(cursor.getString(cursor.getColumnIndex("type")));
            	node.setLatitude(cursor.getInt(cursor.getColumnIndex("lat")));
            	node.setLongitude(cursor.getInt(cursor.getColumnIndex("lng")));
            	node.setAltitude(cursor.getInt(cursor.getColumnIndex("alt")));
            	
                //Log.i("Contenuto nodo i-esimo",node.toString());
            	
                //gestione campi opzionali in optvalues
                
            	where="";
            	
            	if(id_group != 0) where+= "id_group="+id_group+"&";
            	where+="id_node="+id_node;
            	
            	String sql2="SELECT * FROM "+DbHelper.TABLE_NAME5+" WHERE "+where;
            	
            	Cursor cursor_optvalues = db.rawQuery(sql2,null); //ottengo un cursore che punta alle entry ottenute dalla query
                cursor_optvalues.moveToFirst();

            	//Log.i("getNodes", "IDNODE = "+id_node);
                while(!cursor_optvalues.isAfterLast()){
                	node.addOptValue(cursor_optvalues.getString(cursor_optvalues.getColumnIndex("name")), 
                			cursor_optvalues.getString(cursor_optvalues.getColumnIndex("value")));
               	 cursor_optvalues.moveToNext();
                }
                //Log.i("Contenuto nodo i-esimo",node.toString());
                recordset.add(node);
                
                cursor_optvalues.close();
            }
        	cursor.moveToNext();
        	
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();
        
        return recordset;
    }
    
    public ArrayList<String> getTypes(Integer id_group) throws DBOpenException{
    	
    	
    	Cursor cursor;
    	SQLiteDatabase db=openDBR();
        
        String sql="SELECT DISTINCT type FROM "+DbHelper.TABLE_NAME1+" WHERE id_group = "+id_group+" ORDER BY type ASC";
        
        cursor= db.rawQuery(sql,null); //ottengo un cursore che punta alle entry ottenute dalla query
        cursor.moveToFirst();
        
        ArrayList<String> result = new ArrayList<String>();
       
    	
        do{
        	if(cursor!=null && cursor.getCount()>0){
        		result.add(cursor.getString(cursor.getColumnIndex("type")));
        	}
        	cursor.moveToNext();
        }while(!cursor.isAfterLast());
        
        cursor.close();
        db.close();
        return result;
    }
    
    /**
     * Used to search nodes using the type (status).
     * @param		type the type of the node to search
     * @return      nodes of db according to the params
     */
    
    private ArrayList<MapPoint> getNodeByType(Integer id_group,String type) throws DBOpenException{
        
        String str_search="";
                
        if(type.equals("all")){
        	return getNodes(id_group);
        }
        
        str_search=" AND type = '"+type+"'";
        
        
        Log.i(" WHERE str_search = ", ""+str_search);
        
        //select * from Nodes where (lat between [min_lat] AND [max_lat]) and (lng between [min_lng] AND [max_lng]));
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE id_group = "+id_group+str_search;
      
        Log.i("sql",sql);
        
        //select * from Nodes
    	return getNodesBySql(id_group,sql);
       
    }
    
    /**
     * Used to search nodes using the identificator (ID)
     * @param		id the identificator of the node
     * @return      nodes of db according to the params
     */
    private ArrayList<MapPoint> getNodesById(Integer id) throws DBOpenException{
    	
    	Log.v("ID nodo selezionato: ",""+id);
    	
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE _id = "+id;
        
        Log.v("QUERY: ",""+sql);
        
        return getNodesBySql(0,sql);
   	
   }
    /**
     * Check if the table nodes is Empty.
     * @return      true if empty, false otherwise.
     */
    public boolean isEmptyTableNodes() throws DBOpenException{
    	return isEmpty(DbHelper.TABLE_NAME1);
    }
    
    /**
    * Check if the table Updates is Empty.
    * @return      true if empty, false otherwise.
    */
    public boolean isEmptyTableUpdates() throws DBOpenException{
    	return isEmpty(DbHelper.TABLE_NAME2);
    }
    
    /**
     * Check if the table Updates is Empty.
     * @return      true if empty, false otherwise.
     */
     public boolean isEmptyTableGroups() throws DBOpenException{
     	return isEmpty(DbHelper.TABLE_NAME3);
     }
    
    /**
     * Counts the number of rows in the given table.
     * @param		tableName the name of the table
     * @return      the number of the rows.
     */
    public int countRows(String tableName) throws DBOpenException{
		
    	SQLiteDatabase db;
		
		db=openDBR();
		Cursor iterator=db.rawQuery("SELECT COUNT(*) FROM "+tableName,null);    //scandisco il contenuto della tabella passata in input
		iterator.moveToFirst();
		int count= iterator.getInt(0);
		iterator.close();
		db.close();
		
		return count;
    }
    
    /**
     * Check if the given table is empty
     * @param		tableName the name of the table
     * @return      true if empty, false otherwise.
     */
    public boolean isEmpty(String tableName) throws DBOpenException{

    		int tot=countRows(tableName);
    		
    		if(tot !=0){
    			return false;
    		}
    		else{
    			return true;
    		}
    	
    }
    
    /**
     * Updates the table "Updates".
     */
    public void insertUpdate(Integer id_group) throws DBOpenException{
    	SQLiteDatabase db=null;
    	
    		db=openDBW();
    
    		ContentValues values=new ContentValues();
    		values.put("number_nodes", map.size());
    		values.put("id_group", id_group);
    		values.put("type", "automatic");
    		values.put("dateC",getDateTimeSystem());
           	db.insertOrThrow(DbHelper.TABLE_NAME2, null, values);    //inserisco il messaggio nel DB
           	db.close();
    	
    }
    
    /**
     * Inserts a new list of markers in table "Markers". Inner value for images is DEFAULT
     */
    public HashMap<String, String> getMarkers(Integer id_group) throws DBOpenException{
    	
    	Cursor cursor;
    	SQLiteDatabase db;
        db=openDBR();
        
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME4+" WHERE id_group = "+id_group;
        
        cursor= db.rawQuery(sql,null); //ottengo un cursore che punta alle entry ottenute dalla query
        cursor.moveToFirst();
        
        HashMap<String, String> result = new HashMap<String, String>();
       
    	
        do{
        	if(cursor!=null && cursor.getCount()>0){
        		result.put(cursor.getString(cursor.getColumnIndex("name")),
        				cursor.getString(cursor.getColumnIndex("uri")));
        	}
        	cursor.moveToNext();
        }while(!cursor.isAfterLast());
        
        cursor.close();
        db.close();
        return result;
    	
    }
    
    /**
     * Inserts a new list of markers in table "Markers". Inner value for images is DEFAULT
     */
    public void insertMarkers(Integer id_group) throws DBOpenException{
    	SQLiteDatabase db=current.openOrCreateDatabase("/data/data/it.pdm.nodeshotmobile/databases/MapsAppBase.db",
    	                SQLiteDatabase.OPEN_READWRITE, null);
    		
    		ArrayList<String> list = getTypes(id_group);
    		
    		String[] markers_default = current.getResources().getStringArray(SettingsActivity.DEFAULT_VALUE_URI_IMAGE);
    		
    		for(int i=0;i<list.size();i++){
    			
    			Integer real_val = Integer.parseInt(list.get(i));
    			
        		ContentValues values=new ContentValues();
        		values.put("id_group", id_group);
        		values.put("name", list.get(i));
        		
        		if(real_val<4){
        			switch(real_val){
        				case 0:
        					values.put("uri", markers_default[0]);
        				break;
        				case 3:
        					values.put("uri", markers_default[1]);
        				break;
        			} 				
        		}else{
        			values.put("uri", "R.drawable.red_marker");
        		}
               	db.insertOrThrow(DbHelper.TABLE_NAME4, null, values);    //inserisco il marker nel DB
    		}
    		
           	db.close();
    	
    }
    
    /**
     * Deletes all nodes of a group from table "Nodes".
     */
    public void deleteNodesGroup(Integer id_group) throws DBOpenException{
    	
    	SQLiteDatabase db=openDBW();
    	db.execSQL("DELETE FROM Nodes WHERE id_group="+id_group);   	
        db.close();
    }
    
    /**
     * Deletes all nodes of a group from table "Nodes".
     */
    public void deleteGroups() throws DBOpenException{
    	
    	SQLiteDatabase db=openDBW();
    	
    	if(!db.isOpen()){
    		Log.v("ERRORE","IL DB NON è APERTO IN DELETEGROUPS");
    	}
    	
    	db.execSQL("DELETE FROM Groups");   	
        db.close();
    }
    
    /**
     * Deletes all optional values of a group from table "Optvalues".
     */
    public void deleteOptvaluesGroup(Integer id_group) throws DBOpenException{
    	
    	SQLiteDatabase db=openDBW();
    	
    	if(!db.isOpen()){
    		Log.v("ERRORE","IL DB NON è APERTO IN DELETEOPTVALUESGROUP");
    	}
    	
    	db.execSQL("DELETE FROM Optvalues WHERE id_group="+id_group);   	
        db.close();
    }
    
    /**
     * Deletes all markers of a group from table "Markers".
     */
    public void deleteMarkersGroup(Integer id_group) throws DBOpenException{
    	
    	SQLiteDatabase db=openDBW();
    	
    	if(!db.isOpen()){
    		Log.v("ERRORE","IL DB NON è APERTO IN DELETEMARKERSGROUP");
    	}
    	
    	db.execSQL("DELETE FROM Markers WHERE id_group="+id_group);   	
        db.close();
    }
    
    /**
    * Deletes a group from table "Groups".
    */
   public void deleteGroup(Integer id_group) throws DBOpenException{
   	
   		SQLiteDatabase db=openDBW();
   		
   		if(!db.isOpen()){
    		Log.v("ERRORE","IL DB NON è APERTO IN DELETEGROUP");
    	}
   		
   		db.execSQL("DELETE FROM Groups WHERE id_group="+id_group);   	
   		db.close();
   }
    
    /**
     * Put the given element in the table "Nodes", and its bonus field in "Optavalues"
     * @param		mp the MapPoint element to add
     */
    private void insertNode(MapPoint mp) throws DBOpenException,SQLiteConstraintException {
       
    	SQLiteDatabase db=openDBW();
    	
    	if(!db.isOpen()){
    		Log.v("ERRORE","IL DB NON è APERTO IN INSERTNODE");
    	}
    	
    	ContentValues values=new ContentValues();
    	values.put("id_group", mp.getGroupId());
    	values.put("name", mp.getName());
    	values.put("type", mp.getType());
    	values.put("lat", mp.getLatitude());
    	values.put("lng", mp.getLongitude());
    	values.put("alt", mp.getAltitude());
    	
    	try{
    		int node_id = (int)db.insertOrThrow(DbHelper.TABLE_NAME1, null, values);    //inserisco il nodo nel DB
    		
    		Iterator<String> current = mp.getOptValue().keySet().iterator();
        	
        	while (current.hasNext()) {
        		
                String name = current.next();
                ContentValues values2=new ContentValues();
                values2.put("id_group", mp.getGroupId());
                values2.put("id_node", node_id);
                values2.put("name", name);
                values2.put("value", mp.getOptValue().get(name));
                
                db.insertOrThrow(DbHelper.TABLE_NAME5, null, values2);    //inserisco il nodo nel DB
                
            }

        	db.close();  
    		
    	}catch(SQLException e){
    		db.close();
    		throw e;
    	} 
        
    	
    }
    
    /**
     * Put the given element in the table "Group".
     * @param		gr the Group element to add
     */
    private void insertGroup(Group gr) throws DBOpenException,SQLiteConstraintException {
       
    	SQLiteDatabase db=openDBW();
    	
    	if(!db.isOpen()){
    		Log.v("ERRORE","IL DB NON è APERTO IN INSERTGROUP");
    	}
    	
    	ContentValues values=new ContentValues();
    	values.put("name", gr.getName());
    	values.put("uri", gr.getUri());
    	
    	try{
    		db.insertOrThrow(DbHelper.TABLE_NAME3, null, values);    //inserisco il nodo nel DB 
    		db.close();
    	}catch(SQLException e){
    		db.close();
    		throw e;
    	}
        
    	
    }
    
    /**
     * It inserts given groups in the table "Groups". Old are deleted.
     * @param map the arraylist of Groups.
     */
    public void insertGroups(ArrayList<Group> gr) throws DBOpenException,SQLiteConstraintException{
    	
    	deleteGroups();
    	
		//showProgressDialog();

		Log.v("GRANDEZZA MAPPA in DBMANAGER", ""+gr.size());
		for(int i=0; i<gr.size();i++){
    		//Log.v("Sto parsando il gruppo   "+i, gr.get(i).toString());
			insertGroup(gr.get(i));
		}
		
		/*FORZATO*/
		/*Group current = new Group();
		
		current.setName("Ninux Current Map");
		current.setUri("http://www.longinuslab.it/testing/nsm/json/nodes.json");
		insertGroup(current);*/
		/**/
		
		//dismissProgressDialog();
    }
    
    /**
     * It returns the last change in table "Updates". If the table is empty, it will return "0000/00/00 00:00:00".
     * @return      the date of the last change.
     */
    public String getLastUpdate(Integer id_group) throws DBOpenException{
        
        Cursor cursor;
        SQLiteDatabase db;
        
        try{
            
            /*if(isEmptyTableUpdates()){
            	return "0000/00/00 00:00:00";
            }*/
            
        	db=openDBR();
        	
        	if(!db.isOpen()){
        		Log.v("ERRORE","IL DB NON è APERTO IN GETLASTUPD");
        	}
        	
        	String datec;
           
            //select datetimeC from Updates order by datetimeC DESC limit 1
        	//ottengo un cursore che punta alle entry ottenute dalla query
            cursor= db.rawQuery("SELECT dateC FROM "+DbHelper.TABLE_NAME2+" WHERE id_group="+id_group+" ORDER BY _id DESC LIMIT 1",null); 
            if(cursor.moveToFirst()){
            	datec = cursor.getString(cursor.getColumnIndex("dateC"));
            }else{
            	datec = "0000/00/00 00:00:00";
            }
            	cursor.close();
            	db.close();
            	
            return datec;
           
        }catch(SQLException e){
        	Log.e("DB_ERROR - SELECT update",e.getMessage());
        	return "";
        }
        
    
    
    }
       
    
  
}
