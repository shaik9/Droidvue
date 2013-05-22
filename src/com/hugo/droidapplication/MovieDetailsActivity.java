package com.hugo.droidapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MovieDetailsActivity extends FragmentActivity {

	private static final String TAG = "MovieDetailsActivity";
	public final static String PREFS_FILE = "PREFS_FILE";
	private final static String NETWORK_ERROR = "NETWORK_ERROR";

	private SharedPreferences mPrefs;
	private ProgressDialog mProgressDialog;
	//private TextView mMenuName;
	//private ListView mlistView;
	//private Editor mPrefsEditor;
	//private EditText mSearchEt;
	//private Button mSearchBtn;

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

		//mMenuName = (TextView) findViewById(R.id.tv_menuname);
		//mSearchEt = (EditText) findViewById(R.id.search_et);
		//mSearchBtn = (Button) findViewById(R.id.search_btn);
		//mlistView = (ListView) findViewById(R.id.listView);

		//mlistView.setDivider(null);
		//mlistView.setSelector(R.color.listgrad);
		///mlistView.setAdapter(new ArrayAdapter<String>(
		//		MovieDetailsActivity.this, R.layout.lv_item, R.id.list_content,
		//		MenuData.mMap.get(getString(R.string.movies))));
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
		AlertDialog dialog = new AlertDialog.Builder(MovieDetailsActivity.this,AlertDialog.THEME_HOLO_DARK).create();
	        dialog.setTitle("Confirmation");
	        dialog.setMessage("Do you want to continue?...");
	        dialog.setCancelable(false);
	        
	        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int buttonId) {
	            	MovieDetailsFragment fragment = (MovieDetailsFragment)getSupportFragmentManager().findFragmentById(R.id.mvdtls_fragment);
	        		fragment.BookOrder(formatTypeparam,optTypeparam);
	            }
	        });
	        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int buttonId) {
	            	MovieDetailsActivity.this.finish();	                
	            }
	        });
	        dialog.setIcon(android.R.drawable.ic_dialog_alert);
	        dialog.show();
	         }

}
