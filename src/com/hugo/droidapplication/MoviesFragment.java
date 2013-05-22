package com.hugo.droidapplication;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;

public class MoviesFragment extends Fragment {

	private static final String TAG = "MoviesFragment";
	private static int pageIndex;
	private static ArrayList<MovieObj> fraglist;

	public static MoviesFragment newInstance(int i, ArrayList<MovieObj> list) {
		MoviesFragment fragment = new MoviesFragment();
		pageIndex = i;
		fraglist = list;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		GridView gridView = (GridView) inflater.inflate(R.layout.movies_gridvw,
				null);
		gridView.setAdapter(new CustomGridViewAdapter(fraglist, getActivity()));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listview, View imageVw,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						MovieDetailsActivity.class);
				MovieObj movObj = fraglist.get(position);
				intent.putExtra("MediaId", movObj.getId() + "");
				startActivity(intent);
			}
		});
		gridView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				arg1.startAnimation(AnimationUtils.loadAnimation(getActivity(),
						R.anim.zoom_selection));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		return gridView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// needs to get out from null pointer Exception on callling another
		// activity.
		outState.putString("DO NOT CRASH", "OK");
	}
}
