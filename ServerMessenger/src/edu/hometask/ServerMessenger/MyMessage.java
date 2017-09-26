package edu.hometask.ServerMessenger;

public class MyMessage 
{
	private String from;
	private String to;
	private String text;
	
	public MyMessage(String from, String to, String text) 
	{
		this.from = from;
		this.to = to;
		this.text = text;
	}
	public MyMessage() 
	{
		this.from = "";
		this.to = "";
		this.text = "";
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
