package edu.hometask.androidmessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GetMessageService extends Service 
{
  private Connection con;
  private MyMessage message;
  NotificationManager nm;
  private Socket s;
  private DataInputStream dis;
  private DataOutputStream dos;
  private String str;
  private Gson gson;
  
  private SQLiteConnector connector;
	private SQLiteDatabase db;
	
	private class SQLiteConnector extends SQLiteOpenHelper
	{

		public SQLiteConnector(Context context, String name, int version)
		{
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			
			db.execSQL("create table ipserver (_id integer primary key autoincrement, ipServer varchar(25))");
			
			db.execSQL("create table username (_id integer primary key autoincrement, userName varchar(25))");
			
			db.execSQL("create table history (_id integer primary key autoincrement, idUser integer, history varchar(255))");
			//TODO
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			//все возможные варианты
			if(oldVersion==1 && newVersion==2)
			{
//				alterTable etc
			}
			else if(oldVersion==2 && newVersion==3)
			{
				
			}
			else if(oldVersion==1 && newVersion==3)
			{
				
			}
		}
	}
  
public String getStr()
{
	return str;
}

public void setStr(String str) 
{
	this.str = str;
}

@Override
  public void onCreate() 
  {
    super.onCreate();
    
    nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//    message = new MyMessage();//не надо
    gson = new Gson();
    str=null;
    
	connector = new SQLiteConnector(this, "edu.hometask.androidmessenger.Messenger", 1);
	db = connector.getWritableDatabase();
    
   	con = new Connection(new MyMessage(), null,"", "", 3571,db);
    
  }
 
  public int onStartCommand(Intent intent, int flags, int startId)
  {
      
	  con.setUserName(intent.getStringExtra(MainActivity.StoService));
	  
		con.getMessage().setFrom(con.getUserName());
		con.getMessage().setTo("server");
		con.getMessage().setText("regusername");
		
		new Thread(new ServiceThread(this, con)).start(); 
		
	  
    //return super.onStartCommand(intent, flags, startId);
    return START_STICKY;//работает вроде возрождается
  }
  
  public void sendNotifActual()
  {
	  Log.d("Shalom", "str in sendNotifActual = " + str);
	  
		message = gson.fromJson(str, MyMessage.class);
		str=message.getFrom() + ": "+message.getText();
		message.setText(str);
		
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.putExtra(MainActivity.MESSAGE, gson.toJson(message));
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
	    
	    Notification notif = new Notification.Builder(getBaseContext())
	    							.setContentText(message.getText())
	    							.setSmallIcon(R.drawable.ic_launcher)
	    							//.setWhen(System.currentTimeMillis())
	    							.setContentTitle("New message for you")
	    							.setTicker(message.getText())
	    							.setContentIntent(pIntent)
	    							.build();
	    
	    notif.flags |= Notification.FLAG_AUTO_CANCEL;
	    notif.flags |= Notification.DEFAULT_SOUND;
	    notif.flags |= Notification.DEFAULT_VIBRATE;
	    notif.flags |= Notification.DEFAULT_LIGHTS;
	    
	 // отправляем
	    nm.notify(1, notif);  	    		
	  
  }
   
  public IBinder onBind(Intent arg0) 
  {
    return null;
  }
}

