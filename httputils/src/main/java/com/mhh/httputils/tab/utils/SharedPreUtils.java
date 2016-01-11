package com.mhh.httputils.tab.utils;

import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreUtils {

	
	private static SharedPreUtils utils;
	private static SharedPreferences preferences;
	
	public SharedPreUtils(Context context){
		if (preferences == null) {
			preferences = context.getSharedPreferences(Config.getSharedTable(), 0);
		}
	}
	
	public static SharedPreUtils getInstance(Context context){
		if (utils == null) {
			utils = new SharedPreUtils(context);
		}		
		return utils;
	}
	
	public String getDataString(String key){
	    String value = preferences.getString(key, "");
	    return value;
    }
	
	public void setDataString(String key, String value){
	    Editor editor = preferences.edit();
	    editor.putString(key, value);
	    editor.commit();
    }
	
	@SuppressLint("NewApi")
	public Set<String> getDataStringSet(String key){
		Set<String> value = preferences.getStringSet(key, null);
	    return value;
    }
	
	@SuppressLint("NewApi")
	public void setDataStringSet(String key, Set<String> value){
	    Editor editor = preferences.edit();
	    editor.putStringSet(key, value);
	    editor.commit();
    }
	
	public int getDataInt(String key){
	    int value = preferences.getInt(key, 0);
	    return value;
    }
	
	public void setDataInt(String key, int value){
	    Editor editor = preferences.edit();
	    editor.putInt(key, value);
	    editor.commit();
    }
	
	public float getDataFloat(String key){
		float value = preferences.getFloat(key, 0);
	    return value;
    }
	
	public void setDataFloat(String key, float value){
	    Editor editor = preferences.edit();
	    editor.putFloat(key, value);
	    editor.commit();
    }
	
	public long getDataLong(String key){
		long value = preferences.getLong(key, 0);
	    return value;
    }
	
	public void setDataLong(String key, long value){
	    Editor editor = preferences.edit();
	    editor.putLong(key, value);
	    editor.commit();
    }
	
	public boolean getDataBoolean(String key){
		boolean value = preferences.getBoolean(key, false);
	    return value;
    }
	
	public void setDataBoolean(String key, boolean value){
	    Editor editor = preferences.edit();
	    editor.putBoolean(key, value);
	    editor.commit();
    }
	
	public Map<String, ?> getDataAll() {
		Map<String, ?> value = preferences.getAll();
	    return value;
	}
}
