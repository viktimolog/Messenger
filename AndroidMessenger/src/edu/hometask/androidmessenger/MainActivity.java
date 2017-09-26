package edu.hometask.androidmessenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends Activity 
{
public static final int CODE_1 = 173726;
	
	public static String addressServer;
	public static final int HANDLER_KEYSERVER =1;
	public static final int HANDLER_KEYUSERNAME=2;
	public static final String SPKEY = "SPKEY";
	public static final int HANDLER_KEYSEND = 3;
	public static final int HANDLER_KEYGET = 4;
	public static final int HANDLER_KEYCREATESOCKET = 0;
	public static final String MESSAGE = "GetNewMessage";
	public static final String StoService = "StoService";
	public static final int HANDLER_KEYCONTACTS = 5;
	
	static Handler hMain;
	
	private Socket s;
	private String IPServer, userName, newMessage;
	private DataInputStream dis;
	private DataOutputStream dos;
	private SharedPreferences sp;
	private MyMessage myMessage;
	private Gson gson;
	
	private Contacts contacts;
	private ArrayList<String> arrListContacts;
	private ArrayAdapter<String> adapterContacts;
	
	private ArrayList<String> arrListHistory;
	private ArrayAdapter<String> adapterHistory;
	
	EditText etInput, etServerName, etUserName;
	ListView lvHistory, lvContacts;
	Button btnSend;

	static class MyHandler extends Handler
    {
    	WeakReference<MainActivity> wrActivity;

		public MyHandler(MainActivity activity)
		{
			wrActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what==MainActivity.HANDLER_KEYSERVER)
			{
				MainActivity ma = wrActivity.get();
				if(ma!=null)
				{
					ma.setIPServer(msg.obj.toString());
					//Toast.makeText(ma, ma.getIPServer(), Toast.LENGTH_LONG).show();
				}
			}
			if(msg.what==MainActivity.HANDLER_KEYUSERNAME)
			{
				MainActivity ma = wrActivity.get();
				if(ma!=null)
				{
					ma.setUserName(msg.obj.toString());
					
					ma.myMessage.setFrom(ma.userName);
					ma.myMessage.setTo("server");
					ma.myMessage.setText("regusername");
				    
				    //������ ����������� � �������
				    new Thread(new CreateSocketThread(ma)).start(); //TODO ����������: ���� ���������� ���
				}
			}
			if(msg.what==MainActivity.HANDLER_KEYSEND)
			{
				MainActivity ma = wrActivity.get();
				if(ma!=null)
				{
					ma.etInput.setText("");
					
					ma.arrListHistory.add(msg.obj.toString());
					
					ma.adapterHistory = new ArrayAdapter<String>(ma,android.R.layout.simple_list_item_1, ma.arrListHistory);//����� �� ���� ������-��
					
			        ma.lvHistory.setAdapter(ma.adapterHistory);//�����������, ��� ���� �� ��������
				}
			}
			
			if(msg.what==MainActivity.HANDLER_KEYCONTACTS)
			{
				MainActivity ma = wrActivity.get();
				if(ma!=null)
				{
					ma.contacts = (new Gson()).fromJson(msg.obj.toString(), Contacts.class);
					
					ma.arrListContacts = ma.contacts.getContacts();
					
					for (int i=0; i<ma.arrListContacts.size(); i++) 
					{
						if(ma.arrListContacts.get(i).equals(ma.userName))
						{
							ma.arrListContacts.remove(i);//TODO ������ ������ ����
						}
					}
					
					Collections.sort(ma.arrListContacts);//������������ ������ ���������
					
					ma.adapterContacts = new ArrayAdapter<String>(ma,android.R.layout.simple_list_item_single_choice, ma.arrListContacts);//����� �����������
					
			        ma.lvContacts.setAdapter(ma.adapterContacts);
			        
			        ma.lvContacts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//����� ������� ������ ���� ����� � ������
			        
			        if(ma.arrListContacts.size()>0)
			        {
			        	ma.lvContacts.performItemClick(ma.lvContacts.getAdapter().//����������� ������� �� ������� ������ 0
				                getView(0, null, null), 0, ma.lvContacts.getAdapter().
				                getItemId(0));
			        }
			        
			        
			        //ma.lvContacts.setBackgroundColor(5); �� ��������
			        
				}
			}
			
			if(msg.what==MainActivity.HANDLER_KEYCREATESOCKET)
			{
				MainActivity ma = wrActivity.get();
				if(ma!=null)
				{
					ma.s = (Socket) msg.obj;
					new Thread(new GetThread(ma)).start(); // TODO ����� ���������� 
				}
			}
		}
    }
	
	

	
	public ArrayList<String> getArrListHistory() 
	{
		return arrListHistory;
	}

	public ArrayList<String> getArrListContacts()
	{
		return arrListContacts;
	}

	public void setArrListContacts(ArrayList<String> arrListContacts) {
		this.arrListContacts = arrListContacts;
	}

	public DataOutputStream getDos() 
	{
		return dos;
	}

	public void setDos(DataOutputStream dos) 
	{
		this.dos = dos;
	}

	public DataInputStream getDis() 
	{
		return dis;
	}

	public void setDis(DataInputStream dis)
	{
		this.dis = dis;
	}

	public Socket getS()
	{
		return s;
	}

	public void setS(Socket s)
	{
		this.s = s;
	}

	public MyMessage getMyMessage() 
	{
		return myMessage;
	}

	public void setMyMessage(MyMessage myMessage)
	{
		this.myMessage = myMessage;
	}
	
	public void setUserName(String txt)
    {
    	userName = txt;
    }
	
	public String getUserName()
    {
    	return userName;
    }
	public void setIPServer(String txt)
	{
		IPServer = txt;
	}
	
	public String getIPServer()
	{
		return IPServer;
	}
	
    public void setEtInputText(String txt)
    {
    	etInput.setText(txt);
    }
    
    public String getEtInputText()
    {
    	return etInput.getText().toString();
    }

	@Override
	protected void onDestroy()
	{
		
		if(myMessage==null) myMessage = new MyMessage();
		
		myMessage.setFrom(userName);
		myMessage.setText("exit");
		myMessage.setTo("server");
		
		new Thread(new SendThread(MainActivity.this)).start();
				
			Log.d("MyTag", "String after Send evit to server");
		
		
		//startService(new Intent(this, GetMessageService.class).putExtra(MainActivity.StoService, userName));
		startService(new Intent(this, GetMessageService.class).putExtra(MainActivity.StoService, gson.toJson(MainActivity.this)));
		
		if(hMain!=null) hMain.removeCallbacksAndMessages(null);//������� ������������ � ��������� ���������
		super.onDestroy();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        IPServer = "192.168.1.104";
        
        s=null;
        myMessage=new MyMessage();
        gson = new Gson();
        
        //userName = "second";
        
        hMain = new MyHandler(this);
        
        arrListHistory = new ArrayList<String>();
        arrListContacts = new ArrayList<String>();
        
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        lvHistory = (ListView) findViewById(R.id.lvHistory);
        etInput = (EditText) findViewById(R.id.etInput);
        btnSend = (Button) findViewById(R.id.btnSend);
        
        adapterHistory = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrListHistory);
        
        lvHistory.setAdapter(adapterHistory);
        
        adapterContacts = new ArrayAdapter<String>(this,R.layout.mylistview, arrListContacts);
      
        lvContacts.setAdapter(adapterContacts);
        
        
      lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() //������� �� ����� ������
      {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) 
    		{
    			etInput.setText(((TextView) itemClicked).getText()+": ");
    			etInput.setSelection(etInput.getText().length());
    			
    			/*Toast.makeText(getApplicationContext(), ((TextView) itemClicked).getText(),
    			        Toast.LENGTH_SHORT).show();*/
    		}
    	});
        
	    btnSend.setOnClickListener(new OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				myMessage.setFrom(userName);
				myMessage.setText(etInput.getText().toString());
				//myMessage.setTo("first");//���� ����� � ����� ������ TODO
				
				myMessage.setTo(arrListContacts.get(lvContacts.getCheckedItemPosition()));//OK
				
				
				//myMessage.setTo(userName);//����� ��� ���� ��������� userName
				
				new Thread(new SendThread(MainActivity.this)).start();
			}
		});
        
  /*      View view = View.inflate(this, R.layout.serverchoice, null);
	    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
	    alert.setView(view);
	    alert.setTitle(R.string.serverchoicetitle);
	    alert.setCancelable(false);
	    
	    etServerName = (EditText) view.findViewById(R.id.dialog1EditText);
	    
	    alert.setPositiveButton(R.string.newServer, new DialogInterface.OnClickListener()
        {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				IPServer = etServerName.getText().toString();
				
				MainActivity.hMain.sendMessage(
						MainActivity.hMain.obtainMessage(
								MainActivity.HANDLER_KEYSERVER, IPServer));
				
				dialog.cancel();
				
				sp = getSharedPreferences("SelectedServer", MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putString(MainActivity.SPKEY, IPServer);
				edit.commit();
			}
		});
	    
	    alert.setNegativeButton(R.string.oldServer, new DialogInterface.OnClickListener()
        {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				sp = getSharedPreferences("SelectedServer", MODE_PRIVATE);
				IPServer = sp.getString(MainActivity.SPKEY, "");
				//Toast.makeText(getApplicationContext(), IPServer, Toast.LENGTH_LONG).show();
			}
		});
	    
	    AlertDialog ad = alert.create();
	    ad.show();*/
	    
        View viewName = View.inflate(this, R.layout.namechoice, null);
	    AlertDialog.Builder alertName = new AlertDialog.Builder(MainActivity.this);
	    alertName.setView(viewName);
	    alertName.setTitle(R.string.namechoicetitle);
//	    alertName.setMessage(R.string.personName);
	    alertName.setCancelable(false);
	    
	    etUserName = (EditText) viewName.findViewById(R.id.dialog1EditText);
	    
	    alertName.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener()
        {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				userName = etUserName.getText().toString();
				
				//userName = "first";//TODO
				//userName = "second";
				
				MainActivity.hMain.sendMessage(
						MainActivity.hMain.obtainMessage(
								MainActivity.HANDLER_KEYUSERNAME, userName));
				
				dialog.cancel();
				
			/*	sp = getSharedPreferences("SelectedServer", MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putString(MainActivity.KEY, IPServer);
				edit.commit();*/
				
//				  Toast.makeText(getApplicationContext(), IPServer, Toast.LENGTH_LONG).show();
			}
		});
	    
	    AlertDialog ad1 = alertName.create();
	    
	    ad1.show();
	    
	    Intent intent = getIntent();
	    
	    newMessage = intent.getStringExtra(MainActivity.MESSAGE);//����� �����������
	    if (!TextUtils.isEmpty(newMessage))
	    {
	    	
		/*	if(myMessage==null) myMessage = new MyMessage();
			
			myMessage.setFrom(userName);
			myMessage.setText("exit");
			myMessage.setTo("server");
			
			new Thread(new SendThread(MainActivity.this,s,myMessage)).start();//������� ����� ��������� ��������  s = nullpointer!!! TODO*/
	    	
	    	ad1.cancel();
	    	stopService(new Intent(this, GetMessageService.class));
	    	
	    	myMessage.setFrom(userName);
			myMessage.setTo("server");
			myMessage.setText("regusername");
			
			arrListHistory.add(gson.fromJson(newMessage, MyMessage.class).getText());
			
		    new Thread(new CreateSocketThread(this)).start(); 
	    	
	    }
	    
    }
}
