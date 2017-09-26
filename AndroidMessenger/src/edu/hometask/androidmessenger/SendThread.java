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
	private DataOutputStream dos;
	private Gson gson;
	private MainActivity ma;
	
	public SendThread(MainActivity ma)
	{
		this.ma = ma;
		this.gson = new Gson();
		if(dos==null)
		{
		  try
		  {
			dos = new DataOutputStream(new BufferedOutputStream(
					ma.getS().getOutputStream()));
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
				dos.writeUTF(gson.toJson(ma.getMyMessage()));
				dos.flush();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			MainActivity.hMain.sendMessage(
					MainActivity.hMain.obtainMessage(
							MainActivity.HANDLER_KEYSEND, /*ma.getUserName() +*/ "You: " + ma.getMyMessage().getText()));
	}
}
