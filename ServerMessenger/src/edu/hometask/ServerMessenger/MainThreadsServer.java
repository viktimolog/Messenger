package edu.hometask.ServerMessenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainThreadsServer 
{
	public static Map<String, Socket> sockets;
	
	public static Contacts contacts;
	
	public static void main(String[] args)
	{
		sockets = new HashMap<String, Socket>();
		contacts = new Contacts(); 
		System.out.println("starting server on port 3571");
		Scanner sc = new Scanner(System.in);
		ServerSocket ss = null;
		try
		{
			ss = new ServerSocket(3571);
		}
		catch (IOException e)
		{
			System.out.println("Please select new port. This one is busy...");
			int port = sc.nextInt();
			try
			{
				ss = new ServerSocket(port);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}

		while(true)
		{
			try
			{
				Socket s = ss.accept();
				ThreadsServer thS = new ThreadsServer(s,(HashMap<String, Socket>)sockets, contacts);
				Thread t = new Thread(thS);
				t.start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

}
