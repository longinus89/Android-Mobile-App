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
    Context context;
   
    public DbHelper(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
    }
   
    @Override
    public void onCreate(SQLiteDatabase db){
        // TODO Auto-generated method stub
        String sql1="CREATE TABLE IF NOT EXISTS "+TABLE_NAME1;
        sql1+="(_id		INTEGER PRIMARY KEY,";
        sql1+="	name	VARCHAR NOT NULL,";
        sql1+="	status 	VARCHAR NOT NULL,";
        sql1+="	slug	VARCHAR NOT NULL,";
        sql1+="	lat		INTEGER NOT NULL,";
        sql1+="	lng		INTEGER NOT NULL,";
        sql1+="	jslug	VARCHAR NOT NULL);";
        
        String sql2="CREATE TABLE IF NOT EXISTS "+TABLE_NAME2;
        sql2+="(_id		INTEGER PRIMARY KEY,";
        sql2+="	datetimeC	DATETIME NOT NULL,";
        sql2+="	number_nodes	INTEGER NOT NULL,";
        sql2+="	type	VARCHAR NOT NULL);";
       
        try {
            db.execSQL(sql1);        //creo la tabella Nodes
            db.execSQL(sql2);
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
