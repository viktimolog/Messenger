package edu.hometask.androidmessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

public class CreateSocketThread implements Runnable
{
	private MyMessage message;
	private DataOutputStream dos;
	private DataInputStream dis;
	private Socket s;
	private Gson gson;
	private MainActivity ma;
	private String IPServer;
	
	//public CreateSocketThread(MainActivity ma, DataOutputStream dos,DataInputStream dis, MyMessage message, Socket s)
	public CreateSocketThread(MyMessage message)
	{
		/*this.s = s;
		this.ma = ma;
		this.dos = dos;*/
		this.message = message;
		this.gson = new Gson();
	}

	@Override
	public void run() 
	{
		//message.setFrom("second");
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
			IPServer = "10.1.100.78";
			try 
			{
				s = new Socket(IPServer.toString(),3571);
				dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
				dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
				dos.writeUTF(gson.toJson(message));
				dos.flush();
			} 
			catch (UnknownHostException e1)
			{
				e1.printStackTrace();
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
        MainActivity.hMain.sendMessage(
				MainActivity.hMain.obtainMessage(
						MainActivity.HANDLER_KEYCREATESOCKET, s));
	}

}
