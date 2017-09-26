package edu.hometask.androidmessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.os.Message;
import android.widget.Toast;

public class GetThread implements Runnable
{
	private MainActivity ma;
	private Socket s;
	private MyMessage message;
	private String str;
	private Gson gson;
	private DataInputStream dis;
	
	public GetThread(MainActivity ma, Socket s)
	{
		this.ma = ma;
		this.s = s;
		this.message = new MyMessage();
		gson = new Gson();
		str=null;
		
		if(dis==null)
		{
		  try
		  {
			  //dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			dis = new DataInputStream(new BufferedInputStream(ma.getS().getInputStream()));
		  } 
		  catch (IOException e) 
		  {
			e.printStackTrace();
		  }
		}
	}
	@Override
	public void run()
	{
		try {
			while ((str = dis.readUTF()) != null) 
			{
			message = gson.fromJson(str, MyMessage.class);
			
			if(message.getFrom().equals("server")&&message.getTo().equals("all"))
			{
				str = message.getText();
				MainActivity.hMain.sendMessage(
						MainActivity.hMain.obtainMessage(
									MainActivity.HANDLER_KEYCONTACTS, str));//отобразить контакты
				
			}
			
			else
			{
				str=message.getFrom() + ": "+message.getText();
			
			//message.setText(str);
			
			MainActivity.hMain.sendMessage(
					MainActivity.hMain.obtainMessage(
							//MainActivity.HANDLER_KEYSEND, message.getText()));
								MainActivity.HANDLER_KEYSEND, str));//отобразить в хистори от кого пришло и что
			}
		}
	} 
		catch (JsonSyntaxException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

}
