package com.hugo.droidapplication;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;

public class MenuData {

	static Map<String, String[]> mMap = new HashMap<String, String[]>();
	static Map<Integer, String> movCateg = new HashMap<Integer, String>();
	static Map<Integer, String> language = new HashMap<Integer, String>();

	static {

		//mMap.put("Main Menu", new String[] { "TV Channels", "Program Guide",
			//	"Movies", "Personal Account", "None" });
		mMap.put("Movies", new String[] {"New Movies","High Rating","Discount Movies","Promotion",
				"Most Watched","Coming Soon","All Movies"});

		movCateg.put(0, "RELEASE");
		movCateg.put(1, "RATING");
		movCateg.put(2, "DISCOUNT");
		movCateg.put(3, "PROMOTION");
		movCateg.put(4, "WATCHED");
		movCateg.put(5, "COMING");
		movCateg.put(6, "ALL");

		language.put(1, "Telugu");
		language.put(2, "English");
		language.put(3, "Hindi");

	}

}
