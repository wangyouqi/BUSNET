package jp.ikisaki.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SelectARideBusStopActivity extends Activity implements
		TextView.OnEditorActionListener, LocationListener{
	private static final int REQUEST_CODE = 1;

	private LocationManager manager = null;
	private double currentLatitude = 0.0;
	private double currentLongitude = 0.0;

	private int flag = 0;

	private String aroundString[] = new String[5];
	private int aroundId[] = new int[5];

	final Handler handler = new Handler();

	static DBAdapter dbAdapter;
	static RegistrationDBAdapter registrationdbAdapter;

	private void history(String name, int id){
		System.out.println("logcat:5:history1");

		dbAdapter.open();
		System.out.println("logcat:5:history1.1");
		Cursor c = dbAdapter.getAllNotes();
		if(c.getCount() == 0){
			System.out.println("logcat:5:history2");
			dbAdapter.saveNote(name, String.valueOf(id));
		}
		System.out.println("logcat:5:history1.5");
		startManagingCursor(c);
		System.out.println("logcat:5:history1.6");
		if (c.moveToFirst()) {
			do {
				System.out.println("logcat:5:history3");
				Note note = new Note(c.getInt(c
						.getColumnIndex(DBAdapter.COL_ID)), c.getString(c
						.getColumnIndex(DBAdapter.COL_NOTE)), c.getString(c
						.getColumnIndex(DBAdapter.COL_LASTUPDATE)));
				if(note.getNote().equals(name)){
					System.out.println("logcat:5:history4");
					dbAdapter.deleteNote(note.getId());
				}
			} while (c.moveToNext());
			System.out.println("logcat:5:history5");
			dbAdapter.saveNote(name, String.valueOf(id));
		}
		System.out.println("logcat:5:history6");
		stopManagingCursor(c);
		dbAdapter.close();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_a_bus_stop);

		Toast toast = Toast.makeText(SelectARideBusStopActivity.this, "周辺の項目の登録を行いたい場合は\n項目を長押しして下さい", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		dbAdapter = new DBAdapter(this);
		registrationdbAdapter = new RegistrationDBAdapter(this);
		manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = manager.getBestProvider(criteria, true);
		manager.requestLocationUpdates(provider, 100000, 0, this);

		Button homeButton = (Button) findViewById(R.id.close_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		TextView selectARideBusStopText = (TextView) findViewById(R.id.select_a_bus_stop_textView);

		ImageButton voiceButton = (ImageButton) findViewById(R.id.mike_imgbtn);
		voiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
							RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
							"音声を入力してください。");
					startActivityForResult(intent, REQUEST_CODE);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(SelectARideBusStopActivity.this,
							"Not found", Toast.LENGTH_LONG).show();
				}
			}
		});

		EditText exploreEditText = (EditText) findViewById(R.id.specify_point_edittext);
		exploreEditText.setImeOptions(DEFAULT_KEYS_SEARCH_LOCAL);
		exploreEditText.setOnEditorActionListener(this);
		exploreEditText
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

					}
				});

		Button exploreButton = (Button) findViewById(R.id.explore_button);
		exploreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText exploreEditText = (EditText) findViewById(R.id.specify_point_edittext);
				String exploreString = exploreEditText.getText().toString();
				examinLandmark(exploreString);
			}
		});


	}

	public void examinLandmark(String exploreString) {
		InputStream is = null;
		BufferedReader br = null;

		final ArrayList<LandmarkModel> landmarkArray = new ArrayList<LandmarkModel>();

		final StringBuilder sb = new StringBuilder();
		try {
			try {
				is = getAssets().open("busstops.csv");
				br = new BufferedReader(new InputStreamReader(is));

				String str;
				while ((str = br.readLine()) != null) {
					sb.append(str + "\n");

					StringTokenizer st = new StringTokenizer(str, ",");

					int i = 0;
					int hit = 0;

					int id = 0;
					String name = "";
					String yomi = "";
					int longitude = 0;
					int latitude = 0;
					int frequency = 0;

					while (st.hasMoreTokens()) {
						i++;
						if (i == 1) {
							id = Integer.valueOf(st.nextToken());
						} else if (i == 2) {
							name = st.nextToken();

							if (name.indexOf(exploreString) != -1) {

								hit = 1;

								yomi = st.nextToken();

							} else {
								yomi = st.nextToken();

								if (yomi.indexOf(exploreString) != -1) {

									hit = 1;
								}
							}
						} else if (i == 4) {
							// longitude = Integer.valueOf(st.nextToken());
							double lng = Double.valueOf(st.nextToken());
							longitude = (int)(lng * 1E6 * 0.36);

						} else if (i == 5) {
							// latitude = Integer.valueOf(st.nextToken());
							double lat = Double.valueOf(st.nextToken());
							latitude = (int)(lat * 1E6 * 0.36);
						} else if (i == 6) {
							frequency = Integer.valueOf(st.nextToken());

//							longitude = (int)(lng * 1E6 * 0.36);
//							latitude = (int)(lat * 1E6 * 0.36);

							if (hit == 1) {
								LandmarkModel landmarkModel = new LandmarkModel(
										id, name, yomi, longitude, latitude,
										frequency);

								if (landmarkArray.size() == 0) {
									landmarkArray.add(landmarkModel);
								} else {
									int k = landmarkArray.size();
									for (int j = 0; j < k; j++) {

										if (landmarkModel.getFrequency() > landmarkArray
												.get(j).getFrequency()) {
											landmarkArray.add(j, landmarkModel);
											hit = 0;
											break;
										}
									}
									if (hit == 1) {
										landmarkArray.add(landmarkModel);
									}
								}
							}
							i = 0;
							hit = 0;
						}
					}
				}

			} finally {
				if (br != null) {
					br.close();
				}
			}
		} catch (IOException e) {
		}

		String landmarkOfTheCandidate[] = new String[landmarkArray.size()];
		for (int i = 0; i < landmarkArray.size(); i++) {
			landmarkOfTheCandidate[i] = landmarkArray.get(i).getName();
		}

		if (landmarkArray.size() != 0) {
			final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			flag = 0;
			alert.setTitle("候補から選択して下さい。\n(通常モード)");
			Toast toast = Toast.makeText(SelectARideBusStopActivity.this, "登録を行いたい場合は\nモードを切り替えて下さい", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.show();
			alert.setPositiveButton("モード切り替え", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog,int whichButton) {
		        	Toast toast = Toast.makeText(
							SelectARideBusStopActivity.this,
							"モードを切り替えました", Toast.LENGTH_SHORT);

					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();

					String tmp = "";
					if(flag == 0){
						tmp = "登録";
						flag = 1;
					}
					else{
						tmp = "通常";
						flag = 0;
					}
		            alert.show().setTitle("候補から選択して下さい。\n(" + tmp + "モード)");
		        }
			});

			alert.setItems(landmarkOfTheCandidate,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int idx) {
							if(flag == 1){

								final int tmp = idx;

								Intent intent = new Intent(SelectARideBusStopActivity.this,
										ShowMapARideRegistrationActivity.class);
								System.out.println("logcat:2:& " + landmarkArray.get(tmp).getLatitude() + ":" + landmarkArray.get(tmp).getLongitude());
								intent.putExtra("keyword", landmarkArray.get(tmp).getName() + "&" + landmarkArray.get(tmp).getId() + "$" + landmarkArray.get(tmp).getLatitude() + "," + landmarkArray.get(tmp).getLongitude());

								startActivity(intent);
							}
							else{
								TimetableModel.setStartId(landmarkArray.get(idx)
										.getId());
								TimetableModel.setStartName(landmarkArray.get(idx)
										.getName());
								TimetableModel.setDestinationId(0);
								TimetableModel.setDestinationName("");

								history(landmarkArray.get(idx)
										.getName(), landmarkArray.get(idx)
										.getId());

								Intent intent = new Intent(
										SelectARideBusStopActivity.this,
										TimetableSearchActivity.class);
								intent.putExtra("keyword", "keyword");
								startActivity(intent);
							}
						}
					});
			alert.show();
		} else {
			Toast toast = Toast.makeText(SelectARideBusStopActivity.this,
					"該当するランドマークが存在しません。", Toast.LENGTH_LONG);

			toast.setGravity(Gravity.TOP, 0, 250);

			toast.show();
		}
	}

	public boolean examineLandmark(int actionId, KeyEvent event) {
		if (event == null && actionId == 6 || event != null
				&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			EditText exploreEditText = (EditText) findViewById(R.id.specify_point_edittext);
			String exploreString = exploreEditText.getText().toString();
			examinLandmark(exploreString);
		}
		return false;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		examineLandmark(actionId, event);

		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLatitude = location.getLatitude();
		currentLongitude = location.getLongitude();

		final Button around1Button = (Button) findViewById(R.id.around_1_button);
		final Button around2Button = (Button) findViewById(R.id.around_2_button);
		final Button around3Button = (Button) findViewById(R.id.around_3_button);
		final Button around4Button = (Button) findViewById(R.id.around_4_button);
		final Button around5Button = (Button) findViewById(R.id.around_5_button);

		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
//						around1Button.setText("非同期処理中");
//						around2Button.setText("非同期処理中");
//						around3Button.setText("非同期処理中");
//						around4Button.setText("非同期処理中");
//						around5Button.setText("非同期処理中");
					}
				});
				loadGps();
				handler.post(new Runnable() {
					@Override
					public void run() {
						System.out.println("run");
						around1Button.setText(aroundString[0]);
						around2Button.setText(aroundString[1]);
						around3Button.setText(aroundString[2]);
						around4Button.setText(aroundString[3]);
						around5Button.setText(aroundString[4]);

						around1Button.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								TimetableModel.setStartId(aroundId[0]);
								TimetableModel.setStartName(aroundString[0]);
								TimetableModel.setDestinationId(0);
								TimetableModel.setDestinationName("");
								history(aroundString[0], aroundId[0]);
								Intent intent = new Intent(
										SelectARideBusStopActivity.this,
										TimetableSearchActivity.class);
								startActivity(intent);
							}
						});

						around1Button.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								String busstopName = aroundString[0];
								String busstopId = String.valueOf(aroundId[0]);

								regist(busstopName, busstopId);

								return true;
							}
						});

						around2Button.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								TimetableModel.setStartId(aroundId[1]);
								TimetableModel.setStartName(aroundString[1]);
								TimetableModel.setDestinationId(0);
								TimetableModel.setDestinationName("");
								history(aroundString[1], aroundId[1]);
								Intent intent = new Intent(
										SelectARideBusStopActivity.this,
										TimetableSearchActivity.class);
								startActivity(intent);
							}
						});

						around2Button.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								String busstopName = aroundString[1];
								String busstopId = String.valueOf(aroundId[1]);
								regist(busstopName, busstopId);
								return true;
							}
						});

						around3Button.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								TimetableModel.setStartId(aroundId[2]);
								TimetableModel.setStartName(aroundString[2]);
								TimetableModel.setDestinationId(0);
								TimetableModel.setDestinationName("");
								history(aroundString[2], aroundId[2]);
								Intent intent = new Intent(
										SelectARideBusStopActivity.this,
										TimetableSearchActivity.class);
								startActivity(intent);
							}
						});

						around3Button.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								String busstopName = aroundString[2];
								String busstopId = String.valueOf(aroundId[2]);
								regist(busstopName, busstopId);
								return true;
							}
						});

						around4Button.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								TimetableModel.setStartId(aroundId[3]);
								TimetableModel.setStartName(aroundString[3]);
								TimetableModel.setDestinationId(0);
								TimetableModel.setDestinationName("");
								history(aroundString[3], aroundId[3]);
								Intent intent = new Intent(
										SelectARideBusStopActivity.this,
										TimetableSearchActivity.class);
								startActivity(intent);
							}
						});

						around4Button.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								String busstopName = aroundString[3];
								String busstopId = String.valueOf(aroundId[3]);
								regist(busstopName, busstopId);
								return true;
							}
						});

						around5Button.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								TimetableModel.setStartId(aroundId[4]);
								TimetableModel.setStartName(aroundString[4]);
								TimetableModel.setDestinationId(0);
								TimetableModel.setDestinationName("");
								history(aroundString[4], aroundId[4]);
								Intent intent = new Intent(
										SelectARideBusStopActivity.this,
										TimetableSearchActivity.class);
								startActivity(intent);
							}
						});

						around5Button.setOnLongClickListener(new View.OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								String busstopName = aroundString[4];
								String busstopId = String.valueOf(aroundId[4]);
								regist(busstopName, busstopId);

								return true;
							}
						});

					}
				});
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String input = "";
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> candidates = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			if (candidates.size() > 0) {
				input = candidates.get(0);
				Toast.makeText(SelectARideBusStopActivity.this, input,
						Toast.LENGTH_LONG).show();
				examinLandmark(input);
			}
		}
	}

	@Override
	protected void onPause(){
		if(manager != null){
			manager.removeUpdates(this);
		}
		super.onPause();
	}

	@Override
	protected void onResume(){
		System.out.println("logcat::onresume");
		if(manager != null){
			System.out.println("logcat::null");
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);
		}
		super.onResume();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}



	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	private void loadGps() {
		double currentJapLongitude = currentLongitude;
		double currentJapLatitude = currentLatitude;

		currentJapLatitude = currentJapLatitude + currentJapLatitude * 0.00010696 - currentJapLongitude * 0.000017467 - 0.0046020;
		currentJapLongitude = currentJapLongitude + currentJapLatitude * 0.000046047 + currentJapLongitude * 0.000083049 - 0.010041;

		InputStream is = null;
		BufferedReader br = null;
		ArrayList<LandmarkModel> aroundLandmarkArray = new ArrayList<LandmarkModel>();

		final StringBuilder sb = new StringBuilder();
		try {
			try {
				is = getAssets().open("busstops.csv");
				br = new BufferedReader(new InputStreamReader(is));

				String str;
				while ((str = br.readLine()) != null) {
					sb.append(str + "\n");

					StringTokenizer st = new StringTokenizer(str, ",");



					int i = 0;

					int id = 0;
					String name = "";
					String yomi = "";
					int longitude = 0;
					int latitude = 0;
					int frequency = 0;
					double lng = 0.0;
					double lat = 0.0;



					while (st.hasMoreTokens()) {
						i++;

						if (i == 1) {
							id = Integer.valueOf(st.nextToken());
						} else if (i == 2) {
							name = st.nextToken();
						} else if (i == 3) {
							yomi = st.nextToken();
						} else if (i == 4) {
							lng = Double.valueOf(st.nextToken());
							//longitude = (int)(Double.valueOf(st.nextToken()) * 1E6 * 0.36);
						} else if (i == 5) {
							lat = Double.valueOf(st.nextToken());
							//latitude = (int)(Double.valueOf(st.nextToken()) * 1E6 * 0.36);
						} else if (i == 6) {
							frequency = Integer.valueOf(st.nextToken());

//								lat = lat + lat * 0.00010696 - lng * 0.000017467 - 0.0046020;
//								lng = lng + lat * 0.000046047 + lng * 0.000083049 - 0.010041;

							longitude = (int)(lng * 1E6 * 0.36);
							latitude = (int)(lat * 1E6 * 0.36);

							LandmarkModel aroundLandmarkModel = new LandmarkModel(
									id, name, yomi, longitude, latitude,
									frequency);



							if (aroundLandmarkArray.size() == 0) {
								System.out.println("logcat:dame");
								aroundLandmarkArray
										.add(aroundLandmarkModel);
							} else {
								int k = aroundLandmarkArray.size();

								for (int j = 0; j < k && j < 5; j++) {
									if (aroundLandmarkModel.getFrequency() > 10) {
										float[] preResult = new float[3];
										//System.out.println("logcat:" + aroundLandmarkArray.get(j).getLongitude() + ":" + aroundLandmarkArray.get(j).getLatitude());
										Location.distanceBetween(
												(double) aroundLandmarkArray
														.get(j)
														.getLongitude() / 1E6 / 0.36,
												(double) aroundLandmarkArray
														.get(j)
														.getLatitude() / 1E6 / 0.36,
												currentJapLatitude,
												currentJapLongitude,
												preResult);

										float[] currentResult = new float[3];
										Location.distanceBetween(
												(double) aroundLandmarkModel
														.getLongitude() / 1E6 / 0.36,
												(double) aroundLandmarkModel
														.getLatitude() / 1E6 / 0.36,
												currentJapLatitude,
												currentJapLongitude,
												currentResult);

										if (currentResult[0] < preResult[0] && !aroundLandmarkArray.get(j).getName().equals(aroundLandmarkModel.getName())) {

											System.out.println("logcat:pre:" + aroundLandmarkArray.get(j).getName());
											System.out.println("logcat:cure:" + aroundLandmarkModel.getName());

											aroundLandmarkArray.add(j,
													aroundLandmarkModel);
											i = 0;
											j = k;
										}
									}
								}
								i = 0;
							}
						}
					}
				}
				for(int k = 0; k < 5; k++){
					aroundString[k] = aroundLandmarkArray.get(k).getName();
					aroundId[k] = aroundLandmarkArray.get(k).getId();
				}
			}

			finally {
				if (br != null) {
					br.close();
				}
			}
		} catch (IOException e) {

		}
	}

	public void regist(final String name, final String id){

		new AlertDialog.Builder(SelectARideBusStopActivity.this)
		 .setMessage(name)
		 .setCancelable(false)
		 .setPositiveButton("登録", new DialogInterface.OnClickListener() {
		     public void onClick(DialogInterface dialog, int idx) {
		    	 registrationdbAdapter.open();
		 		Cursor c = registrationdbAdapter.getAllNotes();
		 		if (c.getCount() == 0) {
		 			registrationdbAdapter.saveNote(name,
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
		 						name)) {
		 					registrationdbAdapter.deleteNote(note
		 							.getId());
		 					System.out.println("logcat:7:delete");

		 					regName = note.getNote();

		 					Toast toast = Toast.makeText(SelectARideBusStopActivity.this,
		 							"「" + regName + "」は既に登録されています。", Toast.LENGTH_LONG);

		 					toast.setGravity(Gravity.CENTER, 0, 0);

		 					toast.show();
		 					reg = 1;
		 				}
		 			} while (c.moveToNext());
		 			System.out.println("logcat:7:ok");

		 			if(reg == 0){
		 				Toast toast = Toast.makeText(SelectARideBusStopActivity.this,
		 						"「" + name + "」を登録しました。", Toast.LENGTH_LONG);

		 				toast.setGravity(Gravity.CENTER, 0, 0);

		 				toast.show();
		 			}

		 			registrationdbAdapter.saveNote(name,
		 					id);
		 		}
		 		stopManagingCursor(c);
		 		registrationdbAdapter.close();
		    }
		})
		.setNegativeButton("地図表示", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int idx) {
		    	int landmarkLatitude = 0;
            	int landmarkLongitude = 0;

            	InputStream is = null;
        		BufferedReader br = null;

        		final ArrayList<LandmarkModel> landmarkArray = new ArrayList<LandmarkModel>();

        		final StringBuilder sb = new StringBuilder();
        		try {
        			try {
        				is = getAssets().open("landmark.csv");
        				br = new BufferedReader(new InputStreamReader(is));

        				String str;
        				while ((str = br.readLine()) != null) {
        					sb.append(str + "\n");

        					StringTokenizer st = new StringTokenizer(str, ",");

        					int i = 0;
        					int hit = 0;

        					int id_num = 0;
        					String name = "";
        					String yomi = "";
        					int longitude = 0;
        					int latitude = 0;
        					int frequency = 0;

        					while (st.hasMoreTokens()) {
        						i++;
        						if (i == 1) {
        							id_num = Integer.valueOf(st.nextToken());

        							if(id_num == Integer.valueOf(id)){
        								System.out.println("logcat:2: " + id_num);
        								hit = 1;
        							}
        						} else if (i == 2) {
        							name = st.nextToken();
        						} else if (i == 3){
        							yomi = st.nextToken();
        						} else if (i == 4) {
        							longitude = Integer.valueOf(st.nextToken());
        							if(hit == 1){
        								landmarkLongitude = longitude;
        							}
        						} else if (i == 5) {
        							latitude = Integer.valueOf(st.nextToken());
        							if(hit == 1){
        								landmarkLatitude = latitude;
        							}
        						} else if (i == 6) {
        							frequency = Integer.valueOf(st.nextToken());

        							i = 0;
        							hit = 0;
        						}
        					}
        				}

        			} finally {
        				if (br != null) {
        					br.close();
        				}
        			}
        		} catch (IOException e) {

        		}



				Intent intent = new Intent(SelectARideBusStopActivity.this,
						ShowMapARideRegistrationActivity.class);
				System.out.println("logcat:2:& " + name + ":" + landmarkLatitude + "," + landmarkLongitude);
				intent.putExtra("keyword", name + "&" + id + "$" + landmarkLatitude + "," + landmarkLongitude);

				startActivity(intent);
		    }
		})
		.show();



	}
}