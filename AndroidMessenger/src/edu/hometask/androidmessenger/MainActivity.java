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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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
	
//	private Socket s;
	private String /*IPServer, userName,*/ newMessage;
	private DataInputStream dis;
	private DataOutputStream dos;
	private SharedPreferences sp;
	private Gson gson;
	private Connection con;
	
	private Contacts contacts;
	private ArrayList<String> arrListContacts;
	private ArrayAdapter<String> adapterContacts;
	
//	private ArrayList<String> arrListHistory;
	private ArrayAdapter<String> adapterHistory;
	
	EditText etInput, etServerName, etUserName;
	ListView lvHistory, lvContacts;
	Button btnSend;

	private AlertDialog ad1;

	private AlertDialog ad;
	
	
	private SQLiteConnector connector;
	private SQLiteDatabase db;
	
	private class SQLiteConnector extends SQLiteOpenHelper
	{

		public SQLiteConnector(Context context, String name, int version)
		{
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			
			db.execSQL("create table ipserver (_id integer primary key autoincrement, ipServer varchar(25))");
			
			db.execSQL("create table username (_id integer primary key autoincrement, userName varchar(25))");
			
			db.execSQL("create table history (_id integer primary key autoincrement, idUser integer, history varchar(255))");
			//TODO
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			//все возможные варианты
			if(oldVersion==1 && newVersion==2)
			{
//				alterTable etc
			}
			else if(oldVersion==2 && newVersion==3)
			{
				
			}
			else if(oldVersion==1 && newVersion==3)
			{
				
			}
		}
	}

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
					ma.con.setIPServer(msg.obj.toString());
					
					ma.dialogInputName();
//					ma.setIPServer(msg.obj.toString());
					//Toast.makeText(ma, ma.getIPServer(), Toast.LENGTH_LONG).show();
				}
			}
			if(msg.what==MainActivity.HANDLER_KEYUSERNAME)
			{
				MainActivity ma = wrActivity.get();
				if(ma!=null)
				{
					ma.con.setUserName(msg.obj.toString());
					ma.con.getMessage().setFrom(ma.con.getUserName());
					ma.con.getMessage().setTo("server");
					ma.con.getMessage().setText("regusername");
				    
				    //Первое подключение к серверу
				    new Thread(new CreateSocketThread(ma.con)).start(); 
				}
			}
			if(msg.what==MainActivity.HANDLER_KEYSEND)
			{
				MainActivity ma = wrActivity.get();
				if(ma!=null)
				{
					ma.etInput.setText("");
					
					ma.con.getArrListHistory().add(msg.obj.toString());
					
					ma.adapterHistory = new ArrayAdapter<String>(ma,android.R.layout.simple_list_item_1, ma.con.getArrListHistory());//здесь не надо почему-то
					
			        ma.lvHistory.setAdapter(ma.adapterHistory);//обязательно, без него не работает
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
						if(ma.arrListContacts.get(i).equals(ma.con.getUserName()))
						{
							ma.arrListContacts.remove(i);//убрали самого себя
						}
					}
					
					Collections.sort(ma.arrListContacts);//отсортировал список контактов
					
					ma.adapterContacts = new ArrayAdapter<String>(ma,android.R.layout.simple_list_item_single_choice, ma.arrListContacts);//здесь обязательно
					
			        ma.lvContacts.setAdapter(ma.adapterContacts);
			        
			        ma.lvContacts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);//можно выбрать только один пункт в списке
			        
			        if(ma.arrListContacts.size()>0)
			        {
			        	ma.lvContacts.performItemClick(ma.lvContacts.getAdapter().//Программное нажатие на элемент списка 0
				                getView(0, null, null), 0, ma.lvContacts.getAdapter().
				                getItemId(0));
			        }
			        
			        ma.con.getHistoryFromDB();
			        ma.lvHistory.setAdapter(ma.adapterHistory);//обязательно, без него не работает
			        
			        
			        //ma.lvContacts.setBackgroundColor(5); не работает
			        
				}
			}
			
			if(msg.what==MainActivity.HANDLER_KEYCREATESOCKET)
			{
				MainActivity ma = wrActivity.get();
				if(ma!=null)
				{
//					ma.s = (Socket) msg.obj;
					new Thread(new GetThread(ma.con)).start(); 
				}
			}
		}
    }
	
	public void dialogInputName()//TODO 
	{
		   View viewName = View.inflate(this, R.layout.namechoice, null);
		    AlertDialog.Builder alertName = new AlertDialog.Builder(MainActivity.this);
		    alertName.setView(viewName);
		    alertName.setTitle(R.string.namechoicetitle);
//		    alertName.setMessage(R.string.personName);
		    alertName.setCancelable(false);
		    
		    etUserName = (EditText) viewName.findViewById(R.id.dialog1EditText);
		    
		    alertName.setPositiveButton(R.string.newName, new DialogInterface.OnClickListener()
	        {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					con.setUserName(etUserName.getText().toString());
					
					MainActivity.hMain.sendMessage(
							MainActivity.hMain.obtainMessage(
									MainActivity.HANDLER_KEYUSERNAME, con.getUserName()));
					
					dialog.cancel();
					
				}
			});
		    //выбрать последнее зарегеное имя
		/*    alertName.setNegativeButton(R.string.oldName, new DialogInterface.OnClickListener()
	        {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					con.returnLastUserNameFromDB();
					
					MainActivity.hMain.sendMessage(
							MainActivity.hMain.obtainMessage(
									MainActivity.HANDLER_KEYUSERNAME, con.getUserName()));
					dialog.cancel();
				}
			});*/
		    
		    ad1 = alertName.create();
		    
		    ad1.show();
	}
	
	
	public Connection getCon() 
	{
		return con;
	}

	public void setCon(Connection con)
	{
		this.con = con;
	}

/*	public ArrayList<String> getArrListHistory() 
	{
		return arrListHistory;
	}*/

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

    public void setEtInputText(String txt)
    {
    	etInput.setText(txt);
    }
    
    public String getEtInputText()
    {
    	return etInput.getText().toString();
    }

	@Override
	protected void onDestroy()//TODO
	{
		con.getMessage().setFrom(con.getUserName());
		con.getMessage().setText("exit");
		con.getMessage().setTo("server");
		
		new Thread(new SendThread(con)).start();//отправляем серверу чтобы закрыл сокет
				
//			Log.d("MyTag", "String after Send exit to server");
		
		startService(new Intent(this, GetMessageService.class).putExtra(MainActivity.StoService, con.getUserName()));
	
		if(hMain!=null) hMain.removeCallbacksAndMessages(null);//очищает отправленные с задержкой сообщения
		super.onDestroy();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
		connector = new SQLiteConnector(this, "edu.hometask.androidmessenger.Messenger", 1);
		db = connector.getWritableDatabase();
        
       	con = new Connection(new MyMessage(), null,"", "", 3571,db);
        
        gson = new Gson();
        
        hMain = new MyHandler(this);
        
//        arrListHistory = new ArrayList<String>();
        arrListContacts = new ArrayList<String>();
        
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        lvHistory = (ListView) findViewById(R.id.lvHistory);
        etInput = (EditText) findViewById(R.id.etInput);
        btnSend = (Button) findViewById(R.id.btnSend);
        
        adapterHistory = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, con.getArrListHistory());
        
        lvHistory.setAdapter(adapterHistory);
        
        adapterContacts = new ArrayAdapter<String>(this,R.layout.mylistview, arrListContacts);
      
        lvContacts.setAdapter(adapterContacts);
        
        
      lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() //нажатие на пункт списка
      {
    		@Override
    		public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) 
    		{
    			//etInput.setText(((TextView) itemClicked).getText()+": ");
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
				
				con.getMessage().setText(etInput.getText().toString());
				
				con.getMessage().setTo(arrListContacts.get(lvContacts.getCheckedItemPosition()));//OK
				
				new Thread(new SendThread(con)).start();
			}
		});
        
        View view = View.inflate(this, R.layout.serverchoice, null);
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
				con.setIPServer(etServerName.getText().toString());
				
				MainActivity.hMain.sendMessage(
						MainActivity.hMain.obtainMessage(
								MainActivity.HANDLER_KEYSERVER, con.getIPServer()));
				
				dialog.cancel();
				
				sp = getSharedPreferences("SelectedServer", MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putString(MainActivity.SPKEY, con.getIPServer());
				edit.commit();
			}
		});
	    
	    alert.setNegativeButton(R.string.oldServer, new DialogInterface.OnClickListener()
        {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				/*sp = getSharedPreferences("SelectedServer", MODE_PRIVATE);
				con.setIPServer(sp.getString(MainActivity.SPKEY, ""));*/
				
				con.returnLastIpServerFromDB();
				
				MainActivity.hMain.sendMessage(
						MainActivity.hMain.obtainMessage(
								MainActivity.HANDLER_KEYSERVER, con.getIPServer()));
				dialog.cancel();
			}
		});
	    
	    ad = alert.create();
	    ad.show();
	    
     
	    
	    Intent intent = getIntent();
	    
	    newMessage = intent.getStringExtra(MainActivity.MESSAGE);//ловля нотификации
	    if (!TextUtils.isEmpty(newMessage))
	    {
	    	
		/*	if(myMessage==null) myMessage = new MyMessage();
			
			myMessage.setFrom(userName);
			myMessage.setText("exit");
			myMessage.setTo("server");
			
			new Thread(new SendThread(MainActivity.this,s,myMessage)).start();
			//убиваем сокет созданный сервисом  s = nullpointer!!! TODO*/
	    	
	    	ad.cancel();//закрытие диалога с IP сервера
//	    	ad1.cancel();//он не стартует, он внутри ad
	    	stopService(new Intent(this, GetMessageService.class));
	    	
	    	gson = new Gson();
	    	
	    	con = new Connection(new MyMessage(), null,"", "", 3571,db);
	    	
	    	con.returnLastIpServerFromDB();
	    	
//	    	con.getMessage().setText(gson.fromJson(newMessage, MyMessage.class).getText());
	    	
	    	con.setMessage(gson.fromJson(newMessage, MyMessage.class));
	    	
	    	con.setUserName(con.getMessage().getTo());//мне шло из нотифи
	    	
	    	//con.addNewHistoryToDB();
	    	
	    	con.getMessage().setFrom(con.getUserName());
			con.getMessage().setTo("server");
			con.getMessage().setText("regusername");
			
		    new Thread(new CreateSocketThread(con)).start(); 
	    	
	    }
	    
    }
}
