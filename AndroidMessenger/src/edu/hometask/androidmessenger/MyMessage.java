package edu.hometask.androidmessenger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyMessage 
{
	private String from;
	private String to;
	private String text;
	private String currentDateTime;
	
	public MyMessage(String from, String to, String text, String currentDateTime) 
	{
		this.from = from;
		this.to = to;
		this.text = text;
		this.currentDateTime = currentDateTime;
	}
	
	public MyMessage() 
	{
		this.from = "";
		this.to = "";
		this.text = "";
		this.currentDateTime = "";
	}
	
	public String getCurrentDateTime() 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy");
		return sdf.format(new Date());
	}
	public void setCurrentDateTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy");
		currentDateTime = sdf.format(new Date());
	}
	public String getFrom() 
	{
		return from;
	}
	public void setFrom(String from)
	{
		this.from = from;
	}
	public String getTo() 
	{
		return to;
	}
	public void setTo(String to) 
	{
		this.to = to;
	}
	public String getText() 
	{
		return text;
	}
	public void setText(String text) 
	{
		this.text = text;
	}
	

}
