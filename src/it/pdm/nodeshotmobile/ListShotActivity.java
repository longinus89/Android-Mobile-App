package it.pdm.nodeshotmobile;


import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.entities.DbHelper;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.exceptions.ConnectionException;
import it.pdm.nodeshotmobile.exceptions.DBOpenException;
import it.pdm.nodeshotmobile.managers.CalendarManager;
import it.pdm.nodeshotmobile.managers.DbManager;
import it.pdm.nodeshotmobile.managers.ListManager;
import it.pdm.nodeshotmobile.managers.JsonManager;
import it.pdm.nodeshotmobile.managers.RestManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListShotActivity extends ListActivity {
		

		JsonManager parserJson;
		ListView listNodes;
		ListManager listmng;
		Integer current_item;
		private final String TAG = "ListShotActivity - ";
		static private final int ID_OPTION_NODE= 0;
		static private final int ID_SHOW_NODE= 1;
		
		
		private DbHelper dbh;
		private DbManager dbmanager;
		private RestManager client;
		
		private EditText field_search;
        
        public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	
        	try{
             	checkConnection();
             	setContentView(R.layout.list_nodes);
             	
        		dbh=new DbHelper(getApplicationContext());
            	dbmanager= new DbManager(dbh,getDialogContext());
        	
             	parserJson= new JsonManager(getApplicationContext());
             	listNodes=getListView();
             	listNodes.setTextFilterEnabled(true);


             	field_search=(EditText)findViewById(R.id.name_to_search);
                String[] from={"id","name"}; //from these values
                int[] to={R.id.id,R.id.name}; //to these references
             	listmng=new ListManager(this,listNodes,from,to,R.layout.row);
				
				
				field_search.addTextChangedListener(new TextWatcher() {
					
					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void afterTextChanged(Editable s) {
						listmng.filter(s.toString());
					}
				});
				
				listNodes.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						String idItem=""+(Integer)listmng.getMap().get(arg2).get("id");
						current_item=Integer.parseInt(idItem);
						showDialog(0);
						
					}
		        	
				});
				
        	}catch (ConnectionException e) {
        		showToastLong(e.getMessage());
    			closeApplication();
			}
        	
        	
        }
        
        @Override
        protected void onStart() {
        
        	super.onStart();
        
        String lastDateTime;
		
        try {
			lastDateTime = getLastUpdate();
			String currentDateTime=getDateTimeSystem();
			
			double difference=CalendarManager.differenceDates(currentDateTime, lastDateTime);
		
			if(difference>NodeShotActivity.MAX_DAYS_FOR_UPDATE || difference<0 || isEmptyTableNodes()){
	        	
				//java.util.zip.GZIPOutputStream stream = new GZIPOutputStream(os)
				
				parserJson.getNodes(getResponse()); //parsa il file Json recuperato tramite richiesta GET 
				//HTTP utilizzando architettura REST, popola quindi la lista di nodi principale
	       		insertNodes(); //sovrascrive i nodi nella tabella nodes in DB con i nuovi nodi.
	   	    	fillListNodes(); //riempie la lista principale con gli id e i nomi dei Nodi esistenti
	        	
	        //----------------------------------------------------------------------//
			}else{
			
				getNodes();//legge sul DB e popola la lista di nodi principale.
				fillListNodes(); //crea i punti sulla mappa a partire dalla lista di MapPoint
			}
		}catch (DBOpenException e) {
    		Log.e(TAG, e.getMessage());
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.e(TAG, e.getMessage());
		}
		
        
        }
        
        private Context getDialogContext() {
            Context context;
            if (getParent() != null) context = getParent();
            else context = this;
            return context;
       }
        
        
        
        public void showToastLong(String text){
        	Toast t=Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        	t.setGravity(Gravity.CENTER,0,0);
        	t.show();
        }
        
        
        public void showToastShort(String text){
        	Toast t=Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        	t.show();
        }
        
        public boolean isEmptyTableNodes() throws DBOpenException {
        	dbh=new DbHelper(getApplicationContext()); 
        	dbmanager= new DbManager(dbh,getApplicationContext());
        	return dbmanager.isEmptyTableNodes();
        }
    	private String getLastUpdate() throws DBOpenException{
    		return dbmanager.getLastUpdate();
    	}
    	
    	public String getResponse(){
    		
    		AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>(){

    			@Override
    			protected String doInBackground(Void...voids) {
    		
    		client = new RestManager(NodeShotActivity.STRING_CONNECTION_NODES);

    		try {
    		    String message = client.getNodes(true);
    			String headers = client.getResponseHeaders();
    		    //Log.v("Headers from MapServer: ",headers);
    			//Log.v("Message from MapServer: ",message);
    			return message;
    		} catch (Exception e) {
    		    e.printStackTrace();
    		}
    		return "";
    		}
    	};
    	
    	try {
    		
    			String result=asyncTask.execute().get();
    			return result;
    	
    	}catch (InterruptedException e) {
    		Log.e(TAG, e.toString());
    		return null;
    	} catch (ExecutionException e) {
    		Log.e(TAG, e.toString());
    		return null;
    	}
    	
    	
    	}
    	
    	private String getDateTimeSystem(){
    		return CalendarManager.formatDateTimeForDatabase(CalendarManager.getCurrentDateTime());
    	}
        
    	private void getNodes() throws InterruptedException, ExecutionException{
    		dbh=new DbHelper(getApplicationContext()); 
        	dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
        	@SuppressWarnings("unchecked")
    		ArrayList<MapPoint> map=(ArrayList<MapPoint>)dbmanager.execute(0).get();
        	MapPoint.points=map;
    	}
    	
    	private void insertNodes(){
    		dbh=new DbHelper(getApplicationContext()); 
        	dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
        	dbmanager.setListPoint(MapPoint.points);
        	dbmanager.execute(4);
    	}
    	
    	private void fillListNodes(){
    		listmng.load(MapPoint.points);
    	}
    	
        public void checkConnection() throws ConnectionException{
        	
        	if(!isOnline()){
        		throw new ConnectionException();
        	}
        	
        }
        
        public void closeApplication(){
        	
            Thread thread = new Thread(){
                @Override
               public void run() {
                    try {
                       Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                       ListShotActivity.this.finish();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
                }  
              };
              
              thread.start();

        	
        }
        
        public boolean isOnline() throws ConnectionException {
        	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        	    NetworkInfo ni = cm.getActiveNetworkInfo();
        	    if (ni!=null && ni.isAvailable() && ni.isConnected()) {
        	        return true;
        	    } else {
        	        return false;  
        	    }	
        }
        
        @Override
        protected void onPrepareDialog(int id, Dialog dialog) {
			
        	if(id==ID_SHOW_NODE){
        		dialog.setCanceledOnTouchOutside(true);
        		dbmanager=new DbManager(dbh,NodeShotActivity.cntx);
        	
        		dialog.setTitle(getResources().getString(R.string.dialog1));
        		dialog.setContentView(R.layout.show_node);
			
        		TextView data=(TextView) dialog.findViewById(R.id.show_node);
		 	
        		ArrayList<MapPoint> map;
        		try {
        			
        			map = (ArrayList<MapPoint>)dbmanager.execute(6,current_item).get();
        			MapPoint unique=map.get(0);
        			String body="Id: "+unique.getId()+"\nName: "+unique.getName()+"\nLat: "+(double)unique.getLatitude()/NodeShotActivity.factor+"\n" +
					"Long: "+(double)unique.getLongitude()/NodeShotActivity.factor+"\nStatus: "+unique.getStatus()+"\n";
        			data.setText(body);
        		} catch (InterruptedException e) {
        			Log.e(TAG, e.toString());
        		} catch (ExecutionException e) {
	   		Log.e(TAG, e.toString());
        	}
        }
		
        }
        
		protected Dialog onCreateDialog(int id) {
        	
        	Dialog defaultd=new Dialog(this);
        	switch(id){
        		case ID_OPTION_NODE:
        			AlertDialog dialog=createAlertDialogOptions(); 		
        		return dialog;
        		default:
        			return defaultd;
        		
        	}
	
        }
		
		public AlertDialog createAlertDialogOptions(){
			
	    	final CharSequence[] items=new CharSequence []{getResources().getString(R.string.dialog2)
	    			,getResources().getString(R.string.dialog3),getResources().getString(R.string.dialog4)};
	    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
	    	builder.setTitle(getResources().getString(R.string.dialog0));

	    	builder.setItems(items, new DialogInterface.OnClickListener() {

	    	@Override
	    	public void onClick(DialogInterface dialog, int which) {
	    		switch(which){
	    		case 0:
	    			showDialog(ID_SHOW_NODE);
	    		break;
	    		case 1:
	    			//showDialog(ID_SEARCH_NODES_POSITION);
	    		break;
	    		case 2:
	    			//showDialog(ID_SEARCH_NODES_POSITION);
	    		break;
	    		}
	    	}
	    	});

	    	return builder.show();
	    }
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
	    	
	        
	        MenuItem search=menu.add(0,1,0,getResources().getString(R.string.menu0));
	        search.setIcon(android.R.drawable.ic_menu_info_details);
	        
	        search.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Intent intent = new Intent().setClass(ListShotActivity.this, NewsActivity.class);
					startActivity(intent);
					finish();
					return true;
				}
			});
	        
	        return true;
	        
		}
        

        
}
