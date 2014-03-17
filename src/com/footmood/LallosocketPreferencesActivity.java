package com.footmood;


import com.footmood.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class LallosocketPreferencesActivity extends PreferenceActivity {

	SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);   	
        addPreferencesFromResource(R.xml.sockpreferences); 
        
    }
    
    
    @Override
    public void onStart() {
      super.onStart();	  
    }    
    
    
    @Override 
    protected void onResume() { 
        super.onResume(); 
        
    } 
     
    @Override 
    protected void onPause() { 
        super.onPause();        
    }     
    
    
}
