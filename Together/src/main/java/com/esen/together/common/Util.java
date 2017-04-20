package com.esen.together.common;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class Util {
	//------------------------------------------------------------------------------------------------ 앱, 서비스 상태 체크 관련
	public static boolean checkUsageStats(Context context) {
		//Log.d(Define.TAG, "Build.VERSION : " + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
			//Log.d(Define.TAG, "appOps : " + appOps);
			int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
			boolean granted = mode == AppOpsManager.MODE_ALLOWED;

			//Log.d(Define.TAG, "MainActivity : get_usage_stats : " + granted);

			return granted;
		}

		return true;
	}

	public static boolean checkUsageStatsActivity(Context context) {
		Intent intentStats = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
		PackageManager manager = context.getPackageManager();
		List<ResolveInfo> infos = manager.queryIntentActivities(intentStats, 0);

		return infos.size() > 0;
	}

	public static boolean isScreenOn(Context context) {
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);

		return pm.isScreenOn();
	}

	public static String getDeviceID(Context context) {
		//MyLog.getInstance().logCat("getDeviceID : " + Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
		return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	public static String GetDevicesUUID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;

		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();

		return deviceId;
	}

	public static String getAccount(Context context, String param) {
		String addr = "noaddress";

		AccountManager mgr = AccountManager.get(context);
		Account[] accounts = mgr.getAccountsByType(param);

		if(accounts.length > 0) {
			addr = accounts[0].name;
		}

		return addr;
	}

	public static boolean checkNotificationSetting(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
		String packageName = context.getPackageName();

		return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
	}

	public static boolean isServiceRunningCheck(Context context, String id) {
		ActivityManager manager = (ActivityManager)context.getSystemService(Activity.ACTIVITY_SERVICE);
		String serviceName;

		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (id.equals(service.service.getClassName())) {							// "com.esen.aboutme.service.MainService"
				return true;
			}
		}
		return false;
	}

	public static boolean checkAccessibility(Context context, String id) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {       // over 15	4.0.3
			return isAccessibilityEnabled(context, id);                             	// "com.esen.pahost/.service.HookKatalkSC"
		}
		else {                                                                      	// under 15	4.0.3
			return false;
		}
	}

	@SuppressLint("NewApi")
	public static boolean isAccessibilityEnabled(Context context, String id) {
		AccessibilityManager am = (AccessibilityManager) context
				.getSystemService(Context.ACCESSIBILITY_SERVICE);

		List<AccessibilityServiceInfo> runningServices = am
				.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
		for (AccessibilityServiceInfo service : runningServices) {
			if (id.equals(service.getId())) {
				return true;
			}
		}

		return false;
	}

	public static String getWifiSSID(Context context) {
		String ssid = "";

		WifiManager wifimanager;
		wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		WifiInfo info = wifimanager.getConnectionInfo();
		ssid = info.getSSID();

		return ssid;
	}

	public static int getNetType(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if(netInfo == null)
			return 0;

		return netInfo.getType();
	}

	public static boolean checkNetworkState(Context context) {
		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if((networkInfo == null) || (networkInfo.isConnected() == false)){
			return false;
		}

		return true;
	}

	public static String getLocServiceProvider(Context context) {
		LocationManager mLocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String mProvider = mLocManager.getBestProvider(new Criteria(), true);

		return mProvider;
	}

	public static int getPhoneAppVer(Context context) {
		int appVersion = 0;

		try {
			appVersion = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionCode;
		}
		catch (NameNotFoundException e) {
			appVersion = 0;
		}

		return appVersion;
	}

	public static String getModelName() {
		return Build.MODEL;
	}



	//------------------------------------------------------------------------------------------------ 날짜, 시간 관련
	public static  String getThisTimeFromMillisWithFormat(long mills, String format) {
		return new SimpleDateFormat(format, Locale.KOREA).format(new Date(mills));
	}

	public static String getToday() {
		SimpleDateFormat format = new SimpleDateFormat(Define.DATE_TIME_FORMAT, Locale.KOREA);
		Date currentTimeDate = new Date(System.currentTimeMillis());
		String time = format.format(currentTimeDate);
		//Log.i(Define.TAG, "getCurrDateAndTime() > Current Time : " + time);

		return time.substring(0, 10);
	}

	public static String getToday(String format) {
		return new SimpleDateFormat(format, Locale.KOREA).format(new Date());
	}

	public static String getYesterday() {
		SimpleDateFormat format = new SimpleDateFormat(Define.DATE_TIME_FORMAT, Locale.KOREA);
		Date currentTimeDate = new Date(System.currentTimeMillis() - Define.DAYHOURS_IN_MILLIS);
		String time = format.format(currentTimeDate);
		//Log.i(Define.TAG, "getYesterDateAndTime > Yesterday Time : " + time);

		return time.substring(0, 10);
	}

	public static String getYesterday(String format) {
		return new SimpleDateFormat(format, Locale.KOREA).format(new Date(System.currentTimeMillis() - Define.DAYHOURS_IN_MILLIS));
	}

	public static int getDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public static String getDayHourMinFromMillis(long millis) {
		if(millis <= 0) {
			StringBuilder sb = new StringBuilder(64);
			sb.append("0");
			sb.append(" Days ");
			sb.append("0");
			sb.append(" Hours ");
			sb.append("0");
			sb.append(" Minutes ");
			sb.append("0");
			sb.append(" Seconds");

			return sb.toString();
			//throw new IllegalArgumentException("Duration must be greater than zero!");		// saka check, 16.11.15, 15.55, control about 0 millis
		}

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		StringBuilder sb = new StringBuilder(64);
		sb.append(days);
		sb.append(" Days ");
		sb.append(hours);
		sb.append(" Hours ");
		sb.append(minutes);
		sb.append(" Minutes ");
		sb.append(seconds);
		sb.append(" Seconds");

		return sb.toString();
	}

	public static  String get24hoursFromMillis(long millisec) {
		String retValue = "";

		if(millisec <= 0)
			retValue = "00:00:00";
		else
			retValue = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisec),
					TimeUnit.MILLISECONDS.toMinutes(millisec) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisec)),
					TimeUnit.MILLISECONDS.toSeconds(millisec) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisec)));

		return retValue;
	}

	public static  String get24hoursFromSeconds(int sec) {
		String retValue = "";

		if(sec <= 0)
			retValue = "00:00:00";
		else
			retValue = String.format("%02d:%02d:%02d", TimeUnit.SECONDS.toHours(sec),
					TimeUnit.SECONDS.toMinutes(sec) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(sec)),
					TimeUnit.SECONDS.toSeconds(sec) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec)));

		return retValue;
	}



	//------------------------------------------------------------------------------------------------ 전화번호 관련
	public static String getDispNameFromPhoneNumber(Context context, String phoneNumber) {
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME};
		String displayName = "noname";

		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()){
				displayName = cursor.getString(0);
			}

			cursor.close();
		}

		return displayName;
	}
	
	public static String getPhoneNumWithout82nDash(Context context) {
		String _phoneNum = "";

		try {
			_phoneNum = (getPhoneNumWithout82(context)).replaceAll("-", "");	//Remove dash
		} catch (SecurityException e) {
			_phoneNum = Define.EXCEPTION_PHONE_NUMBER;
		}
		
		return _phoneNum;
	}
	
	public static String getPhoneNumWithout82(Context context) {
		String phoneNumber = null;
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);

		phoneNumber = telManager.getLine1Number();
		phoneNumber = phoneNumber.replace("+82", "0");

		return phoneNumber;
	}

	public static String getPhoneNumWithDash(Context context) {
		String _phoneNum = getPhoneNumWithout82nDash(context);

		if(_phoneNum.equals("")) {
			return "";
		}

		_phoneNum = _phoneNum.substring(0, 3) + "-" + _phoneNum.substring(3, _phoneNum.length()-4) + "-" + _phoneNum.substring(_phoneNum.length()-4);
		return _phoneNum;
	}	

	public static String getPhoneNumberFormat(String src) {
		if (src.length() < 10) {
			return "";
		}

		if (src.length() == 11) {
			return src.substring(0, 3) + "-" + src.substring(3, 7) + "-" + src.substring(7);
		}
		else {
			return src.substring(0, 3) + "-" + src.substring(3, 6) + "-" + src.substring(6);
		}
	}



	//------------------------------------------------------------------------------------------------ 유틸
	public static void registGCM(Context context) {
		/*GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);

		final String regId = GCMRegistrar.getRegistrationId(context);

		if("".equals(regId) || regId == null)
			GCMRegistrar.register(context, Defines.GCM_SENDER_ID);
		else
			Log.i("==============", regId);*/
	}

	public static void showMsg(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void printMapFromInt(Map<String, Integer> map) {
		if(map == null)
			return;

		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			Log.i(Define.TAG, "Key : " + entry.getKey() + ", Value : " + entry.getValue());
		}
	}

	public static int printMapFromhString(Map<String, String> map) {
		if(map == null)
			return 1;

		for (Map.Entry<String, String> entry : map.entrySet()) {
			Log.i(Define.TAG, "[Key] : " + entry.getKey() + ", [Value] : " + entry.getValue());
		}

		return 0;
	}



	//------------------------------------------------------------------------------------------------	암호관련
	public static String md5(String in) {
		MessageDigest digest;

		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(in.getBytes());
			byte[] a = digest.digest();
			int len = a.length;
			StringBuilder sb = new StringBuilder(len << 1);

			for (int i = 0; i < len; i++) {
				sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
				sb.append(Character.forDigit(a[i] & 0x0f, 16));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) { e.printStackTrace(); }

		return null;
	}

	public static byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() == 0) {
			return null;
		}

		byte[] ba = new byte[hex.length() / 2];

		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}

		return ba;
	}

	public static String byteArrayToHex(byte[] ba) {
		if (ba == null || ba.length == 0) {
			return null;
		}

		StringBuffer sb = new StringBuffer(ba.length * 2);
		String hexNumber;

		for (int x = 0; x < ba.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & ba[x]);
			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}

		return sb.toString();
	}
}
