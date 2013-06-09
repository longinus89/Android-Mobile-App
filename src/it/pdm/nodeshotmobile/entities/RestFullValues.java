package it.pdm.nodeshotmobile.entities;

import java.util.ArrayList;

public class RestFullValues
{
    public static String DATEDESC = "-date";
    public static String DATEASC = "+date";
    public static String STATUSDESC = "-status";
    public static String STATUSASC = "+status";
    public static String NAMEDESC = "-name";
    public static String NAMEASC = "+name";
    public static String TRUE = "true";
    public static String FALSE = "false";
    public static String GZIP = "gzip";
    public static String XML = "application/xml";
    public static String JSON = "application/json";
    
    public static ArrayList<String> orderByValues=new ArrayList<String>();
    static{
    	orderByValues.add(DATEDESC);
    	orderByValues.add(DATEASC);
    	orderByValues.add(STATUSDESC);
    	orderByValues.add(STATUSASC);
    	orderByValues.add(NAMEDESC);
    	orderByValues.add(NAMEASC);
    }
    
    public static ArrayList<String> groupByValues=new ArrayList<String>();
    static{
    	groupByValues.add(TRUE);
    	groupByValues.add(FALSE);
    }
    
    public static ArrayList<String> acceptEncodingValues=new ArrayList<String>();
    static{
    	acceptEncodingValues.add(GZIP);
    }
    
    public static ArrayList<String> acceptValues=new ArrayList<String>();
    static{
    	acceptValues.add(XML);
    	acceptValues.add(JSON);
    }
    	
    
}
