package it.pdm.nodeshotmobile.entities;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DbHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "MapsAppBase.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME1 = "Nodes";
    public static final String TABLE_NAME2 = "Updates";
    public static final String TABLE_NAME3 = "Groups";
    public static final String TABLE_NAME4 = "Markers";
    public static final String TABLE_NAME5 = "Optvalues";
    
    
    Context context;
   
    public DbHelper(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
    }
   
    public void onCreate(SQLiteDatabase db){
        // TODO Auto-generated method stub
        String sql1="CREATE TABLE IF NOT EXISTS "+TABLE_NAME1;
        sql1+="(_id		INTEGER PRIMARY KEY AUTOINCREMENT,";
        sql1+="id_group		INTEGER NOT NULL,";
        sql1+="	name	VARCHAR NOT NULL,";
        sql1+="	lat		INTEGER NOT NULL,";
        sql1+="	lng		INTEGER NOT NULL,";
        sql1+="	alt		INTEGER NOT NULL,";
        sql1+="	type 	VARCHAR NOT NULL)";
        
        String sql2="CREATE TABLE IF NOT EXISTS "+TABLE_NAME2;
        sql2+="(_id		INTEGER PRIMARY KEY AUTOINCREMENT,";
        sql2+="id_group	INTEGER NOT NULL,";
        sql2+="	dateC	DATETIME NOT NULL,";
        sql2+="	number_nodes	INTEGER NOT NULL,";
        sql2+="	type	VARCHAR NOT NULL);";
        
        String sql3="CREATE TABLE IF NOT EXISTS "+TABLE_NAME3;
        sql3+="(_id		INTEGER PRIMARY KEY AUTOINCREMENT,";
        sql3+="	name	VARCHAR NOT NULL,"; //group name
        sql3+="	uri		VARCHAR NOT NULL);"; //URI is the resource for file JSON
        
        String sql4="CREATE TABLE IF NOT EXISTS "+TABLE_NAME4;
        sql4+="(id_group	INTEGER NOT NULL,";
        sql4+="	name	VARCHAR NOT NULL,"; //type of node
        sql4+="	uri		VARCHAR NOT NULL);"; //URI is the resource for marker image
        
        String sql5="CREATE TABLE IF NOT EXISTS "+TABLE_NAME5;
        sql5+="(id_group	INTEGER NOT NULL,";
        sql5+="	id_node		INTEGER NOT NULL,"; //option field of this group ~es Address
        sql5+="	name		VARCHAR NOT NULL,"; //option field of this group ~es Address
        sql5+="	value		VARCHAR NOT NULL);";
       
        try {
            db.execSQL(sql1);        //creo la tabella Nodes
            db.execSQL(sql2);
            db.execSQL(sql3);
            db.execSQL(sql4);
            db.execSQL(sql5);
        } catch (SQLException e) {
            // TODO: handle exception
            Log.e("SQL", "Errore durante la creazione di una delle tabelle");
            Toast.makeText(context, "Errore durante la creazione di una delle tabelle", Toast.LENGTH_SHORT).show();
        }
       
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
       
    }
   
}
