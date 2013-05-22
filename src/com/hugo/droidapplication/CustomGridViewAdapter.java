package com.hugo.droidapplication;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hugo.imagehandler.SmartImageView;

public class CustomGridViewAdapter extends BaseAdapter {
	private List<MovieObj> movieList;
	private LayoutInflater inflater;
	private Activity context;

	public CustomGridViewAdapter(List<MovieObj> listMovies, Activity context) {
		this.movieList = listMovies;
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {

		return movieList.size();
	}

	@Override
	public Object getItem(int position) {

		return movieList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.gridview_item, null);
		MovieObj movBean = movieList.get(position);
		SmartImageView siv = ((SmartImageView) layout
				.findViewById(R.id.gridview_img));
		siv.setImageUrl(movBean.getImage());
		((ImageView) layout.findViewById(R.id.gridview_rating_img))
				.setImageResource(R.drawable.rating);

		((ImageView) layout.findViewById(R.id.gridview_rating_img))
				.setImageResource(context.getResources().getIdentifier(
						"rate" + movBean.getRating().replace('.', '_'),
						"drawable", "com.hugo.droidapplication"));
		return layout;
	}
}
