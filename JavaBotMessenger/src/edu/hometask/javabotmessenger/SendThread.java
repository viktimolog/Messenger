package edu.hometask.javabotmessenger;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;


public class SendThread  implements Runnable
{
	private MyMessage message;
	private DataOutputStream dos;
	private Socket s;
	private Gson gson;
	
	public SendThread(Socket s, MyMessage message)
	{
		this.message = message;
		this.s = s;
		this.gson = new Gson();

		  try
		  {
			dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
		  } 
		  catch (IOException e) 
		  {
			e.printStackTrace();
		  }
	}
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
	}
}
