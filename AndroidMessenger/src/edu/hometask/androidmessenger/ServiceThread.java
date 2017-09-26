package edu.hometask.androidmessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

public class ServiceThread implements Runnable
{
	private NotificationManager nm;
	private MyMessage message;
	private DataOutputStream dos;
	private DataInputStream dis;
	private Socket s;
	private Gson gson;
	private String IPServer, str;
	private GetMessageService serv;
	
//	public ServiceThread(DataInputStream dis, DataOutputStream dos, MyMessage message)
	public ServiceThread(GetMessageService serv, MyMessage message)
	{
		this.serv = serv;
		this.message = message;
		str=null;
		this.gson = new Gson();
	}
	
	@Override
	public void run() 
	{
//		message.setFrom("second");
        try
		{
//        	IPServer = "192.168.1.104";
        	IPServer = "10.1.100.78";
			s = new Socket(IPServer.toString(),3571);
			dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
			dos.writeUTF(gson.toJson(message));
			dos.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

    	try 
    	{
    		while ((str = dis.readUTF()) != null) 
    		{
    			Log.d("Shalom", str);
//    			serv.sendNotif();
    			serv.setStr(str);
    			serv.sendNotifActual();
    		}
    	} 
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
	}
}
