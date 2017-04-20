package com.esen.together.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedData {
	private static SharedData sInstance;
	private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    
	private SharedData(Context context) {
	    mPref = PreferenceManager.getDefaultSharedPreferences(context);
	    mEditor = mPref.edit();
	}
    
	public synchronized static SharedData getInstance(Context context) {		// public static SharedData getInstance(Context context)
	    if (sInstance == null) {
	        sInstance = new SharedData(context);
	    }
	    
	    return sInstance;
	}

    public void setServiceState(int status) {
        mEditor.putInt("service_status", status);
        mEditor.commit();
    }

    public int getServiceState() {
        return mPref.getInt("service_status", 0);
    }
}
