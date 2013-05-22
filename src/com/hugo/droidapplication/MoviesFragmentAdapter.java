package com.hugo.droidapplication;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class MoviesFragmentAdapter extends FragmentStatePagerAdapter {

	// no of total movie objects we have
	private int total;
	public static int obj4page = 10;
	public static ArrayList<MovieObj> movieArr;

	public MoviesFragmentAdapter(FragmentManager fm, String result) {
		super(fm);
		// movieArr = MovieEngine.parseMovieDetails(result);
		this.total = movieArr.size();
	}

	@Override
	public Fragment getItem(int position) {
		ArrayList<MovieObj> fraglist = new ArrayList<MovieObj>();
		for (int i = (position * obj4page); ((i < (position + 1) * obj4page) && i < movieArr
				.size()); i++) {
			fraglist.add(movieArr.get(i));
		}
		return MoviesFragment.newInstance(position, fraglist);
	}

	@Override
	public int getCount() {
		// no.of fragments needed
		return (total % obj4page) == 0 ? (total / obj4page)
				: (total / obj4page) + 1;
	}
}