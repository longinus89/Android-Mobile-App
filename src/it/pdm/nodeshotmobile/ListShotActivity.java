package it.pdm.nodeshotmobile;


import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.entities.DbHelper;
import it.pdm.nodeshotmobile.entities.Group;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.exceptions.ConnectionException;
import it.pdm.nodeshotmobile.exceptions.DBOpenException;
import it.pdm.nodeshotmobile.managers.CalendarManager;
import it.pdm.nodeshotmobile.managers.DbManager;
import it.pdm.nodeshotmobile.managers.ListManager;
import it.pdm.nodeshotmobile.managers.JsonManager;
import it.pdm.nodeshotmobile.managers.RestManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost.Settings;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
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
		static private final int ID_SEND_MAIL= 2;
		
		private DbHelper dbh;
		private DbManager dbmanager;
		private RestManager client;
		
		private EditText field_search;
	    private ProgressDialog progressDialog;
        

	    @Override
	    protected void onRestart() {
	    	super.onRestart();
	    	Log.v("Posizione", "Onrestart");
	    }
	    
	    @Override
	    protected void onResume() {
	    	super.onResume();
	    	Log.v("Posizione", "Onresume");
	    }
	    
	    @Override
	    protected void onPostResume() {
	    	super.onPostResume();
	    	Log.v("Posizione", "Onpostresume");
	    	

	    	//showProgressDialog();
	    	
	        String lastDateTime;
			
	        try {
	         	
	        	if(isEmptyTableGroups()){
	        		parserJson.getGroups(getResponse(SettingsActivity.DEFAULT_VALUE_URL+SettingsActivity.DEFAULT_VALUE_URL_GROUPS+"?format=json")); //parsa il file Json recuperato tramite richiesta GET 
	        		insertGroups();
	        	}
	        	
	        	getGroups();
	        	
	        	//Log.v("list of groups get from DB", Group.groups.toString());
	        	
	        	//legge le preferenze, se non sono settate associa valori di default
	        	HashMap<String, Object> preferences =readPreferences();
	        	 	
	        	//prelevo le preferenze sul gruppo di nodi
	        	Integer idgroup = (Integer)preferences.get(SettingsActivity.DEFAULT_KEY_GROUP);
	        	String urlgroup = (String)preferences.get(SettingsActivity.DEFAULT_KEY_URL_NODES);
	        	
	    		dbh=new DbHelper(getApplicationContext());
	        	dbmanager= new DbManager(dbh,getDialogContext());
	        	
	        	Log.v("Dati gruppo", "id = "+idgroup);
	        	Log.v("Dati gruppo", "url = "+urlgroup);
	        	
	        	urlgroup = matchUrl(urlgroup);
	        	
				lastDateTime = getLastUpdate(idgroup);
				
				String currentDateTime=dbmanager.getDateTimeSystem();
				double difference=CalendarManager.differenceDates(currentDateTime, lastDateTime);
				
				clearNodes();
				
				if(difference>SettingsActivity.MAX_DAYS_FOR_UPDATE || difference<0 || isEmptyTableNodes()){	    				
		    			
		    				parserJson.getNodes(getResponse(urlgroup+"?format=json")); //parsa il file Json recuperato tramite richiesta GET 
		    				//HTTP, popola quindi la lista di nodi principale
		    				try {
								insertNodes(idgroup); //aggiunge i nodi del gruppo in DB
								getNodes(idgroup);//legge sul DB e popola la lista di nodi principale.
							} catch (DBOpenException e) {
								Log.e("DBOpenException onStart()", e.toString());
							} catch (InterruptedException e) {
								Log.e("InterruptedException onStart()", e.toString());
							} catch (ExecutionException e) {
								Log.e(" ExecutionException onStart()", e.toString());
							}
		    				
		    	   	    	fillListNodes(); //riempie la lista principale con gli id e i nomi dei Nodi esistenti				
		    	   	    	//dismissProgressDialog();
		        //----------------------------------------------------------------------//
				}else{
				
					getNodes(idgroup);//legge sul DB e popola la lista di nodi principale.
					fillListNodes(); //aggiunge le voci alla lista grafica
					//dismissProgressDialog();
				}
			}catch (DBOpenException e) {
	    		Log.e(TAG, e.getMessage());
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
			} catch (ExecutionException e) {
				Log.e(TAG, e.getMessage());
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage());
			}
	    }
		
		public void showProgressDialog(){
	        progressDialog = new ProgressDialog(this);
	        progressDialog.setMessage(this.getResources().getString(R.string.loading));
	        progressDialog.setCancelable(false);
	        progressDialog.setIndeterminate(true);
	        progressDialog.show();
		}
		
		public void dismissProgressDialog(){
			progressDialog.dismiss();
		}
	    
	    
        public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	Log.v("Posizione", "Oncreate");
        	try{
             	checkConnection();
             	setContentView(R.layout.list_nodes);
     
            	//showProgressDialog();
             	
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
        
    	
    	public HashMap<String, Object> readPreferences(){
    		// Preferences
            SharedPreferences prefs = getSharedPreferences(SettingsActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
    		
            HashMap<String,Object> hlist = new HashMap<String, Object>();
            
            Group gr = Group.groups.get(0);
            
            hlist.put(SettingsActivity.DEFAULT_KEY_URL_NODES,prefs.getString(SettingsActivity.DEFAULT_KEY_URL_NODES, gr.getUri()));
            hlist.put(SettingsActivity.DEFAULT_KEY_GROUP,prefs.getInt(SettingsActivity.DEFAULT_KEY_GROUP, gr.getId()));
            
            return hlist;
            
    	}
    	
    	
        
        @Override
        protected void onStart() {
        
        super.onStart();
        Log.v("Posizione", "Onstart");

		
        
        }
        
        private String matchUrl(String urlgroup) {
			if(urlgroup.contains("http")){
				return urlgroup;
			}else{
				return SettingsActivity.DEFAULT_VALUE_URL+urlgroup;
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
        
        public boolean isEmptyTableGroups() throws DBOpenException {
        	dbh=new DbHelper(getApplicationContext()); 
        	dbmanager= new DbManager(dbh,getApplicationContext());
        	return dbmanager.isEmptyTableGroups();
        }
        
    	private String getLastUpdate(Integer id_group) throws DBOpenException{
    		return dbmanager.getLastUpdate(id_group);
    	}
    	
    	public String getResponse(final String url){
    		
    		AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>(){

    			@Override
    			protected String doInBackground(Void...voids) {
    		
    		client = new RestManager(url);

    		try {
    		    String message = client.getNodes(true);
    			//String headers = client.getResponseHeaders();
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
        
    	private void getNodes(Integer id_group) throws InterruptedException, ExecutionException, DBOpenException{
    		dbh=new DbHelper(getApplicationContext()); 
        	dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
        	@SuppressWarnings("unchecked")
    		ArrayList<MapPoint> map=dbmanager.getNodes(id_group);
        	
        	MapPoint.points=map;
    	}
    	
    	private void clearNodes() throws InterruptedException, ExecutionException, DBOpenException{
    		
        	
        	MapPoint.points=new ArrayList<MapPoint>();
    	}
    	
    	private void getGroups() throws InterruptedException, ExecutionException, DBOpenException{
    		dbh=new DbHelper(getApplicationContext()); 
        	dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
        	@SuppressWarnings("unchecked")
    		ArrayList<Group> gr=dbmanager.getGroups();
        	Group.groups=gr;
    	}
    	
    	private void insertNodes(Integer group) throws DBOpenException{
    		dbh=new DbHelper(getApplicationContext()); 
        	dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
        	dbmanager.insertNodes(group, MapPoint.points);
    	}
    	
    	private void insertGroups() throws DBOpenException{
    		
    	    Log.v("Grandezza lista gruppi DOPO", ""+Group.groups.size());
    	    ArrayList<Group> temp = new ArrayList<Group>();
	        String[] whiteList = this.getResources().getStringArray(R.array.group_white_list);
	        List<String> arraylist = Arrays.asList(whiteList);
	        Log.v("list of groups PRIMA", Group.groups.toString());
    	    for(int i = 0; i <Group.groups.size();i++){
    	    	
    	    	Log.v("matching", Group.groups.get(i).getName()+" è contenuto in lista?");
    	    	
    	    	if((arraylist.contains(Group.groups.get(i).getName()))){
    	    		temp.add(Group.groups.get(i));
    	    	}
    	    }
    	    Log.v("list of groups DOPO", temp.toString());
    		
    		dbh=new DbHelper(getApplicationContext()); 
        	dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
        	dbmanager.insertGroups(temp);
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
        		try {
					dbmanager=new DbManager(dbh,NodeShotActivity.cntx);
				} catch (DBOpenException e1) {
					Log.e(TAG, e1.toString());
				}
        	
        		dialog.setTitle(getResources().getString(R.string.dialog1));
        		dialog.setContentView(R.layout.show_node);
			
        		TextView data=(TextView) dialog.findViewById(R.id.show_node);
		 	
        		ArrayList<MapPoint> map;
        		try {
        			
        			map = (ArrayList<MapPoint>)dbmanager.execute(6,0,current_item).get();
        			MapPoint unique=map.get(0);
        			String body=unique.toString(this);
        			
        			data.setText(body);
        		} catch (InterruptedException e) {
        			Log.e(TAG, e.toString());
        		} catch (ExecutionException e) {
        			Log.e(TAG, e.toString());
        		}
        }
        	
        	
        if(id==ID_SEND_MAIL){
        		dialog.setCanceledOnTouchOutside(true);
        		        	
        		dialog.setTitle(getResources().getString(R.string.dialog13));
        		dialog.setContentView(R.layout.send_mail);
			
        		final EditText edit1=(EditText) dialog.findViewById(R.id.namesurname);
        		final EditText edit2=(EditText) dialog.findViewById(R.id.body);
        		Button btn1=(Button) dialog.findViewById(R.id.send);
		 	
        		btn1.setOnClickListener(new OnClickListener() {
        			
        			@Override
        			public void onClick(View v) {
        				
        				String sender = edit1.getText().toString();
        				String body = edit2.getText().toString();
        				
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        String[] recipients = new String[]{"komodo00155@gmail.com"};
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Comunication from App - "+sender);
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
                        emailIntent.setType("text/plain");
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        finish();
        				
        			}
        		});
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
	    			,/*getResources().getString(R.string.dialog3)*/};
	    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
	    	builder.setTitle(getResources().getString(R.string.dialog0));

	    	builder.setItems(items, new DialogInterface.OnClickListener() {

	    	@SuppressWarnings("deprecation")
			@Override
	    	public void onClick(DialogInterface dialog, int which) {
	    		switch(which){
	    		case 0:
	    			showDialog(ID_SHOW_NODE);
	    		break;
	    		case 1:
	    			showDialog(ID_SEND_MAIL);
	    		break;
	    		}
	    	}
	    	});

	    	return builder.show();
	    }
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
	        
	        MenuItem settings=menu.add(0,1,0,getResources().getString(R.string.menu3));
	        settings.setIcon(android.R.drawable.ic_menu_preferences);

	        settings.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Intent intent = new Intent().setClass(ListShotActivity.this, SettingsActivity.class);
					startActivity(intent);
					return true;
				}
			});
	        
	        
	        
	        return true;
	        
		}
        

        
}
