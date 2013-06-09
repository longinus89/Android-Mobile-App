package it.pdm.nodeshotmobile;

import it.pdm.nodeshotmobile.entities.DbHelper;
import it.pdm.nodeshotmobile.entities.Group;
import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.exceptions.DBOpenException;
import it.pdm.nodeshotmobile.managers.DbManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends Activity{
	
	// Identificatore delle preferenze dell'applicazione
    final static String MY_PREFERENCES = "pdmpref";
	static public String PATH="/mnt/sdcard/";
    static public double FACTOR=1E6; 
	static public final double MAX_DAYS_FOR_UPDATE=7.0;

    // Costante relativa al nome della particolare preferenza
    static public final String DEFAULT_KEY_URL_NODES = "url";
    static public final String DEFAULT_KEY_GROUP= "group";
    static public final String DEFAULT_VALUE_URL_GROUPS = "/api/v1/zones/";
    static public final String DEFAULT_VALUE_URL_NODES = "http://longinuslab.it/testing/nsm/json/nodes.json";
    static public final String DEFAULT_VALUE_URL = "http://nodeshot2.ninux.org";
    
	static public final String DEFAULT_VALUE_URL_NEWS = "http://blog.ninux.org/feed/";
    //static public final String DEFAULT_VALUE_URL_NEWS = "http://nodeshot2.ninux.org/media/external/rss/provincia-di-roma.xml";
    
    static public final int DEFAULT_VALUE_URI_IMAGE = R.array.source_images_group_ninux;
    static public final Integer DEFAULT_VALUE_GROUP = 1;
    
	
    public String STRING_CONNECTION;
    public String TAG = "SettingsActivity";
    
	private Context cntx;
	
	EditText url;
	String surl;
	Button btn_confirm;
	
    public void showToastLong(String text){
    	Toast t=Toast.makeText(cntx, text, Toast.LENGTH_LONG);
    	t.setGravity(Gravity.CENTER,0,0);
    	t.show();
    }
    
    
    public void showToastShort(String text){
    	Toast t=Toast.makeText(cntx, text, Toast.LENGTH_SHORT);
    	t.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
    	t.show();
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
	
	public void savePreferences(Group gr){
		// Preferences
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SettingsActivity.DEFAULT_KEY_URL_NODES, gr.getUri());
        editor.putInt(SettingsActivity.DEFAULT_KEY_GROUP, gr.getId());
        editor.commit();
        
	}
	
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    
	    cntx=SettingsActivity.this;
	    
	    HashMap<String, Object> preferences = readPreferences();
	    Integer idg = (Integer)preferences.get(DEFAULT_KEY_GROUP);
	    
	    //url=(EditText) findViewById(R.id.url);
	    
	    final Spinner spinner = (Spinner) findViewById(R.id.groups);
	    
	    
	    
	    ArrayAdapter<String> spinnerArrayAdapter;
		try {
			spinnerArrayAdapter = new ArrayAdapter<String>(this,
			        android.R.layout.simple_spinner_item, getGroups());
		    spinner.setAdapter(spinnerArrayAdapter);  
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.e(TAG, e.getMessage());
		} catch (DBOpenException e) {
			Log.e(TAG, e.getMessage());
		}
		
		spinner.setSelection(idg-1); // da controllare
	    
	    btn_confirm=(Button)findViewById(R.id.btn_confirm);
	    
	    btn_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int id = (int)spinner.getSelectedItemId();
				Group gr = Group.groups.get(id);
				savePreferences(gr);
				
				try {
					Thread.sleep(1000);
					showToastShort("Caricamento gruppo in corso. Non premere nessun tasto!!");
					Intent intent = new Intent().setClass(SettingsActivity.this, NodeShotActivity.class);
					startActivity(intent);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	   
	    
	    
	}
	
	/**
	 * Returns the URL defined by the user. It can be empty
	 * @return {@link String} with the URL
	 */
	public String getUrl(){
		return surl;
	}
	
	public ArrayList<String> getGroups() throws InterruptedException, ExecutionException, DBOpenException{
    	
		
		ArrayList<String> list_strings = new ArrayList<String>();
		for(int i = 0;i<Group.groups.size();i++){
			list_strings.add(Group.groups.get(i).getName());
		}
		
    	return list_strings;
	}
	
	/**
	 * Checks if the user inserted a not-empty-String containing a URL 
	 * @param url A string to check
	 * @return TRUE if it's a valid URL, FALSE otherwise.
	 */

	
	public boolean validateUrl(String url){
		String urlPattern="^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
	    if(!url.equals("") && url.matches(urlPattern))	//controllo se l'url � valido
	    	return true;
	    return false;
	}
	
}
