package jp.ikisaki.www;

import java.util.List;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;
//import android.support.v4.app.FragmentActivity;
//import com.google.android.maps.*;

//MapActivityを拡張したclassを作成
public class CurrentPositionActivity extends Activity {

	private static final String TAG = "MyMapActivity";
	private static final boolean DEBUG = true;

	private static final String ACTION_LOCATION_UPDATE = "com.android.practice.map.ACTION_LOCATION_UPDATE";
	private GoogleMap mMap;
    //private MapController mMapController;
	//private MyLocationOverlay mMyLocationOverlay;
	private AlertDialog.Builder alertDialog;
	private AlertDialog gpsAlertDialog;

	private int counter = 0;
	private int gmapdLatitude = 0;
	private int gmapdLongitude = 0;
	private String data = "";

	static RegistrationDBAdapter registrationdbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		registrationdbAdapter = new RegistrationDBAdapter(this);

		Intent intent = getIntent();
		data = intent.getStringExtra("keyword");

		PointModel departureModel = new PointModel();

		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(R.string.wait_a_minute);
		alertDialog.setMessage(R.string.while_positioning_gps
				);
		alertDialog.create();
		gpsAlertDialog = alertDialog.show();

		setContentView(R.layout.current_position);
        setUpMapIfNeeded();

		Button decisionButton = (Button)findViewById(R.id.decision_button);
		Button cancelButton = (Button)findViewById(R.id.cancel_button);
		Button registrationButton = (Button)findViewById(R.id.registration_button);

		decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data.equals("departure")){
					BasicModel.resetDeparture();
					PointModel departurePointModel = new PointModel(0, "設定出発地", "", gmapdLongitude, gmapdLatitude, 0);
					BasicModel.setDeparture(departurePointModel);
				}
				else{
					BasicModel.resetDestination();
					PointModel destinationPointModel = new PointModel(0, "設定目的地", "", gmapdLongitude, gmapdLatitude, 0);
					BasicModel.setDestination(destinationPointModel);
				}
				Intent intent = new Intent(CurrentPositionActivity.this,
						RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				startActivity(intent);
			}
		});



		registrationButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				LayoutInflater inflater
		          = LayoutInflater.from(CurrentPositionActivity.this);
		        View view = inflater.inflate(R.layout.dialog, null);
		        final EditText editText
		          = (EditText)view.findViewById(R.id.dialog_editText);

		        new AlertDialog.Builder(CurrentPositionActivity.this)
		        .setTitle("登録名を入力して下さい。")

		        .setView(view)
		        .setPositiveButton(
		          "決定",
		          new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		              System.out.println("logcat:7:" + editText.getText().toString());

		              int noName = 0;

		              if(editText.getText().toString().equals("")){
		            	  noName = 1;
		              }

		              if(data.equals("departure")){
							BasicModel.resetDeparture();
							PointModel departurePointModel = new PointModel(0, editText.getText().toString(), "", gmapdLongitude, gmapdLatitude, 0);
							BasicModel.setDeparture(departurePointModel);
						}
						else{
							BasicModel.resetDestination();
							PointModel destinationPointModel = new PointModel(0, editText.getText().toString(), "", gmapdLongitude, gmapdLatitude, 0);
							BasicModel.setDestination(destinationPointModel);
						}

		              	int flag = 1;


		                registrationdbAdapter.open();
						Cursor c = registrationdbAdapter.getAllNotes();
						if(c.getCount() == 0 && noName == 0){
							registrationdbAdapter.saveNote(editText.getText().toString(), gmapdLongitude + "," + gmapdLatitude);
							flag = 1;
						}
						startManagingCursor(c);
						if (c.moveToFirst()) {
							do {
								Note note = new Note(c.getInt(c
										.getColumnIndex(DBAdapter.COL_ID)), c.getString(c
										.getColumnIndex(DBAdapter.COL_NOTE)), c.getString(c
										.getColumnIndex(DBAdapter.COL_LASTUPDATE)));
								if(note.getNote().equals(editText.getText().toString())){
									//registrationdbAdapter.deleteNote(note.getId());
									flag = 0;
								}
							} while (c.moveToNext());
							if(flag == 1 && noName == 0){
								registrationdbAdapter.saveNote(editText.getText().toString(), gmapdLongitude + "," + gmapdLatitude);
							}
						}
						stopManagingCursor(c);
						registrationdbAdapter.close();


						if(noName == 1){
							Toast toast = Toast.makeText(CurrentPositionActivity.this, "文字が入力されていません。" + "\n" + "名称を入力して再登録して下さい。", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
						else if(flag == 1){
							Toast toast = Toast.makeText(CurrentPositionActivity.this, "「" + editText.getText().toString() + "」" + "\n" + "を登録しました。", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							Intent intent = new Intent(CurrentPositionActivity.this,
									RouteSearchActivity.class);
							intent.putExtra("keyword", "");
							startActivity(intent);
						}
						else{
							Toast toast = Toast.makeText(CurrentPositionActivity.this, "既にその名前は使われています。" + "\n" + "別の名前を入力して再登録して下さい。", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
		            }
		        })
		        .show();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		setIntentFilterToReceiver();
		requestLocationUpdates();
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeUpdates();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                MapsInitializer.initialize(getApplicationContext());
                mMap.setMyLocationEnabled(true);
            }
        }
    }

	private void setIntentFilterToReceiver() {
		final IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOCATION_UPDATE);
		registerReceiver(new LocationUpdateReceiver(), filter);
	}

	private void requestLocationUpdates() {
		final PendingIntent requestLocation = getRequestLocationIntent(this);
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		for (String providerName : lm.getAllProviders()) {
			if (lm.isProviderEnabled(providerName)) {
				lm.requestLocationUpdates(providerName, 0, 0, requestLocation);
				if (DEBUG)
					Log.d(TAG, "Provider: " + providerName);
			}
		}
	}

	private PendingIntent getRequestLocationIntent(Context context) {
		return PendingIntent.getBroadcast(context, 0, new Intent(
				ACTION_LOCATION_UPDATE), PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private void removeUpdates() {
		final PendingIntent requestLocation = getRequestLocationIntent(this);
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		lm.removeUpdates(requestLocation);
		// if(DEBUG)Toast.makeText(this, "Remove update1",
		// Toast.LENGTH_SHORT).show();
	}

	public class LocationUpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int flag = 0;
			if (action == null) {
				return;
			}
			if (action.equals(ACTION_LOCATION_UPDATE)) {
				// Location情報を取得
				Location loc = (Location) intent.getExtras().get(
						LocationManager.KEY_LOCATION_CHANGED);
				// 試しにMapControllerで現在値に移動する
				//mMapController.animateTo(new GeoPoint(
				//		(int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6)));

				double latitude = loc.getLatitude();
				double longitude = loc.getLongitude();

                mMap.moveCamera(CameraUpdateFactory
                        .newLatLng(new LatLng(latitude, longitude)) );
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15.5f));

//		変更		latitude = la + la * 0.00010696 - ln * 0.000017467 - 0.0046020;
//				longitude = ln + la  * 0.000046047 + ln * 0.000083049 - 0.010041;

				System.out.println("logcat:" + counter++ + ":" + (int) (loc.getLatitude() * 1E6) + ":" + (int) (loc
						.getLongitude() * 1E6));
				System.out.println("logcat:" + (int)(latitude * 1E6 * 0.36) + ":" + (int)(longitude * 1E6 * 0.36));

				gmapdLatitude = (int)(latitude * 1E6 * 0.36);
				gmapdLongitude = (int)(longitude * 1E6 * 0.36);

				Button decisionButton = (Button)findViewById(R.id.decision_button);
				decisionButton.setEnabled(true);
				Button registrationButton = (Button)findViewById(R.id.registration_button);
				registrationButton.setEnabled(true);
				gpsAlertDialog.dismiss();

				// if(DEBUG)Toast.makeText(context, "latitude:" +
				// loc.getLatitude() + "\nlongitude:" + loc.getLongitude(),
				// Toast.LENGTH_SHORT).show();
			}
		}

	}
}