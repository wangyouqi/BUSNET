package jp.ikisaki.www;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.ActionBar;
import android.view.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;

@SuppressWarnings("deprecation")
public class ResultOfRouteSearchActivity extends TabActivity {

	int toastFlag = 0;
	int widgetFlag = 0;
	private int routeNum = 1;
	private int layoutCounter = 0;
	private int previousLayoutCounter = 0;
	private WindowManager wm;
	private Display disp;
	private int width;
	private int height;
	private String special = "";
	private String specialtime = "";
	private String shareUrl = "";
	private int shareHour = 0;
    private int shareMinute = 0;
    private String data = "";

    private String departureString = "";
    private String destinationString = "";

	String dateString = "";

	String timeString = "";
	String arriveLandmark = "";
	String totalInfomation = "";

	ArrayList<TableRow> tableRowArrayList = new ArrayList<TableRow>();

	public String numCheck(int num){
		String str = Integer.toString(num);
		if(num < 10){
			return "0" + str;
		}
		return str;
	}


	public TableRow setDate(String str, int color){
		TableRow tableRow = new TableRow(this);
		tableRow.setOrientation(TableRow.HORIZONTAL);
		tableRow.setPadding(10, 5, 10, 5);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);
		TextView textView = new TextView(this);
		textView.setTextColor(color);
		textView.setBackgroundColor(Color.TRANSPARENT);
		textView.setText(str);

		if(color == Color.RED){
			textView.setTextSize(25);
		}
		else{
			textView.setTextSize(30);
		}

		layout.addView(textView);

		tableRow.addView(layout);
		layoutCounter++;
		return tableRow;
	}

	public TableRow setShare(String str, int color, final int shareH, final int shareM){
		TableRow tableRow = new TableRow(this);
		tableRow.setOrientation(TableRow.HORIZONTAL);
		tableRow.setPadding(10, 5, 10, 5);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);


		//TextView textView = new TextView(this);
        Button closeButton = new Button(this);
        closeButton.setText(getResources().getString(R.string.close));
        closeButton.setTextSize(25);
        closeButton.setBottom(10);
        closeButton.setBackgroundColor(Color.BLUE);
        closeButton.setTextColor(Color.WHITE);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

		Button shareButton = new Button(this);
		shareButton.setText(getResources().getString(R.string.share_route));
		shareButton.setTextSize(25);
		shareButton.setBottom(10);
		shareButton.setBackgroundColor(Color.GREEN);
		shareButton.setTextColor(Color.WHITE);

		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int index = shareUrl.indexOf("route_search");
			    System.out.println("logcat:q = " + shareUrl.substring(index));

			    int hour = shareH;
			    int minute = shareM;

			    if(minute < 56){
					int time = minute % 5;
					if(time != 0){
						minute += (5 - time);
					}
				}
				else{
					minute = 0;
					hour += 1;
				}

			    String encodeStr = "";
				try {
					encodeStr = URLEncoder.encode("経路探索", "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

			    shareUrl = "http://www.ikisaki.jp/search/search.cgi?target=" + "経路探索" + "&page=" + shareUrl.substring(index);

			    shareUrl.replaceAll("&search=1",
				"");

				System.out.println("logcat:share1: " + shareUrl);

				BasicModel.setRouteShareUrl(shareUrl);

				BasicModel.setShareHour(hour);
				BasicModel.setShareMinute(minute);

				System.out.println("logcat:sharehour:" + ":" + hour);
				System.out.println("logcat:sharemin:" + ":" + minute);

				Intent intent = new Intent(ResultOfRouteSearchActivity.this, ShareRouteActivity.class);
				intent.putExtra("url", shareUrl);
				startActivity(intent);

			}
		});

		layout.addView(shareButton);
        layout.addView(closeButton);
		tableRow.addView(layout);
		layoutCounter++;
		return tableRow;
	}

	public LinearLayout setlayout(LinearLayout layout, String left, String right, int color){
		return setlayout(layout, left, right, color, "", "");
	}

	public LinearLayout setlayout(LinearLayout layout, String left, String right, int color, String minute, String yen){
		//layout.setPadding(3, 3, 3, 0);
		layout.setBackgroundColor(Color.BLACK);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER_VERTICAL);
		TextView leftTextView = new TextView(this);
		leftTextView.setTextColor(Color.BLACK);

		if(color == Color.RED || color == Color.BLUE){
			leftTextView.setBackgroundColor(Color.WHITE);
		}
		else{
			leftTextView.setBackgroundColor(Color.LTGRAY);
		}

		leftTextView.setText(left);

		if(color == Color.RED){
			leftTextView.setHeight(height / 8);
			leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 24);
		}
		else{
			leftTextView.setHeight(height / 10);
			leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 24);
		}

		leftTextView.setGravity(Gravity.CENTER);
		leftTextView.setWidth((int)(width / 3.5));
		layout.addView(leftTextView);

		TextView bandTextView = new TextView(this);
		bandTextView.setText(".");



		if(color == Color.RED){
			bandTextView.setHeight(height / 7);
		}
		else{
			bandTextView.setHeight(height / 10);
		}

		bandTextView.setWidth(width / 75);
		bandTextView.setGravity(Gravity.CENTER_VERTICAL);
		layout.addView(bandTextView);


		TextView rightTextView = new TextView(this);
		rightTextView.setTextColor(Color.BLACK);
        rightTextView.setTextSize(145);

		if(color == Color.RED || color == Color.BLUE){
			rightTextView.setBackgroundColor(Color.WHITE);
			if(color == Color.BLUE){
				rightTextView.setText(right);
			}
			else if(color == Color.RED){

				if(right.indexOf("線") != -1){
					int newLine = right.indexOf("線");

                    //140527 mod by wyq
					//rightTextView.setText(right.substring(0, newLine + 1) + "\n" + right.substring(newLine + 2) + "\t" + "(" + minute + "分)");
					rightTextView.setText(right.substring(0, newLine + 1) + "\n" + right.substring(newLine + 2) + "\t" + "(" + minute + "分" + "," + (yen.equals("") ? 0 : yen) + "円)"); //料金表示が完成したらこのコメントをはずす
				}
				else{
					rightTextView.setText(right + "\n" + "(" + minute + "分)");
					//rightTextView.setText(right + "\n" + "(" + minute + "分" + "," + yen + "円)"); //料金表示が完成したらこのコメントをはずす
				}
			}

		}
		else{
			rightTextView.setBackgroundColor(Color.LTGRAY);
			//ここにif文入れる

			if(right.indexOf("(") != -1){
				int newLine = right.indexOf("(");

				rightTextView.setText(right.substring(0, newLine) + "\n" + right.substring(newLine));
			}
			else{
				rightTextView.setText(right);
			}
		}



		if(color == Color.YELLOW || color == Color.GREEN){
			System.out.println("logcat:yellow");
			rightTextView.setWidth(width - (int)(width / 3.5) - width / 40);
		}
		else{
			rightTextView.setWidth(width / 2);
		}

		if(color == Color.RED){
			rightTextView.setHeight(height / 7);
			rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 40);
		}
		else{
			rightTextView.setHeight(height / 10);
			rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 25);
		}

		rightTextView.setGravity(Gravity.CENTER);
		if(color == Color.YELLOW || color == Color.GREEN || color == Color.BLACK){
			color = Color.LTGRAY;
		}

		bandTextView.setTextColor(color);
		bandTextView.setBackgroundColor(color);
        rightTextView.setTextSize(25);
		layout.addView(rightTextView);

//		Button btn = new Button(this);
//		btn.setText("表示");
//		btn.setGravity(Gravity.CENTER);
//		btn.setTextColor(color);
//		btn.setBackgroundColor(Color.GRAY);
//		btn.setHeight(height / 10);
//		btn.setWidth(width / 75);
//		btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 31);
//
//		layout.addView(btn);

		return layout;
	}

	public TableRow setTableRow(String left, String right, int color){
		LinearLayout layout = new LinearLayout(this);

		TableRow tableRow = new TableRow(this);
		tableRow.setOrientation(TableRow.HORIZONTAL);
		tableRow.setPadding(10, 0, 10, 0);
		tableRow.addView(setlayout(layout, left, right, color));

		Button btn = new Button(this);
		btn.setText("表示");
		btn.setGravity(Gravity.CENTER);
		btn.setTextColor(Color.YELLOW);
		btn.setBackgroundColor(Color.LTGRAY);
		btn.setHeight(height / 10);
		btn.setWidth(width / 100);
		btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 31);
		//layout.addView(btn);

		layoutCounter++;
		return tableRow;
	}



	public TableRow setTableRow(String left, String right, int color, WalkModel walkModel){
		LinearLayout layout = new LinearLayout(this);

		final int walk[] = {walkModel.getStartNorthInt(), walkModel.getStartEastInt(), walkModel.getGoalNorthInt(), walkModel.getGoalEastInt()};

		Button btn = new Button(this);
		btn.setText("経路");
		btn.setGravity(Gravity.CENTER);
		btn.setTextColor(color);
		btn.setBackgroundColor(Color.GRAY);
        btn.setBackground(getResources().getDrawable(R.drawable.btn_background));
		btn.setHeight(height / 11);
		btn.setWidth(width / 6);
		btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 33);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ResultOfRouteSearchActivity.this, RouteOfWalkActivity.class);
				intent.putExtra("key", walk);
				startActivity(intent);
			}
		});

		TableRow tableRow = new TableRow(this);
		tableRow.setOrientation(TableRow.HORIZONTAL);
		tableRow.setPadding(10, 0, 10, 0);
		layout = setlayout(layout, left, right, color);
		layout.setBackgroundColor(Color.WHITE);
		layout.addView(btn);
		tableRow.addView(layout);

		layoutCounter++;
		return tableRow;
	}

	public TableRow setTableRow(String left, String right, int color, PlatformModel platformModel){
		LinearLayout layout = new LinearLayout(this);

		final int platform[] = {platformModel.getStation(), platformModel.getNum()};

		Button btn = new Button(this);
		btn.setText("乗場");
		btn.setGravity(Gravity.CENTER);
		btn.setTextColor(color);
		btn.setBackgroundColor(Color.GRAY);
        btn.setBackground(getResources().getDrawable(R.drawable.btn_background));
		btn.setHeight(height / 11);
		btn.setWidth(width / 6);
		btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 33);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ResultOfRouteSearchActivity.this, PlatformActivity.class);
				intent.putExtra("key", platform);
				startActivity(intent);
			}
		});

		color = Color.BLACK;
		TableRow tableRow = new TableRow(this);
		tableRow.setOrientation(TableRow.HORIZONTAL);
		tableRow.setPadding(10, 0, 10, 0);
		layout = setlayout(layout, left, right, color);
		layout.setBackgroundColor(Color.LTGRAY);
		layout.addView(btn);
		tableRow.addView(layout);

		layoutCounter++;
		return tableRow;
	}

	public TableRow setTableRow(String left, String right, int color, final String query, final String minute, final String yen){
		LinearLayout layout = new LinearLayout(this);

		Button btn = new Button(this);
		btn.setText("詳細");
		btn.setGravity(Gravity.CENTER);
		btn.setTextColor(color);
		btn.setBackgroundColor(Color.GRAY);
        btn.setBackground(getResources().getDrawable(R.drawable.btn_background));
		btn.setHeight((int)(height / 8.5) + height / 55);
		btn.setWidth(width / 6);
		btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 31);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ResultOfRouteSearchActivity.this, PassActivity.class);
				intent.putExtra("query", query);
				startActivity(intent);
			}
		});

		TableRow tableRow = new TableRow(this);
		tableRow.setOrientation(TableRow.HORIZONTAL);
		tableRow.setPadding(10, 0, 10, 0);
		layout = setlayout(layout, left, right, color, minute, yen);
		layout.setBackgroundColor(Color.WHITE);
		layout.addView(btn);
		tableRow.addView(layout);

		layoutCounter++;
		return tableRow;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
	    String action = intent.getAction();
	    String uriStr = "";
	    String uriStrCpy = "";
	    String label = "";
	    data += intent.getStringExtra("keyword");

	    if (Intent.ACTION_VIEW.equals(action)){
	      android.net.Uri uri = intent.getData();
	      System.out.println("logcat:w" + uri.toString());
	      uriStr = uri.toString();

	      toastFlag = 1;

	      if(uriStr.indexOf("pre_S_id") != -1){

	    	  Intent routeSearchIntent = new Intent(ResultOfRouteSearchActivity.this, RouteSearchActivity.class);
	    	  routeSearchIntent.putExtra("keyword", uriStr);
	    	  System.out.println("logcat:2:back1");
	    	  startActivity(routeSearchIntent);
	    	  System.out.println("logcat:2:back2");
	    	  finish();
	      }

	      int index = uriStr.indexOf("route_search");
	      System.out.println("logcat:q = " + uriStr.substring(index + 1));
	      //uriStr = "http://www.ikisaki.jp/index.cgi?device=xml&page=r" + uriStr.substring(index + 1);
	      uriStr = "http://www.ikisaki.jp/index.cgi?device=xml&page=r" + uriStr.substring(index + 1);
	      uriStrCpy = uriStr;

	      if(uriStr.indexOf("&cluster_label") != -1){
	    	  System.out.println("logcat:z = " + uriStr);
	    	  index = uriStrCpy.indexOf("&cluster_label");
	    	  System.out.println("logcat:y = " + uriStrCpy.substring(index + 15));
	    	  label = URLDecoder.decode(uriStrCpy.substring(index + 15));
	    	  System.out.println("logcat:label = " + label);
	    	  widgetFlag = 1;
	      }
	      System.out.println("logcat:2:back3");

	    }

	    wm = (WindowManager)getSystemService(WINDOW_SERVICE);
		disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();

		ArrayList<TableLayout> tableLayoutArrayList = new ArrayList<TableLayout>();

		ArrayList<ScrollView> scrollViewArrayList = new ArrayList<ScrollView>();

		TabHost tabHost = getTabHost();

		tabHost.getTabContentView();

		System.out.println("logcat:tab");

		LayoutInflater.from(this).inflate(R.layout.result_of_route_search,
				tabHost.getTabContentView(), true);

		try{
		    XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
		    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		    URL url;
		    //String urlString = "http://www.ikisaki.jp/index.cgi?device=xml&page=route_search&s_id=716&d_id=1092&date=1,30&hour=11&min=50&bus=nikkou&bus=hinomaru&after=10&dir=forward&perpage=7&timetype=just";

		    PointModel departurePointModel = new PointModel();
		    departurePointModel = BasicModel.getDeparture();
		    PointModel destinationPointModel = new PointModel();
	        destinationPointModel = BasicModel.getDestination();

	        String arriveTomorrow = "";
	        String noTimeTransfer = "";
	        String getOnExpress = "";

	        String departure = (departurePointModel.getId()>0?departurePointModel.getId():0) + "";
	        String destination = (departurePointModel.getId()>0?departurePointModel.getId():0) + "";

	       // http://busdev.ike.tottori-u.ac.jp

	        if(departurePointModel.getId() <= 0){
	        	departure = "GMAPD:" + departurePointModel.getLatitude() + ":" + departurePointModel.getLongitude();
	        	departureString = departurePointModel.getName();
	        }

	        if(destinationPointModel.getId() <= 0){
	        	destination = "GMAPD:" + destinationPointModel.getLatitude() + ":" + destinationPointModel.getLongitude();
	        	destinationString = destinationPointModel.getName();
	        }

		    String urlString =
		    	"http://www.ikisaki.jp/index.cgi?device=xml&page=route_search&s_id=" + departure +
		    	"&d_id=" + destination  + "&perpage=7" + BasicModel.getTime() + BasicModel.getForwardOrBackward() + BasicModel.getLine() + BasicModel.getDate();

		    Time times = new Time("Asia/Tokyo");
		    times.setToNow();
		    if(BasicModel.getDate() == ""){
				urlString += "&date=" + (times.month + 1) + "," + times.monthDay;
			}
			if(BasicModel.getTime() == ""){
				urlString += "&hour=" + times.hour + "&min=" + times.minute;
			}

		    System.out.println("logcat:" + urlString);

		    String uuid = "&android_id=";
		    final Context context = this;
		    BufferedReader in = null;
	        try{
	        	FileInputStream file = context.openFileInput("sample.txt");
	        	in = new BufferedReader(new InputStreamReader(file));
	        	//System.out.println("logcat:10:" + in.readLine());
	        	uuid += in.readLine();
	        	in.close();
	        }catch(IOException e){
	        	e.printStackTrace();
	        }

		    if(uriStr == ""){
		    	if(BasicModel.getParameter() == 1){
		    		urlString += "&search=1";
		    	}
		    	url = new URL(urlString + uuid);
		    	shareUrl = urlString;
		    	System.out.println("logcat:keiro1:url=" + urlString);
		    }
		    else {
		    	if(BasicModel.getParameter() == 1){
		    		uriStr += "&search=1";
		    	}
		    	url = new URL(uriStr + uuid);
		    	shareUrl = uriStr;
		    	System.out.println("logcat:keiro2:url=" + uriStr);
		    }


		    System.out.println("logcat:10:url= " +url);


		    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		    connection.setRequestProperty("User-Agent", "Android/Busnet");
		    connection.setRequestMethod("GET");

		    xmlPullParser.setInput(connection.getInputStream(), "UTF-8");
//exp by wyq route result start
		    int eventType;
		    while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {

		    	if (eventType == XmlPullParser.START_TAG && "PathInfo".equals(xmlPullParser.getName())) {
		    		xmlPullParser.next();

		    		ScrollView scrollview = new ScrollView(this);
		    		scrollViewArrayList.add(scrollview);

		    		TableLayout tableLayout = new TableLayout(this);
		    		tableLayout.setOrientation(TableLayout.VERTICAL);
		    		tableLayoutArrayList.add(tableLayout);

		        }
		    	else if (eventType == XmlPullParser.START_TAG && "Year".equals(xmlPullParser.getName())) {
		    		String str = xmlPullParser.nextText();

		            dateString += str + "年";
		            }
		    	else if (eventType == XmlPullParser.START_TAG && "Month".equals(xmlPullParser.getName())) {
		    		String str = xmlPullParser.nextText();

		            dateString += str + "月";
		        }
		    	else if (eventType == XmlPullParser.START_TAG && "Day".equals(xmlPullParser.getName())) {
		    		String str = xmlPullParser.nextText();

		            dateString += str + "日";
		        }

		    	else if(eventType == XmlPullParser.START_TAG && "Start".equals(xmlPullParser.getName())){
		    		int subEventType;
		    		String time = "";
		    		String place = "";
		    		String str = "";
		    		subEventType = xmlPullParser.next();
		    		while(!(subEventType == XmlPullParser.END_TAG && "Start".equals(xmlPullParser.getName()))){
		    			if(eventType == XmlPullParser.START_TAG && "Hour".equals(xmlPullParser.getName())){
		    				str = xmlPullParser.nextText();

				            timeString = numCheck(Integer.valueOf(str)) + ":";
		    			}
		    			else if(eventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
		    				str = xmlPullParser.nextText();

				            timeString += numCheck(Integer.valueOf(str)) + "発";

				            time = timeString;
		    			}
		    			else if(eventType == XmlPullParser.START_TAG && "LandmarkName".equals(xmlPullParser.getName())){
		    				str = xmlPullParser.nextText();

		    				if(label == ""){
		    					place = str;
		    				}
		    				else {
		    					place = "現在地";
		    				}

		    				if(place.equals(getResources().getString(R.string.get_your_destination)) ){
		    					place = getResources().getString(R.string.get_your_departure);
		    				}

		    				if(!departureString.equals("")){
		    					place = departureString;
		    				}

		    			}
		    			else if(eventType == XmlPullParser.START_TAG && "LandmarkId".equals(xmlPullParser.getName())){

		    			}
		    			subEventType = xmlPullParser.next();
		    		}
		    		tableRowArrayList.add(setDate(dateString, Color.BLACK));

		    		System.out.println("logt:" + place);

		    		if(place.indexOf("JR") != -1 || place.indexOf("バス停") != -1 || place.indexOf("智頭急行") != -1 || place.indexOf("若桜鉄道") != -1){
		    			special = place;
		    			specialtime = time;
		    		}
		    		else{
		    			tableRowArrayList.add(setTableRow(time, place, Color.YELLOW));

		    		}

		    	}

		    	else if (eventType == XmlPullParser.START_TAG && "Walk".equals(xmlPullParser.getName())) {

		            int subEventType;
		            String time = "";
		            String str = "";
		            subEventType = xmlPullParser.next();

		            int startNorthInt = 0;
	        		int startEastInt = 0;
	        		int goalNorthInt = 0;
	        		int goalEastInt = 0;

		            while(!(subEventType == XmlPullParser.END_TAG && "Walk".equals(xmlPullParser.getName()))){

		            	if(subEventType == XmlPullParser.START_TAG && "Source".equals(xmlPullParser.getName())){
		            		while(!(subEventType == XmlPullParser.END_TAG && "Source".equals(xmlPullParser.getName()))){
		            			if(subEventType == XmlPullParser.START_TAG && "LandmarkName".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();
		            			}
		            			else if(subEventType == XmlPullParser.START_TAG && "LandmarkId".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();
		            			}
		            			else if(subEventType == XmlPullParser.START_TAG && "Latitude".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				startNorthInt = Integer.valueOf(str);
		            			}
		            			else if(subEventType == XmlPullParser.START_TAG && "Longitude".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				startEastInt = Integer.valueOf(str);
		            			}
		            			subEventType = xmlPullParser.next();
		            		}
		            	}
		            	else if(subEventType == XmlPullParser.START_TAG && "Destination".equals(xmlPullParser.getName())){
		            		while(!(subEventType == XmlPullParser.END_TAG && "Destination".equals(xmlPullParser.getName()))){
		            			if(subEventType == XmlPullParser.START_TAG && "LandmarkName".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            			}
		            			else if(subEventType == XmlPullParser.START_TAG && "LandmarkId".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            			}
		            			else if(subEventType == XmlPullParser.START_TAG && "Latitude".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				goalNorthInt = Integer.valueOf(str);
		            			}
		            			else if(subEventType == XmlPullParser.START_TAG && "Longitude".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				goalEastInt = Integer.valueOf(str);
		            			}
		            			subEventType = xmlPullParser.next();
		            		}
		            	}
		            	else if(subEventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
		            		str = xmlPullParser.nextText();

		            		time = str;
		            	}
		            	subEventType = xmlPullParser.next();
		            }
		            WalkModel walkModel = new WalkModel();

	            	walkModel.setStartNorthInt(startNorthInt);
	            	walkModel.setStartEastInt(startEastInt);
	            	walkModel.setGoalNorthInt(goalNorthInt);
	            	walkModel.setGoalEastInt(goalEastInt);

	            	if(special != ""){
		    			tableRowArrayList.add(setTableRow(specialtime, special, Color.YELLOW));
	            	}

		            tableRowArrayList.add(setTableRow("徒歩", time + "分", Color.BLUE, walkModel));
		        }
		    	else if(eventType == XmlPullParser.END_TAG && "Walk".equals(xmlPullParser.getName())){

		    	}





		    	else if (eventType == XmlPullParser.START_TAG && "Arrive".equals(xmlPullParser.getName())) {

		            int subEventType;
		            String str = "";
		            String timeString = "";
		            String departureTime = "";
		            String arriveTime = "";
		            String landmark = "";
		            PlatformModel platform = new PlatformModel();

		            subEventType = xmlPullParser.next();

		            while(!((subEventType == XmlPullParser.START_TAG && "Departure".equals(xmlPullParser.getName())) ||
		            		(subEventType == XmlPullParser.START_TAG && "Walk".equals(xmlPullParser.getName())))){

		            	if(subEventType == XmlPullParser.START_TAG && "LandmarkName".equals(xmlPullParser.getName())){
		            		str = xmlPullParser.nextText();
		            		landmark = str;

		            		//tableRowArrayList.add(setTableRow(arriveTime, landmark, Color.YELLOW));
		            	}

		            	if(subEventType == XmlPullParser.START_TAG && "Time".equals(xmlPullParser.getName())){
		            		while(!(subEventType == XmlPullParser.END_TAG && "Time".equals(xmlPullParser.getName()))){
		            			if(subEventType == XmlPullParser.START_TAG && "Hour".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				timeString = numCheck(Integer.valueOf(str)) + ":";
		            			}
		            			else if(subEventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
		            					str = xmlPullParser.nextText();
		            					timeString += numCheck(Integer.valueOf(str));
		            					arriveTime = timeString;

		            			}
		            			subEventType = xmlPullParser.next();
		            		}
		            		//256

		            	}

		            	subEventType = xmlPullParser.next();
		            }

		            if(subEventType == XmlPullParser.START_TAG && "Departure".equals(xmlPullParser.getName())){
		            	String companyName = "";
		            	String rosenName = "";
		            	String destinationName = "";
		            	String query = "";
		            	String minute = "";
		            	String yen = "";

		            	while(!(subEventType == XmlPullParser.END_TAG && "Departure".equals(xmlPullParser.getName()))){
		            		if(subEventType == XmlPullParser.START_TAG && "Time".equals(xmlPullParser.getName())){
		            			while(!(subEventType == XmlPullParser.END_TAG && "Time".equals(xmlPullParser.getName()))){
			            			if(subEventType == XmlPullParser.START_TAG && "Hour".equals(xmlPullParser.getName())){
			            				str = xmlPullParser.nextText();

			            				timeString = numCheck(Integer.valueOf(str)) + ":";
			            			}
			            			else if(subEventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
			            					str = xmlPullParser.nextText();
			            					timeString += numCheck(Integer.valueOf(str));

			            					departureTime = timeString;


			            			}
			            			subEventType = xmlPullParser.next();
			            		}
		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "Company".equals(xmlPullParser.getName())){
		            			while(!(subEventType == XmlPullParser.END_TAG && "Company".equals(xmlPullParser.getName()))){
			            			if(subEventType == XmlPullParser.START_TAG && "Name".equals(xmlPullParser.getName())){
			            				str = xmlPullParser.nextText();

			            				companyName = str;

			            			}
			            			else if(subEventType == XmlPullParser.START_TAG && "Tel".equals(xmlPullParser.getName())){
			            				str = xmlPullParser.nextText();

			            			}
			            			subEventType = xmlPullParser.next();
			            		}

		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "RouteInfo".equals(xmlPullParser.getName())){
		            			while(!(subEventType == XmlPullParser.END_TAG && "RouteInfo".equals(xmlPullParser.getName()))){
			            			if(subEventType == XmlPullParser.START_TAG && "RosenName".equals(xmlPullParser.getName())){
			            				str = xmlPullParser.nextText();

			            				rosenName = str + "線";
			            			}
			            			else if(subEventType == XmlPullParser.START_TAG && "Destination".equals(xmlPullParser.getName())){
			            				str = xmlPullParser.nextText();

			            				destinationName = str + "行";
			            			}

			            			subEventType = xmlPullParser.next();
			            		}

		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "Platform".equals(xmlPullParser.getName())){

		            			while(!(subEventType == XmlPullParser.END_TAG && "Platform".equals(xmlPullParser.getName()))){
			            			if(subEventType == XmlPullParser.START_TAG && "LandmarkId".equals(xmlPullParser.getName())){
			            				str = xmlPullParser.nextText();
			            				platform.setStation(Integer.valueOf(str));
			            			}
			            			else if(subEventType == XmlPullParser.START_TAG && "Num".equals(xmlPullParser.getName())){
			            				str = xmlPullParser.nextText();
			            				platform.setNum(Integer.valueOf(str));
			            			}
			            			subEventType = xmlPullParser.next();
			            		}
		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "BusstopListLinkQuery".equals(xmlPullParser.getName())){
		            			str = xmlPullParser.nextText();
		            			query = str;
		            			if(platform.getStation() == 0){
        		            		tableRowArrayList.add(setTableRow(arriveTime + "着" + "\n" + departureTime + "発", landmark, Color.GREEN));
        		            	}

		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "BusstopNum".equals(xmlPullParser.getName())){
		            			str = xmlPullParser.nextText();

		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
		            			str = xmlPullParser.nextText();
		            			minute = str;
		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "Fare".equals(xmlPullParser.getName())){
		            			str = xmlPullParser.nextText();
		            			yen = str;
		            		}

		            		subEventType = xmlPullParser.next();
		            	}
		            	if(platform.getStation() != 0){
		            		tableRowArrayList.add(setTableRow(arriveTime + "着" + "\n" + departureTime + "発", landmark, Color.GREEN, platform));
		            		platform.setNum(0);
		            		platform.setStation(0);
		            	}
		            	tableRowArrayList.add(setTableRow("乗車", companyName + " " + rosenName + " " + destinationName, Color.RED, query, minute, yen));
		            }

		            else if(subEventType == XmlPullParser.START_TAG && "Walk".equals(xmlPullParser.getName())){
		            	tableRowArrayList.add(setTableRow(arriveTime + "着" + "\n" + arriveTime + "発", landmark, Color.YELLOW));

		            	String time = "";

		            	int startNorthInt = 0;
		        		int startEastInt = 0;
		        		int goalNorthInt = 0;
		        		int goalEastInt = 0;

		            	while(!(subEventType == XmlPullParser.END_TAG && "Walk".equals(xmlPullParser.getName()))){

		            		if(subEventType == XmlPullParser.START_TAG && "Source".equals(xmlPullParser.getName())){
		            			while(!(subEventType == XmlPullParser.END_TAG && "Source".equals(xmlPullParser.getName()))){

		            				if(subEventType == XmlPullParser.START_TAG && "LandmarkName".equals(xmlPullParser.getName())){

		            				}
		            				else if(subEventType == XmlPullParser.START_TAG && "LandmarkId".equals(xmlPullParser.getName())){

		            				}
		            				else if(subEventType == XmlPullParser.START_TAG && "Latitude".equals(xmlPullParser.getName())){
		            					str = xmlPullParser.nextText();

		            					startNorthInt = Integer.valueOf(str);
		            				}
		            				else if(subEventType == XmlPullParser.START_TAG && "Longitude".equals(xmlPullParser.getName())){
		            					str = xmlPullParser.nextText();

		            					startEastInt = Integer.valueOf(str);
		            				}

		            				subEventType = xmlPullParser.next();
		            			}
		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "Destination".equals(xmlPullParser.getName())){
		            			while(!(subEventType == XmlPullParser.END_TAG && "Destination".equals(xmlPullParser.getName()))){

		            				if(subEventType == XmlPullParser.START_TAG && "LandmarkName".equals(xmlPullParser.getName())){

		            				}
		            				else if(subEventType == XmlPullParser.START_TAG && "LandmarkId".equals(xmlPullParser.getName())){

		            				}
		            				else if(subEventType == XmlPullParser.START_TAG && "Latitude".equals(xmlPullParser.getName())){
		            					str = xmlPullParser.nextText();

		            					goalNorthInt = Integer.valueOf(str);
		            				}
		            				else if(subEventType == XmlPullParser.START_TAG && "Longitude".equals(xmlPullParser.getName())){
		            					str = xmlPullParser.nextText();

		            					goalEastInt = Integer.valueOf(str);
		            				}

		            				subEventType = xmlPullParser.next();
		            			}
		            		}
		            		else if(subEventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
		            			str = xmlPullParser.nextText();
		            			time = str;
		            		}

		            		subEventType = xmlPullParser.next();
		            	}

		            	WalkModel walkModel = new WalkModel();

		            	walkModel.setStartNorthInt(startNorthInt);
		            	walkModel.setStartEastInt(startEastInt);
		            	walkModel.setGoalNorthInt(goalNorthInt);
		            	walkModel.setGoalEastInt(goalEastInt);


		            	tableRowArrayList.add(setTableRow("徒歩", time + "分", Color.BLUE, walkModel));
		            }

		        }


		    	else if(eventType == XmlPullParser.START_TAG && "Departure".equals(xmlPullParser.getName())){
	            	String companyName = "";
	            	String rosenName = "";
	            	String destinationName = "";
	            	String query = "";
	            	String str = "";
	            	String departureTime = "";
	            	String landmark = "";
	            	String arriveTime = "";
	            	String minute = "";
	            	String yen = "";
		            PlatformModel platform = new PlatformModel();

	            	while(!(eventType == XmlPullParser.END_TAG && "Departure".equals(xmlPullParser.getName()))){
	            		if(eventType == XmlPullParser.START_TAG && "Time".equals(xmlPullParser.getName())){
	            			while(!(eventType == XmlPullParser.END_TAG && "Time".equals(xmlPullParser.getName()))){
		            			if(eventType == XmlPullParser.START_TAG && "Hour".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				timeString = numCheck(Integer.valueOf(str)) + ":";
		            			}
		            			else if(eventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
		            					str = xmlPullParser.nextText();
		            					timeString += numCheck(Integer.valueOf(str)) + "発";

		            					departureTime = timeString;


		            			}
		            			eventType = xmlPullParser.next();
		            		}
	            		}
	            		else if(eventType == XmlPullParser.START_TAG && "Company".equals(xmlPullParser.getName())){
	            			while(!(eventType == XmlPullParser.END_TAG && "Company".equals(xmlPullParser.getName()))){
		            			if(eventType == XmlPullParser.START_TAG && "Name".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				companyName = str;

		            			}
		            			else if(eventType == XmlPullParser.START_TAG && "Tel".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            			}
		            			eventType = xmlPullParser.next();
		            		}

	            		}
	            		else if(eventType == XmlPullParser.START_TAG && "RouteInfo".equals(xmlPullParser.getName())){
	            			while(!(eventType == XmlPullParser.END_TAG && "RouteInfo".equals(xmlPullParser.getName()))){
		            			if(eventType == XmlPullParser.START_TAG && "RosenName".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				rosenName = str + "線";
		            			}
		            			else if(eventType == XmlPullParser.START_TAG && "Destination".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();

		            				destinationName = str + "行";
		            			}

		            			eventType = xmlPullParser.next();
		            		}

	            		}
	            		else if(eventType == XmlPullParser.START_TAG && "Platform".equals(xmlPullParser.getName())){

	            			while(!(eventType == XmlPullParser.END_TAG && "Platform".equals(xmlPullParser.getName()))){
		            			if(eventType == XmlPullParser.START_TAG && "LandmarkId".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();
		            				platform.setStation(Integer.valueOf(str));
		            			}
		            			else if(eventType == XmlPullParser.START_TAG && "Num".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();
		            				platform.setNum(Integer.valueOf(str));
		            			}
		            			eventType = xmlPullParser.next();
		            		}
	            		}
	            		else if(eventType == XmlPullParser.START_TAG && "BusstopListLinkQuery".equals(xmlPullParser.getName())){
	            			str = xmlPullParser.nextText();
	            			query = str;
	            			if(platform.getStation() == 0){
	            				if(special != ""){
	            					tableRowArrayList.add(setTableRow(departureTime, special, Color.GREEN));
	            				}
    		            	}

	            		}
	            		else if(eventType == XmlPullParser.START_TAG && "BusstopNum".equals(xmlPullParser.getName())){
	            			str = xmlPullParser.nextText();
	            		}
	            		else if(eventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
	            			str = xmlPullParser.nextText();
	            			minute = str;
	            		}
	            		else if(eventType == XmlPullParser.START_TAG && "Fare".equals(xmlPullParser.getName())){
	            			str = xmlPullParser.nextText();
	            			yen = str;
	            		}

	            		eventType = xmlPullParser.next();
	            	}
	            	if(platform.getStation() != 0){
	            		if(special != ""){
	            			tableRowArrayList.add(setTableRow(departureTime, special, Color.GREEN, platform));
	            			platform.setNum(0);
	            			platform.setStation(0);
	            		}
	            	}
	            	tableRowArrayList.add(setTableRow("乗車", companyName + " " + rosenName + " " + destinationName, Color.RED, query, minute, yen));
	            }


		    	else if (eventType == XmlPullParser.START_TAG && "Goal".equals(xmlPullParser.getName())) {
		    		System.out.println("goal1");
		            int subEventType;
		            String place = "";
		            String str = "";
		            String time = "";
		            subEventType = xmlPullParser.next();
		            while(!(subEventType == XmlPullParser.END_TAG && "Goal".equals(xmlPullParser.getName()))){
		            	if(subEventType == XmlPullParser.START_TAG && "Time".equals(xmlPullParser.getName())){
		            		while(!(subEventType == XmlPullParser.END_TAG && "Time".equals(xmlPullParser.getName()))){
		            			if(subEventType == XmlPullParser.START_TAG && "Hour".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();
		            				shareHour = Integer.valueOf(str);
		            				time = numCheck(Integer.valueOf(str)) + ":";
		            			}
		            			else if(subEventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
		            				str = xmlPullParser.nextText();
		            				shareMinute = Integer.valueOf(str);
		            				time += numCheck(Integer.valueOf(str)) + "着";

		            			}

		            			subEventType = xmlPullParser.next();
		            		}
		            	}
		            	else if(subEventType == XmlPullParser.START_TAG && "LandmarkName".equals(xmlPullParser.getName())){
            				str = xmlPullParser.nextText();

            				if(label == ""){
		    					place = str;
		    				}
		    				else {
		    					place = label;
		    				}
            				if(!destinationString.equals("")){
		    					place = destinationString;
		    				}
            			}

		            	subEventType = xmlPullParser.next();

		            }
		            tableRowArrayList.add(setTableRow(time, place, Color.YELLOW));

		    	}

		        else if (eventType == XmlPullParser.START_TAG && "TotalMinute".equals(xmlPullParser.getName())) {
		        	String str = xmlPullParser.nextText();

		        	totalInfomation = str + "分/";
		        }
		        else if (eventType == XmlPullParser.START_TAG && "TotalTransfer".equals(xmlPullParser.getName())) {
		        	String str = xmlPullParser.nextText();

		        	totalInfomation += "乗換" + str + "回/";
		        	tableRowArrayList.add(setDate(totalInfomation, Color.BLACK));
		        }
		        else if (eventType == XmlPullParser.START_TAG && "TotalWalkMinute".equals(xmlPullParser.getName())) {
		        	String str = xmlPullParser.nextText();

		        	totalInfomation += "徒歩" + str + "分";

		        	String indexString = totalInfomation;//料金の部分が
	    			int index = indexString.indexOf("回/");//完成したら
	    			tableRowArrayList.add(setDate(indexString.substring(index + 2), Color.BLACK));//この三行はコメントアウト

		        }
		        else if (eventType == XmlPullParser.START_TAG && "ArriveTomorrow".equals(xmlPullParser.getName())) {
		        	arriveTomorrow = "目的地に着くのは翌日です";
		        	tableRowArrayList.add(setDate(arriveTomorrow, Color.RED));
		        }
		        else if (eventType == XmlPullParser.START_TAG && "NoTimeTransfer".equals(xmlPullParser.getName())) {
		        	noTimeTransfer = "乗り換えに余裕がありません";
		        	tableRowArrayList.add(setDate(noTimeTransfer, Color.RED));
		        }
		        else if (eventType == XmlPullParser.START_TAG && "GetOnExpress".equals(xmlPullParser.getName())) {
		        	getOnExpress = "特急料金が別途必要です";
		        	tableRowArrayList.add(setDate(getOnExpress, Color.RED));
		        }

		    	else if (eventType == XmlPullParser.START_TAG && "TotalFare".equals(xmlPullParser.getName()) || eventType == XmlPullParser.START_TAG && "Error".equals(xmlPullParser.getName())) {
		    		System.out.println("logcat:10:in");
		        	String str = "";
		    		if(eventType == XmlPullParser.START_TAG && "Error".equals(xmlPullParser.getName())){
		    			ScrollView scrollview = new ScrollView(this);
			    		scrollViewArrayList.add(scrollview);

			    		TableLayout tableLayout = new TableLayout(this);
			    		tableLayout.setOrientation(TableLayout.VERTICAL);
			    		tableLayoutArrayList.add(tableLayout);

//		    			xmlPullParser.next();
//		    			str = xmlPullParser.nextText();
//		    			totalInfomation = str;
		    			System.out.println("logcat:10" + str);

		    			if(str.indexOf("到着しません") != -1){
		    				System.out.println("logcat:2:toutyaku");
		    				data = "1";
		    			}

		    			str += "到着しません\n\n" + "出発地と目的地が" + "\n" + "近すぎませんか？" + "\n\n" + "出発地/目的地は不適切" + "\n" + "ではありませんか？";
		    			totalInfomation = str;
		    			tableRowArrayList.add(setDate(str, Color.BLACK));


		    			RouteSearchActivity.waitDialog.dismiss();

		    			if(toastFlag != 1){
		    				Toast toast = Toast.makeText(ResultOfRouteSearchActivity.this, str, Toast.LENGTH_LONG);
		    				toast.setGravity(Gravity.CENTER, 0, 0);
		    				toast.show();
		    			}

						finish();

//						Intent intent2 = new Intent(ResultOfRouteSearchActivity.this,
//								RouteSearchActivity.class);
//						intent2.putExtra("keyword", "");
//						startActivity(intent);


		    		}
		    		else{
		    			str = xmlPullParser.nextText();

		    			System.out.println("logcat:10:2");

		    			//totalInfomation += "/合計" + str + "円"; 料金が完成したらこのコメントをはずす

		    			String indexString = totalInfomation;
		    			int index = indexString.indexOf("回/");
		    			//tableRowArrayList.add(setDate(indexString.substring(index + 2), Color.BLACK)); 料金が完成したらここはコメントはずす


		    		}
//		            for(int i = previousLayoutCounter; i < layoutCounter; i++){
//		            	tableLayoutArrayList.get(routeNum - 1).addView(tableRowArrayList.get(i));
//		            }
//		            previousLayoutCounter = layoutCounter;
//
//		            scrollViewArrayList.get(routeNum - 1).addView(tableLayoutArrayList.get(routeNum - 1));
//
//		            tabHost.addTab(tabHost.newTabSpec("tab3")
//		            		//.setIndicator("経路" + routeNum)
//		            		.setIndicator(totalInfomation)
//
//		            		.setContent(new MyTabContentFactory(scrollViewArrayList.get(routeNum - 1))));
//		            tabHost.setBackgroundColor(Color.GRAY);
//
//		            routeNum++;

		        }
		    	else if (eventType == XmlPullParser.END_TAG && "PathInfo".equals(xmlPullParser.getName())) {
		    		tableRowArrayList.add(setShare("テスト", Color.BLACK, shareHour, shareMinute));

		    		for(int i = previousLayoutCounter; i < layoutCounter; i++){
		    			tableLayoutArrayList.get(routeNum - 1).addView(tableRowArrayList.get(i));
		    		}
		    		previousLayoutCounter = layoutCounter;



		    		scrollViewArrayList.get(routeNum - 1).addView(tableLayoutArrayList.get(routeNum - 1));

		    		tabHost.addTab(tabHost.newTabSpec("tab3")
		    				//.setIndicator("経路" + routeNum)
		    				.setIndicator(totalInfomation)

		    				.setContent(new MyTabContentFactory(scrollViewArrayList.get(routeNum - 1))));
		    		tabHost.setBackgroundColor(Color.GRAY);

		    		routeNum++;
		    	}
		    }
		} catch (Exception e){
		    System.out.println("logcat:(errore)→" + e);
		}


		// for (int i = 0; i < 5; i++) {
		// tabHost.addTab(tabHost.newTabSpec("tab3")
		// .setIndicator("経路" + (i + 1))
		// .setContent(new MyTabContentFactory(this)));
		// tabHost.setBackgroundColor(Color.GRAY);
		//
		// }

	}

	class MyTabContentFactory extends Activity implements
			TabHost.TabContentFactory {
		private ScrollView layout;

		public MyTabContentFactory(ScrollView layout) {
			this.layout = layout;
		}

		public View createTabContent(String tag) {
		    RouteSearchActivity.waitDialog.dismiss();
			return layout;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if(keyCode==KeyEvent.KEYCODE_BACK && widgetFlag == 1){
    	  	System.out.println("logcat:back");
    	  	moveTaskToBack(true);
    	  	widgetFlag = 0;
    	  	return true;
      }
      else if(keyCode==KeyEvent.KEYCODE_BACK && widgetFlag == 0){
  	  	System.out.println("logcat:back");
  	  	finish();
	  	return true;
      }
      return false;
    }

    //140528 add by wyq add actionbar event
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar, menu);

        ActionBar actionbar = this.getActionBar();
        Common.mActivityList.add(this);
        Common.actionbartool(actionbar);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        return Common.menu(item, this);
    }
    //140528 end
}
