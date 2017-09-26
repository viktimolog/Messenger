package edu.hometask.androidmessenger;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

import android.os.Message;
import android.widget.Toast;

public class SendThread  implements Runnable
{
	private MyMessage message;
	private DataOutputStream dos;
	private Socket s;
	private Gson gson;
	private MainActivity ma;
	
	public SendThread(MainActivity ma, Socket s, MyMessage message)
	{
		this.ma = ma;
//		this.dos = dos;
		this.message = message;
		this.s = s;
		this.gson = new Gson();
		if(dos==null)
		{
		  try
		  {
			dos = new DataOutputStream(new BufferedOutputStream(
					s.getOutputStream()));
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
			try 
			{
				dos.writeUTF(gson.toJson(message));
				dos.flush();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			MainActivity.hMain.sendMessage(
					MainActivity.hMain.obtainMessage(
							MainActivity.HANDLER_KEYSEND, /*ma.getUserName() +*/ "You: " + message.getText()));
	}
}
