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
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
 
public class GetMessageService extends Service 
{
  NotificationManager nm;
  private Socket s;
  private DataInputStream dis;
  private DataOutputStream dos;
  private MyMessage message;
  private String IPServer, str;
  private Gson gson;
  
public String getStr()
{
	return str;
}

public void setStr(String str) 
{
	this.str = str;
}

/*@Override
public void onDestroy()
{
	
	if(myMessage==null) myMessage = new MyMessage();
	
	myMessage.setFrom(userName);
	myMessage.setText("exit");
	myMessage.setTo("server");
	
	new Thread(new SendThread(MainActivity.this,s,myMessage)).start();
			
		Log.d("MyTag", "String after Send evit to server");
	
	
	startService(new Intent(this, GetMessageService.class).putExtra(MainActivity.StoService, userName));
	
	if(hMain!=null) hMain.removeCallbacksAndMessages(null);//очищает отправленные с задержкой сообщения
	super.onDestroy();
}*/

@Override
  public void onCreate() 
  {
    super.onCreate();
    
    nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    
    message = new MyMessage();
    gson = new Gson();
    str=null;
    
    
  }
 
  public int onStartCommand(Intent intent, int flags, int startId)
  {
	  
	//  String tmpS = intent.getStringExtra(MainActivity.StoService);
	  
	//  dis = gson.fromJson(tmpS, DataInputStream.class);
	  
		message.setFrom(intent.getStringExtra(MainActivity.StoService));//TODO типа поймали username из мейна
		message.setTo("server");
		message.setText("regusername");
		
		new Thread(new ServiceThread(this, message)).start(); 
		
	  
    //return super.onStartCommand(intent, flags, startId);
    return START_STICKY;//работает вроде возрождается
  }
  
  public void sendNotif()
  {
	  
	  Log.d("Shalom", "sendNotif запустился");
	  
			message = (new Gson()).fromJson(str, MyMessage.class);
			str=message.getFrom() + ": "+message.getText();
			message.setText(str);
			
		    // 1-я часть
		    Notification notif = new Notification(R.drawable.ic_launcher, "New Message",System.currentTimeMillis());
		     
		    // 3-я часть
		    Intent intent = new Intent(this, MainActivity.class);
		    intent.putExtra(MainActivity.MESSAGE, (new Gson()).toJson(message));
		    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		     
		    // 2-я часть
		    notif.setLatestEventInfo(this, "У вас новое сообщение", message.getText(), pIntent);
		     
		    // ставим флаг, чтобы уведомление пропало после нажатия и другие
		    notif.flags |= Notification.FLAG_AUTO_CANCEL;
		    notif.flags |= Notification.DEFAULT_SOUND;
		    notif.flags |= Notification.DEFAULT_VIBRATE;
		    notif.flags |= Notification.DEFAULT_LIGHTS;
		     
		    // отправляем
		    nm.notify(1, notif);  
  }
  
  public void sendNotifActual()
  {
	  Log.d("Shalom", "str in sendNotifActual = " + str);
	  
		message = (new Gson()).fromJson(str, MyMessage.class);
		str=message.getFrom() + ": "+message.getText();
		message.setText(str);
		
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.putExtra(MainActivity.MESSAGE, (new Gson()).toJson(message));
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

