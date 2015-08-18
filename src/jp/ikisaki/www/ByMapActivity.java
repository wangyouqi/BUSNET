package jp.ikisaki.www;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class ByMapActivity extends MapActivity {
	private MapView mview = null;
	private String point = "";

	private int gmapdLatitude = 0;
	private int gmapdLongitude = 0;

	static RegistrationDBAdapter registrationdbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		registrationdbAdapter = new RegistrationDBAdapter(this);

		Intent intent = getIntent();
		if (intent.getStringExtra("keyword").equals("departure")) {
			point = "出発地";
		} else {
			point = "目的地";
		}

		Toast toast = Toast.makeText(ByMapActivity.this, "設定したい" + point
				+ "を中心に合わせて下さい", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		mview = (MapView) findViewById(R.id.mapview);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onGetCenter(View view) {
		GeoPoint gpo = mview.getMapCenter();
		double latitude = gpo.getLatitudeE6() / 1E6;
		double longitude = gpo.getLongitudeE6() / 1E6;
		// Toast.makeText(this, "中心位置\n緯度" + latitude + "\n経度:" + longitude,
		// Toast.LENGTH_LONG).show();

		double la = latitude;
		double ln = longitude;

//	変更	latitude = la + la * 0.00010696 - ln * 0.000017467 - 0.0046020;
//		longitude = ln + la * 0.000046047 + ln * 0.000083049 - 0.010041;

		gmapdLatitude = (int) (latitude * 1E6 * 0.36);
		gmapdLongitude = (int) (longitude * 1E6 * 0.36);

//		if (point.equals("出発地")) {
//			BasicModel.resetDeparture();
//			PointModel departurePointModel = new PointModel(0, getResources().getString(R.string.get_your_departure), "",
//					gmapdLongitude, gmapdLatitude, 0);
//			BasicModel.setDeparture(departurePointModel);
//		} else {
//			BasicModel.resetDestination();
//			PointModel destinationPointModel = new PointModel(0, getResources().getString(R.string.get_your_destination), "",
//					gmapdLongitude, gmapdLatitude, 0);
//			BasicModel.setDestination(destinationPointModel);
//		}

		final EditText editText = new EditText(
				ByMapActivity.this);

		editText.setHint("名前を入力して下さい");
		editText.setBackgroundColor(Color.WHITE);
		editText.setTextColor(Color.BLACK);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);

		new AlertDialog.Builder(
				ByMapActivity.this)

				//.setIcon(R.drawable.icon)

				.setTitle("開催地")

				.setView(editText)


				.setPositiveButton(
						"OK",
						new DialogInterface.OnClickListener() {

							public void onClick(
									DialogInterface dialog,
									int id) {

								if(editText.getText().toString().equals("")){
									Toast toast = Toast.makeText(ByMapActivity.this, "開催地名を入力して下さい", Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
								else{
									SnsModel.setPlaceId("GMAPD:" + gmapdLatitude + ":" + gmapdLongitude);
									System.out.println("logcat:2: " + "&d_id=GMAPD:" + gmapdLatitude + ":" + gmapdLongitude);
									SnsModel.setPlaceName(editText.getText().toString());
									Intent intent = new Intent(ByMapActivity.this, SocialMediaActivity.class);
									startActivity(intent);

								}
							}

						})

				.show();



	}

	public void onCancel(View view) {
		finish();
	}

}
