package edu.hometask.ServerMessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;


public class ThreadsServer implements Runnable
{
	private Socket s;
	private MainThreadsServer ms;
	private Map<String, Socket> sockets;
	private Gson gson;
	private MyMessage myMessage;
	private Contacts contacts;

	public ThreadsServer(Socket s, HashMap<String, Socket> sockets, Contacts contacts)
	{
		this.contacts = contacts;
		this.s = s;
		this.sockets = sockets;
		gson = new Gson();
	}
	
	public void sendContacts()
	{
		contacts.getContacts().clear();
		for (Map.Entry entry: sockets.entrySet())
		{ 
			String key = (String) entry.getKey(); 
			Socket value = (Socket) entry.getValue(); 
			contacts.getContacts().add(key);
//TODO  в каком-то виде отправить ВСЕМ клиентам			
		}
		
		String contactsJson = gson.toJson(contacts);//Json все контакты класс
		
		if(myMessage==null) new MyMessage();
		
		for (Map.Entry entry: sockets.entrySet())
		{ 
			Socket value = (Socket) entry.getValue(); 
			
		try 
		{
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(value.getOutputStream()));
			myMessage.setFrom("server");
			myMessage.setTo("all");
			myMessage.setText(contactsJson);//Json все контакты класс
			dos.writeUTF(gson.toJson(myMessage));
			dos.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		}
		
	}
	
	public void showMap()
	{
		System.out.println("All sockets in map:");
		for (Map.Entry entry: sockets.entrySet())
		{ 
			String key = (String) entry.getKey(); 
			Socket value = (Socket) entry.getValue(); 
			
			System.out.println("key = "+key);
			System.out.println("value = "+value);
		}
	}

	public void run() 
	{
		try
		{
			DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
			
			String chat=null;
			
			//while(!chat.equals("exit"))
			while ((chat = dis.readUTF()) != null) 
			{
			/*	System.out.println("Заходит в while ((chat = dis.readUTF()) != null) ");
				System.out.println("chat = " + chat);*/
				
				myMessage = gson.fromJson(chat, MyMessage.class);
				
				try 
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				if(myMessage.getTo().equals("server")&&myMessage.getText().equals("exit"))//выход клиента, убрать сокет из коллекции
				{
					System.out.println("Зашло в server-exit");
					s=null;
					
				for (Map.Entry entry: sockets.entrySet())
				{ 
					String key = (String) entry.getKey(); 
					Socket value = (Socket) entry.getValue(); 
					
				//	System.out.println("key = "+key);
				//	System.out.println("value = "+value);
					
					if(key.equals(myMessage.getFrom()))
						s = value;
				}
				
				if(s!=null)
				{
					s.close();
//					System.out.println("Закрыло сокет");//работает
					for(Iterator<Entry<String, Socket>> it = sockets.entrySet().iterator(); it.hasNext(); )
					{
						Map.Entry<String, Socket> entry = it.next();
						if(entry.getKey().equals(myMessage.getFrom()))
						{
//							System.out.println("нашло сокет чтобы удалиь");//работает
							it.remove();
						}
					}
					if(sockets.size()>0)
					{
						showMap();
						sendContacts();
					}
					return;//выход из треда вроде норм TODO
				}
					
				}//end 	if(myMessage.getText().equals("exit"))//выход клиента, убрать сокет из коллекции
				
				
				
				if(myMessage.getTo().equals("server")&&myMessage.getText().equals("regusername"))
				{
					
				sockets.put(myMessage.getFrom(), s);
					
				System.out.println("Зарегистрировал " + myMessage.getFrom());
					
				System.out.println("Теперь сокетов стало: " + sockets.size());
				
				for (Map.Entry entry: sockets.entrySet())
				{ 
					String key = (String) entry.getKey(); 
					Socket value = (Socket) entry.getValue(); 
					
					System.out.println("key = "+key);
					System.out.println("value = "+value);
				}
				
					sendContacts();
					/*for (String key : sockets.keySet())
					{
						    System.out.println("Key: " + key);
					}	*/
					
				}
				
				else if(!myMessage.getText().equals("exit"))
				{
					s=null;
					
				for (Map.Entry entry: sockets.entrySet())
				{ 
					String key = (String) entry.getKey(); 
					Socket value = (Socket) entry.getValue(); 
					
					System.out.println("key = "+key);
					System.out.println("value = "+value);
					
					if(key.equals(myMessage.getTo()))
						s = value;
				} 
				
				//System.out.println("s = "+s.toString());
				if(s != null)
				{
					System.out.println("НАШЕЛ адресат в коллекции сокетов!!!");
					System.out.println("от кого = "+myMessage.getFrom());
					System.out.println("кому = "+myMessage.getTo());
					System.out.println("текст = "+myMessage.getText());
					
					dos = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
					
					dos.writeUTF(chat);
					dos.flush();
				}
				else
					{
					System.out.println("Не нашел адресат в коллекции сокетов");
					System.out.println("от кого = "+myMessage.getFrom());
					System.out.println("кому = "+myMessage.getTo());
					System.out.println("текст = "+myMessage.getText());
					}

				}
			}
			

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
