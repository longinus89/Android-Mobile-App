package it.pdm.nodeshotmobile.entities;

import it.pdm.nodeshotmobile.R;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class MapsOverlay extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public MapsOverlay(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  mContext = context;
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	}
	
	public void populateNow(){
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}

	@Override
	public int size() {
	  return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		
		final int num=index;
		Resources res=mContext.getResources();
		final CharSequence[] items=new CharSequence []{res.getString(R.string.dialog2)
				,/*res.getString(R.string.dialog3),res.getString(R.string.dialog4)*/};
    	AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
    	builder.setTitle(res.getString(R.string.dialog0));
    	builder.setItems(items, new DialogInterface.OnClickListener() {

    	@Override
    	public void onClick(DialogInterface dialog, int which) {
    		switch(which){
    		case 0:
    			 OverlayItem item = mOverlays.get(num);
    			  AlertDialog.Builder dialogh = new AlertDialog.Builder(mContext);
    			  dialogh.setTitle(item.getTitle());
    			  dialogh.setMessage(item.getSnippet());
    			  AlertDialog ald=dialogh.show();
    			  ald.setCanceledOnTouchOutside(true);
    			  
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
		builder.show();
    	return true;
	 
	}

}
