package jp.ikisaki.www;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class ShowMapRegistrationActivity extends MapActivity{


	static DBAdapter dbAdapter;
	static RegistrationDBAdapter registrationdbAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_map_regist);

		Button homeButton = (Button) findViewById(R.id.home_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		dbAdapter = new DBAdapter(this);
		registrationdbAdapter = new RegistrationDBAdapter(this);



		TextView mapTextView = (TextView)findViewById(R.id.map_TextView);
		mapTextView.setTextColor(Color.BLACK);
		//mapTextView.setText("OK");

		Intent intent = getIntent();

		String data = intent.getStringExtra("keyword");

		int tempIndex = data.indexOf("$");
		String tempString = data.substring(0, tempIndex);

		int tmpIndex = tempString.indexOf("&");
		final String temString = tempString.substring(0, tmpIndex);

		final String id = data.substring(tmpIndex + 1, tempIndex);

		mapTextView.setText(temString);


		Button decisionButton = (Button) findViewById(R.id.decision_button);
		decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				PointModel departureModel = new PointModel(Integer.valueOf(id), temString,
						"", 0, 0, 0);

				BasicModel.setDeparture(departureModel);


				dbAdapter.open();
				Cursor c = dbAdapter.getAllNotes();
				if(c.getCount() == 0){
					dbAdapter.saveNote(temString, id);
				}
				startManagingCursor(c);
				if (c.moveToFirst()) {
					do {
						Note note = new Note(c.getInt(c
								.getColumnIndex(DBAdapter.COL_ID)), c.getString(c
										.getColumnIndex(DBAdapter.COL_NOTE)), c.getString(c
												.getColumnIndex(DBAdapter.COL_LASTUPDATE)));
						if(note.getNote().equals(temString)){
							dbAdapter.deleteNote(note.getId());
						}
					} while (c.moveToNext());
					dbAdapter.saveNote(temString, id);
				}
				stopManagingCursor(c);
				dbAdapter.close();

				Intent intent = new Intent(ShowMapRegistrationActivity.this, RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				startActivity(intent);
			}
		});

		Button registButton = (Button) findViewById(R.id.registration_button);
		registButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				registrationdbAdapter.open();
				Cursor c = registrationdbAdapter.getAllNotes();
				if (c.getCount() == 0) {
					registrationdbAdapter.saveNote(temString,
							id);
					System.out.println("logcat:7:ko");
				}
				startManagingCursor(c);
				int reg = 0;
				String regName = "";
				if (c.moveToFirst()) {
					do {
						Note note = new Note(
								c.getInt(c
										.getColumnIndex(RegistrationDBAdapter.COL_ID)),
								c.getString(c
										.getColumnIndex(RegistrationDBAdapter.COL_NOTE)),
								c.getString(c
										.getColumnIndex(RegistrationDBAdapter.COL_LASTUPDATE)));
						if (note.getNote().equals(
								temString)) {
							registrationdbAdapter.deleteNote(note
									.getId());
							System.out.println("logcat:7:delete");

							regName = note.getNote();

							Toast toast = Toast.makeText(ShowMapRegistrationActivity.this,
									"「" + regName + "」は既に登録されています。", Toast.LENGTH_LONG);

							toast.setGravity(Gravity.CENTER, 0, 0);

							toast.show();
							reg = 1;
						}
					} while (c.moveToNext());
					System.out.println("logcat:7:ok");

					if(reg == 0){
						Toast toast = Toast.makeText(ShowMapRegistrationActivity.this,
								"「" + temString + "」を登録しました。", Toast.LENGTH_LONG);

						toast.setGravity(Gravity.CENTER, 0, 0);

						toast.show();
					}

					registrationdbAdapter.saveNote(temString,
							id);
				}
				stopManagingCursor(c);
				registrationdbAdapter.close();

				//loadNote();


			}
		});


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
