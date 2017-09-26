package edu.hometask.javabotmessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.google.gson.Gson;

public class JavaBotMain 
{
	public static void stopStartGetThread(Thread tGet, GetThread getThread, Socket s)
	{
		tGet.stop();
		
		tGet = new Thread(getThread);
		getThread = new GetThread(s);
		tGet.start();
	}
	
	public static void main(String[] args) 
	{
		String IPServer;
		String UserName;
		
		
		/*GetThread getThread;
		Thread tGet = new Thread();
		Thread tSend = new Thread();*/
		
		/*System.out.println("Input IPServer: ");
		Scanner inServer = new Scanner(System.in);
		IPServer = inServer.nextLine(); 
		System.out.println("IPServer in Bot = " + IPServer);
		
		System.out.println("Input UserName: ");
		Scanner inUserName = new Scanner(System.in);
		UserName = inUserName.nextLine();
		System.out.println("UserName in Bot = " + UserName);*/
		
		IPServer = args[0];
		
		UserName = args[1];
		
//		IPServer = "10.1.100.78";
		
//		UserName = "first";
		
		MyMessage message = new MyMessage();
		
		message.setFrom(UserName);
		message.setTo("server");
		message.setText("regusername");
		
		Socket s = null;
		DataInputStream dis=null;
		DataOutputStream dos=null;
//		String str=null;
		Gson gson = new Gson();
		
		try
		{
			s = new Socket(IPServer.toString(),3571);
//			dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
			dos.writeUTF(gson.toJson(message));
			dos.flush();
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
new Thread(new GetThread(s)).start();
		
	}

}
