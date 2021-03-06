package jp.ikisaki.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.impl.client.TunnelRefusedException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

public class SelectAnAlightingBusStopActivity extends Activity implements TextView.OnEditorActionListener{
	private static final int REQUEST_CODE = 1;
	static DBAdapter dbAdapter;
	static RegistrationDBAdapter registrationdbAdapter;

	private int flag = 0;
	private int candidateFlag = 0;

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
        setContentView(R.layout.select_an_alighting_busstop);

        Toast toast = Toast.makeText(SelectAnAlightingBusStopActivity.this, "候補地の登録を行いたい場合は\n項目を長押しして下さい", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

        dbAdapter = new DBAdapter(this);
        registrationdbAdapter = new RegistrationDBAdapter(this);
        System.out.println("logcat:5:onc1");

        Button homeButton = (Button)findViewById(R.id.close_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectAnAlightingBusStopActivity.this, TimetableSearchActivity.class);
				startActivity(intent);
			}
		});

        Button candidate1Button = (Button)findViewById(R.id.candidate_1_button);
        Button candidate2Button = (Button)findViewById(R.id.candidate_2_button);
        Button candidate3Button = (Button)findViewById(R.id.candidate_3_button);
        Button candidate4Button = (Button)findViewById(R.id.candidate_4_button);
        Button candidate5Button = (Button)findViewById(R.id.candidate_5_button);
        Button candidateListButton = (Button)findViewById(R.id.candidate_6_button);

        System.out.println("logcat:5:onc2");

        final ArrayList<BusstopsModel> busstopsModels = TimetableModel.getBusstopsModel();

        System.out.println("logcat:5:onc3");

        System.out.println("logcat:5:" + "k:" + busstopsModels.get(0).getName());

        candidate1Button.setText(busstopsModels.get(0).getName());
        candidate2Button.setText(busstopsModels.get(1).getName());
        candidate3Button.setText(busstopsModels.get(2).getName());
        candidate4Button.setText(busstopsModels.get(3).getName());
        candidate5Button.setText(busstopsModels.get(4).getName());

        candidate1Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimetableModel.setDestinationId(busstopsModels.get(0).getId());
				TimetableModel.setDestinationName(busstopsModels.get(0).getName());
				history(busstopsModels.get(0).getName(), busstopsModels.get(0).getId());
				Intent intent = new Intent(SelectAnAlightingBusStopActivity.this,
						TimetableSearchActivity.class);
				startActivity(intent);
			}
		});

        candidate1Button.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String busstopName = busstopsModels.get(0).getName();
				String busstopId = String.valueOf(busstopsModels.get(0).getId());

				regist(busstopName, busstopId);

				return true;
			}
		});

        candidate2Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimetableModel.setDestinationId(busstopsModels.get(1).getId());
				TimetableModel.setDestinationName(busstopsModels.get(1).getName());
				history(busstopsModels.get(1).getName(), busstopsModels.get(1).getId());
				Intent intent = new Intent(SelectAnAlightingBusStopActivity.this,
						TimetableSearchActivity.class);
				startActivity(intent);
			}
		});

        candidate2Button.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String busstopName = busstopsModels.get(1).getName();
				String busstopId = String.valueOf(busstopsModels.get(1).getId());

				regist(busstopName, busstopId);

				return true;
			}
		});

        candidate3Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimetableModel.setDestinationId(busstopsModels.get(2).getId());
				TimetableModel.setDestinationName(busstopsModels.get(2).getName());
				history(busstopsModels.get(2).getName(), busstopsModels.get(2).getId());
				Intent intent = new Intent(SelectAnAlightingBusStopActivity.this,
						TimetableSearchActivity.class);
				startActivity(intent);
			}
		});

        candidate3Button.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String busstopName = busstopsModels.get(2).getName();
				String busstopId = String.valueOf(busstopsModels.get(2).getId());

				regist(busstopName, busstopId);

				return true;
			}
		});

        candidate4Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimetableModel.setDestinationId(busstopsModels.get(3).getId());
				TimetableModel.setDestinationName(busstopsModels.get(3).getName());
				history(busstopsModels.get(3).getName(), busstopsModels.get(3).getId());
				Intent intent = new Intent(SelectAnAlightingBusStopActivity.this,
						TimetableSearchActivity.class);
				startActivity(intent);
			}
		});

        candidate4Button.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String busstopName = busstopsModels.get(3).getName();
				String busstopId = String.valueOf(busstopsModels.get(3).getId());

				regist(busstopName, busstopId);

				return true;
			}
		});

        candidate5Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimetableModel.setDestinationId(busstopsModels.get(4).getId());
				TimetableModel.setDestinationName(busstopsModels.get(4).getName());
				history(busstopsModels.get(4).getName(), busstopsModels.get(4).getId());
				Intent intent = new Intent(SelectAnAlightingBusStopActivity.this,
						TimetableSearchActivity.class);
				startActivity(intent);
			}
		});

        candidate5Button.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				String busstopName = busstopsModels.get(4).getName();
				String busstopId = String.valueOf(busstopsModels.get(4).getId());

				regist(busstopName, busstopId);

				return true;
			}
		});

        ImageButton voiceButton = (ImageButton) findViewById(R.id.mike_imgbtn);
		voiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力してください。");
					startActivityForResult(intent, REQUEST_CODE);
				}
				catch(ActivityNotFoundException e){
					Toast.makeText(SelectAnAlightingBusStopActivity.this, "Not found", Toast.LENGTH_LONG).show();
				}
			}
		});

    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        candidateListButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String landmarkOfTheCandidate[] = new String[busstopsModels.size() - 5];
				for (int i = 5; i < busstopsModels.size(); i++) {
					landmarkOfTheCandidate[i - 5] = busstopsModels.get(i).getName();
					//System.out.println("logcat:" + i + busstopsModels.get(i).getName());
				}

				if ((busstopsModels.size() - 5) != 0) {
					candidateFlag = 0;
					alert.setTitle("候補から選択して下さい。\n(通常モード)");
					Toast toast = Toast.makeText(SelectAnAlightingBusStopActivity.this, "登録を行いたい場合は\nモードを切り替えて下さい", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 0, 0);
					toast.show();
					alert.setPositiveButton("モード切り替え", new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog,int whichButton) {
				        	Toast toast = Toast.makeText(
									SelectAnAlightingBusStopActivity.this,
									"モードを切り替えました", Toast.LENGTH_SHORT);

							toast.setGravity(Gravity.TOP, 0, 0);
							toast.show();

							String tmp = "";
							if(candidateFlag == 0){
								tmp = "登録";
								candidateFlag = 1;
							}
							else{
								tmp = "通常";
								candidateFlag = 0;
							}
				            alert.show().setTitle("候補から選択して下さい。\n(" + tmp + "モード)");
				        }
					});

					alert.setItems(landmarkOfTheCandidate,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int idx) {
									if(candidateFlag == 1){

										final int tmp = idx + 5;

										int landmarkLatitude = 0;
						            	int landmarkLongitude = 0;

						            	System.out.println("logcat:2: " + busstopsModels.get(tmp).getId());

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

						        							if(id_num == Integer.valueOf(busstopsModels.get(tmp).getId())){
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



										Intent intent = new Intent(SelectAnAlightingBusStopActivity.this,
												ShowMapAnAlightingRegistrationActivity.class);
										System.out.println("logcat:2:& " + busstopsModels.get(tmp) + ":" + landmarkLatitude + "," + landmarkLongitude);
										intent.putExtra("keyword", busstopsModels.get(tmp).getName() + "&" + busstopsModels.get(tmp).getId() + "$" + landmarkLatitude + "," + landmarkLongitude);

										startActivity(intent);
									}
									else{
										TimetableModel.setDestinationId(busstopsModels.get(idx + 5).getId());
										TimetableModel.setDestinationName(busstopsModels.get(idx + 5).getName());
										history(busstopsModels.get(idx + 5).getName(), busstopsModels.get(idx + 5).getId());
										Intent intent = new Intent(SelectAnAlightingBusStopActivity.this, TimetableSearchActivity.class);
										startActivity(intent);
									}
								}
							});
					alert.show();
				} else {
					Toast toast = Toast.makeText(
							SelectAnAlightingBusStopActivity.this,
							"該当するランドマークが存在しません．", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});


        Button exploreButton = (Button) findViewById(R.id.explore_button);
		exploreButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText exploreEditText = (EditText) findViewById(R.id.specify_point_edittext);
				String exploreString = exploreEditText.getText().toString();
				examinBusstops(exploreString);
			}
		});

		EditText exploreEditText = (EditText)findViewById(R.id.specify_point_edittext);
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

    }

    public void examinBusstops(String exploreString){
    	final ArrayList<BusstopsModel> busstopsModels = TimetableModel.getBusstopsModel();
    	final ArrayList<BusstopsModel> examinModel = new ArrayList<BusstopsModel>();
    	for(int i = 0; i < busstopsModels.size(); i++){
    		if ((busstopsModels.get(i).getName().indexOf(exploreString) != -1) || (busstopsModels.get(i).getYomi().indexOf(exploreString) != -1)){
    			examinModel.add(busstopsModels.get(i));
			}
    	}
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	String landmarkOfTheCandidate[] = new String[examinModel.size()];
		for (int i = 0; i < examinModel.size(); i++) {
			landmarkOfTheCandidate[i] = examinModel.get(i).getName();
		}
    	if (examinModel.size() != 0) {
    		candidateFlag = 0;
			alert.setTitle("候補から選択して下さい。\n(通常モード)");
			Toast toast = Toast.makeText(SelectAnAlightingBusStopActivity.this, "登録を行いたい場合は\nモードを切り替えて下さい", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.show();
			alert.setPositiveButton("モード切り替え", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog,int whichButton) {
		        	Toast toast = Toast.makeText(
							SelectAnAlightingBusStopActivity.this,
							"モードを切り替えました", Toast.LENGTH_SHORT);

					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();

					String tmp = "";
					if(candidateFlag == 0){
						tmp = "登録";
						candidateFlag = 1;
					}
					else{
						tmp = "通常";
						candidateFlag = 0;
					}
		            alert.show().setTitle("候補から選択して下さい。\n(" + tmp + "モード)");
		        }
			});

			alert.setItems(landmarkOfTheCandidate,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int idx) {
							if(candidateFlag == 1){

								final int tmp = idx;

								int landmarkLatitude = 0;
				            	int landmarkLongitude = 0;

				            	System.out.println("logcat:2: " + examinModel.get(tmp).getId());

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

				        							if(id_num == Integer.valueOf(examinModel.get(tmp).getId())){
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

				        		//







				            	//

								Intent intent = new Intent(SelectAnAlightingBusStopActivity.this,
										ShowMapAnAlightingRegistrationActivity.class);
								System.out.println("logcat:2:& " + examinModel.get(tmp) + ":" + landmarkLatitude + "," + landmarkLongitude);
								intent.putExtra("keyword", examinModel.get(tmp).getName() + "&" + examinModel.get(tmp).getId() + "$" + landmarkLatitude + "," + landmarkLongitude);

								startActivity(intent);
							}
							else{
								TimetableModel.setDestinationId(examinModel.get(idx).getId());
								TimetableModel.setDestinationName(examinModel.get(idx).getName());
								history(examinModel.get(idx).getName(), examinModel.get(idx).getId());
								Intent intent = new Intent(SelectAnAlightingBusStopActivity.this, TimetableSearchActivity.class);
								startActivity(intent);
							}
						}
					});
			alert.show();
		} else {
			Toast toast = Toast.makeText(
					SelectAnAlightingBusStopActivity.this,
					"該当するランドマークが存在しません．", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
    }

    public boolean examineBusstops(int actionId, KeyEvent event){
		if (event == null && actionId == 6 || event != null
				&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			EditText exploreEditText = (EditText) findViewById(R.id.specify_point_edittext);
			String exploreString = exploreEditText.getText().toString();
			examinBusstops(exploreString);
		}
		return false;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		examineBusstops(actionId, event);

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		String input = "";
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
			ArrayList<String> candidates = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			if(candidates.size() > 0){
				input = candidates.get(0);
				Toast.makeText(SelectAnAlightingBusStopActivity.this, input, Toast.LENGTH_LONG).show();
				examinBusstops(input);
			}
		}
	}

	public void regist(final String name, final String id){
		new AlertDialog.Builder(SelectAnAlightingBusStopActivity.this)
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

		 					Toast toast = Toast.makeText(SelectAnAlightingBusStopActivity.this,
		 							"「" + regName + "」は既に登録されています。", Toast.LENGTH_LONG);

		 					toast.setGravity(Gravity.CENTER, 0, 0);

		 					toast.show();
		 					reg = 1;
		 				}
		 			} while (c.moveToNext());
		 			System.out.println("logcat:7:ok");

		 			if(reg == 0){
		 				Toast toast = Toast.makeText(SelectAnAlightingBusStopActivity.this,
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



				Intent intent = new Intent(SelectAnAlightingBusStopActivity.this,
						ShowMapAnAlightingRegistrationActivity.class);
				System.out.println("logcat:2:& " + name + ":" + landmarkLatitude + "," + landmarkLongitude);
				intent.putExtra("keyword", name + "&" + id + "$" + landmarkLatitude + "," + landmarkLongitude);

				startActivity(intent);
		    }
		})
		.show();



	}
}