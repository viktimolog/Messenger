package edu.hometask.androidmessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class ServiceThread implements Runnable
{
	private Connection con;
//	private MyMessage message;
//	private MainActivity ma;
	private DataOutputStream dos;
	private DataInputStream dis;
	private Gson gson;
	private String str;
	private GetMessageService serv;
	
//	public ServiceThread(DataInputStream dis, DataOutputStream dos, MyMessage message)
	public ServiceThread(GetMessageService serv, Connection con)
	{
		this.con = con;
		this.serv = serv;
		str=null;
		this.gson = new Gson();
	}
	
	@Override
	public void run() 
	{
		con.returnLastIpServerFromDB();
        try
		{
        	con.setS(new Socket(con.getIPServer().toString(),3571));
			dos = new DataOutputStream(new BufferedOutputStream(con.getS().getOutputStream()));
			dis = new DataInputStream(new BufferedInputStream(con.getS().getInputStream()));
			con.getMessage().setText("regusername");
			dos.writeUTF(gson.toJson(con.getMessage()));
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
    			serv.setStr(str);
    			serv.sendNotifActual();
    			con.addNewHistoryToDB();
    		}
    	} 
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
	}
}
