package jp.ikisaki.www;

import java.util.List;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.maps.*;

//MapActivityを拡張したclassを作成
public class CurrentPositionActivity extends MapActivity {

	private static final String TAG = "MyMapActivity";
	private static final boolean DEBUG = true;
	private static final String ACTION_LOCATION_UPDATE = "com.android.practice.map.ACTION_LOCATION_UPDATE";

	private MapController mMapController;
	private MapView mMapView;
	private MyLocationOverlay mMyLocationOverlay;
	private AlertDialog.Builder alertDialog;
	private AlertDialog gpsAlertDialog;

	private int counter = 0;
	private double latitude = 0;
	private double longitude = 0;
	private double la = 0.0;
	private double ln = 0.0;
	private int gmapdLatitude = 0;
	private int gmapdLongitude = 0;
	private String data = "";

	static RegistrationDBAdapter registrationdbAdapter;

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
		initMapSet();

		Button decisionButton = (Button)findViewById(R.id.decision_button);
		Button cancelButton = (Button)findViewById(R.id.cancel_button);
		Button registrationButton = (Button)findViewById(R.id.registration_button);

		decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(data.equals("departure")){
					BasicModel.resetDeparture();
					PointModel departurePointModel = new PointModel(0, getResources().getString(R.string.get_your_departure), "", gmapdLongitude, gmapdLatitude, 0);
					BasicModel.setDeparture(departurePointModel);
				}
				else{
					BasicModel.resetDestination();
					PointModel destinationPointModel = new PointModel(0, getResources().getString(R.string.get_your_destination), "", gmapdLongitude, gmapdLatitude, 0);
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
		setOverlays();
		setIntentFilterToReceiver();
		requestLocationUpdates();
	}

	@Override
	protected void onPause() {
		super.onPause();
		resetOverlays();
		removeUpdates();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void initMapSet() {
		// MapView objectの取得
		mMapView = (MapView) findViewById(R.id.map_view);
		// MapView#setBuiltInZoomControl()でZoom controlをbuilt-in moduleに任せる
		mMapView.setBuiltInZoomControls(true);
		// MapController objectを取得
		mMapController = mMapView.getController();
	}

	private void setOverlays() {
		// User location表示用のMyLocationOverlay objectを取得
		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		// 初めてLocation情報を受け取った時の処理を記載
		// 試しにそのLocationにanimationで移動し、zoom levelを19に変更
		mMyLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				GeoPoint gp = mMyLocationOverlay.getMyLocation();
				mMapController.animateTo(gp);
				mMapController.setZoom(19);
			}
		});
		// LocationManagerからのLocation update取得
		mMyLocationOverlay.enableMyLocation();

		// overlayのlistにMyLocationOverlayを登録
		List<Overlay> overlays = mMapView.getOverlays();
		overlays.add(mMyLocationOverlay);
	}

	private void resetOverlays() {
		// LocationManagerからのLocation update情報を取得をcancel
		mMyLocationOverlay.disableMyLocation();

		// overlayのlistからMyLocationOverlayを削除
		List<Overlay> overlays = mMapView.getOverlays();
		overlays.remove(mMyLocationOverlay);
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
				mMapController.animateTo(new GeoPoint(
						(int) (loc.getLatitude() * 1E6), (int) (loc
								.getLongitude() * 1E6)));

				latitude = loc.getLatitude();
				longitude = loc.getLongitude();

				la = latitude;
				ln = longitude;

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