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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import it.pdm.nodeshotmobile.entities.News;
import it.pdm.nodeshotmobile.managers.ListManager;
import it.pdm.nodeshotmobile.managers.RestManager;
import it.pdm.nodeshotmobile.managers.XmlManager;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.demo.app.ServerUtilities;
import com.msi.androidrss.RSSFeed;
import com.msi.androidrss.RSSHandler;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
    Context current;
    ListManager listmng;
    RestManager client;
    XmlManager xsmng;
    Integer current_item;
    
    static int ID_SHOW_NEWS = 0;
    
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	ArrayList ob = (ArrayList)msg.obj;
            trunkList();
            fillListNodes(ob); //riempie la lista principale con gli id e i nomi dei Nodi esistenti

        }
    };
    
    static private String TAG = "NewsActivity"; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xsmng=new XmlManager();
        
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        
        String[] from={"id","name"}; //dai valori contenuti in queste chiavi
        int[] to={R.id.id_news,R.id.title_news}; //agli id delle view
        setContentView(R.layout.news_layout);
        
        listNews=getListView();
        
        current = this;
        
        listNews.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String idItem=""+(Integer)listmng.getMap().get(arg2).get("id");
				current_item=Integer.parseInt(idItem);
				//Log.v("Current Item in click:", ""+current_item);
				showDialog(ID_SHOW_NEWS);
				
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
        
    	
		//mDisplay.append("Start");
	AsyncTask< Void, Void, Void> getNewsThread = new AsyncTask<Void, Void, Void>() {
		
		private ProgressDialog progressDialog = null;
		
        @Override
        protected Void doInBackground(Void... params) {
	    try {
	    	getNews();
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
		} catch (XmlPullParserException e) {
			Log.e("XmlPullParserException", e.getMessage());
		}
	    
	    //parsa il file Json recuperato tramite richiesta GET 
		//HTTP utilizzando architettura REST, popola quindi la lista di nodi principale
	    	
	    Message msg = new Message();
	    msg.obj = xsmng.getParsedData();
	    mHandler.sendMessage(msg);
	    	
	    	return null;
        }

    	@Override
        protected void onPreExecute() {
    		showProgressDialog();

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
    	protected void onPostExecute(Void result) {
    		dismissProgressDialog();
    	}
    	
	};
    //----------------------------------------------------------------------//
	
	getNewsThread.execute();
        
    }
	
	private void fillListNodes(ArrayList list){
		
		listmng.loadNews(list);
	}
	private void trunkList(){
		listmng.deleteAll();
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
    
    
    public void getNews() throws ClientProtocolException, IOException, XmlPullParserException{
		    	
    	xsmng.parseRSSNews(SettingsActivity.DEFAULT_VALUE_URL_NEWS);
	}
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
		
    	if(id==ID_SHOW_NEWS){
    		dialog.setCanceledOnTouchOutside(true);
    		dialog.setContentView(R.layout.show_news);
		
    		TextView date=(TextView) dialog.findViewById(R.id.date);
    		
    		TextView body=(TextView) dialog.findViewById(R.id.body);

    	    TextView link = (TextView) dialog.findViewById(R.id.link);
    	    link.setMovementMethod(LinkMovementMethod.getInstance());
    	    
    	    
	 	
    		ArrayList<News> messages = (ArrayList<News>)xsmng.getParsedData();
    		
    		for(News n : messages){
    			if(n.getId()==current_item){
    	    		dialog.setTitle(n.getTitle());
            		
    	    		date.setText(n.getDate());
    	    		body.setText(Html.fromHtml(n.getContent()));
					link.setText(n.getLink());
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



