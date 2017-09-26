package edu.hometask.androidmessenger;

import java.util.ArrayList;

public class Contacts
{
	private ArrayList<String> contacts;

	public Contacts() 
	{
		contacts = new ArrayList<String>();
	}

	public ArrayList<String> getContacts() 
	{
		return contacts;
	}

	public void setContacts(ArrayList<String> contacts)
	{
		this.contacts = contacts;
	}
}
