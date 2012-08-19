package it.pdm.nodeshotmobile;

import it.pdm.nodeshotmobile.R;
import it.pdm.nodeshotmobile.exceptions.ConnectionException;

import java.io.File;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TabHost;
import android.widget.Toast;

public class NodeShotActivity extends TabActivity {
		static final String STRING_CONNECTION_NODES = "http://map.ninux.org/nodes.json";
		static final String STRING_CONNECTION_NEWS = "http://test.ninux.org/~komodo00155/gsoc/get_news.php";
	    static public double factor=1E6; 
		static final double MAX_DAYS_FOR_UPDATE=7.0;
		static Context cntx;
        
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.home);
            
            try{
            	checkConnection();
            
            ProgressDialog progressDialog = new ProgressDialog(NodeShotActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            Resources res = getResources(); // Resource object to get Drawables
            TabHost tabHost = getTabHost();  // The activity TabHost
            TabHost.TabSpec spec;  // Reusable TabSpec for each tab
            Intent intent;  // Reusable Intent for each tab
            
            NodeShotActivity.cntx=NodeShotActivity.this;
            
            // Create an Intent to launch an Activity for the tab (to be reused)
            intent = new Intent().setClass(this, ListShotActivity.class);
            spec = tabHost.newTabSpec("list").setIndicator(getResources().getString(R.string.tab0),
                              res.getDrawable(R.drawable.icon_list))
                          .setContent(intent);
            tabHost.addTab(spec);
            
            // Create an Intent to launch an Activity for the tab (to be reused)
            intent = new Intent().setClass(this, MapShotActivity.class);
            
            // Initialize a TabSpec for each tab and add it to the TabHost
            spec = tabHost.newTabSpec("map").setIndicator(getResources().getString(R.string.tab1),
                              res.getDrawable(R.drawable.icon_map)).setContent(intent);
            tabHost.addTab(spec);

            // Do the same for the other tabs
            intent= new Intent().setClass(this, ARShotActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://"), "application/mixare-json");
            spec = tabHost.newTabSpec("ar").setIndicator(getResources().getString(R.string.tab2),res.getDrawable(R.drawable.ic_tab_artists_white)).setContent(intent);
            tabHost.addTab(spec);
            
          // Create an Intent to launch an Activity for the tab (to be reused)
            intent = new Intent().setClass(this, NewsActivity.class);
            spec = tabHost.newTabSpec("news").setIndicator(getResources().getString(R.string.tab3),
                              res.getDrawable(android.R.drawable.ic_dialog_info))
                          .setContent(intent);
            tabHost.addTab(spec);

            progressDialog.dismiss();
            tabHost.setCurrentTab(0);
            
            }catch(ConnectionException e){
    			showToastLong(e.getMessage());
    			closeApplication();
            }
            
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
                           NodeShotActivity.this.finish();
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
        
        
        


	
}
