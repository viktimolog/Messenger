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
	private Connection con;
	private DataOutputStream dos;
//	private Socket s;
	private Gson gson;
	
	public CreateSocketThread(Connection con)
	{
		this.con = con;
		this.gson = new Gson();
	}

	@Override
	public void run() 
	{
		con.addNewUserNameToDB();
		con.addNewIpServerToDB();
        try
		{
			con.setS(new Socket(con.getIPServer().toString(),3571));
			dos = new DataOutputStream(new BufferedOutputStream(con.getS().getOutputStream()));
			dos.writeUTF(gson.toJson(con.getMessage()));
			dos.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
        MainActivity.hMain.sendMessage(
				MainActivity.hMain.obtainMessage(
						MainActivity.HANDLER_KEYCREATESOCKET, con.getS()));
	}

}
