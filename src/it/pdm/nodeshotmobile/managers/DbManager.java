package it.pdm.nodeshotmobile.managers;

import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.entities.DbHelper;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.entities.POI;
import it.pdm.nodeshotmobile.exceptions.DBOpenException;
import it.pdm.nodeshotmobile.exceptions.MapPointsException;
import it.pdm.nodeshotmobile.exceptions.PointException;

import java.util.ArrayList;

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
    
    private ArrayList<POI> map;
    private DbHelper dbHelp;
    private Context current;
    private ProgressDialog progressDialog;
    private Integer current_op;
       
    public DbManager(DbHelper helper,Context context){
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
       4: insertNodes(map);<br>
       5: insertNode(mp);<br>
       6: getNodeById(id);<br>
       7: removeNodes();<br>
       8: removeNode(id);<br>
       9: getNodesByType(type);<br>
    */

    @Override
    protected Object doInBackground(Object... params) throws MapPointsException,PointException {
        // TODO Auto-generated method stub

    	Integer lat;
    	Integer lng;
    	Integer grade_lat;
    	Integer grade_lng;
    	
    	current_op=(Integer)params[0];
    	
    	switch(current_op){

    	case 0:
    		
			
			try {
				ArrayList<MapPoint> m;
				m = getNodes();
				return m;
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
    	
    	case 1:
    		
    		lat=(Integer)params[1];
    		lng=(Integer)params[2];
    		grade_lat=(Integer)params[3];
    		grade_lng=(Integer)params[4];
			try {
				ArrayList<MapPoint> m;
				m = getNodeByPosition(lat, lng, grade_lat, grade_lng);
				return m;
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
    			
    		
    		
    	case 2:

    		lat=(Integer)params[1];
    		lng=(Integer)params[2];
    		
    		try {
				return getNodeByPosition(lat, lng);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
    		
    	case 3:

    		String tosearch=(String)params[1];
    		
    		try {
				return getNodeByName(tosearch);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}	
    		
    	case 4:
    		
    		
    		if(map.equals(null)){
    		
    			throw new MapPointsException();
    		
    		}else{
    			
        		try {
        	    	deleteNodes();
					insertNodes(map);
				} catch (DBOpenException e) {
					Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
				} catch (SQLiteConstraintException e) {
					Log.e("DB_ERROR - SQLITE COSTRAINT",e.getMessage());
					return -2;
			    }
    		}    		
    	break;
    	
    	
    	case 5:
    		
    		MapPoint mp1=(MapPoint)params[1];
    		
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
    				Integer id=(Integer)params[1];
    				return getNodesById(id);
    			} catch (DBOpenException e) {
    				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
    			}
    			
    	    	break;	
    	    	
    	case 7:
			
    		
			try {
		    	deleteNodes();
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
			
	    	break;	
	    	
    	case 8:
			
    		
			try {
				Integer id=(Integer)params[1];
		    	deleteNode(id);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}
			
	    	break;	
    	
    	case 9:

    		String tosearch_type=(String)params[1];
    		
    		try {
				return getNodeByType(tosearch_type);
			} catch (DBOpenException e) {
				Log.e("DB_ERROR - OPEN DATABASE",e.getMessage());
			}	
	
	    	
    	default:
    		return null;
    	}
    	
    	
		return null;
    	
        
        
       
    }
    
    
    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(current);
        progressDialog.setMessage(current.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

    }
    
    @Override
    protected void onPostExecute(Object result) {
    	progressDialog.dismiss();
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
    
    public void setListPoint(ArrayList m){
    	this.map=m;
    }
    
    /**
     * It inserts given nodes in the table "Nodes". Old are deleted.
     * @param		map the arraylist of MapPoint that contains the nodes.
     */
    private void insertNodes(ArrayList map) throws DBOpenException,SQLiteConstraintException{
    	
		//Log.v("TOTALE NODI IN MAPPA", ""+map.size());
		
		for(int i=0; i<map.size();i++){
			MapPoint mp=(MapPoint)map.get(i);
			insertNode(mp);
		}    	
    	
    	insertUpdate();
    }
    
    /**
     * @return      all nodes of the DB
     */
    private ArrayList<MapPoint> getNodes() throws DBOpenException{
    	
    	
    	 ArrayList<MapPoint> valori=new ArrayList<MapPoint>();
         Cursor cursor;
         SQLiteDatabase db=null;

         db=openDBR();
            
         //select * from Nodes
         
         cursor= db.query(DbHelper.TABLE_NAME1, null, null, null, null, null, null); //ottengo un cursore che punta alle entry ottenute dalla query
         cursor.moveToFirst();
         do{
             MapPoint node=new MapPoint();
             node.setId(cursor.getInt(cursor.getColumnIndex("_id")));
             node.setName(cursor.getString(cursor.getColumnIndex("name")));
             node.setStatus(cursor.getString(cursor.getColumnIndex("status")));
             node.setSlug(cursor.getString(cursor.getColumnIndex("slug")));
             node.setLatitude(cursor.getInt(cursor.getColumnIndex("lat")));
             node.setLongitude(cursor.getInt(cursor.getColumnIndex("lng")));
             node.setJslug(cursor.getString(cursor.getColumnIndex("jslug")));
             
             valori.add(node);
             cursor.moveToNext();
         }while(!cursor.isAfterLast());

             cursor.close();
             db.close();
         
     
         return valori;
    	
    	
    	
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
    private ArrayList<MapPoint> getNodeByPosition(Integer lat, Integer lng, Integer grade_lat, Integer grade_lng) throws DBOpenException{
        
        ArrayList<MapPoint> valori=new ArrayList<MapPoint>();
        Cursor cursor;
        SQLiteDatabase db=null;
        Integer min_lat=lat-grade_lat;
        Integer max_lat=lat+grade_lat;
        Integer min_lng=lng-grade_lng;
        Integer max_lng=lng+grade_lng;
        db=openDBR();
           
        //select * from Nodes where (lat between [min_lat] AND [max_lat]) and (lng between [min_lng] AND [max_lng]));
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE (lat BETWEEN " +min_lat.toString()+ " AND "+max_lat.toString()+") " +
        		"AND (lng BETWEEN "+min_lng.toString()+ " AND "+max_lng.toString()+")";
        cursor= db.rawQuery(sql,null); //ottengo un cursore che punta alle entry ottenute dalla query
        cursor.moveToFirst();
        do{
        	if(cursor!=null && cursor.getCount()>0){
            	MapPoint node=new MapPoint();
            	node.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            	node.setName(cursor.getString(cursor.getColumnIndex("name")));
            	node.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            	node.setSlug(cursor.getString(cursor.getColumnIndex("slug")));
            	node.setLatitude(cursor.getInt(cursor.getColumnIndex("lat")));
            	node.setLongitude(cursor.getInt(cursor.getColumnIndex("lng")));
            	node.setJslug(cursor.getString(cursor.getColumnIndex("jslug")));
                valori.add(node);
                cursor.moveToNext();
            }
            
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();
        
    
        return valori;
       
    }
    
   /**
    * Used to search nodes in a specific latitude/longitude. 
    * @param		lat the latitude of the node to search
    * @param		lng the longitude of the node to search
    * @return       nodes of db according to the params
    */
    
    private ArrayList<MapPoint> getNodeByPosition(Integer lat, Integer lng) throws DBOpenException{
        
    	ArrayList<MapPoint> valori=new ArrayList<MapPoint>();
        Cursor cursor=null;
        SQLiteDatabase db=null;
        
        db=openDBR();
           
         //select * from Nodes where lat=lat AND lng=lng                //"SELECT * FROM Nodes WHERE (? = ?) AND (? = ?)";
         String sql="(? = ?) AND (? = ?)";
         String values[]=new String[]{"lat",lat.toString(),"lng",lng.toString()};
         cursor= db.query(DbHelper.TABLE_NAME1, null, sql, values, null, null, null); //ottengo un cursore che punta alle entry ottenute dalla query
         cursor.moveToFirst();
         do{
                MapPoint node=new MapPoint();

            node.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            node.setName(cursor.getString(cursor.getColumnIndex("name")));
            node.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            node.setSlug(cursor.getString(cursor.getColumnIndex("slug")));
            node.setLatitude(cursor.getInt(cursor.getColumnIndex("lat")));
            node.setLongitude(cursor.getInt(cursor.getColumnIndex("lng")));
            node.setJslug(cursor.getString(cursor.getColumnIndex("jslug")));
            
            valori.add(node);

                cursor.moveToNext();
         }while(!cursor.isAfterLast());
           
        
        return valori;
       
    }
    
    /**
     * Used to search nodes using the name.
     * @param		name the name of the node to search
     * @return      nodes of db according to the params
     */
    private ArrayList<MapPoint> getNodeByName(String name) throws DBOpenException{
        
        ArrayList<MapPoint> valori=new ArrayList<MapPoint>();
        Cursor cursor;
        SQLiteDatabase db=null;
        db=openDBR();
           
        //select * from Nodes where (lat between [min_lat] AND [max_lat]) and (lng between [min_lng] AND [max_lng]));
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE name like '%"+name+"%'";
        cursor= db.rawQuery(sql,null); //ottengo un cursore che punta alle entry ottenute dalla query
        cursor.moveToFirst();
        do{
        	if(cursor!=null && cursor.getCount()>0){
            	MapPoint node=new MapPoint();
            	node.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            	node.setName(cursor.getString(cursor.getColumnIndex("name")));
            	node.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            	node.setSlug(cursor.getString(cursor.getColumnIndex("slug")));
            	node.setLatitude(cursor.getInt(cursor.getColumnIndex("lat")));
            	node.setLongitude(cursor.getInt(cursor.getColumnIndex("lng")));
            	node.setJslug(cursor.getString(cursor.getColumnIndex("jslug")));
                valori.add(node);
                cursor.moveToNext();
            }
            
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();
        
    
        return valori;
       
    }
    
    /**
     * Used to search nodes using the type (status).
     * @param		type the type of the node to search
     * @return      nodes of db according to the params
     */
    
    private ArrayList<MapPoint> getNodeByType(String type) throws DBOpenException{
        
        ArrayList<MapPoint> valori=new ArrayList<MapPoint>();
        Cursor cursor;
        SQLiteDatabase db=null;
        db=openDBR();
        
        String str_search="";
        
        String values0[] = current.getResources().getStringArray(R.array.type_node_values); 
        String values1[] = current.getResources().getStringArray(R.array.type_node_real_values); 
        
        if(type.equals(values0[1])){
        	str_search=" WHERE status = '"+values1[0]+"'";
        }
        
        if(type.equals(values0[2])){
        	str_search=" WHERE status = '"+values1[1]+"' OR status = '"+values1[2]+"'";
        }
        
        if(type.equals(values0[3])){
        	str_search=" WHERE status = '"+values1[3]+"'";
        }
        
        Log.i(" WHERE str_search = ", ""+str_search);
        
        //select * from Nodes where (lat between [min_lat] AND [max_lat]) and (lng between [min_lng] AND [max_lng]));
        String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+str_search;
        cursor= db.rawQuery(sql,null); //ottengo un cursore che punta alle entry ottenute dalla query
        cursor.moveToFirst();
        do{
        	if(cursor!=null && cursor.getCount()>0){
            	MapPoint node=new MapPoint();
            	node.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            	node.setName(cursor.getString(cursor.getColumnIndex("name")));
            	node.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            	node.setSlug(cursor.getString(cursor.getColumnIndex("slug")));
            	node.setLatitude(cursor.getInt(cursor.getColumnIndex("lat")));
            	node.setLongitude(cursor.getInt(cursor.getColumnIndex("lng")));
            	node.setJslug(cursor.getString(cursor.getColumnIndex("jslug")));
                valori.add(node);
                cursor.moveToNext();
            }
            
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();
        
    
        return valori;
       
    }
    
    /**
     * Used to search nodes using the identificator (ID)
     * @param		id the identificator of the node
     * @return      nodes of db according to the params
     */
    private ArrayList<MapPoint> getNodesById(Integer id) throws DBOpenException{
    	
    	
   	 ArrayList<MapPoint> valori=new ArrayList<MapPoint>();
        Cursor cursor;
        SQLiteDatabase db=openDBR();
        //String sql="SELECT * FROM "+DbHelper.TABLE_NAME1+" WHERE _id='"+id+"'";
        //Log.v("Sql1", sql);
        
        String sql="(_id = "+id+")";
        String values[]=new String[]{"_id",""+id};
        cursor= db.query(DbHelper.TABLE_NAME1,new String[]{"_id","name","status","lat","lng"}, sql, null, null, null, null);//ottengo un cursore che punta alle entry ottenute dalla query

        
        cursor.moveToFirst();
        do{
            MapPoint node=new MapPoint();
            node.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            node.setName(cursor.getString(cursor.getColumnIndex("name")));
            node.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            node.setLatitude(cursor.getInt(cursor.getColumnIndex("lat")));
            node.setLongitude(cursor.getInt(cursor.getColumnIndex("lng")));
            
            valori.add(node);
            cursor.moveToNext();
        }while(!cursor.isAfterLast());
        
        	
            cursor.close();
            db.close();
    
        return valori;
   	
   	
   	
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
     * It Updates the table "Updates".
     */
    public void insertUpdate() throws DBOpenException{
    	SQLiteDatabase db=null;
    	
    		db=openDBW();
    		String datetime=CalendarManager.getCurrentDateTime();
    		String forDatabase=CalendarManager.formatDateTimeForDatabase(datetime);
    
    		ContentValues values=new ContentValues();
    		values.put("number_nodes", map.size());
    		values.put("type", "automatic");
    		values.put("datetimeC",forDatabase);
           	db.insertOrThrow(DbHelper.TABLE_NAME2, null, values);    //inserisco il messaggio nel DB
           	db.close();
    	
    }
    
    /**
     * It deletes all nodes from table "Nodes".
     */
    public void deleteNodes() throws DBOpenException{
    	
    	truncateTable(DbHelper.TABLE_NAME1);
    	
        
    }
    
    
    /**
     * It adds the element given, in the table "Nodes"
     * @param		mp the MapPoint element to add
     */
    private void insertNode(MapPoint mp) throws DBOpenException,SQLiteConstraintException {
       
    	SQLiteDatabase db=null;
    	
    	db=openDBW();
    	ContentValues values=new ContentValues();
    	values.put("_id", mp.getId());
    	values.put("name", mp.getName());
    	values.put("status", mp.getStatus());
    	values.put("slug", mp.getSlug());
    	values.put("lat", mp.getLatitude());
    	values.put("lng", mp.getLongitude());
    	values.put("jslug", mp.getJslug());
    	
    	try{
    		db.insertOrThrow(DbHelper.TABLE_NAME1, null, values);    //inserisco il messaggio nel DB
    	}catch(SQLException e){
    		db.close();
    		throw e;
    	}
    	db.close();   
        
    	
    }
    
    /**
     * It returns the last change in table "Updates". If the table is empty, it will return "0000/00/00 00:00:00".
     * @return      the date of the last change.
     */
    public String getLastUpdate() throws DBOpenException{
        
        Cursor cursor=null;
        SQLiteDatabase db=null;
        
        if(isEmptyTableUpdates()){
        	return "0000/00/00 00:00:00";
        }
        
        try{
        	db=openDBR();
           
            //select datetimeC from Updates order by datetimeC DESC limit 1
        	//ottengo un cursore che punta alle entry ottenute dalla query
            cursor= db.rawQuery("SELECT datetimeC FROM "+DbHelper.TABLE_NAME2+" ORDER BY _id DESC LIMIT 1",null); 
            cursor.moveToFirst();
           	return cursor.getString(cursor.getColumnIndex("datetimeC"));
           
        }catch(SQLException e){
        	Log.e("DB_ERROR - SELECT update",e.getMessage());
        }
        finally{
            cursor.close();
            db.close();
        }

        return "";
    }
       
    
  
}
