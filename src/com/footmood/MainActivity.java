package com.footmood;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.footmood.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnClickListener {
   
	final String TAG = "LALLOSOCKET";
  	SharedPreferences sharedPreferences; 
  	String pref_server_ip = "1.2.3.4";
  	String pref_server_port = "2000";
//  	ArrayList<String> stringhe_comandi = new ArrayList<String>();
//  	ArrayList<String> stringhe_bottoni = new ArrayList<String>();
//  	ArrayList<Button> bottoni = new ArrayList<Button>();
  	public Socket socket;
	public NetworkTask networktask;
	public TextView textX, textY, textZ;
	public TextView textP0, textP1, textSteps;
	public Button testButton;
	public Button buttonAzzera;
	public Long stepCounter;
	public Long sogliaBassa;
	public Long sogliaAlta;
	public Long X, Y, Z, P0, P1;
	public boolean flagStep;
	public Long passiT0;
	public Long passiT1;
	public Long contaPassi;
	public Long deltaWalk, deltaRun;
	public Integer status = 0;
	public TextView textStatus;
	public boolean flagAlza;
	public boolean flagAbbassa;
	public Long sogliaAbbassa;
	public Long sogliaAlza;
	
	private Mood currMood = null;
	
	
  	ToggleButton togglebuttonconnect;
  	
	private Handler timerHandler = new Handler(); 
	public static final int timerDelay = 1800; // in ms  	
 
	MediaPlayer player;
	AudioManager audio;
	static int maxVolumePercent, minVolumePercent, maxVolumeStream, originalVolume, currentVolume;
	static int originalVolumeAlarm;
	static double maxVolume, minVolume; 	
	
	public boolean connesso = false;
	private StereoFootPlayList stereoFootPlayList;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        player = new MediaPlayer();	
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        // setta il volume corrente e originale
        originalVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        currentVolume = originalVolume;
        maxVolumeStream = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        maxVolume = maxVolumeStream;
        minVolume = 0;
		// porta a zero il volume per il playback
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.ceil(minVolume), AudioManager.FLAG_SHOW_UI);        
        // setta il volume per gli alarm
		originalVolumeAlarm = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxVolumeAlarm = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// volume allarmi a 3/4
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.floor((maxVolumeAlarm / 4.0) * 3.0), AudioManager.FLAG_SHOW_UI);                
        
        
       passiT0 = 0L;
       passiT1 = 0L;
       contaPassi = 0L;
       deltaWalk = 1L;
       deltaRun = 4L;
       
       flagAbbassa = false;
       flagAlza = false;
       
       sogliaAlza = 2100L;
       sogliaAbbassa = 1600L;
        
        textX = (TextView) findViewById(R.id.textX);
        textY = (TextView) findViewById(R.id.textY);
        textZ = (TextView) findViewById(R.id.textZ);
        textP0 = (TextView) findViewById(R.id.textP0);
        textP1 = (TextView) findViewById(R.id.textP1);       
        textSteps = (TextView) findViewById(R.id.textSteps); 
        textStatus = (TextView) findViewById(R.id.textStatus); 
        
        azzera();
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this); 
        
        togglebuttonconnect = (ToggleButton) findViewById(R.id.toggleButtonConnect);
        togglebuttonconnect.setOnClickListener(this);
        
        testButton = (Button) findViewById(R.id.Button01);
        testButton.setOnClickListener(this);
        
        buttonAzzera = (Button) findViewById(R.id.buttonAzzera);
        buttonAzzera.setOnClickListener(this);        

		stereoFootPlayList = new StereoFootPlayList(this.getApplicationContext());
        
		// fai partire il timer
		timerChecker.run();        
        
//        bottoni.add((Button) findViewById(R.id.Button01));
        
//        for(int i=0; i<bottoni.size(); i++)  {
//        	bottoni.get(i).setOnClickListener(this);
//        	bottoni.get(i).setEnabled(togglebuttonconnect.isChecked());
//        }
		
		
        
    }
 
	
	
	
	public void azzera()  {
		X = 0L;
		Y = 0L;
		Z = 0L;
		P0 = 0L;
		P1 = 0L;
		stepCounter = 0L;
		sogliaBassa = 100L;
		sogliaAlta = 1000L;
		flagStep = false;		
	}
    
	  void getMyPreferences() {
//		    try {
//				Log.i(TAG, "recupero preferences");
//		    	// configurazione server
//		    	pref_server_ip = sharedPreferences.getString("pref_server_ip", pref_server_ip);
//		    	pref_server_port = sharedPreferences.getString("pref_server_port", pref_server_port);
//		        // configurazione bottoni e comandi
//		      	stringhe_comandi = new ArrayList<String>();
//		      	stringhe_bottoni = new ArrayList<String>();
//		    	for(int i=0; i<=bottoni.size(); i++) {
//		    		stringhe_bottoni.add(sharedPreferences.getString("pref_comando_button_"+i, getString(R.string.bottonetest)));
//		    		stringhe_comandi.add(sharedPreferences.getString("pref_comando_comando_"+i, ""));	    			
//		    	}
//		    } catch (Exception e) {
//		    	// --> Log.e(TAG, e.toString());
//		    }    	
	    }  		
	
	  
	   void setMyButtons()  {
		   Log.i(TAG, "settaggio visualizzazione");
//	       for(int i=0; i<=bottoni.size(); i++) {
//		       	String nomebottone = stringhe_bottoni.get(i);
//		       	String comandobottone = stringhe_comandi.get(i);
//		       	Log.i(TAG, "nome bottone "+(i+1)+" = "+nomebottone);
//		       	bottoni.get(i).setVisibility(nomebottone.contentEquals("") ||  comandobottone.contentEquals("") ? View.INVISIBLE : View.VISIBLE);
//		       	bottoni.get(i).setText(nomebottone);
//	       }	  
	   }
	   
	   public boolean onCreateOptionsMenu(Menu menu) {
	    	getMenuInflater().inflate(R.menu.menu, menu);
	    	return true;
	    }     
	    
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	    	return true;
	    }      
	      
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
	    	case R.id.itemSettings:
	        	Intent i = new Intent(this, LallosocketPreferencesActivity.class);   
	    		startActivity(i);
	    		break;
	    	}
	    	return super.onOptionsItemSelected(item);
	    }


		@Override
		public void onClick(View v) {
			if(v.getId()==togglebuttonconnect.getId())  {
				if(togglebuttonconnect.isChecked())
					serverConnect();
				else
					serverSconnect();
			} else if(v.getId()==testButton.getId())	{
				String comando = "s";
				Log.i(TAG, "invio comando: "+comando);	
	            try {
	                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
	                out.println(comando);
	                Log.d(TAG, "client sent message");
	                
	             } catch (UnknownHostException e) {
	                errore(e.toString());
	                e.printStackTrace();
	             } catch (IOException e) {
	            	errore(e.toString());
	                e.printStackTrace();
	             } catch (Exception e) {
	            	errore(e.toString());
	                e.printStackTrace();
	             }
			} else if(v.getId()==buttonAzzera.getId())	{ 
				azzera();
				textSteps.setText(String.valueOf(stepCounter));
			}
		} 
	    
		void serverConnect()  {
			Log.i(TAG, "inizio connessione");		
		      try {
		          InetAddress serverAddr = InetAddress.getByName(pref_server_ip);
		          socket = new Socket(serverAddr, Integer.parseInt(pref_server_port));
		          socket.setSoTimeout(5000);
		          buttonSetEnabledState(true);
		          networktask = new NetworkTask(); //New instance of NetworkTask
		          networktask.execute();
		          connesso = true;
		       } catch (Exception e) {
		    	  errore(e.toString());
		          e.printStackTrace();
		          togglebuttonconnect.setChecked(false);
		          connesso = false;
		       } 
		}
		
		void serverSconnect()  {
			Log.i(TAG, "sconnessione");	
			socket = null;
			buttonSetEnabledState(false);
			connesso = false;
		}
    
		void buttonSetEnabledState(boolean enable) {
//	        for(int i=0; i<=bottoni.size(); i++)  {
//	        	bottoni.get(i).setEnabled(enable);
//	        }			
		}
		
		void sendCommand(int i)  {
//			String comando = stringhe_comandi.get(i);
//			Log.i(TAG, "invio comando: "+comando);	
//            try {
//                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
//                out.println(comando);
//                Log.d(TAG, "client sent message");
//                
//             } catch (UnknownHostException e) {
//                errore(e.toString());
//                e.printStackTrace();
//             } catch (IOException e) {
//            	errore(e.toString());
//                e.printStackTrace();
//             } catch (Exception e) {
//            	errore(e.toString());
//                e.printStackTrace();
//             }
		}
		
		void errore(String err) {
			Toast.makeText(getBaseContext(), getString(R.string.errore)+err, Toast.LENGTH_LONG).show();
		}
		
		
		 public class NetworkTask extends AsyncTask<Void, byte[], Boolean> {
		        Socket nsocket; //Network Socket
		        InputStream nis; //Network Input Stream
		        OutputStream nos; //Network Output Stream

		        @Override
		        protected void onPreExecute() {
		            Log.i(TAG, "onPreExecute");
		        }

		        @Override
		        protected Boolean doInBackground(Void... params) { //This runs on a different thread
		            boolean result = false;
		            try {
		                Log.i(TAG, "doInBackground: Creating socket");
//		                SocketAddress sockaddr = new InetSocketAddress("192.168.1.1", 80);
//		                nsocket = new Socket();
//		                nsocket.connect(sockaddr, 5000); //10 second connection timeout
		                nsocket = socket;
		                if (socket.isConnected()) { 
		                    nis = nsocket.getInputStream();
		                    nos = nsocket.getOutputStream();
		                    Log.i(TAG, "doInBackground: Socket created, streams assigned");
		                    Log.i(TAG, "doInBackground: Waiting for inital data...");
		                    byte[] buffer = new byte[4096];
		                    int read = nis.read(buffer, 0, 4096); //This is blocking
		                    while(read != -1){
		                        byte[] tempdata = new byte[read];
		                        System.arraycopy(buffer, 0, tempdata, 0, read);
		                        publishProgress(tempdata);
		                        Log.i(TAG, "doInBackground: Got some data");
		                        read = nis.read(buffer, 0, 4096); //This is blocking
		                    }
		                }
		            } catch (IOException e) {
		                e.printStackTrace();
		                Log.i(TAG, "doInBackground: IOException");
		                result = true;
		            } catch (Exception e) {
		                e.printStackTrace();
		                Log.i(TAG, "doInBackground: Exception");
		                result = true;
		            } finally {
		                try {
		                    nis.close();
		                    nos.close();
		                    nsocket.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		                Log.i(TAG, "doInBackground: Finished");
		            }
		            return result;
		        }

		        public void SendDataToNetwork(String cmd) { //You run this from the main thread.
		            try {
		                if (nsocket.isConnected()) {
		                    Log.i(TAG, "SendDataToNetwork: Writing received message to socket");
		                    nos.write(cmd.getBytes());
		                } else {
		                    Log.i(TAG, "SendDataToNetwork: Cannot send message. Socket is closed");
		                }
		            } catch (Exception e) {
		                Log.i(TAG, "SendDataToNetwork: Message send failed. Caught an exception");
		            }
		        }

		        @Override
		        protected void onProgressUpdate(byte[]... values) {
		            if (values.length > 0) {
		                Log.i(TAG, "onProgressUpdate: " + values[0].length + " bytes received.");
		                Log.d(TAG, new String(values[0]));
		                String scarpa =  new String(values[0]);
		                if(scarpa.startsWith("!") && scarpa.length()>=19)  {
		                	X = Long.parseLong(scarpa.substring(1, 4), 16);
		                	Y = Long.parseLong(scarpa.substring(4, 7), 16);
		                	Z = Long.parseLong(scarpa.substring(7, 10), 16);
		                	P0 = Long.parseLong(scarpa.substring(10, 13), 16);
		                	P1 = Long.parseLong(scarpa.substring(13, 16), 16);
		                	textX.setText(String.valueOf(X));
		                	textY.setText(String.valueOf(Y));
		                	textZ.setText(String.valueOf(Z));
		                	textP0.setText(String.valueOf(P0));
		                	textP1.setText(String.valueOf(P1));
		                	if(P1 > sogliaAlta && !flagStep)  {
		                		flagStep =! flagStep;
		                		stepCounter++;
		                		contaPassi++;
		                		textSteps.setText(String.valueOf(stepCounter));
		                	} else if(P1< sogliaBassa && flagStep) {
		                		flagStep =! flagStep;
		                	}
		                }
//		                textStatus.setText(new String(values[0]));
		            }
		        }
		        @Override
		        protected void onCancelled() {
		            Log.i(TAG, "Cancelled.");
//		            btnStart.setVisibility(View.VISIBLE);
		        }
		        @Override
		        protected void onPostExecute(Boolean result) {
		            if (result) {
		                Log.i(TAG, "onPostExecute: Completed with an Error.");
		                //textStatus.setText("There was a connection error.");
		            } else {
		                Log.i(TAG, "onPostExecute: Completed.");
		            }
		            //btnStart.setVisibility(View.VISIBLE);
		        }
		    }				

		    @Override
		    protected void onResume() {
		    	super.onResume(); 
		    	Log.i("onResume()", "onResume");
		        timerHandler.postDelayed(timerChecker, timerDelay);
		    }		 
		 
		    @Override
		    protected void onPause() {
		   		super.onPause(); 
		   	  	Log.i("onPause()", "onPause");
		   		timerHandler.removeCallbacks(timerChecker); 
		    }

		    @Override
		    public void onStart() {
		      super.onStart();
		      Log.i("onStart()", "onStart");
		    }		    
	
		    @Override    
		    public void onDestroy() {

		    	if (player != null) {
		    		player.release();
		    	}
		    	// ripristina volumi iniziali
		    	if(audio != null) {
		    		//audio.setStreamVolume(AudioManager.STREAM_MUSIC, (int) Math.round(maxVolume), AudioManager.FLAG_SHOW_UI);
		    	}
		    	super.onDestroy();
		    }		    
		    
		    
		    public Runnable timerChecker = new Runnable() { 
		        public void run() {       		
		            timerHandler.removeCallbacks(timerChecker); // remove the old callback
		            Log.d(TAG, "dentro il timerChecker");
//		            passiT0 = 0L;
//		            passiT1 = 0L;
//		            contaPassi = 0L;
//		            deltaWalk = 2L;
//		            deltaRun = 4L;		
		            passiT1 = contaPassi;
		            Long deltaPassi = passiT1 - passiT0;
		            passiT0 = passiT1;
		            if(deltaPassi < deltaWalk)
		            	status = 0;
		            else if(deltaPassi >= deltaWalk && deltaPassi < deltaRun)
		            	status = 1;
		            else
		            	status = 2;
		            azioneStato();
		            if(connesso){
		            	azioneVolume();
		            }
		            timerHandler.postDelayed(timerChecker, timerDelay); // register a new one 		        
		        }
		    }; 	 
		    
		 public void azioneStato() {
			 Log.d(TAG, "status = "+status);
			 String labelStatus = "";
			 Mood mood;
			 switch (status)  {
			 case 0:
				 labelStatus = "Fermo";
				 mood = Mood.STILL;
				 break;
			 case 1:
				 labelStatus = "Camminata";
				 mood = Mood.WALK;
				 break;
			 case 2:
				 labelStatus = "Corsa";
				 mood = Mood.RUN;
				 break;
				 default:
					 mood = Mood.STILL;
			 }
			 textStatus.setText(labelStatus);
			 
			 if(currMood == null || !currMood.equals(mood)){
				 //cambio la play
				 currMood = mood;
				 Cursor csongs = stereoFootPlayList.getSongsList(currMood);
				 String data = null;
				 Integer sid = null;
				 String title = null;
				 if(csongs.moveToNext()){
					   sid = csongs.getInt(csongs.getColumnIndex(MediaStore.Audio.Playlists.Members._ID));
					   title = csongs.getString(csongs.getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));
					   data = csongs.getString(csongs.getColumnIndex(MediaStore.Audio.Playlists.Members.DATA));
					  };
					  
					  if(data!=null){
					   Uri myUri = Uri.parse(data);
					   try {
						   if(player.isPlaying()){
							   player.stop();
							   player.reset();
						   }
						   player.setDataSource(getApplicationContext(), myUri);
						   player.prepare();
						   player.start();
					    
					   } catch (Exception e) {
					    // TODO: handle exception
					   }

					  }
				 
			 }
		 }

		 
		 public void azioneVolume() {
			 Log.d(TAG, "azioneVolume");

			 if(Y>sogliaAlza) {
				 if(!flagAlza)
					 flagAlza =! flagAlza;
				 else
					 audio.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (audio.getStreamVolume(AudioManager.STREAM_MUSIC) +2), AudioManager.FLAG_SHOW_UI);
			 } else {
				 if(flagAlza) flagAlza =! flagAlza;
			 }

			 if(Y<sogliaAbbassa) {
				 if(!flagAbbassa)
					 flagAbbassa =! flagAbbassa;
				 else
					 audio.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (audio.getStreamVolume(AudioManager.STREAM_MUSIC) -2), AudioManager.FLAG_SHOW_UI);
			 } else {
				 if(flagAbbassa) flagAbbassa =! flagAbbassa;
			 }			 
		 }		 
		 
}
		