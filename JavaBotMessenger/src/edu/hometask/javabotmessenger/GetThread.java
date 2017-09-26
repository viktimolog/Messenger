package edu.hometask.javabotmessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GetThread implements Runnable
{
	private Socket s;
	private MyMessage message;
	private String str;
	private Gson gson;
	private DataInputStream dis;
	
	public GetThread(Socket s)
	{
		this.s = s;
		gson = new Gson();
		
		  try
		  {
			  dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		  } 
		  catch (IOException e) 
		  {
			e.printStackTrace();
		  }
	}
	
	public void run()
	{

		try {
			while ((str = dis.readUTF()) != null) 
			{
				message = gson.fromJson(str, MyMessage.class);
				str="I, " + message.getTo() + ", have got from " + message.getFrom()+": " + message.getText();
				message.setText(str);
				
				String newFrom = message.getTo();
				message.setTo(message.getFrom());
				message.setFrom(newFrom);
				
				new Thread(new SendThread(s,message)).start();
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
