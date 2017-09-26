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
	private DataOutputStream dos;
	private Socket s;
	private Gson gson;
	private MainActivity ma;
	
	public CreateSocketThread(MainActivity ma)
	{
		this.ma = ma;
		this.gson = new Gson();
	}

	@Override
	public void run() 
	{
        try
		{
			s = new Socket(ma.getIPServer().toString(),3571);
			dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
			dos.writeUTF(gson.toJson(ma.getMyMessage()));
			dos.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
        MainActivity.hMain.sendMessage(
				MainActivity.hMain.obtainMessage(
						MainActivity.HANDLER_KEYCREATESOCKET, s));
	}

}
