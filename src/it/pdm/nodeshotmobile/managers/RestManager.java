package it.pdm.nodeshotmobile.managers;

import it.pdm.nodeshotmobile.SettingsActivity;
import it.pdm.nodeshotmobile.entities.RestFullValues;
import it.pdm.nodeshotmobile.entities.RequestMethod;
import it.pdm.nodeshotmobile.exceptions.AcceptEncodingException;
import it.pdm.nodeshotmobile.exceptions.AcceptException;
import it.pdm.nodeshotmobile.exceptions.GroupByException;
import it.pdm.nodeshotmobile.exceptions.OrderByException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

public class RestManager {

    private ArrayList <NameValuePair> params;
    private ArrayList<String> accept;
    private ArrayList<String> accept_encoding;
    private ArrayList<String> if_modified_since;
    private String url;

    private int responseCode;
    private String message;
    private Header[] responseH;
    private String response ="VUOTO";

    public String getResponseMessage() {
        return response;
    }
    
    public String getResponseHeaders() {
    	
    	String result="";
    	
    	for(Header head : responseH){
    		result+= head.getName()+" : "+head.getValue()+"\n";
    	}
    	
    	return result;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestManager(String url)
    {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        
        accept = new ArrayList<String>();
        accept_encoding = new ArrayList<String>();
        if_modified_since = new ArrayList<String>();
    }

    public void addParam(String name, String value)
    {
        params.add(new BasicNameValuePair(name, value));
    }
    
    public void clearParams(String name, String value){
        params.clear();
    }

    public void clearHeaders(String name, String value){
        accept.clear();
        accept_encoding.clear();
    	if_modified_since.clear();
    }
    
    public String getAccept(){

        String result="";
        
        for(String h : accept)
        {
        	result+=h;
        	result+=";";
        }
        
        return result;
    }
    
    public String getAcceptEncoding(){
        
        String result="";
        
        for(String h : accept_encoding)
        {
        	result+=h;
        	result+=";";
        }
        
        return result;
    }
    
  
    public String getIfModifiedSince(){
    	
        String result="";
        
        for(String h : if_modified_since)
        {
        	result+=h;
        	result+=";";
        }
        
        return result;
    }

    public void Execute(RequestMethod method) throws UnsupportedEncodingException, MalformedURLException{
        switch(method) {
            case GET:
            {
                //add parameters
                String combinedParams = "";
                if(!params.isEmpty()){
                    combinedParams += "?";
                    for(NameValuePair p : params)
                    {
                        String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(),"UTF-8");
                        if(combinedParams.length() > 1)
                        {
                            combinedParams  +=  "&" + paramString;
                        }
                        else
                        {
                            combinedParams += paramString;
                        }
                    }
                }
                

                HttpGet request = new HttpGet(url /*+ combinedParams*/);

                //add headers
                
                //request.addHeader("Host", SettingsActivity.DEFAULT_VALUE_URL);                
                request.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                request.addHeader("Accept-Encoding", getAcceptEncoding());
                //request.addHeader("Connection","close");      
                
                //request.addHeader("If-Modified-Since", getIfModifiedSince());

                executeRequest(request);
                break;
            }
            
            case POST:
            {
                HttpPost request = new HttpPost(url);

                if(!params.isEmpty()){
                    request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                }

                //add headers
                
                request.addHeader("Accept", getAccept());                
                request.addHeader("Accept-Encoding", getAcceptEncoding());
                request.addHeader("If-Modified-Since", getIfModifiedSince());
                
                executeRequest(request);
                break;
            }
        }
    }
    
    private void executeRequest(HttpUriRequest request) throws UnsupportedEncodingException, MalformedURLException
    {
    	
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
        	
        	/*PARTE STAMPA OUTPUT*/
        	
        	String headers="";
        	
        	for(Header head : request.getAllHeaders()){
        		headers+= head.getName()+": "+head.getValue()+"\n";
        	}
        	
        	//String params="?";
        	RequestLine params=request.getRequestLine();
        	
        	Log.v("Request url",params.toString());
        	Log.v("Request Header",headers);
        
        	httpResponse = client.execute(request);
        	        	
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();
            

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);
                responseH = httpResponse.getAllHeaders();

                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            Log.e("ClientprotocolException",e.toString());
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            Log.e("IOException",e.toString());
        } catch (IllegalStateException e) {
            client.getConnectionManager().shutdown();
            Log.e("IllegalStateException",e.toString());
        }
    }

    private static String convertStreamToString(InputStream iss) throws IOException {
    	
    	GZIPInputStream in = new GZIPInputStream(iss);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
            	in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    
    public String getNodes(boolean status) throws ClientProtocolException, URISyntaxException, IOException, AcceptEncodingException{
    	//accept(RestFullValues.XML);
    	acceptEncoding(RestFullValues.GZIP);
    	if(status){
    		//addParam("group-by-status", RestFullValues.TRUE);
    	}else{
    		//addParam("group-by-status", RestFullValues.FALSE);
    	}
    	//(RestFullValues.);
    	Execute(RequestMethod.GET);
    	//Log.v("responsemessage", getResponseMessage());
    	return getResponseMessage();
    }
    
    public String getNews() throws UnsupportedEncodingException, AcceptException, AcceptEncodingException, MalformedURLException{
    	//acceptEncoding(RestFullValues.GZIP);
    	//(RestFullValues.);
    	Execute(RequestMethod.GET);
    	return getResponseMessage();
    }
    
    
    public String getNode(String name) throws UnsupportedEncodingException, MalformedURLException{
    	addParam("slug", name);
    	Execute(RequestMethod.GET);
    	return getResponseMessage();
    }

    public void accept(String application) throws AcceptException{
    	
    	boolean check=false;
    	
    	for(String value : RestFullValues.acceptValues){
    		if(value.equals(application)){
    	        accept.add(value); //"Accept",
    			check=true;
    		}
    	}
    	
    	if(!check){
    		throw new AcceptException();
    		}
    }	
    
    private void acceptEncoding(String encoding) throws AcceptEncodingException{
    	
    	boolean check=false;
    	
    	for(String value : RestFullValues.acceptEncodingValues){
    		if(value.equals(encoding)){
    	    	accept_encoding.add(encoding); //"Accept-Encoding",
    			check=true;
    		}
    	}
    	
    	if(!check){
    		throw new AcceptEncodingException();
    		}
    }
    
    private void ifModifiedSince(String date){
		if_modified_since.add(date); //"If-Modified-Since"
    }
    
    private void groupByStatus(String status) throws GroupByException{
    	
    	boolean check=false;
    	
    	for(String value : RestFullValues.groupByValues){
    		if(value.equals(status)){    	    	
    	    	addParam("group-by-status", value);
    			check=true;
    		}
    	}
    	
    	if(!check){
    		throw new GroupByException();
    		}
    }
    
    private void orderBy(String by) throws OrderByException{
    	
    	boolean check=false;
    	
    	for(String value : RestFullValues.orderByValues){
    		if(value.equals(by)){
    			addParam("order-by",by);
    			check=true;
    		}
    	}
    	
    	if(!check){
    		throw new OrderByException();
    		}
    	
    }
    
    
    
    
    
    
    
    
}