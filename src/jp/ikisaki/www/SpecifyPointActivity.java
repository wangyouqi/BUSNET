package jp.ikisaki.www;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.location.*;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.io.*;
import java.util.*;

public class SpecifyPointActivity extends Activity implements
		TextView.OnEditorActionListener, LocationListener{
	/** Called when the activity is first created. */
	private static final int REQUEST_CODE = 1;
	private String data = "";
	private LocationManager manager = null;
	private double currentLatitude = 0.0;
	private double currentLongitude = 0.0;
	private int around = 0;
	private int flag = 0;
	private int aroundFlag = 0;
    private GridView gridview;
	static DBAdapter dbAdapter;
    private EditText exploreEditText;
    String[] landmarkOfTheCandidate;
	ArrayList<LandmarkModel> landmarkArray = new ArrayList<LandmarkModel>();

    //140528 add by wyq add actionbar event
    public boolean onCreateOptionsMenu(Menu menu) {
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
        setContentView(R.layout.specify_point);

        dbAdapter = new DBAdapter(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = manager.getBestProvider(criteria, true);
        manager.requestLocationUpdates(provider, 100000, 0, this);

        Intent intent = getIntent();
        data = intent.getStringExtra("keyword");
        System.out.println("logcat:" + data);

        Button homeButton = (Button) findViewById(R.id.close_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView selectARideBusStopText = (TextView) findViewById(R.id.specify_point_textView);
        selectARideBusStopText.setText(data.equals("departure") ?
                getResources().getString(R.string.departure) : getResources().getString(R.string.destination));

        Button gpsMapButton = (Button) findViewById(R.id.gps_map_button);
        gpsMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyPointActivity.this,
                        CurrentPositionActivity.class);
                intent.putExtra("keyword", data);
                startActivity(intent);
            }
        });

        exploreEditText = (EditText) findViewById(R.id.specify_point_edittext);
        exploreEditText.setImeOptions(DEFAULT_KEYS_SEARCH_LOCAL);
        exploreEditText.setOnEditorActionListener(this);
        exploreEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                //EditText exploreEditText = (EditText)findViewById(R.id.specify_point_edittext);
                String exploreString = exploreEditText.getText().toString();
                examinLandmark(exploreString);
            }
        });

        ImageButton voiceButton = (ImageButton) findViewById(R.id.mike_imgbtn);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力してください。");
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(SpecifyPointActivity.this, "Not found", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button setMapButton = (Button) findViewById(R.id.set_map_button);
        setMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpecifyPointActivity.this,
                        SetMapActivity.class);
                intent.putExtra("keyword", data);
                startActivity(intent);
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        Button setAroundButton = (Button) findViewById(R.id.around_button);
        setAroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (around == 1) {
                    String landmarkOfTheCandidate[] = new String[landmarkArray.size()];
                    for (int i = 0; i < landmarkArray.size(); i++) {
                        landmarkOfTheCandidate[i] = landmarkArray.get(i).getName();
                    }

                    if (landmarkArray.size() != 0) {
                        //AlertDialog.Builder alert = new AlertDialog.Builder(this);
                        alert.setTitle("候補から選択して下さい。\n(通常モード)");
                        Toast toast = Toast.makeText(SpecifyPointActivity.this, "登録を行いたい場合は\nモードを切り替えて下さい", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                        aroundFlag = 0;
                        alert.setPositiveButton("モード切り替え", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast toast = Toast.makeText(SpecifyPointActivity.this, "モードを切り替えました", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, 0, 0);
                                toast.show();

                                String tmp = "";
                                if (aroundFlag == 0) {
                                    tmp = "登録";
                                    aroundFlag = 1;
                                } else {
                                    tmp = "通常";
                                    aroundFlag = 0;
                                }
                                alert.show().setTitle("候補から選択して下さい。\n(" + tmp + "モード)");
                            }
                        });
                        alert.setItems(landmarkOfTheCandidate,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int idx) {
                                        if (aroundFlag == 1) {

                                            final int tmp = idx;

                                            if (data.equals("departure")) {
                                                Intent intent = new Intent(SpecifyPointActivity.this,
                                                        ShowMapRegistrationActivity.class);
                                                intent.putExtra("keyword", landmarkArray.get(tmp).getName() + "&" + landmarkArray.get(tmp).getId() + "$" + landmarkArray.get(tmp).getLatitude() + "," + landmarkArray.get(tmp).getLongitude());

                                                startActivity(intent);

                                            } else {
                                                Intent intent = new Intent(SpecifyPointActivity.this,
                                                        ShowMapRegistrationDestinationActivity.class);
                                                intent.putExtra("keyword", landmarkArray.get(tmp).getName() + "&" + landmarkArray.get(tmp).getId() + "$" + landmarkArray.get(tmp).getLatitude() + "," + landmarkArray.get(tmp).getLongitude());

                                                startActivity(intent);
                                            }

                                        } else {
                                            if (data.equals("departure")) {
                                                PointModel departureModel = new PointModel(landmarkArray.get(idx).getId(), landmarkArray.get(idx).getName(),
                                                        landmarkArray.get(idx).getYomi(), landmarkArray.get(idx).getLongitude(), landmarkArray.get(idx).getLatitude(), landmarkArray.get(idx).getFrequency());

                                                BasicModel.setDeparture(departureModel);
                                            } else {
                                                PointModel destinationModel = new PointModel(landmarkArray.get(idx).getId(), landmarkArray.get(idx).getName(),
                                                        landmarkArray.get(idx).getYomi(), landmarkArray.get(idx).getLongitude(), landmarkArray.get(idx).getLatitude(), landmarkArray.get(idx).getFrequency());

                                                BasicModel.setDestination(destinationModel);
                                            }

                                            dbAdapter.open();
                                            Cursor c = dbAdapter.getAllNotes();
                                            if (c.getCount() == 0) {
                                                dbAdapter.saveNote(landmarkArray.get(idx).getName(), String.valueOf(landmarkArray.get(idx).getId()));
                                            }
                                            startManagingCursor(c);
                                            if (c.moveToFirst()) {
                                                do {
                                                    Note note = new Note(c.getInt(c
                                                            .getColumnIndex(DBAdapter.COL_ID)), c.getString(c
                                                            .getColumnIndex(DBAdapter.COL_NOTE)), c.getString(c
                                                            .getColumnIndex(DBAdapter.COL_LASTUPDATE)));
                                                    if (note.getNote().equals(landmarkArray.get(idx).getName())) {
                                                        dbAdapter.deleteNote(note.getId());
                                                    }
                                                } while (c.moveToNext());
                                                dbAdapter.saveNote(landmarkArray.get(idx).getName(), String.valueOf(landmarkArray.get(idx).getId()));
                                            }
                                            stopManagingCursor(c);
                                            dbAdapter.close();

                                            Common.setLocation(SpecifyPointActivity.this);
                                            if (Common.CHKSETTING == true) finish();
                                            else {
                                            Intent intent = new Intent(SpecifyPointActivity.this, RouteSearchActivity.class);
                                            intent.putExtra("keyword", "");
                                            startActivity(intent); }
                                        }
                                    }
                                }
                        );

                        ////////////


                        ///////////
                        alert.show();
                    }
                } else {
                    Toast.makeText(SpecifyPointActivity.this, "位置制度が安定するまでお待ちください", Toast.LENGTH_LONG).show();
                }
            }
        });

        //20140429 add by wyq ひらがなボード
        gridview = (GridView) findViewById(R.id.gridview);
        String[] str = {"ゃ", "ぁ", "わ", "ら", "や", "ま", "は", "な", "た", "さ", "か", "あ",
                "ゅ", "ぃ", "を", "り", "ゆ", "み", "ひ", "に", "ち", "し", "き", "い",
                "ょ", "ぅ", "ん", "る", "よ", "む", "ふ", "ぬ", "つ", "す", "く", "う",
                "ｘ", "ぇ", "っ", "れ", "”", "め", "へ", "ね", "て", "せ", "け", "え",
                "Ｘ", "ぉ", "ー", "ろ", "°", "も", "ほ", "の", "と", "そ", "こ", "お"};

        ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str);
        gridview.setAdapter(arrAdapter);
        gridview.setOnItemClickListener(new ItemClickListener());
        //20140429 end
    }

    //当AdapterView被??(触摸屏或者??)，?返回的Item??事件
        class ItemClickListener implements AdapterView.OnItemClickListener
        {
            public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened
                                    View arg1,//The view within the AdapterView that was clicked
                                    int arg2,//The position of the view in the adapter
                                    long arg3//The row id of the item that was clicked
            ) {
                //在本例中arg2=arg3
                //?示所?Item的ItemText
                //setTitle((String)item.get("ItemText"));
                //Toast.makeText(BusnetActivity.this, "you choose enough",Toast.LENGTH_SHORT).show();
                if (arg0.getItemAtPosition(arg2).toString()=="”") {
                    if (exploreEditText.getText().length() > 0) {
                        System.out.println("logcat:char=" + exploreEditText.getText().toString().substring(exploreEditText.getText().length() - 1, exploreEditText.getText().toString().length()));
                        switch (exploreEditText.getText().toString().substring(exploreEditText.getText().length() - 1, exploreEditText.getText().toString().length()).charAt(0)) {
                            case 'か':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'が');
                                break;
                            case 'き':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ぎ');
                                break;
                            case 'く':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ぐ');
                                break;
                            case 'け':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'げ');
                                break;
                            case 'こ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ご');
                                break;
                            case 'さ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ざ');
                                break;
                            case 'し':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'じ');
                                break;
                            case 'す':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ず');
                                break;
                            case 'せ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ぜ');
                                break;
                            case 'そ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ぞ');
                                break;
                            case 'た':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'だ');
                                break;
                            case 'ち':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ぢ');
                                break;
                            case 'つ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'づ');
                                break;
                            case 'て':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'で');
                                break;
                            case 'と':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ど');
                                break;
                            case 'は':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ば');
                                break;
                            case 'ひ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'び');
                                break;
                            case 'ふ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ぶ');
                                break;
                            case 'へ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'べ');
                                break;
                            case 'ほ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1) + 'ぼ');
                                break;
                        }
                    }
                }
                else if (arg0.getItemAtPosition(arg2).toString()=="°") {
                    if (exploreEditText.getText().length() > 0) {
                        switch (exploreEditText.getText().toString().substring(exploreEditText.getText().length() - 1, exploreEditText.getText().toString().length()).charAt(0)) {
                            case 'は':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0, exploreEditText.getText().length() - 1) + 'ぱ');
                                break;
                            case 'ひ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0, exploreEditText.getText().length() - 1) + 'ぴ');
                                break;
                            case 'ふ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0, exploreEditText.getText().length() - 1) + 'ぷ');
                                break;
                            case 'へ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0, exploreEditText.getText().length() - 1) + 'ぺ');
                                break;
                            case 'ほ':
                                exploreEditText.setText(exploreEditText.getText().toString().substring(0, exploreEditText.getText().length() - 1) + 'ぽ');
                                break;
                        }
                    }
                } else {
                switch (arg0.getItemAtPosition(arg2).toString().charAt(0)) {
                    case 'ｘ':
                        if (exploreEditText.getText().length() > 0)
                            exploreEditText.setText(exploreEditText.getText().toString().substring(0,exploreEditText.getText().length()-1));
                        break;
                    case 'Ｘ':
                        if (exploreEditText.getText().length() > 0)
                            exploreEditText.setText("");
                        break;
                    default:
                        exploreEditText.setText(exploreEditText.getText().toString() + arg0.getItemAtPosition(arg2).toString());
                }
                }

                if (exploreEditText.getText().length() > 1) {
                    String exploreString = exploreEditText.getText().toString();
                    examinLandmark(exploreString);
                    //System.out.println("logcat:char="+arg0.getItemAtPosition(arg2).toString());
                } else
                    return;
            }
        }
        //20140429 end

    public void examinLandmark(String exploreString) {
		InputStream is = null;
		BufferedReader br = null;
		// String sample = "とっとりよしなりてん";


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
							longitude = Integer.valueOf(st.nextToken());
						} else if (i == 5) {
							latitude = Integer.valueOf(st.nextToken());
						} else if (i == 6) {
							frequency = Integer.valueOf(st.nextToken());

							if (hit == 1) {
								LandmarkModel landmarkModel = new LandmarkModel(
										id, name, yomi, longitude,
										latitude, frequency);

								if (landmarkArray.size() >= 0) {
									landmarkArray.add(landmarkModel);
								} else {
									int k = landmarkArray.size();
									for (int j = 0; j < k; j++) {

										if (landmarkModel.getFrequency() > landmarkArray
												.get(j).getFrequency()) {
											landmarkArray.add(j,
													landmarkModel);
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

			Toast toast = Toast.makeText(SpecifyPointActivity.this, "登録を行いたい場合は\nモードを切り替えて下さい", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.show();

			alert.setPositiveButton("モード切り替え", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog,int whichButton) {
		        	Toast toast = Toast.makeText(
							SpecifyPointActivity.this,
							"モードを切り替えました", Toast.LENGTH_SHORT);

					toast.setGravity(Gravity.TOP, 0, 0);
					toast.show();

					String tmp = "";
					if (flag == 0) {
						tmp = "登録";
						flag = 1;
					} else {
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
							if (flag == 1) {

								final int tmp = idx;

								if (data.equals("departure")) {
									Intent intent = new Intent(SpecifyPointActivity.this,
											ShowMapRegistrationActivity.class);
									intent.putExtra("keyword", landmarkArray.get(tmp).getName() + "&" + landmarkArray.get(tmp).getId() + "$" + landmarkArray.get(tmp).getLatitude() + "," + landmarkArray.get(tmp).getLongitude());

									startActivity(intent);

								} else {
									Intent intent = new Intent(SpecifyPointActivity.this,
											ShowMapRegistrationDestinationActivity.class);
									intent.putExtra("keyword", landmarkArray.get(tmp).getName() + "&" + landmarkArray.get(tmp).getId() + "$" + landmarkArray.get(tmp).getLatitude() + "," + landmarkArray.get(tmp).getLongitude());

									startActivity(intent);
								}

							} else {
								if (data.equals("departure")) {
									PointModel departureModel = new PointModel(landmarkArray.get(idx).getId(), landmarkArray.get(idx).getName(),
											landmarkArray.get(idx).getYomi(), landmarkArray.get(idx).getLongitude(), landmarkArray.get(idx).getLatitude(), landmarkArray.get(idx).getFrequency());

									BasicModel.setDeparture(departureModel);
								} else {
									PointModel destinationModel = new PointModel(landmarkArray.get(idx).getId(), landmarkArray.get(idx).getName(),
											landmarkArray.get(idx).getYomi(), landmarkArray.get(idx).getLongitude(), landmarkArray.get(idx).getLatitude(), landmarkArray.get(idx).getFrequency());

									BasicModel.setDestination(destinationModel);
								}

								dbAdapter.open();
								Cursor c = dbAdapter.getAllNotes();
								if (c.getCount() == 0) {
									dbAdapter.saveNote(landmarkArray.get(idx).getName(), String.valueOf(landmarkArray.get(idx).getId()));
								}
								startManagingCursor(c);
								if (c.moveToFirst()) {
									do {
										Note note = new Note(c.getInt(c
												.getColumnIndex(DBAdapter.COL_ID)), c.getString(c
														.getColumnIndex(DBAdapter.COL_NOTE)), c.getString(c
																.getColumnIndex(DBAdapter.COL_LASTUPDATE)));
										if (note.getNote().equals(landmarkArray.get(idx).getName())) {
											dbAdapter.deleteNote(note.getId());
										}
									} while (c.moveToNext());
									System.out.println("logcat:5:ok");
									dbAdapter.saveNote(landmarkArray.get(idx).getName(), String.valueOf(landmarkArray.get(idx).getId()));
								}
								stopManagingCursor(c);
								dbAdapter.close();

                                Common.setLocation(SpecifyPointActivity.this);
                                if (Common.CHKSETTING == true) finish();
                                else {
								Intent intent = new Intent(SpecifyPointActivity.this, RouteSearchActivity.class);
								intent.putExtra("keyword", "");
								startActivity(intent); }
							}
						}
					});

            //140522 add by wyq tablet mode
            if (exploreEditText.isEnabled() == false) {
                ListView listView = (ListView) findViewById(R.id.lstview_addr);
                //String[] str = {"aaaa","bbbbbb","cccccc","vvvvvvvv","ddd","ee"};
                listView.setAdapter(new ArrayAdapter<String>(SpecifyPointActivity.this, android.R.layout.simple_list_item_1, landmarkOfTheCandidate));
                //ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(SpecifyPointActivity.this,android.R.layout.simple_list_item_multiple_choice,landmarkOfTheCandidate);
                //listView.setAdapter(arrAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (id == -1) {
                            //headerView 或 footerView
                            return;
                        }

                        if (data.equals("departure")) {
                            PointModel departureModel = new PointModel(landmarkArray.get(position).getId(), landmarkArray.get(position).getName(),
                                    landmarkArray.get(position).getYomi(), landmarkArray.get(position).getLongitude(), landmarkArray.get(position).getLatitude(), landmarkArray.get(position).getFrequency());
                            BasicModel.setDeparture(departureModel);
                        } else {
                            PointModel destinationModel = new PointModel(landmarkArray.get(position).getId(), landmarkArray.get(position).getName(),
                                    landmarkArray.get(position).getYomi(), landmarkArray.get(position).getLongitude(), landmarkArray.get(position).getLatitude(), landmarkArray.get(position).getFrequency());
                            BasicModel.setDestination(destinationModel);
                        }

                        Common.setLocation(SpecifyPointActivity.this);
                        if (Common.CHKSETTING == true) finish();
                        else {
                        Intent intent = new Intent(SpecifyPointActivity.this, RouteSearchActivity.class);
                        intent.putExtra("keyword", "");
                        startActivity(intent); }
                        //Toast.makeText(SpecifyPointActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show();
                    }
                });
            } else
			    alert.show();
            //140522 end
		} else {
			Toast toast = Toast.makeText(
					SpecifyPointActivity.this,
					"該当するランドマークが存在しません．", Toast.LENGTH_LONG);

			toast.setGravity(Gravity.TOP, 0, 250);
			toast.show();
		}
	}

	public boolean examineLandmark(int actionId, KeyEvent event) {
		if (event == null && actionId == 6 || event != null
				&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			exploreEditText = (EditText) findViewById(R.id.specify_point_edittext);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String input = "";
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> candidates = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			if (candidates.size() > 0) {
				input = candidates.get(0);
				Toast.makeText(SpecifyPointActivity.this, input, Toast.LENGTH_LONG).show();
				examinLandmark(input);
			}
		}
	}


	@Override
	protected void onPause() {
		if (manager != null) {
			manager.removeUpdates(this);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
        super.onResume();
		System.out.println("logcat::onresume");
		if (manager != null) {
			System.out.println("logcat::null");
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);
		}
	}

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
	public void onLocationChanged(Location location) {

		currentLatitude = location.getLatitude();
		currentLongitude = location.getLongitude();

		setAround(currentLatitude, currentLongitude);
		//around = 1;
	}

	public void setAround(double currentLatitude, double currentLongitude) {
		double currentJapLongitude = currentLongitude;
		double currentJapLatitude = currentLatitude;

		currentJapLatitude = currentJapLatitude + currentJapLatitude * 0.00010696 - currentJapLongitude * 0.000017467 - 0.0046020;
		currentJapLongitude = currentJapLongitude + currentJapLatitude * 0.000046047 + currentJapLongitude * 0.000083049 - 0.010041;

		InputStream is = null;
		BufferedReader br = null;

		final ArrayList<LandmarkModel> aroundLandmarkArray = new ArrayList<LandmarkModel>();
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
						} else if (i == 3) {
							yomi = st.nextToken();
						} else if (i == 4) {
							longitude = Integer.valueOf(st.nextToken());
						} else if (i == 5) {
							latitude = Integer.valueOf(st.nextToken());
						} else if (i == 6) {
							frequency = Integer.valueOf(st.nextToken());

							LandmarkModel aroundLandmarkModel = new LandmarkModel(
									id, name, yomi, longitude,
									latitude, frequency);

							if (aroundLandmarkArray.size() == 0) {
								System.out.println("logcat::dame");
								aroundLandmarkArray.add(aroundLandmarkModel);
							} else {
								int k = aroundLandmarkArray.size();


								for (int j = 0; j < k && j < 5; j++) {
									if (aroundLandmarkModel.getFrequency() > 10) {
										float[] preResult = new float[3];
										Location.distanceBetween((double)aroundLandmarkArray.get(j).getLatitude() / 1E6 / 0.36, (double)aroundLandmarkArray.get(j).getLongitude() / 1E6 / 0.36,
												currentJapLatitude, currentJapLongitude, preResult);

										float[] currentResult = new float[3];
										Location.distanceBetween((double)aroundLandmarkModel.getLatitude() / 1E6 / 0.36, (double)aroundLandmarkModel.getLongitude() / 1E6 / 0.36,
												currentJapLatitude, currentJapLongitude, currentResult);


										if (currentResult[0] < preResult[0]) {

											aroundLandmarkArray.add(j,aroundLandmarkModel);
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
				around = 1;

			} finally {

				if (br != null) {
					br.close();
				}
			}
		} catch (IOException e) {

		}

//		System.out.println("logcat::" + aroundLandmarkArray.get(0).getName());
//		System.out.println("logcat::" + aroundLandmarkArray.get(1).getName());
//		System.out.println("logcat::" + aroundLandmarkArray.get(2).getName());
//		System.out.println("logcat::" + aroundLandmarkArray.get(3).getName());
//		System.out.println("logcat::" + aroundLandmarkArray.get(4).getName());

		landmarkArray.clear();
		landmarkArray = aroundLandmarkArray;

//		String landmarkOfTheCandidate[] = new String[aroundLandmarkArray.size()];
//		for (int i = 0; i < aroundLandmarkArray.size(); i++) {
//			landmarkOfTheCandidate[i] = aroundLandmarkArray.get(i).getName();
//		}

//		if (aroundLandmarkArray.size() != 0) {
//			AlertDialog.Builder alert = new AlertDialog.Builder(this);
//			alert.setTitle("候補から選択してください．");
//			alert.setItems(landmarkOfTheCandidate,
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int idx) {
//							if (data.equals("departure")) {
//								PointModel departureModel = new PointModel(aroundLandmarkArray.get(idx).getId(), aroundLandmarkArray.get(idx).getName(),
//										aroundLandmarkArray.get(idx).getYomi(), aroundLandmarkArray.get(idx).getLongitude(), aroundLandmarkArray.get(idx).getLatitude(), aroundLandmarkArray.get(idx).getFrequency());
//
//								BasicModel.setDeparture(departureModel);
//							} else {
//								PointModel destinationModel = new PointModel(aroundLandmarkArray.get(idx).getId(), aroundLandmarkArray.get(idx).getName(),
//										aroundLandmarkArray.get(idx).getYomi(), aroundLandmarkArray.get(idx).getLongitude(), aroundLandmarkArray.get(idx).getLatitude(), aroundLandmarkArray.get(idx).getFrequency());
//
//								BasicModel.setDestination(destinationModel);
//							}
//
//							Intent intent = new Intent(SpecifyPointActivity.this, RouteSearchActivity.class);
//							startActivity(intent);
//						}
//					});
//			alert.show();
//		}

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
}