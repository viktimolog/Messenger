package edu.hometask.androidmessenger;

import java.net.Socket;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;

public class Connection 
{
	private ArrayList<String> arrListHistory;
	
	private MyMessage message;
	private Socket s;
	private String userName;
	private String ipServer;
	private int port;
	private SQLiteDatabase db;

	public Connection(MyMessage message, Socket s, String userName, String ipServer, int port, SQLiteDatabase db)
	{
		this.message = message;
		this.s = s;
		this.userName = userName;
		this.ipServer = ipServer;
		this.port = port;
		this.db = db;
		this.arrListHistory = new ArrayList<String>();
	}
	
	public void getHistoryFromDB()
	{
		String tmp="";
		
		Cursor result = db.rawQuery("SELECT _id FROM username WHERE userName = '"+userName+"'", null);
		
		if(result.moveToNext())
		{
			tmp = result.getString(0);
		}
		
		int idUser = Integer.parseInt(tmp); //нашли айди юзера текущего 
		
		Cursor result1 = db.rawQuery("SELECT * FROM history WHERE idUser = '"+idUser+"'", null); 

		while(result1.moveToNext())
		{
			arrListHistory.add(result1.getString(2));
		}
		
		Log.d("get History From DB",Integer.toString(arrListHistory.size())); //TODO тест
	}
	
	public void addNewHistoryToDB()
	{
		String tmp="";
		
		Cursor result = db.rawQuery("SELECT _id FROM username WHERE userName = '"+userName+"'", null);//нашли айди юзера текущего 
		
		if(result.moveToNext())
		{
			tmp = result.getString(0);
		}
		
		int idUser = Integer.parseInt(tmp);
		
		ContentValues cv = new ContentValues();
		cv.put("idUser", idUser);
		cv.put("history", message.getText());
		
		db.insert("history", null, cv);
		Log.d("add New History To DB",message.getText()); 
	}
	
	public void addNewIpServerToDB()
	{
		String tmpIpServer="";
		
		ContentValues cv = new ContentValues();
		cv.put("ipServer", ipServer);
		
		Cursor result = db.rawQuery("SELECT * FROM ipserver WHERE ipServer = '"+ipServer+"'", null); 
		
		if(result.moveToNext())
		{
			tmpIpServer = result.getString(1);
		}
		
		db.delete("ipserver", "ipServer = ?", new String[]{ipServer});//удалить из базы сервер если был ранее 
		Log.d("Delete server from DB",ipServer); 
		
		db.insert("ipserver", null, cv);
		Log.d("addNewIpServerToDB",ipServer); 
		
		
	/*	if(!tmpIpServer.equals(ipServer))//если нет такого сервера, то добавлять в базу
		{
			db.insert("ipserver", null, cv);
			Log.d("addNewIpServerToDB",ipServer); 
		}*/
	}
	
	public void returnLastIpServerFromDB()
	{
		String ID="";
		
		Cursor id = db.rawQuery("SELECT MAX(_id) from ipserver", null);
		
		if(id.moveToNext())
		{
			ID = id.getString(0);
		}
		
		Cursor result = db.rawQuery("SELECT * FROM ipserver WHERE _id = '"+ID+"'", null); 

		if(result.moveToNext())
		{
			ipServer = result.getString(1);//0 - это id, счет с 1, используем result.getString, когда не надо показывать а что-то надо сделать
		}
		
		Log.d("returnLastIpServerFromDB",ipServer); //TODO тест
	}
	
	public void addNewUserNameToDB()
	{
		String tmpUserName="";
		
		ContentValues cv = new ContentValues();
		cv.put("userName", userName);
		
		Cursor result = db.rawQuery("SELECT * FROM username WHERE userName = '"+userName+"'", null); 
		
		if(result.moveToNext())
		{
			tmpUserName = result.getString(1);
		}
		
		if(!tmpUserName.equals(userName))//если нет такого имени, то добавлять в базу
		{
			db.insert("username", null, cv);
			Log.d("addNewUserNameToDB",userName); //TODO тест
		}
	}
	
	public void returnLastUserNameFromDB()
	{
		String ID="";
		
		Cursor id = db.rawQuery("SELECT MAX(_id) from username", null);
		
		if(id.moveToNext())
		{
			ID = id.getString(0);
		}
		
		Cursor result = db.rawQuery("SELECT * FROM username WHERE _id = '"+ID+"'", null); 

		if(result.moveToNext())
		{
			userName = result.getString(1);//0 - это id, счет с 1, используем result.getString, когда не надо показывать а что-то надо сделать
		}
		
		Log.d("returnLastUserNameFromDB",userName); //TODO тест
	}
	
	

	public ArrayList<String> getArrListHistory() {
		return arrListHistory;
	}

	public void setArrListHistory(ArrayList<String> arrListHistory) {
		this.arrListHistory = arrListHistory;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}



	public String getIPServer() {
		return ipServer;
	}



	public void setIPServer(String ipServer) {
		this.ipServer = ipServer;
	}



	public int getPort() {
		return port;
	}



	public void setPort(int port) {
		this.port = port;
	}



	public MyMessage getMessage() {
		return message;
	}

	public void setMessage(MyMessage message) {
		this.message = message;
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
