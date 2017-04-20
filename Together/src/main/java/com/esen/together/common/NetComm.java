package com.esen.together.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public final class NetComm {	
	public static final int NET_TCP_TIMEOUT_CONNECT					= 12000;	// Timeout
	public static final int NET_TCP_TIMEOUT_READ					= 12000;	// Timeout
	
	public static final int NET_UDP_TIMEOUT_SOCKET					= 30000;	// UDP Timeout
	public static final int NET_UDP_RESPONSE_SIZE					= 6;		// UDP Response size



	//------------------------------------------------------------------------------------------------------------
	// upper 5.1.1 (api level 22)
	public static String connectJSon(Context context, String url, String jsonData) {
		return _connectJSon(context, url, jsonData);
	}

	public static String _connectJSon(Context context, String url, String jsonData) {
		String result = Define.NETCOMM_ERROR;

		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

//		Log.d(Defines.NETCOMM_TAG, "connMgr : " + connMgr);
//		Log.d(Defines.NETCOMM_TAG, "networkInfo : " + networkInfo.isConnected());

		if((networkInfo == null) || (networkInfo.isConnected() == false)){
			return Define.NETCOMM_NO_NETWORK;
		}

		HttpURLConnection urlConnection;
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);

			URL pageUrl = new URL(url);
			urlConnection = (HttpURLConnection) pageUrl.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setUseCaches(false);
			urlConnection.setConnectTimeout(NET_TCP_TIMEOUT_CONNECT);
			urlConnection.setReadTimeout(NET_TCP_TIMEOUT_READ);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.connect();

			//Log.i(Defines.TAG, "_connectJSon > url : " + url);
			OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
			out.write(jsonData);
			out.close();

			int status = urlConnection.getResponseCode();

			//Log.d(Defines.TAG,"_connectJSon > Url : " +  url + ", status : " + status + ", jsonData : " + jsonData);
			if(status == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					//sb.append(line + "\n");
					if(sb.length() != 0) {
						sb.append("\n");
					}
					sb.append(line);
					//Log.i(Defines.TAG, "line : " + line);
				}
				br.close();


				result = sb.toString();
				//Log.d(Defines.TAG, "result : " + result);

				return result;
			}
			else {
				//Log.d(Defines.TAG,"Url Result Errror = " +  url + "==>" + status);
				return Define.NETCOMM_ERROR;
			}

		}
		catch(Exception e) {
			e.printStackTrace();
			return Define.NETCOMM_ERROR;
		}
	}

	// under 5.1.1 (api level 22)
	/*public static String _connectJSonHttpClient(Context context, String url, String jsonData) {
		String result = Define.NETCOMM_ERROR;

		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo	= connMgr.getActiveNetworkInfo();

		//Log.d(Defines.NETCOMM_TAG, "connMgr : " + connMgr);
		//Log.d(Defines.NETCOMM_TAG, "networkInfo : " + networkInfo.isConnected());

		if((networkInfo == null) || (networkInfo.isConnected() == false)) {
			return Define.NETCOMM_NO_NETWORK;
		}

		try {
			//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			//StrictMode.setThreadPolicy(policy);

			HttpClient httpClient		= new DefaultHttpClient();
			HttpParams httpParams		= new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(httpParams, NET_TCP_TIMEOUT_CONNECT);
			HttpConnectionParams.setSoTimeout(httpParams, NET_TCP_TIMEOUT_READ);

			HttpPost httpPost			= new HttpPost(url);
			//StringEntity stringEntity	= new StringEntity(jsonData);
			StringEntity stringEntity	= new StringEntity(jsonData, HTTP.UTF_8);	//For UTF-8

			stringEntity.setChunked(false);
			//stringEntity.setContentEncoding(HTTP.UTF_8);
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);

			HttpResponse httpResponse	= httpClient.execute(httpPost);
			HttpEntity httpEntity		= httpResponse.getEntity();

			int status = httpResponse.getStatusLine().getStatusCode();
			Log.d(Define.NETCOMM_TAG, "Url : " + url + ", status : " + status);
			if(status == HttpStatus.SC_OK){
				result = EntityUtils.toString(httpEntity, HTTP.UTF_8);				//For UTF-8

				return result;
			} else{
				//Log.d(Defines.NETCOMM_TAG,"Error, Url : " +  url + ", status : " + status);

				return Define.NETCOMM_ERROR;
			}
		}
		catch (UnsupportedEncodingException e){
			//e.printStackTrace();
			return Define.NETCOMM_ERROR;
		}
		catch (ClientProtocolException e){
			//e.printStackTrace();
			return Define.NETCOMM_ERROR;
		}
		catch (IOException e){
			//e.printStackTrace();
			return Define.NETCOMM_ERROR;
		}
	}*/



	//------------------------------------------------------------------------------------------------------------
	/*
	public static String makePostData(Map<String, String> params){
		String result = null;

        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }

        result = bodyBuilder.toString();

        return result;
	}

	public static String connectPost(Context context, String url, String postData){
		String result = Defines.NETCOMM_ERROR;

		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo		= connMgr.getActiveNetworkInfo();

		if( (networkInfo == null) || (networkInfo.isConnected() == false) ){
			com.esen.pahostanother.common.Utils.showMsg(context, Defines.NETCOMM_NO_NETWORK);
			return Defines.NETCOMM_NO_NETWORK;
		}

		ConntPostThread thread = new ConntPostThread(context, url, postData);
		thread.setDaemon(true);
		thread.start();

		try {
			thread.join();
		}
		catch (InterruptedException e) { }

		result = thread.getResult();
		return result;
	}

	public static class ConntPostThread extends Thread {
		Context mContext;
		String mUrl, mPostData, mResult = Defines.NETCOMM_ERROR;

		ConntPostThread(Context context, String url, String postData) {
			mContext = context;
			mUrl = url;
			mPostData = postData;
		}

		public void run() {
			try{
//				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//				StrictMode.setThreadPolicy(policy);

				HttpClient httpClient		= new DefaultHttpClient();
				HttpParams httpParams		= new BasicHttpParams();

				HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
				HttpConnectionParams.setSoTimeout(httpParams, 5000);

				HttpPost httpPost			= new HttpPost(mUrl);
//				StringEntity stringEntity	= new StringEntity(jsonData);
				StringEntity stringEntity	= new StringEntity(mPostData, HTTP.UTF_8);   //For UTF-8

				stringEntity.setChunked(false);
//				stringEntity.setContentEncoding(HTTP.UTF_8);
//				stringEntity.setContentType("application/json");
				stringEntity.setContentType("application/x-www-form-urlencoded");
				httpPost.setEntity(stringEntity);

				HttpResponse httpResponse	= httpClient.execute(httpPost);
				HttpEntity httpEntity		= httpResponse.getEntity();

				int status                  = httpResponse.getStatusLine().getStatusCode();
				Log.d(Defines.NETCOMM_TAG,"Url : " +  mUrl + ", status : " + status + ", postData : " + mPostData);
				if(status == HttpStatus.SC_OK){
					mResult = EntityUtils.toString(httpEntity, HTTP.UTF_8);				 //For UTF-8
					Log.d(Defines.NETCOMM_TAG,"ConnectPostThread > mResult : " + mResult);

				}
				else{
//					Log.d(Defines.NETCOMM_TAG,"Error, Url : " +  mUrl + ", status : " + status);
				}
			}
			catch (UnsupportedEncodingException e){ }
			catch (ClientProtocolException e){ }
			catch (IOException e){ }
		}

		public String getResult(){
			return mResult;
		}
	}

	public static void connectPostWithHandler(Context context, Handler handler, String url, String postData){
		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo		= connMgr.getActiveNetworkInfo();

		if( (networkInfo == null) || (networkInfo.isConnected() == false) ){
			com.esen.pahostanother.common.Utils.showMsg(context, Defines.NETCOMM_NO_NETWORK);
			return;
		}

		ConnPostThreadWithHandler thread = new ConnPostThreadWithHandler(context, handler, url, postData);
		thread.setDaemon(true);
		thread.start();
	}

	public static class ConnPostThreadWithHandler extends Thread {
		Context mContext;
		Handler mHandler;
		String mUrl, mPostData, mResult = Defines.NETCOMM_ERROR;

		ConnPostThreadWithHandler(Context context, Handler handler, String url, String postData) {
			mContext = context;
			mHandler = handler;
			mUrl = url;
			mPostData = postData;
		}

		public void run() {
			Message msg  = Message.obtain();

			try{
//				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//				StrictMode.setThreadPolicy(policy);

				HttpClient httpClient		= new DefaultHttpClient();
				HttpParams httpParams		= new BasicHttpParams();

				HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
				HttpConnectionParams.setSoTimeout(httpParams, 5000);

				HttpPost httpPost			= new HttpPost(mUrl);
//				StringEntity stringEntity	= new StringEntity(jsonData);
				StringEntity stringEntity	= new StringEntity(mPostData, HTTP.UTF_8);   //For UTF-8

				stringEntity.setChunked(false);
//				stringEntity.setContentEncoding(HTTP.UTF_8);
//				stringEntity.setContentType("application/json");
				stringEntity.setContentType("application/x-www-form-urlencoded");
				httpPost.setEntity(stringEntity);

				HttpResponse httpResponse	= httpClient.execute(httpPost);
				HttpEntity httpEntity		= httpResponse.getEntity();

				int status                  = httpResponse.getStatusLine().getStatusCode();
				Log.d(Defines.NETCOMM_TAG,"Url : " +  mUrl + ", status : " + status + ", postData : " + mPostData);
				if(status == HttpStatus.SC_OK){
					mResult = EntityUtils.toString(httpEntity, HTTP.UTF_8);				 //For UTF-8
					Log.d(Defines.NETCOMM_TAG,"ConnectPostThread > mResult : " + mResult);

				}
				else{
//					Log.d(Defines.NETCOMM_TAG,"Error, Url : " +  mUrl + ", status : " + status);
				}
			}
			catch (UnsupportedEncodingException e){	}
			catch (ClientProtocolException e){ }
			catch (IOException e){ }

			msg.what = 0;
			msg.obj = mResult;
			mHandler.sendMessage(msg);
		}
	}



	//------------------------------------------------------------------------------------------------------------
	public static String connectGet(Context context, String url){
		String result = Defines.NETCOMM_ERROR;

		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo		= connMgr.getActiveNetworkInfo();

		if( (networkInfo == null) || (networkInfo.isConnected() == false) ){
			com.esen.pahostanother.common.Utils.showMsg(context, Defines.NETCOMM_NO_NETWORK);
			return Defines.NETCOMM_NO_NETWORK;
		}

		ConnectGetThread thread = new ConnectGetThread(context, url);
		thread.setDaemon(true);
		thread.start();

		try {
			thread.join();
		}
		catch (InterruptedException e) { }

		result = thread.getResult();
		return result;
	}

	public static class ConnectGetThread extends Thread {
		Context mContext;
		String mUrl, mResult = Defines.NETCOMM_ERROR;

		ConnectGetThread(Context context, String url) {
			mContext = context;
			mUrl = url;
		}

		public void run() {
			try{
				HttpClient httpClient		= new DefaultHttpClient();
				HttpGet getMethod           = new HttpGet(mUrl);

				HttpResponse httpResponse	= httpClient.execute(getMethod);
				HttpEntity httpEntity		= httpResponse.getEntity();

				int status                   = httpResponse.getStatusLine().getStatusCode();
				Log.d(Defines.NETCOMM_TAG,"Url : " +  mUrl + ", status : " + status);
				if(status == HttpStatus.SC_OK){
					mResult = EntityUtils.toString(httpEntity);				 //For UTF-8			// HTTP.UTF_8
					Log.d(Defines.NETCOMM_TAG,"ConnectGetThread > mResult : " + mResult);
				}
				else{
//					Log.d(Defines.NETCOMM_TAG,"Error, Url : " +  mUrl + ", status : " + status);
				}
			}
			catch (UnsupportedEncodingException e){	}
			catch (ClientProtocolException e){ }
			catch (IOException e){ }
		}

		public String getResult(){
			return mResult;
		}
	}


	public static void connectGetWithHandler(Context context, Handler handler, String url){
		String result = Defines.NETCOMM_ERROR;

		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo		= connMgr.getActiveNetworkInfo();

		if( (networkInfo == null) || (networkInfo.isConnected() == false) ){
			com.esen.pahostanother.common.Utils.showMsg(context, Defines.NETCOMM_NO_NETWORK);
//			return Defines.NETCOMM_NO_NETWORK;
		}

		ConnectGetThreadWithHandler thread = new ConnectGetThreadWithHandler(context, handler, url);
		thread.setDaemon(true);
		thread.start();
	}

	public static class ConnectGetThreadWithHandler extends Thread {
		Context mContext;
		Handler mHandler;
		String mUrl, mResult = Defines.NETCOMM_ERROR;

		ConnectGetThreadWithHandler(Context context, Handler handler, String url) {
			mContext = context;
			mHandler = handler;
			mUrl = url;
		}

		public void run() {
			Message msg  = Message.obtain();

			try{
				HttpClient httpClient		= new DefaultHttpClient();
				HttpGet getMethod           = new HttpGet(mUrl);

				HttpResponse httpResponse	= httpClient.execute(getMethod);
				HttpEntity httpEntity		= httpResponse.getEntity();

				int status                   = httpResponse.getStatusLine().getStatusCode();
				Log.d(Defines.NETCOMM_TAG,"Url : " +  mUrl + ", status : " + status);
				if(status == HttpStatus.SC_OK){
					mResult = EntityUtils.toString(httpEntity);				 //For UTF-8		// mResult = EntityUtils.toString(HTTP.UTF_8);
					Log.d(Defines.NETCOMM_TAG,"ConnectGetThread > mResult : " + mResult);
				}
				else{
//					Log.d(Defines.NETCOMM_TAG,"Error, Url : " +  mUrl + ", status : " + status);
				}
			}
			catch (UnsupportedEncodingException e){ }
			catch (ClientProtocolException e){ }
			catch (IOException e){ }

			msg.what = 0;
			msg.obj = mResult;
			mHandler.sendMessage(msg);
		}
	}



	//------------------------------------------------------------------------------------------------------------
	synchronized public static String connectUdp(Context context, String url, int sendPort, int receivePort, String reqString)
	{
		String result = Defines.NETCOMM_ERROR;

		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo		= connMgr.getActiveNetworkInfo();
		if( (networkInfo == null) || (networkInfo.isConnected() == false) )
		{
			com.esen.pahostanother.common.Utils.showMsg(context, Defines.NETCOMM_NO_NETWORK);
			return Defines.NETCOMM_NO_NETWORK;
		}

		ConnUdpThread thread = new ConnUdpThread(context, url, sendPort, receivePort, reqString);
		thread.setDaemon(true);
		thread.start();

		try {
			thread.join();
		}
		catch (InterruptedException e) { }

		result = thread.getResult();
		return result;
	}

	public static class ConnUdpThread extends Thread {
		Context mContext;
		String mUrl, mResult = Defines.NETCOMM_ERROR, mReqString;
		int mSendPort, mReceivePort;

		ConnUdpThread(Context context, String url, int sendPort, int receivePort, String reqString) {
			mContext = context;
			mUrl = url;

			mSendPort = sendPort;
			mReceivePort = receivePort;
			mReqString = reqString;
		}

		public void run() {
			DatagramSocket socket = null;

			try{
				InetAddress serverAddr = InetAddress.getByName(mUrl);
				Log.d(Defines.NETCOMM_TAG, "IP : " + serverAddr.getHostAddress());
				socket = new DatagramSocket(mReceivePort);

				byte[] buf = mReqString.getBytes();
				byte[] recvbuf = new byte[512];

				DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, mSendPort);

				socket.send(packet);
				Log.d(Defines.NETCOMM_TAG, "UDP Send : " + serverAddr.getHostAddress());
				Log.d(Defines.NETCOMM_TAG, "mReqString : " + mReqString);

				packet = new DatagramPacket(recvbuf, recvbuf.length);
				socket.setSoTimeout(NET_UDP_TIMEOUT_SOCKET);
				socket.receive(packet);

				mResult = new String(packet.getData(), 0, packet.getLength());
				Log.d(Defines.NETCOMM_TAG,"ConnectUdpThread > mResult : " + mResult);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				if(socket != null){
					socket.close();
				}
			}
		}

		public String getResult(){
			return mResult;
		}
	}



	//------------------------------------------------------------------------------------------------------------
	public static Source connectHtmlParsing(Context context, String url){
		Source result = null;

		ConnectivityManager connMgr	= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo		= connMgr.getActiveNetworkInfo();

		if( (networkInfo == null) || (networkInfo.isConnected() == false) ){
			com.esen.pahostanother.common.Utils.showMsg(context, Defines.NETCOMM_NO_NETWORK);
			return null;
		}

		ConnHtmlParsingThread thread = new ConnHtmlParsingThread(context, url);
		thread.setDaemon(true);
		thread.start();

		try {
			thread.join();
		}
		catch (InterruptedException e) { }

		result = thread.getResult();
		return result;
	}

	public static class ConnHtmlParsingThread extends Thread {
		Context mContext;
		String mUrl, mResult = Defines.NETCOMM_ERROR;
		Source mSource;

		ConnHtmlParsingThread(Context context, String url) {
			mContext = context;
			mUrl = url;
		}

		public void run() {
			URL nURL;

			try {
				nURL = new URL(mUrl);
				InputStream html = nURL.openStream();
				mSource = new Source(new InputStreamReader(html, "euc-kr"));
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public Source getResult(){
			return mSource;
		}
	}
	*/
}

























