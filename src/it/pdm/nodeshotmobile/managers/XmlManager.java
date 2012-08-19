package it.pdm.nodeshotmobile.managers;

import it.pdm.nodeshotmobile.entities.MapPoint;

import java.util.ArrayList;

import android.util.Log;

public class XmlManager {
    
	static void vDebug(String debugString){         //metodi di convenienza
            Log.v("DomParsing", debugString+"\n");
    }
    static void eDebug(String debugString){
            Log.e("DomParsing", debugString+"\n");
    }
   
    private ArrayList<MapPoint> parsedData=new ArrayList<MapPoint>(); //struttura dati che immagazzinerà i dati letti
    
    public ArrayList<MapPoint> getParsedData() {  //metodo di accesso alla struttura dati
            return parsedData;
    }

}
