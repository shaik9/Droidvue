package com.hugo.droidapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Video extends Activity{
	
	
	// Put in your Video URL here
	//private String VideoURL ="http://183.82.98.11:1935/vod/mp4:Sample.mp4/playlist.m3u8";//"rtsp://183.82.98.11:1935/vod/mp4:BigBuckBunny_115k.mov";//"rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov"; //"http://183.82.98.11:1935/vod/mp4:sample.mp4/playlist.m3u8";//"http://183.82.98.11:1935/vod/mp4:sample1.mp4/playlist.m3u8";//"rtsp://183.82.98.11:1935/vod/sample.mp4 ";//"http://183.82.98.11:1935/vod/mp4:sample1.mp4/playlist.m3u8";//"http://192.168.2.38:1935/vod/sample.mp4";//"http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
	// Declare some variables
	 //String  VideoURL= getIntent().getStringExtra("url");
	//Bundle bdl ;
    // String VideoURL=this.getIntent().getStringExtra("url");
     //String VideoURL = bdl.getString("url");
	private ProgressDialog pDialog;
	VideoView videoview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the layout from video_main.xml
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_main);
		// Find your VideoView in your video_main.xml layout
		videoview = (VideoView) findViewById(R.id.VideoView);
		
		// Execute StreamVideo AsyncTask
		new StreamVideo().execute();

	}

	// StreamVideo AsyncTask
	private class StreamVideo extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Create a progressbar
			pDialog = new ProgressDialog(Video.this);
			// Set progressbar title
			pDialog.setTitle("Android Video Streaming ");
			// Set progressbar message
			pDialog.setMessage("Buffering...");
			pDialog.setIndeterminate(false);
			// Show progressbar
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {

			try {
				// Start the MediaController
				MediaController mediacontroller = new MediaController(
						Video.this);
				mediacontroller.setAnchorView(videoview);
				// Get the URL from String VideoURL
				String  VideoURL= getIntent().getStringExtra("url");
				Uri video = Uri.parse(VideoURL);
				videoview.setMediaController(mediacontroller);
				videoview.setVideoURI(video);

				videoview.requestFocus();
				pDialog.dismiss();
				videoview.start();
				/*videoview.setOnPreparedListener(new OnPreparedListener() {
					// Close the progress bar and play the video
					public void onPrepared(MediaPlayer mp) {
						pDialog.dismiss();
						videoview.start();
					}
					
				});*/
				videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() 
		        {           
		            public void onCompletion(MediaPlayer mp) 
		            {
		                // Do whatever u need to do here
		            	Log.v("log_tag","Complete Video");
		            	/*Intent intent = new Intent();
		                startActivity(intent); */
		            	//Toast.makeText(getApplicationContext(), "Video Streaming is Completed!!! =)", Toast.LENGTH_LONG).show();
		            	Video.this.finish();
		            }           
		        });   
			} catch (Exception e) {
				pDialog.dismiss();
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

		}
	}

	// Not using options menu for this tutorial
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/
	
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		  // TODO Auto-generated method stub
		  super.onKeyDown(keyCode, event);
		  return true;
		 }
		 @Override
		 public boolean onKeyUp(int keyCode, KeyEvent event) {
		  // TODO Auto-generated method stub
		  if(keyCode==85){
			  if(videoview.isPlaying()){
				  videoview.pause();
				  videoview.requestFocus();
			  }else{
			  //playVideo();
				  videoview.start();
			  }
		       // appView.loadUrl("javascript:toggleMenu("+keyCode+");"); 
		        }
		  else
			  if(keyCode==23){
				  videoview.stopPlayback();
				  videoview.requestFocus();
			       // appView.loadUrl("javascript:toggleMenu("+keyCode+");"); 
			        }
			  else
				  if(keyCode==19){
					  videoview.seekTo(0);
					  videoview.start();
					  videoview.requestFocus();
				       // appView.loadUrl("javascript:toggleMenu("+keyCode+");"); 
				        }
				  else	
					  if(keyCode==89){
						 
						  if(videoview.getCurrentPosition()-60000>0 && (videoview.isPlaying()))
						  {
							  videoview.seekTo(videoview.getCurrentPosition()-60000);
							  videoview.start();
						  }
					        }
					  else
						  if(keyCode==90){
							  if(videoview.getCurrentPosition()+60000<videoview.getDuration() && (videoview.isPlaying()))
							  {
								  videoview.seekTo(videoview.getCurrentPosition()+60000);
								  videoview.start();
							  }
						        }
						        
						  else
		  super.onKeyDown(keyCode, event);
		  return true;
		 }

}
