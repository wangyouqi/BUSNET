package jp.ikisaki.www;

import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PassActivity extends Activity{
	int widgetFlag = 0;

	public String numCheck(int num){
		String str = Integer.toString(num);
		if(num < 10){
			return "0" + str;
		}
		return str;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_of_walk);

		Intent intent = getIntent();
	    String action = intent.getAction();
	    String uriStr = "";
	    String passUrl = "";


		if (Intent.ACTION_VIEW.equals(action)){
		    android.net.Uri uri = intent.getData();
		    uriStr = uri.toString();
		    int index = uriStr.indexOf("db_id");
		    uriStr = "http://www.ikisaki.jp/index.cgi?device=xml&page=busstop_list&d" + uriStr.substring(index + 1);
		    passUrl = uriStr;
		    widgetFlag = 1;
		}

		String query = intent.getStringExtra("query");

		ScrollView scrollView = new ScrollView(this);
	    TableLayout tableLayout = new TableLayout(this);
	    tableLayout.setOrientation(TableLayout.VERTICAL);
	    WindowManager wm;
		Display disp;

		wm = (WindowManager)getSystemService(WINDOW_SERVICE);
		disp = wm.getDefaultDisplay();
		int width = disp.getWidth();
		int	height = disp.getHeight();

		TableLayout passTableLayout = new TableLayout(this);
		passTableLayout.setOrientation(TableLayout.VERTICAL);

		TableRow menuTableRow = new TableRow(this);
		LinearLayout menuLayout = new LinearLayout(this);
		menuLayout.setOrientation(LinearLayout.HORIZONTAL);
		menuLayout.setBackgroundColor(Color.GRAY);
		menuLayout.setPadding(2, 2, 2, 2);

		TextView menuTextView = new TextView(this);
		menuTextView.setBackgroundColor(Color.LTGRAY);
		menuTextView.setTextColor(Color.BLACK);
		menuTextView.setWidth(width - (width / 4));
		menuTextView.setHeight(height / 12);
		menuTextView.setGravity(Gravity.CENTER);
		menuTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 24);
		menuTextView.setText("通過バス停");

		Button menuButton = new Button(this);
		menuButton.setBackgroundColor(Color.BLUE);
		menuButton.setHeight(height / 12);
		menuButton.setGravity(Gravity.CENTER);
		menuButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 24);
		menuButton.setText("閉じる");
		menuButton.setWidth(width / 4);
		menuButton.setPadding(2, 2, 2, 2);

		menuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});


		menuLayout.addView(menuButton);
		menuLayout.addView(menuTextView);

		if(widgetFlag == 0){
			menuTableRow.addView(menuLayout);
		}


		passTableLayout.addView(menuTableRow);


		try{
		    XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
		    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		    URL url;

		    if(passUrl == ""){
		    	passUrl = "http://www.ikisaki.jp/index.cgi?device=xml&page=busstop_list&" + query;
		    }

		    url = new URL(passUrl);

		    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		    connection.setRequestMethod("GET");

		    xmlPullParser.setInput(connection.getInputStream(), "UTF-8");


		    int eventType;
		    while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
		    	if(eventType == XmlPullParser.START_TAG && "Busstop".equals(xmlPullParser.getName())){
		    		int subEventType;
		    		String hour = "";
		    		String minute = "";
		    		String time = "";
		    		String name = "";
		    		String str = "";
		    		TableRow tableRow = new TableRow(this);
		    		tableRow.setPadding(2, 2, 2, 2);
		    		tableRow.setBackgroundColor(Color.BLACK);
		    		LinearLayout layout = new LinearLayout(this);
		    		layout.setOrientation(LinearLayout.HORIZONTAL);
	    			//layout.setGravity(Gravity.CENTER_VERTICAL);

		    		subEventType = xmlPullParser.next();
		    		while(!(subEventType == XmlPullParser.END_TAG && "Busstop".equals(xmlPullParser.getName()))){

		    			if(eventType == XmlPullParser.START_TAG && "Name".equals(xmlPullParser.getName())){
		    				str = xmlPullParser.nextText();
		    				name = str;
		    			}
		    			else if(eventType == XmlPullParser.START_TAG && "Hour".equals(xmlPullParser.getName())){
		    				str = xmlPullParser.nextText();
		    				hour = str;
		    			}
		    			else if(eventType == XmlPullParser.START_TAG && "Minute".equals(xmlPullParser.getName())){
		    				str = xmlPullParser.nextText();
		    				minute = str;
		    			}
		    			subEventType = xmlPullParser.next();
		    		}
		    		TextView nameTextView = new TextView(this);
	    			nameTextView.setBackgroundColor(Color.WHITE);
	    			nameTextView.setTextColor(Color.BLACK);
	    			nameTextView.setWidth(width - (width / 4));
	    			nameTextView.setHeight(height / 10);
	    			nameTextView.setGravity(Gravity.CENTER);
	    			nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 24);

	    			TextView timeTextView = new TextView(this);


	    			timeTextView.setBackgroundColor(Color.WHITE);
	    			timeTextView.setTextColor(Color.BLACK);
	    			timeTextView.setWidth(width / 4);
	    			timeTextView.setHeight(height / 10);
	    			timeTextView.setGravity(Gravity.CENTER);
	    			timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, height / 24);

		    		time = numCheck(Integer.valueOf(hour)) + ":" + numCheck(Integer.valueOf(minute));

		    		timeTextView.setText(time);
		    		nameTextView.setText(name);

		    		layout.setGravity(Gravity.CENTER);
		    		layout.addView(timeTextView);
		    		layout.addView(nameTextView);

		    		tableRow.addView(layout);
		    		tableLayout.addView(tableRow);
		    	}
		    }
		    scrollView.addView(tableLayout);
		    passTableLayout.addView(scrollView);
		    setContentView(passTableLayout);
		} catch (Exception e){
		    System.out.println("logcat:(errore)→" + e);
		}

	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if(keyCode==KeyEvent.KEYCODE_BACK && widgetFlag == 1){
    	  	System.out.println("logcat:back");
//    	  	finish();
//    	  	finish();
//    	  	finish();
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
}
