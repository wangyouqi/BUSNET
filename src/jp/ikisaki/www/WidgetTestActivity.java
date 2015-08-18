package jp.ikisaki.www;

import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

public class WidgetTestActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_of_walk);

		try{
			System.out.println("logcat:ok");

		    XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
		    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		    URL url;

		    url = new URL("http://www.ikisaki.jp/index.cgi?device=xml&page=route_search&s_id=783&d_id=1791&date=12,25&bus=nikkou&bus=hinomaru&hour=18&min=15&dir=forward&perpage=7");

		    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		    connection.setRequestMethod("GET");

		    xmlPullParser.setInput(connection.getInputStream(), "UTF-8");

		    int eventType;
		    String str = "";
		    while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
		    	//System.out.println("logcat:while:" + xmlPullParser.getName());
		    	if(eventType == XmlPullParser.START_TAG && "TimetableLinkQuery".equals(xmlPullParser.getName())){
		    		str = xmlPullParser.nextText();
		    		System.out.println("logcat:inname");
		    		System.out.println("http://www.ikisaki.jp/search/rosen.cgi?&" + str);
		    	}

		    	//eventType = xmlPullParser.next();
		    }
		    System.out.println("logcat:end");

		} catch (Exception e){
		    System.out.println("logcat:(errore)→" + e);
		}

	}
}
