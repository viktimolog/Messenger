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
	private Connection con;
	private DataOutputStream dos;
	private Gson gson;
	
	public SendThread(Connection con)
	{
		this.con = con;
		this.gson = new Gson();
		if(dos==null)
		{
		  try
		  {
			dos = new DataOutputStream(new BufferedOutputStream(
					con.getS().getOutputStream()));
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
				dos.writeUTF(gson.toJson(con.getMessage()));
				dos.flush();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			String str = con.getMessage().getFrom() + " (" + con.getMessage().getCurrentDateTime() + ")\n"+con.getMessage().getText();
			
			con.getMessage().setText(con.getMessage().getFrom()+" to "+ con.getMessage().getTo()+" (" + con.getMessage().getCurrentDateTime() + ")\n"+con.getMessage().getText());
			
			con.addNewHistoryToDB();
			
			MainActivity.hMain.sendMessage(
					MainActivity.hMain.obtainMessage(
//							MainActivity.HANDLER_KEYSEND, "You: " + con.getMessage().getText()));
							MainActivity.HANDLER_KEYSEND, con.getMessage().getText()));
	}
}
