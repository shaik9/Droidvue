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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MovieDetailsActivity extends FragmentActivity {

	private static final String TAG = "MovieDetailsActivity";
	public final static String PREFS_FILE = "PREFS_FILE";
	private final static String NETWORK_ERROR = "NETWORK_ERROR";
	private final static String GETMOVIEDETAILS = "GETMOVIEDETAILS";
	private final static String BOOKORDER = "BOOKORDER";
	private final static String INVALID_REQUEST = "INVALID_REQUEST";
	
	private SharedPreferences mPrefs;
	private ProgressDialog mProgressDialog;
	String mediaId;
	String eventId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mvdtls_activity);

		Log.d(TAG, "onCreate");

		mPrefs = getSharedPreferences(MovieDetailsActivity.PREFS_FILE, 0);
		/*
		 * mPrefsEditor = mPrefs.edit();
		 * mPrefsEditor.putString(getString(R.string
		 * .menu_name),getString(R.string.main_menu)); // set menu name
		 * mPrefsEditor.commit();
		 */

		// mMenuName = (TextView) findViewById(R.id.tv_menuname);
		// mSearchEt = (EditText) findViewById(R.id.search_et);
		// mSearchBtn = (Button) findViewById(R.id.search_btn);
		// mlistView = (ListView) findViewById(R.id.listView);

		// mlistView.setDivider(null);
		// mlistView.setSelector(R.color.listgrad);
		// /mlistView.setAdapter(new ArrayAdapter<String>(
		// MovieDetailsActivity.this, R.layout.lv_item, R.id.list_content,
		// MenuData.mMap.get(getString(R.string.movies))));
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

	private class getDetailsAsynTask extends AsyncTask<String, Void, String> {
		private String taskName = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Log.d(TAG, "onPreExecute");
			mProgressDialog = new ProgressDialog(MovieDetailsActivity.this,
					ProgressDialog.THEME_HOLO_DARK);
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

			if (Utilities.isNetworkAvailable(MovieDetailsActivity.this
					.getApplicationContext())) {
				if (taskName.equalsIgnoreCase(GETMOVIEDETAILS)) {

					HashMap<String, String> map = new HashMap<String, String>();
					map.put("TagURL", "assetdetails/" + mediaId);
					map.put("eventId", eventId);

					return Utilities.callExternalApiGetMethod(
							MovieDetailsActivity.this.getApplicationContext(),
							map);
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
					return Utilities.callExternalApiPostMethod(
							MovieDetailsActivity.this.getApplicationContext(),
							map);
				} else {
					return INVALID_REQUEST;
				}
			} else {
				return NETWORK_ERROR;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d(TAG, "onPostExecute");
			if (!result.equals("")) {
				Log.d(TAG, result);
				if (taskName.equalsIgnoreCase(GETMOVIEDETAILS)) {
					updateUI(result);
				} else if (taskName.equalsIgnoreCase(BOOKORDER)) {
					Intent intent = new Intent(MovieDetailsActivity.this,
							Video.class);
					try {
						intent.putExtra("url",
								((String) (new JSONObject(result))
										.get("resourceIdentifier")));
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

		public void updateUI(String result) {
			Log.d(TAG, "updateUI" + result);
			if (result != null) {

				MovieDetailsObj mvDtlsObj = MovieDetailsEngine
						.parseMovieDetails(result);
				((SmartImageView) findViewById(R.id.mvdtls_mov_img))
						.setImageUrl(mvDtlsObj.getImage());
				((ImageView) findViewById(R.id.mvdtls_rating_img))
						.setImageResource(getResources().getIdentifier(
								"rate"
										+ mvDtlsObj.getRating().replace('.',
												'_'), "drawable",
								"com.hugo.droidapplication"));
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
