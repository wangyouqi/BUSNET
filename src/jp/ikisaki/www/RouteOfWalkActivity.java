package jp.ikisaki.www;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

public class RouteOfWalkActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.route_of_walk);

		double startNorth;
		double goalNorth;
		double startEast;
		double goalEast;

		double startNorthJapan;
		double goalNorthJapan;
		double startEastJapan;
		double goalEastJapan;

		int startNorthInt;
		int goalNorthInt;
		int startEastInt;
		int goalEastInt;

		Intent intent = getIntent();
		int walk[] = intent.getIntArrayExtra("key");

		startNorthInt = walk[0];
		startEastInt = walk[1];
		goalNorthInt = walk[2];
		goalEastInt = walk[3];

		System.out.println("logcat:8:walk:" + startNorthInt + ", " + startEastInt);

		startNorthJapan = (double)startNorthInt / 1000000 / 0.36;
		goalNorthJapan = (double)goalNorthInt / 1000000 / 0.36;
		startEastJapan = (double)startEastInt / 1000000 / 0.36;
		goalEastJapan = (double)goalEastInt / 1000000 / 0.36;

		startNorth = startNorthJapan - startNorthJapan * 0.00010695 + startEastJapan * 0.000017464 + 0.0046017;
		startEast = startEastJapan - startNorthJapan * 0.000046038 - startEastJapan * 0.000083043 + 0.010040;

		goalNorth = goalNorthJapan - goalNorthJapan * 0.00010695 + goalEastJapan * 0.000017464 + 0.0046017;
		goalEast = goalEastJapan - goalNorthJapan * 0.000046038 - goalEastJapan * 0.000083043 + 0.010040;


        intent.setAction(Intent.ACTION_VIEW);

        System.out.println("logcat:8: = " + "http://maps.google.com/maps?saddr=" + startNorth + "," + startEast + "(現在地)" + "&daddr=" + goalNorth + "," + goalEast + "(バス停)" + "&dirflg=w");

        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + startNorthJapan + "," + startEastJapan + "(現在地)" + "&daddr=" + goalNorthJapan + "," + goalEastJapan + "(バス停)" + "&dirflg=w"));

        startActivity(intent);
        finish();

	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if(keyCode==KeyEvent.KEYCODE_BACK){
    	  	System.out.println("logcat:back");
        finish();
        return true;
      }
      return false;
    }
}
