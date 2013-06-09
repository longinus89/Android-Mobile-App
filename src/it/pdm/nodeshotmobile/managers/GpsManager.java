package it.pdm.nodeshotmobile.managers;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GpsManager implements LocationListener 
{
  double longitudine;
  double latitudine;

  public void setLongitude(double longitudine) {
      this.longitudine = longitudine;
  }

  public void setLatitude(double latitudine) {
      this.latitudine = latitudine ;
  }
  
  public double getLongitude() {
      return longitudine;
  }

  public double getLatitude() {
      return latitudine ;
  }

  public void onLocationChanged(Location location) {
    if (location != null) {
  	  
  	  setLatitude(location.getLatitude());
  	  setLongitude(location.getLongitude());


    }
      

    }

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

  }
