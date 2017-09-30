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
	private Connection con;
	private MyMessage message;
	private String str;
	private Gson gson;
	private DataInputStream dis;
	
	public GetThread(Connection con)
	{
		this.con = con;
		this.message = new MyMessage();
		gson = new Gson();
		str=null;
		
		if(dis==null)
		{
		  try
		  {
			dis = new DataInputStream(new BufferedInputStream(con.getS().getInputStream()));
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
			//	str=message.getFrom() + " (" + message.getCurrentDateTime() + ")\n"+message.getText();
				message.setText(message.getFrom()+" to "+ message.getTo() + " (" + message.getCurrentDateTime() + ")\n"+message.getText());
				
				con.getMessage().setText(message.getText());//TODO страшно, в хистори хня попасть может
				con.addNewHistoryToDB();
				
			MainActivity.hMain.sendMessage(
					MainActivity.hMain.obtainMessage(
								MainActivity.HANDLER_KEYSEND, message.getText()));//отобразить в хистори от кого пришло и что
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
