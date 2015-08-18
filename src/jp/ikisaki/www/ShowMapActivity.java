package jp.ikisaki.www;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class ShowMapActivity extends MapActivity{

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_map);

		Button homeButton = (Button) findViewById(R.id.home_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});



		TextView mapTextView = (TextView)findViewById(R.id.map_TextView);
		mapTextView.setTextColor(Color.BLACK);
		//mapTextView.setText("OK");

		Intent intent = getIntent();

		String data = intent.getStringExtra("keyword");

		int tempIndex = data.indexOf("$");
		String tempString = data.substring(0, tempIndex);

		mapTextView.setText(tempString);

		String iconString = data.substring(tempIndex + 1);

		int index = iconString.indexOf(",");

		//
		double startNorth;
		double startEast;

		double startNorthJapan;
		double startEastJapan;

		int startNorthInt;
		int startEastInt;

		startEastInt = Integer.valueOf(iconString.substring(0, index));
		startNorthInt = Integer.valueOf(iconString.substring(index + 1));

		System.out.println("logcat:8:start:" + startNorthInt + ":" + startEastInt);

		startNorthJapan = (double)startNorthInt / 1000000 / 0.36;
		startEastJapan = (double)startEastInt / 1000000 / 0.36;

		startNorth = startNorthJapan - startNorthJapan * 0.00010695 + startEastJapan * 0.000017464 + 0.0046017;
		startEast = startEastJapan - startNorthJapan * 0.000046038 - startEastJapan * 0.000083043 + 0.010040;


//		startNorth = startNorthJapan;
//		startEast = startEastJapan;

		System.out.println("logcat:8:finlati, finlon, :" + startNorth + "+" + startEast);

		MapView map = (MapView)findViewById(R.id.map_view);
	        map.setClickable(true);
	        map.setBuiltInZoomControls(true);
	        map.getController().setZoom(17);

	        Drawable pin = getResources().getDrawable( R.drawable.mappin);

	        PinItemizedOverlay pinOverlay = new PinItemizedOverlay( pin);
	        map.getOverlays().add( pinOverlay);

	        GeoPoint point = new GeoPoint((int)(startNorth * 1E6), (int)(startEast * 1E6));
	        pinOverlay.addPoint( point);

	        map.getController().animateTo(point);

	    }

	    @Override
	    protected boolean isRouteDisplayed() {
	        return false;
	    }


}
