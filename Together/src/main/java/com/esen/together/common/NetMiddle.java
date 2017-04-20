package com.esen.together.common;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

public final class NetMiddle {
	// Main url
	public static final String SERVER_URL                       = "http://asm130415.cafe24.com";


	// Sub url
	public static final String JOIN_TARGETINFO                  = "/pa/pa_join.php";

	
	public static String updateServiceState(Context context, int serviceType) {
		JSONObject json = new JSONObject();
		
		try {
			json.put("service_type", serviceType);
			json.put("phone_num", "01012345678");
		}
		catch(Exception e) {
			//e.printStackTrace();
		}

		String jsonData = json.toString();
		Log.d(Define.TAG,"updateServiceState : " + jsonData);
		
		return NetComm.connectJSon(context, SERVER_URL + JOIN_TARGETINFO, jsonData);
	}
}























