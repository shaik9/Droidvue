package com.hugo.droidapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

public class Utilities {

	private static final String TAG = "Utilities";
	private static final int IO_BUFFER_SIZE = 4 * 1024;

	public static Bitmap loadBitmap(String url) {
		Bitmap bitmap = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		try {
			in = new BufferedInputStream(new URL(url).openStream(),
					IO_BUFFER_SIZE);

			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
			out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
			// copy(in, out);
			out.flush();

			final byte[] data = dataStream.toByteArray();
			BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 1;

			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
		} catch (IOException e) {
			Log.e(TAG, "Could not load Bitmap from: " + url);
		} finally {
			closeStream(in);
			closeStream(out);
		}

		return bitmap;
	}

	/**
	 * Closes the specified stream.
	 * 
	 * @param stream
	 *            The stream to close.
	 */
	private static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				android.util.Log.e(TAG, "Could not close stream", e);
			}
		}
	}

	public static String callExternalApiGetMethod(Context context,
			HashMap<String, String> param) {
		Log.d(TAG, "callExternalApiGetMethod");
		StringBuilder builder = new StringBuilder();
		HttpClient client = MySSLSocketFactory.getNewHttpClient();
		StringBuilder url = new StringBuilder(
				context.getString(R.string.server_url));
		url.append(param.get("TagURL") + "?");
		param.remove("TagURL");
		// adding params to url
		for (int i = 0; i < param.size(); i++) {
			url.append("&" + (String) param.keySet().toArray()[i] + "="
					+ (String) param.values().toArray()[i]);
		}
		// append device id to url
		String androidId = Settings.Secure.getString(
				context.getContentResolver(), Settings.Secure.ANDROID_ID);
		//url.append("&deviceId="+androidId);
		url.append("&deviceId=efa4c629924f8139");
		HttpGet httpGet = new HttpGet(url.toString());
		httpGet.setHeader("X-Mifos-Platform-TenantId", "default");
		httpGet.setHeader("Authorization", "Basic " + "bWlmb3M6cGFzc3dvcmQ=");
		httpGet.setHeader("Content-Type", "application/json");

		Log.i("callClientsApi", "Calling " + httpGet.getURI());
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("callExternalAPI", "Failed to download file" + statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public static String callExternalApiPostMethod(Context context,
			HashMap<String, String> param) {
		Log.d(TAG, "callExternalApi");
		StringBuilder builder = new StringBuilder();
		HttpClient client = MySSLSocketFactory.getNewHttpClient();
		String url = context.getString(R.string.server_url);
		url += (param.get("TagURL"));
		param.remove("TagURL");
		JSONObject json = new JSONObject();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("mifos", "password");
		httpPost.setHeader("X-Mifos-Platform-TenantId", "default");
		httpPost.setHeader("Authorization", "Basic " + "bWlmb3M6cGFzc3dvcmQ=");
		httpPost.setHeader("Content-Type", "application/json");
		// append device id to url
		String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		try {
			//json.put("deviceId",androidId);
			json.put("deviceId","efa4c629924f8139");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		try {
			for (int i = 0; i < param.size(); i++) {
				json.put((String) param.keySet().toArray()[i], (String) param
						.values().toArray()[i]);
			}
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		StringEntity se = null;
		try {
			se = new StringEntity(json.toString());
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		se.setContentType("application/json");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		httpPost.setEntity(se);
		Log.i("callExternalApiPostMethod", " httpPost.getURI " + httpPost.getURI());
		Log.i("callExternalApiPostMethod", "json: " +json);
		try {
			HttpResponse response = client.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("callExternalAPI", "Failed to download file" + statusCode);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	public static boolean isNetworkAvailable(Context context) {
		Log.d(TAG, "getDetails");
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo wifiNetwork = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}
		return false;
	}
	
	public static Bitmap scaleImage(Context context, Bitmap bitmap,int reqScale)
	{
		// reqScale in dp
	   /* // Get the ImageView and its bitmap
	    ImageView view = (ImageView) findViewById(R.id.image_box);
	    Drawable drawing = view.getDrawable();
	    if (drawing == null) {
	        return; // Checking for null & return, as suggested in comments
	    }
	    Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();*/

	    // Get current dimensions AND the desired bounding box
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    
	    float density = context.getResources().getDisplayMetrics().density;
	    int bounding = Math.round((float)reqScale * density);;
	    
	    Log.i("Test", "original width = " + Integer.toString(width));
	    Log.i("Test", "original height = " + Integer.toString(height));
	    Log.i("Test", "bounding = " + Integer.toString(bounding));

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.  
	    float xScale = ((float) bounding) / width;
	    float yScale = ((float) bounding) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;
	    Log.i("Test", "xScale = " + Float.toString(xScale));
	    Log.i("Test", "yScale = " + Float.toString(yScale));
	    Log.i("Test", "scale = " + Float.toString(scale));

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView 
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	   /* width = scaledBitmap.getWidth(); // re-use
	    height = scaledBitmap.getHeight(); // re-use
	    BitmapDrawable result = new BitmapDrawable(scaledBitmap);*/
	    Log.i("Test", "scaled width = " + Integer.toString(width));
	    Log.i("Test", "scaled height = " + Integer.toString(height));

	    // Apply the scaled bitmap
	    /*view.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams(); 
	    params.width = width;
	    params.height = height;
	    view.setLayoutParams(params);*/

	    Log.i("Test", "done");
	    return scaledBitmap;
	}

	/*static int dpToPx(int dp)
	{
	    float density = getApplicationContext().getResources().getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	}*/
}
