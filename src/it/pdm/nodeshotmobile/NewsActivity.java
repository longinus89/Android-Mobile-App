/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.pdm.nodeshotmobile;

import static com.google.android.gcm.demo.app.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.google.android.gcm.demo.app.CommonUtilities.EXTRA_MESSAGE;
import static com.google.android.gcm.demo.app.CommonUtilities.SENDER_ID;
import static com.google.android.gcm.demo.app.CommonUtilities.SERVER_URL;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.pdm.nodeshotmobile.entities.MapPoint;
import it.pdm.nodeshotmobile.entities.News;
import it.pdm.nodeshotmobile.exceptions.DBOpenException;
import it.pdm.nodeshotmobile.managers.CalendarManager;
import it.pdm.nodeshotmobile.managers.DbManager;
import it.pdm.nodeshotmobile.managers.JsonManager;
import it.pdm.nodeshotmobile.managers.ListManager;
import it.pdm.nodeshotmobile.managers.RestManager;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.demo.app.ServerUtilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Main UI for the demo app.
 */
public class NewsActivity extends ListActivity {

    TextView mDisplay;
    AsyncTask<Void, Void, Void> mRegisterTask;
    ListView listNews;
    ListManager listmng;
    RestManager client;
    JsonManager jsmng;
    Integer current_item;
    
    static int ID_SHOW_NEWS = 0;
    
    static private String TAG = "NewsActivity"; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jsmng=new JsonManager(getApplicationContext());
        
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        
        String[] from={"id","name"}; //dai valori contenuti in queste chiavi
        int[] to={R.id.id_news,R.id.title_news}; //agli id delle view
        setContentView(R.layout.news_layout);
        listNews=getListView();
        
        listNews.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String idItem=""+(Integer)listmng.getMap().get(arg2).get("id");
				current_item=Integer.parseInt(idItem);
				//Log.v("Current Item in click:", ""+current_item);
				showDialog(0);
				
			}
        	
		});
        
        listmng=new ListManager(this,listNews,from,to,R.layout.row2);
        
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        mDisplay = (TextView) findViewById(R.id.display);
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(DISPLAY_MESSAGE_ACTION));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                //mDisplay.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);
                        // At this point all attempts to register with the app
                        // server failed, so we need to unregister the device
                        // from GCM - the app will try to register again when
                        // it is restarted. Note that GCM will send an
                        // unregistered callback upon completion, but
                        // GCMIntentService.onUnregistered() will ignore it.
                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }
    
    @Override
    protected void onStart() {
    
    	super.onStart();
        	
    		//mDisplay.append("Start");
    	
			//java.util.zip.GZIPOutputStream stream = new GZIPOutputStream(os)
    	    jsmng.getNews(getResponse()); //parsa il file Json recuperato tramite richiesta GET 
			//HTTP utilizzando architettura REST, popola quindi la lista di nodi principale
   	    	fillListNodes(); //riempie la lista principale con gli id e i nomi dei Nodi esistenti
        	
        //----------------------------------------------------------------------//
	
    
    }
	
	private void fillListNodes(){
		listmng.loadNews(News.news);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /*
             * Typically, an application registers automatically, so options
             * below are disabled. Uncomment them if you want to manually
             * register or unregister the device (you will also need to
             * uncomment the equivalent options on options_menu.xml).
             */
            /*
            case R.id.options_register:
                GCMRegistrar.register(this, SENDER_ID);
                return true;
            case R.id.options_unregister:
                GCMRegistrar.unregister(this);
                return true;
             */
            case R.id.options_clear:
                mDisplay.setText(null);
                return true;
            case R.id.options_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            //mDisplay.append(newMessage + "\n");
        }
    };
    
    
    public String getResponse(){
		
		AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>(){

			@Override
			protected String doInBackground(Void...voids) {
		
		client = new RestManager(NodeShotActivity.STRING_CONNECTION_NEWS);

		try {
		    String message = client.getNews();
			String headers = client.getResponseHeaders();
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
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
		
    	if(id==ID_SHOW_NEWS){
    		dialog.setCanceledOnTouchOutside(true);
    		dialog.setContentView(R.layout.show_news);
		
    		TextView data=(TextView) dialog.findViewById(R.id.show_news);
	 	
    		ArrayList<News> messages = (ArrayList<News>)News.news.clone();
    		
    		for(News n : messages){
    			if(n.getId()==current_item){
    	    		dialog.setTitle(n.getName());
            		String body=n.getContent()+"\n";
					data.setText(body);
    			}
     			
    		}
    	}
    }
	
    
	protected Dialog onCreateDialog(int id) {
    	
    	Dialog defaultd=new Dialog(this);
    	switch(id){
    		default:
    			return defaultd;
    		
    	}

    }
    
    
    

}



