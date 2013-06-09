package it.pdm.nodeshotmobile;

import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.entities.DbHelper;
import it.pdm.nodeshotmobile.entities.Group;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.entities.MapsOverlay;
import it.pdm.nodeshotmobile.exceptions.ConnectionException;
import it.pdm.nodeshotmobile.exceptions.DBOpenException;
import it.pdm.nodeshotmobile.managers.DbManager;
import it.pdm.nodeshotmobile.managers.GpsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapShotActivity extends MapActivity {
    /** Called when the activity is first created. */
	
	private List<Overlay> listPoints;
	
	private MapsOverlay marker_special;
	private HashMap<String,MapsOverlay> markers;
	private GeoPoint centerMap= new GeoPoint(41872225,12582114); //coordinate per centro in Roma
	private ArrayList<String> list_types;
	private HashMap<String, String> list_markers;
	private DbManager dbmanager;
	private MapView mapView;
	private GpsManager local;
	private Context cntx;
	private  final String TAG = "NodeShot - ";
	static private final int ID_OPTION_NODE = 0;
	static private final int ID_CONFIRM_GPS= 1;
	static private final int ID_SEARCH_NODES_POSITION= 2;
	static private final int ID_SEARCH_NODES_BY_NAME= 3;
	static private final int ID_SEARCH_NODES= 4;
	static private final int ID_SEARCH_NODES_TYPE= 5;
	
	/*ASSOLUTAMENTE DA OTTIMIZZARE*/
	int prec=0;
	int gps_location_lock = 0;
	/******************************/
	
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        	
        try{
        	checkConnection();
        	
        	cntx= getApplicationContext();
        	
        	//legge le preferenze, se non sono settate associa valori di default
        	HashMap<String, Object> preferences =readPreferences();
        	
        	//prelevo le preferenze sul gruppo di nodi
        	Integer group = (Integer)preferences.get("group");
        	
        	
        	markers = new HashMap<String, MapsOverlay>();
        	
        	setContentView(R.layout.map_view);
        	
        	mapView = (MapView) findViewById(R.id.mapview);
        	mapView.setBuiltInZoomControls(true);
        	mapView.getController().setCenter(centerMap); //setto il centro in Roma
        	mapView.getController().setZoom(11);
        
        	listPoints = mapView.getOverlays();
        	
        	/**/
        	Log.v("Sei dentro a MapShotActivity","CREATE");
        	listPoints.clear();
        	mapView.invalidate();
        	/**/
        	
        	DbHelper dbh=new DbHelper(cntx);
        	dbmanager= new DbManager(dbh,cntx);
        	list_types = dbmanager.getTypes(group);
        	list_markers = dbmanager.getMarkers(group);
        	
        	for(int i=0;i<list_types.size();i++){
        		
        		String imageResourceString = list_markers.get(list_types.get(i));
        		String names[] = imageResourceString.split("\\.");
        		String name = names[2];
        		
        		Log.i("Resource #"+i,name);
        		
        		int imageResource = getResources().getIdentifier(name, "drawable", getPackageName());
        		Drawable temp = getResources().getDrawable(imageResource);
        		markers.put(list_types.get(i),new MapsOverlay(temp, this));
        	}

        	Drawable drawable_special = this.getResources().getDrawable(R.drawable.red_marker);
        	marker_special = new MapsOverlay(drawable_special, this);
        	
				
		}        	
        catch (ConnectionException e){
			showToastLong(e.getMessage());
			closeApplication();
		} catch (DBOpenException e) {
			showToastLong(e.getMessage());
		}

            
        
        
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	/**/
    	Log.v("Sei dentro a MapShotActivity","START");
    		try {
				clearNodes(false);
			} catch (DBOpenException e) {
				Log.v("",""+e.getStackTrace());
			}
		
    	Log.v("Sei dentro a MapShotActivity","DOPO CLEAR START");
    	/**/
    	drawNodes(MapPoint.points);
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
                   MapShotActivity.this.finish();
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
    
    public boolean isEmptyTableNodes() throws DBOpenException {
    	DbHelper dbh=new DbHelper(getApplicationContext()); 
    	DbManager dbmanager= new DbManager(dbh,getApplicationContext());
    	return dbmanager.isEmptyTableNodes();
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

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public GeoPoint getCenterMap(){
		return centerMap;
	}
	
	public void drawNodes(final ArrayList<MapPoint> m){
		
		AsyncTask<String, Void, Void> creationPoints = new AsyncTask<String, Void, Void>(){

			@Override
			protected Void doInBackground(String ...strings) {
				

				
				for(int i=0;i<m.size();i++){
					MapPoint info=m.get(i);
		        	/*PECIONATA*/
					
					//Log.v("Stai stampando il nodo"+i,info.toString());
					GeoPoint point = new GeoPoint(info.getLatitude(), info.getLongitude());
					OverlayItem overlayitem = new OverlayItem(point, info.getName(), info.toString(getApplicationContext()));
					((MapsOverlay)markers.get(info.getType())).addOverlay(overlayitem);			
				}
				
				
				
	        	for(int j=0;j<list_types.size();j++){
	        			((MapsOverlay)markers.get(list_types.get(j))).populateNow();
	        			listPoints.add((MapsOverlay)markers.get(list_types.get(j)));	
	        	}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				mapView.postInvalidate();
			}
			
		};
		
		creationPoints.execute();
		
	}
	
	public void clearNodes(boolean clearnodes) throws DBOpenException{
		
    	markers = new HashMap<String, MapsOverlay>();
		
    	if(clearnodes){
    		MapPoint.points.clear();
    	}
    	
    	//legge le preferenze, se non sono settate associa valori di default
    	HashMap<String, Object> preferences =readPreferences();
		
    	//prelevo le preferenze sul gruppo di nodi
    	Integer group = (Integer)preferences.get("group");
		
		//MapPoint.points.clear();
		
		DbHelper dbh=new DbHelper(cntx);
    	dbmanager= new DbManager(dbh,cntx);
    	list_types = dbmanager.getTypes(group);
    	list_markers = dbmanager.getMarkers(group);
    	
    	for(int i=0;i<list_types.size();i++){
    		
    		String imageResourceString = list_markers.get(list_types.get(i));
    		String names[] = imageResourceString.split("\\.");
    		String name = names[2];
    		
    		Log.i("Resource #"+i,name);
    		
    		int imageResource = getResources().getIdentifier(name, "drawable", getPackageName());
    		Drawable temp = getResources().getDrawable(imageResource);
    		markers.put(list_types.get(i),new MapsOverlay(temp, this));
    	}

    	Drawable drawable_special = this.getResources().getDrawable(R.drawable.red_marker);
    	marker_special = new MapsOverlay(drawable_special, this);
	    
    	mapView.getOverlays().clear();
	    mapView.invalidate();
	}
	

	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        MenuItem h=menu.add(0,1,0,getResources().getString(R.string.menu1));
        h.setIcon(android.R.drawable.ic_menu_mylocation);
        
        h.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(ID_CONFIRM_GPS);
				return true;
			}
		});
        
        MenuItem search=menu.add(0,1,0,getResources().getString(R.string.menu2));
        search.setIcon(android.R.drawable.ic_menu_search);
        
        search.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(ID_SEARCH_NODES);
				return true;
			}
		});
        
        return true;
        
	}
	
	
	
    protected Dialog onCreateDialog(int id) {
    	
		final DbHelper dbh=new DbHelper(getApplicationContext());
	 	
    	
    	switch(id){
    		/*case ID_OPTION_NODE:
    			AlertDialog dialog=createAlertDialogOptions(); 		
			return dialog;*/
    		case ID_CONFIRM_GPS:
    			final Dialog dialog_Gps = new Dialog(this);
    			dialog_Gps.setCanceledOnTouchOutside(true);
    			dialog_Gps.setContentView(R.layout.search_gps);
    			dialog_Gps.setTitle(getResources().getString(R.string.dialog5));
    			
				Button ok=(Button)dialog_Gps.findViewById(R.id.confirm_gps);
				Button undo=(Button)dialog_Gps.findViewById(R.id.undo_request);
				
				ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
		    			getPosition();
		    			dialog_Gps.dismiss();
					}
				});
    		
    			undo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					 	dialog_Gps.dismiss();
					}
				});
    			
    		return dialog_Gps;
    		
    	    case ID_SEARCH_NODES_BY_NAME:
    			final Dialog dialog_Search_Node_By_Name = new Dialog(this);
    			dialog_Search_Node_By_Name.setCanceledOnTouchOutside(true);
    			
    			dialog_Search_Node_By_Name.setContentView(R.layout.search_nodes_by_name);
    			dialog_Search_Node_By_Name.setTitle(getResources().getString(R.string.dialog6));
    			
    			Button search_by_name=(Button)dialog_Search_Node_By_Name.findViewById(R.id.confirm_search_by_name);
    			Button undo_by_name=(Button)dialog_Search_Node_By_Name.findViewById(R.id.undo_request_by_name);
    			
    			final EditText name=(EditText)dialog_Search_Node_By_Name.findViewById(R.id.name_to_search);
    			
    			search_by_name.setOnClickListener(new OnClickListener() {
    				
    				@SuppressWarnings("unchecked")
					@Override
    				public void onClick(View v) {
    					dialog_Search_Node_By_Name.dismiss();
    			    	ArrayList<MapPoint> searched;
    			    	try {
    			    		
    			    		
							dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
						} catch (DBOpenException e1) {
							Log.e(TAG, e1.toString());
						}
    			    	
    			    	String name_to_search;
    			    	
    			    	try {
    			    		name_to_search=name.getText().toString();
    						
    					}catch (NullPointerException e) {
    						showToastLong(getResources().getString(R.string.error0));
    						return;
    					}
    			    	
    					try {
    						
    						HashMap<String, Object> preferences =readPreferences();
    						
    						//prelevo le preferenze sul gruppo di nodi
    				    	Integer idgroup = (Integer)preferences.get(SettingsActivity.DEFAULT_KEY_GROUP);
    				    	String urlgroup = (String)preferences.get(SettingsActivity.DEFAULT_KEY_URL_NODES); 
    			    		
    				    	Log.v("CONTENUTO RICHIESTA", "IDGROUP: "+idgroup+", NAMETOSEARCH: "+name_to_search);
    						
    						searched = (ArrayList<MapPoint>) dbmanager.execute(3,idgroup,name_to_search).get();
    						
    						if(searched.isEmpty()){
    							showToastShort(getResources().getString(R.string.result0));
    							return;
    						}
    						clearNodes(true);//cancello tutti gli Overlay e rispettivi piani dalla mappa.
    						drawNodes(searched);
    					}catch (IndexOutOfBoundsException e ){
    						Log.e("ERROR DB NULL POINTER LIST: ",e.getMessage());
    						showToastShort(getResources().getString(R.string.result0));
    					}
    					catch(NullPointerException e){
    						Log.e("ERROR DB NULL POINTER LIST: ",e.getMessage());
    						showToastShort(getResources().getString(R.string.result0));
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						//Log.e("",e.getMessage());
    					} catch (ExecutionException e) {
    						// TODO Auto-generated catch block
    						//Log.e("",e.getMessage());
    					} catch (DBOpenException e) {
    						Log.e("DBOpenException: ",e.getMessage());
						}
    				}
    			}); 
    			
    			undo_by_name.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					dialog_Search_Node_By_Name.dismiss();
    				}
    			});
    			
    		return dialog_Search_Node_By_Name;		
    
    	    case ID_SEARCH_NODES_TYPE:
    			final Dialog dialog_Search_Node_By_Type = new Dialog(this);
    			dialog_Search_Node_By_Type.setCanceledOnTouchOutside(true);
    			
    			dialog_Search_Node_By_Type.setContentView(R.layout.search_nodes_by_type);
    			dialog_Search_Node_By_Type.setTitle(getResources().getString(R.string.dialog7));
    			
    			Button search_by_type=(Button)dialog_Search_Node_By_Type.findViewById(R.id.confirm_search_by_name);
    			Button undo_by_type=(Button)dialog_Search_Node_By_Type.findViewById(R.id.undo_request_by_name);
    			final Spinner type=(Spinner)dialog_Search_Node_By_Type.findViewById(R.id.select_node_type);
    			
    			ArrayAdapter<String> spinnerArrayAdapter;
    			
    				HashMap<String, Object> preferences =readPreferences();
					
					//prelevo le preferenze sul gruppo di nodi
			    	Integer idgroup = (Integer)preferences.get(SettingsActivity.DEFAULT_KEY_GROUP);
    				
			try {
				
				ArrayList<String> types = getTypes(idgroup);
				ArrayList<String> result = new ArrayList<String>();
		    	String[] translates = cntx.getResources().getStringArray(R.array.source_labels_group_ninux);
		    	
		    	for(String stype : types){
		    		Integer itype = Integer.parseInt(stype);
		    		result.add(translates[itype]);
		    	}
		    	
		    	result.add(0, "All");
				
				spinnerArrayAdapter = new ArrayAdapter<String>(this,
				        android.R.layout.simple_spinner_item, result);
				type.setAdapter(spinnerArrayAdapter);
			} catch (InterruptedException e2) {
				Log.e("InterruptedException",e2.getMessage());
			} catch (ExecutionException e2) {
				Log.e("ExecutionException",e2.getMessage());
			} catch (DBOpenException e2) {
				Log.e("DBOpenException",e2.getMessage());
			}  
    			
    			search_by_type.setOnClickListener(new OnClickListener() {
    				
    				@SuppressWarnings("unchecked")
					@Override
    				public void onClick(View v) {
    					
    					Integer ntype = 0;
    					dialog_Search_Node_By_Type.dismiss();
    			    	ArrayList<MapPoint> searched;
    			    	try {	
    			    		HashMap<String, Object> preferences =readPreferences();
    						
    						//prelevo le preferenze sul gruppo di nodi
    				    	Integer idgroup = (Integer)preferences.get(SettingsActivity.DEFAULT_KEY_GROUP);
    				    	String urlgroup = (String)preferences.get(SettingsActivity.DEFAULT_KEY_URL_NODES); 
    			    		
    			    		
							dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
						} catch (DBOpenException e1) {
							Log.e(TAG, e1.toString());
						}
    			    	
    			    	String type_to_search;
    			    	
    			    	try {

   			    			type_to_search=type.getSelectedItem().toString();
   			    			
   			    			String[] translates = cntx.getResources().getStringArray(R.array.source_labels_group_ninux);
   			    			for(int i=0;i<translates.length;i++){
   			    				if(translates[i].equals(type_to_search)){
   			    					ntype = i;
   			    				}
   			    			}
    						
    					}catch (NullPointerException e) {
    						showToastLong(getResources().getString(R.string.error0));
    						return;
    					}
    			    	
    					try {
    						
    						HashMap<String, Object> preferences =readPreferences();
    						
    						//prelevo le preferenze sul gruppo di nodi
    				    	Integer idgroup = (Integer)preferences.get(SettingsActivity.DEFAULT_KEY_GROUP);
    			    		
    						Log.v("Tipo ricercato", type_to_search);
    				    	
    			    		if(!type_to_search.equals("All")){
    							searched = (ArrayList<MapPoint>) dbmanager.execute(9,idgroup,Integer.toString(ntype)).get();
    			    		}else{
    			    			searched = (ArrayList<MapPoint>) dbmanager.execute(0,idgroup).get();
    			    		}
    			    		
    						if(searched.isEmpty()){
    							showToastShort(getResources().getString(R.string.result0));
    							return;
    						}
    						clearNodes(true);//cancello tutti gli Overlay e rispettivi piani dalla mappa.
    						drawNodes(searched);
    					}catch (IndexOutOfBoundsException e ){
    						Log.e("ERROR DB NULL POINTER LIST: ",e.getMessage());
    						showToastShort(getResources().getString(R.string.result0));
    					}
    					catch(NullPointerException e){
    						Log.e("ERROR DB NULL POINTER LIST: ",e.getMessage());
    						showToastShort(getResources().getString(R.string.result0));
    					} catch (InterruptedException e) {
    						// TODO Auto-generated catch block
    						//Log.e("",e.getMessage());
    					} catch (ExecutionException e) {
    						// TODO Auto-generated catch block
    						//Log.e("",e.getMessage());
    					} catch (DBOpenException e) {
    						Log.e("DBOpenException",e.getMessage());
						}
    				}
    			}); 
    			
    			undo_by_type.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					dialog_Search_Node_By_Type.dismiss();
    				}
    			});
    			
    		return dialog_Search_Node_By_Type;
    			
    		
    case ID_SEARCH_NODES:
    	AlertDialog ald=createAlertDialog();
    	ald.setCanceledOnTouchOutside(true);
    	return ald;
    		
    case ID_SEARCH_NODES_POSITION:
    	
		final Dialog dialog_Search_Node = new Dialog(this);
		dialog_Search_Node.setContentView(R.layout.search_nodes_position);
		dialog_Search_Node.setTitle(getResources().getString(R.string.dialog8));
		
		Button search=(Button)dialog_Search_Node.findViewById(R.id.confirm_search);
		Button undo2=(Button)dialog_Search_Node.findViewById(R.id.undo_request);
		final EditText latitude=(EditText)dialog_Search_Node.findViewById(R.id.latitude);
		final EditText longitude=(EditText)dialog_Search_Node.findViewById(R.id.longitude);
		final SeekBar precision=(SeekBar)dialog_Search_Node.findViewById(R.id.precision);
		final TextView label_precision=(TextView)dialog_Search_Node.findViewById(R.id.label_precision2);
		
		precision.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				if(precision!=null){
					prec=precision.getProgress();
				}
				
				
				label_precision.setText(""+prec);
				
				
			}
		});

		
		search.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				
		    	ArrayList<MapPoint> searched;
		    	try {
		    		
		    		
		    		
					dbmanager= new DbManager(dbh,NodeShotActivity.cntx);
				} catch (DBOpenException e1) {
					Log.e(TAG, e1.toString());
				}
		    	Integer lat=0;
		    	Integer lng=0;
		    	try {
		    		lat=Integer.parseInt(latitude.getText().toString());
		    		lng=Integer.parseInt(longitude.getText().toString());
		    		//Log.v("","Fields\n\nlat="+lat+"\nlng="+lng+"\nprec="+prec);
					
				}catch (NumberFormatException e) {
					showToastLong(getResources().getString(R.string.error0));
					return;
				}
		    	
				try {
					HashMap<String, Object> preferences =readPreferences();
					
					//prelevo le preferenze sul gruppo di nodi
			    	Integer idgroup = (Integer)preferences.get(SettingsActivity.DEFAULT_KEY_GROUP);
			    	String urlgroup = (String)preferences.get(SettingsActivity.DEFAULT_KEY_URL_NODES); 
		    		
					
					searched = (ArrayList<MapPoint>) dbmanager.execute(1,idgroup,lat,lng,prec,prec).get();
					if(searched.isEmpty()){
						showToastShort(getResources().getString(R.string.result0));
						return;
					}
					clearNodes(true);//cancello tutti gli Overlay e rispettivi piani dalla mappa.
					drawNodes(searched);
					dialog_Search_Node.dismiss();
				}catch (IndexOutOfBoundsException e ){
					Log.e("ERROR DB NULL POINTER LIST: ",e.getMessage());
					showToastShort(getResources().getString(R.string.result0));
				}
				catch(NullPointerException e){
					Log.e("ERROR DB NULL POINTER LIST: ",e.getMessage());
					showToastShort(getResources().getString(R.string.result0));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//Log.e("",e.getMessage());
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					//Log.e("",e.getMessage());
				} catch (DBOpenException e) {
					Log.e("DBOpenException",e.getMessage());
				}
			}
		}); 
		
		undo2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 	dialog_Search_Node.dismiss();
			}
		});
	
		
	return dialog_Search_Node;
    }
    	
    		return null;
}
    
    
    public AlertDialog createAlertDialog(){
		
    	final CharSequence[] items=new CharSequence []{getResources().getString(R.string.dialog9)
    			,getResources().getString(R.string.dialog10),getResources().getString(R.string.dialog11)};
    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
    	builder.setTitle("Ricerca Nodi");

    	builder.setItems(items, new DialogInterface.OnClickListener() {

    	@Override
    	public void onClick(DialogInterface dialog, int which) {
    		switch(which){
    		case 0:
    			showDialog(ID_SEARCH_NODES_BY_NAME);
    		break;
    		case 1:
    			showDialog(ID_SEARCH_NODES_POSITION);
    		break;
    		case 2:
    			showDialog(ID_SEARCH_NODES_TYPE);
    		break;
    		}
    	}
    	});

    	return builder.show();
    }
    
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(cntx.getResources().getString(R.string.activeGps))
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                       startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                   }
               })
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                   }
               });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    
	public ArrayList<String> getTypes(Integer id_group) throws InterruptedException, ExecutionException, DBOpenException{
    	
		return dbmanager.getTypes(id_group);
	}
	
	public void getPosition(){
		/* Use the LocationManager class to obtain GPS locations */

		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	
	    if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
	    	//showToastLong(cntx.getResources().getString(R.string.unableGps));
	    	buildAlertMessageNoGps();
	    	return;
	    }
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		    	GeoPoint point = new GeoPoint(
			            (int) (location.getLatitude()*SettingsActivity.FACTOR), 
			            (int) (location.getLongitude()*SettingsActivity.FACTOR));
			        
		    	int lat =(int)(location.getLatitude()*SettingsActivity.FACTOR);
		    	int longi =(int)(location.getLongitude()*SettingsActivity.FACTOR);
		    	
		    	if(gps_location_lock==0){
		    	Log.e("Position ------->", "Lat = "+lat+"  Long= "+longi);
		    	
		    		MapController mapController=mapView.getController();
		    		mapController.animateTo(point);
		    		//mapController.setZoom(10);
		    	
		    		OverlayItem overlayitem = new OverlayItem(point, getResources().getString(R.string.dialog12), "Lat: "+(int)location.getLatitude()*SettingsActivity.FACTOR+"\n" +
					"Long: "+(int)location.getLongitude()*SettingsActivity.FACTOR+"\n");
			        
		    		marker_special.addOverlay(overlayitem);
		    		marker_special.populateNow();
		    		listPoints.add(marker_special);
		    		
		    		gps_location_lock = 1;
		    	}
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {
		    	
		    	
		    	
		    }

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
    	
			
		//return loc;*/
	}
			

	
	
	
	

	
	
	









}