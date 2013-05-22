package com.hugo.droidapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.hugo.imagehandler.SmartImageView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieDetailsFragment extends Fragment {

	private static final String TAG = "MovieDetailsFragment";

	public final static String PREFS_FILE = "PREFS_FILE";
	private final static String NETWORK_ERROR = "NETWORK_ERROR";
	private final static String GETMOVIEDETAILS = "GETMOVIEDETAILS";
	private final static String BOOKORDER = "BOOKORDER";
	private final static String INVALID_REQUEST = "INVALID_REQUEST";
	ProgressDialog mProgressDialog;
	String mediaId;
	String eventId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View frg2 = (View) inflater.inflate(R.layout.movie_details_frag,
				container, true);

		Bundle b = getActivity().getIntent().getExtras();
		mediaId = b.getString("MediaId");
		eventId = b.getString("EventId");

		if ((!(mediaId.equalsIgnoreCase("")) || mediaId != null)
				&& (!(eventId.equalsIgnoreCase("")) || eventId != null)) {
			UpdateDetails();
		}
		return frg2;
	}

	public void BookOrder(String formatType,String optType) {
		// Log.d(TAG, "getDetails");
		try {
			new getDetailsAsynTask().execute(BOOKORDER,formatType,optType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void UpdateDetails() {
		// Log.d(TAG, "getDetails");
		try {

			new getDetailsAsynTask().execute(GETMOVIEDETAILS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class getDetailsAsynTask extends AsyncTask<String, Void, String> {
       private String taskName="" ;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Log.d(TAG, "onPreExecute");
			mProgressDialog = new ProgressDialog(getActivity(),ProgressDialog.THEME_HOLO_DARK);
			mProgressDialog.setMessage("Retrieving Details...");
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// Log.d(TAG, "doInBackground");
            taskName = params[0];
			/*
			 * if (isNetworkAvailable()) {
			 * 
			 * return callExternalApi(params[0], params[1]);
			 * 
			 * } else { return NETWORK_ERROR; }
			 */
			
				if (Utilities.isNetworkAvailable(getActivity()
						.getApplicationContext())) {
					if (taskName.equalsIgnoreCase(GETMOVIEDETAILS)) {
						

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("TagURL", "assetdetails/" + mediaId);
					map.put("eventId", eventId);

					return Utilities.callExternalApiGetMethod(getActivity()
							.getApplicationContext(), map);
					}else if(taskName.equalsIgnoreCase(BOOKORDER)){
							HashMap<String, String> map = new HashMap<String, String>();
							String sDateFormat = "yyyy-mm-dd";
							DateFormat df = new SimpleDateFormat(sDateFormat);
							String formattedDate = df.format(new Date()); 
							
							map.put("TagURL", "eventorder");
							map.put("locale","en");
    	 					map.put("dateFormat",sDateFormat);
    	 					map.put("eventBookedDate",formattedDate);
    	 					map.put("formatType",params[1]);
    	 					map.put("optType",params[2]);
							map.put("eventId", eventId);
							return Utilities.callExternalApiPostMethod(getActivity()
									.getApplicationContext(), map);
				}
				else {
						return INVALID_REQUEST;
				}
				}
				else {
					return NETWORK_ERROR;
				}
				
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d(TAG, "onPostExecute");
			if (!result.equals("")) {
				Log.d(TAG, result);
				if (taskName.equalsIgnoreCase(GETMOVIEDETAILS)){
					updateUI(result);
				}else if(taskName.equalsIgnoreCase(BOOKORDER)){
					Intent intent = new Intent(getActivity(),Video.class);
					try {
						intent.putExtra("url",((String)(new JSONObject(result)).get("resourceIdentifier")));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startActivity(intent);
				}

			} else {
				// set no details for selection
			}
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
		}

		public String callExternalApi(String mediaId, String eventId) {
			Log.d(TAG, "callExternalApi");
			StringBuilder builder = new StringBuilder();
			HttpClient client = MySSLSocketFactory.getNewHttpClient();
			JSONObject json = new JSONObject();
			/*
			 * HttpGet httpGet = new HttpGet(
			 * "https://spark.openbillingsystem.com/mifosng-provider/api/v1/assets/None"
			 * );
			 */
			// HttpPost httpPost = new
			// HttpPost("https://192.168.2.38:8443/mifosng-provider/api/v1/assets");
			HttpPost httpPost = new HttpPost(
					"https://183.82.98.11:8443/mifosng-provider/api/v1/assetdetails/"
							+ mediaId);
			/*
			 * HttpPost httpPost = new
			 * HttpPost("https://183.82.98.11:8443/mifosng-provider/api/v1/assets"
			 * );
			 */
			// httpGet.setHeader("mifos","password");
			httpPost.setHeader("X-Mifos-Platform-TenantId", "default");
			httpPost.setHeader("Authorization", "Basic "
					+ "bWlmb3M6cGFzc3dvcmQ=");
			httpPost.setHeader("Content-Type", "application/json");

			try {
				json.put("deviceId", "efa4c629924f8139");
				json.put("eventId", "2");
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
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

			Log.i("callClientsApi", "Calling " + httpPost.getURI());
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
					Log.e("callExternalAPI", "Failed to download file"
							+ statusCode);
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

		public boolean isNetworkAvailable() {
			Log.d(TAG, "getDetails");
			ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
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

			NetworkInfo activeNetwork = connectivityManager
					.getActiveNetworkInfo();
			if (activeNetwork != null && activeNetwork.isConnected()) {
				return true;
			}
			return false;
		}
	}

	public void updateUI(String result) {
		Log.d(TAG, "updateUI" + result);
		if (result != null) {

			View dtls_frag = getActivity().findViewById(R.id.mvdtls_fragment);
			MovieDetailsObj mvDtlsObj = MovieDetailsEngine
					.parseMovieDetails(result);
			((SmartImageView) dtls_frag.findViewById(R.id.mvdtls_mov_img))
					.setImageUrl(mvDtlsObj.getImage());
			((ImageView) dtls_frag.findViewById(R.id.mvdtls_rating_img))
					.setImageResource(getResources().getIdentifier(
							"rate" + mvDtlsObj.getRating().replace('.', '_'),
							"drawable", "com.hugo.droidapplication"));
			((TextView) dtls_frag.findViewById(R.id.mvdtls_title_tv))
					.setText(mvDtlsObj.getTitle());
			((TextView) dtls_frag.findViewById(R.id.mvdtls_descr_tv))
					.setText(mvDtlsObj.getOverview());
			((TextView) dtls_frag.findViewById(R.id.mvdtls_dirby_val_tv))
					.setText(mvDtlsObj.getDirectors());
			((TextView) dtls_frag.findViewById(R.id.mvdtls_writby_val_tv))
					.setText(mvDtlsObj.getWriters());
			((TextView) dtls_frag.findViewById(R.id.mvdtls_genere_val_tv))
					.setText(mvDtlsObj.getGenres());
			((TextView) dtls_frag.findViewById(R.id.mvdtls_durn_val_tv))
					.setText(mvDtlsObj.getDuration());
			((TextView) dtls_frag.findViewById(R.id.mvdtls_lang_val_tv))
					.setText(mvDtlsObj.getLanguage());
			((TextView) dtls_frag.findViewById(R.id.mvdtls_cast_val_tv))
					.setText(mvDtlsObj.getActors());
			if (mvDtlsObj.arrPriceDetails.size() > 0) {
				((TextView) dtls_frag.findViewById(R.id.mvdtls_rent_tv))
						.setVisibility(View.VISIBLE);
				for (int i = 0; i < mvDtlsObj.arrPriceDetails.size(); i++) {
					PriceDetailsObj priceObj = mvDtlsObj.arrPriceDetails.get(i);
					if (priceObj.getFormatType().equalsIgnoreCase("HDX")) {
						((Button) dtls_frag.findViewById(R.id.mvdtls_hdx_btn))
								.setVisibility(View.VISIBLE);
						((Button) dtls_frag.findViewById(R.id.mvdtls_hdx_btn))
								.setText("HDX   $"
										+ String.format("%2.2f",
												priceObj.getPrice()));
					} else if (priceObj.getFormatType().equalsIgnoreCase("HD")) {
						((Button) dtls_frag.findViewById(R.id.mvdtls_hd_btn))
								.setVisibility(View.VISIBLE);
						((Button) dtls_frag.findViewById(R.id.mvdtls_hd_btn))
								.setText("HD   $"
										+ String.format("%2.2f",
												priceObj.getPrice()));
					} else if (priceObj.getFormatType().equalsIgnoreCase("SD")) {
						((Button) dtls_frag.findViewById(R.id.mvdtls_sd_btn))
								.setVisibility(View.VISIBLE);
						((Button) dtls_frag.findViewById(R.id.mvdtls_sd_btn))
								.setText("SD   $"
										+ String.format("%2.2f",
												priceObj.getPrice()));
					}
				}
			}
			getActivity().findViewById(R.id.mvdtls_rlayout1).setVisibility(
					View.VISIBLE);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// needs to get out from null pointer Exception on callling another
		// activity.
		outState.putString("DO NOT CRASH", "OK");
	}

}
