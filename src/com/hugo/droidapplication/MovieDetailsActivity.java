package com.hugo.droidapplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.hugo.imagehandler.SmartImageView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MovieDetailsActivity extends FragmentActivity {

	private static final String TAG = "MovieDetailsActivity";
	public final static String PREFS_FILE = "PREFS_FILE";
	private final static String NETWORK_ERROR = "NETWORK_ERROR";
	private final static String GETMOVIEDETAILS = "GETMOVIEDETAILS";
	private final static String BOOKORDER = "BOOKORDER";
	private final static String INVALID_REQUEST = "INVALID_REQUEST";
	
	private ProgressDialog mProgressDialog;
	String mediaId;
	String eventId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mvdtls_activity);

		Log.d(TAG, "onCreate");

		
		Bundle b = getIntent().getExtras();
		mediaId = b.getString("MediaId");
		eventId = b.getString("EventId");

		if ((!(mediaId.equalsIgnoreCase("")) || mediaId != null)
				&& (!(eventId.equalsIgnoreCase("")) || eventId != null)) {
			UpdateDetails();
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

	public void ButtonOnClick(View v) {
		String formatType = "";
		String optType = "";
		switch (v.getId()) {
		case R.id.mvdtls_hdx_btn:
			optType = "RENT";
			formatType = "HDX";
			break;
		case R.id.mvdtls_hd_btn:
			formatType = "HD";
			optType = "RENT";
			break;
		case R.id.mvdtls_sd_btn:
			formatType = "SD";
			optType = "RENT";
			break;
		}
		final String formatTypeparam = formatType;
		final String optTypeparam = optType;
		AlertDialog dialog = new AlertDialog.Builder(MovieDetailsActivity.this,
				AlertDialog.THEME_HOLO_DARK).create();
		dialog.setTitle("Confirmation");
		dialog.setMessage("Do you want to continue?...");
		dialog.setCancelable(false);

		dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int buttonId) {
						BookOrder(formatTypeparam, optTypeparam);
					}
				});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int buttonId) {
						MovieDetailsActivity.this.finish();
					}
				});
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.show();
	}

	public void BookOrder(String formatType, String optType) {
		// Log.d(TAG, "getDetails");
		try {
			new getDetailsAsynTask().execute(BOOKORDER, formatType, optType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class getDetailsAsynTask extends AsyncTask<String, Void, ResponseObj> {
		private String taskName = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Log.d(TAG, "onPreExecute");
			if(mProgressDialog!= null){
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			mProgressDialog = new ProgressDialog(MovieDetailsActivity.this,
					ProgressDialog.THEME_HOLO_DARK);
			mProgressDialog.setMessage("Retrieving Details...");
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
		}

		@Override
		protected ResponseObj doInBackground(String... params) {
			// Log.d(TAG, "doInBackground");
			taskName = params[0];
			ResponseObj resObj = new ResponseObj();
			if (Utilities.isNetworkAvailable(MovieDetailsActivity.this
					.getApplicationContext())) {
				if (taskName.equalsIgnoreCase(GETMOVIEDETAILS)) {

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("TagURL", "assetdetails/" + mediaId);
					map.put("eventId", eventId);

					resObj= Utilities.callExternalApiGetMethod(
							MovieDetailsActivity.this.getApplicationContext(),
							map);
					return resObj; 
				} else if (taskName.equalsIgnoreCase(BOOKORDER)) {
					HashMap<String, String> map = new HashMap<String, String>();
					String sDateFormat = "yyyy-mm-dd";
					DateFormat df = new SimpleDateFormat(sDateFormat);
					String formattedDate = df.format(new Date());

					map.put("TagURL", "eventorder");
					map.put("locale", "en");
					map.put("dateFormat", sDateFormat);
					map.put("eventBookedDate", formattedDate);
					map.put("formatType", params[1]);
					map.put("optType", params[2]);
					map.put("eventId", eventId);
					
					resObj= Utilities.callExternalApiPostMethod(
							MovieDetailsActivity.this.getApplicationContext(),
							map);
					return resObj;
				} else {
					resObj.setFailResponse(100, INVALID_REQUEST);
					return resObj;
				}
			} else {
				resObj.setFailResponse(100, NETWORK_ERROR);
				return resObj;
			}

		}

		@Override
		protected void onPostExecute(ResponseObj resObj) {
			super.onPostExecute(resObj);
			Log.d(TAG, "onPostExecute");
			
			if(resObj.getStatusCode()==200){
 				if (taskName.equalsIgnoreCase(GETMOVIEDETAILS)) {
					updateUI(resObj.getsResponse());
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
				} else if (taskName.equalsIgnoreCase(BOOKORDER)) {
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					Intent intent = new Intent(MovieDetailsActivity.this,
							Video.class);
					try {
						intent.putExtra("url",
								((String) (new JSONObject(resObj.getsResponse()))
										.get("resourceIdentifier")));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startActivity(intent);
				}

			} else {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this,AlertDialog.THEME_HOLO_DARK);
				// Add the buttons
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { 
				           public void onClick(DialogInterface dialog, int id) {
				        	  // MovieDetailsActivity.this.finish();
				           }
				       });
				AlertDialog dialog =builder.create();
				dialog.setMessage(resObj.getsErrorMessage());
				/*TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
				messageText.setGravity(Gravity.CENTER);*/
				dialog.show();
			}	
		}

		public void updateUI(String result) {
			Log.d(TAG, "updateUI" + result);
			if (result != null) {

				MovieDetailsObj mvDtlsObj = MovieDetailsEngine
						.parseMovieDetails(result);
				((SmartImageView) findViewById(R.id.mvdtls_mov_img))
						.setImageUrl(mvDtlsObj.getImage());
				
				((RatingBar) findViewById(R.id.mvdtls_rating_img)).setRating(Float.parseFloat(mvDtlsObj.getRating()));
				((TextView) findViewById(R.id.mvdtls_title_tv))
						.setText(mvDtlsObj.getTitle());
				((TextView) findViewById(R.id.mvdtls_descr_tv))
						.setText(mvDtlsObj.getOverview());
				((TextView) findViewById(R.id.mvdtls_dirby_val_tv))
						.setText(mvDtlsObj.getDirectors());
				((TextView) findViewById(R.id.mvdtls_writby_val_tv))
						.setText(mvDtlsObj.getWriters());
				((TextView) findViewById(R.id.mvdtls_genere_val_tv))
						.setText(mvDtlsObj.getGenres());
				((TextView) findViewById(R.id.mvdtls_durn_val_tv))
						.setText(mvDtlsObj.getDuration());
				((TextView) findViewById(R.id.mvdtls_lang_val_tv))
						.setText(mvDtlsObj.getLanguage());
				((TextView) findViewById(R.id.mvdtls_cast_val_tv))
						.setText(mvDtlsObj.getActors());
				if (mvDtlsObj.arrPriceDetails.size() > 0) {
					((TextView) findViewById(R.id.mvdtls_rent_tv))
							.setVisibility(View.VISIBLE);
					Button btn = null;
					boolean isFocusSet = false;
					for (int i = 0; i < mvDtlsObj.arrPriceDetails.size(); i++) {
						PriceDetailsObj priceObj = mvDtlsObj.arrPriceDetails
								.get(i);
						if (priceObj.getFormatType().equalsIgnoreCase("HDX")) {

							btn = ((Button) findViewById(R.id.mvdtls_hdx_btn));
							btn.setVisibility(View.VISIBLE);
							btn.setText("HDX   $"
									+ String.format("%2.2f",
											priceObj.getPrice()));
							btn.setFocusable(true);
							if (!isFocusSet)
							{
								 btn.setFocusableInTouchMode(true);
								isFocusSet =true;
								btn.requestFocus();
							}

						} else if (priceObj.getFormatType().equalsIgnoreCase(
								"HD")) {
							btn = ((Button) findViewById(R.id.mvdtls_hd_btn));
							btn.setVisibility(View.VISIBLE);
							btn.setText("HD   $"
									+ String.format("%2.2f",
											priceObj.getPrice()));
							btn.setFocusable(true);
							if (!isFocusSet)
							{
								 btn.setFocusableInTouchMode(true);
								 btn.requestFocus();
								isFocusSet =true;
							}

						} else if (priceObj.getFormatType().equalsIgnoreCase(
								"SD")) {
							btn = ((Button) findViewById(R.id.mvdtls_sd_btn));
							btn.setVisibility(View.VISIBLE);
							btn.setText("SD   $"
									+ String.format("%2.2f",
											priceObj.getPrice()));
							btn.setFocusable(true);
							if (!isFocusSet)
							{
								 btn.setFocusableInTouchMode(true);
								isFocusSet =true;
								btn.requestFocus();
							}
							
						}
						btn = null;
					}
				}
				MovieDetailsActivity.this.findViewById(R.id.mvdtls_rlayout1)
						.setVisibility(View.VISIBLE); 
			}
		}

	}
}
