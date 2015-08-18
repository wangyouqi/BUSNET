package jp.ikisaki.www;

import java.io.*;
import java.util.*;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.*;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

public class RouteSearchActivity extends Activity implements Runnable {
	public static ProgressDialog waitDialog;
    public static RegistrationDBAdapter dbregnote;
	private String uriStr = "";
    private static float siz = 0;

	static boolean isInteger(String num) {
		try{
			int n = Integer.parseInt(num);
			return true;
		}catch(NumberFormatException e) {
			return false;
		}
	}

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_search);
		waitDialog = new ProgressDialog(this);
	    Intent intent = getIntent();
	    uriStr = intent.getStringExtra("keyword");

        //140410 add by wyq 方法1 Android screen width & height
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        final int screenWidth = display.getWidth();
        final int screenHeight = display.getHeight();

        dbregnote = new RegistrationDBAdapter(this);
        Button timeAndRouteButto = (Button) findViewById(R.id.time_and_route_button);
        siz = timeAndRouteButto.getTextSize();
        //140410 end

        Button runButton = (Button) findViewById(R.id.run_button);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (BasicModel.getDeparture().getName().equals("") || BasicModel.getDestination().getName().equals("")) {
                    String toastString = "";

                    if (BasicModel.getDeparture().getName().equals("")) {
                        if (BasicModel.getDestination().getName().equals("")) {
                            toastString = "出発地，目的地を入力して下さい．";
                        } else {
                            toastString = "出発地を入力して下さい。";
                        }
                    } else {
                        toastString = "目的地を入力して下さい。";
                    }

                    Toast toast = Toast.makeText(RouteSearchActivity.this, toastString, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else if (BasicModel.getDeparture().getName().equals(BasicModel.getDestination().getName()) ) {
                    String toastString = "";
                    toastString = "出発地と目的地が同じです。";

                    Toast toast = Toast.makeText(RouteSearchActivity.this, toastString, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    waitDialog.setMessage("経路探索中");
                    waitDialog.setCancelable(true);
                    waitDialog.show();

                    Toast toast = Toast.makeText(RouteSearchActivity.this, "SNSを使って\n経路を共有することができます", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();

                    //140522 add by wyq save address log
                    dbregnote.open();
                    Cursor c = null;

                    if (BasicModel.getDestination().getId() > 0)
                        c = dbregnote.getSpecNotes(Integer.toString(BasicModel.getDestination().getId()) );
                    else if (BasicModel.getDestination().getId() == 0)
                        c = dbregnote.getSpecNotes(BasicModel.getDestination().getLongitude() + "," + BasicModel.getDestination().getLatitude());

                    if (c != null && c.getCount() == 0) {
                        dbregnote.saveNote(BasicModel.getDestination().getName(), Integer.toString(BasicModel.getDestination().getId()));
                    }
                    startManagingCursor(c);
                    if (c != null && c.moveToFirst()) {
                        do {
                            Note note = new Note(
                                    c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL_ID)),
                                    c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_NOTE)),
                                    c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_LASTUPDATE)) );
                            if (note.getNote().equals(BasicModel.getDestination().getName()) ) {
                                dbregnote.countNote(note.getId());
                                //dbregnote.deleteNote(note.getId());
                            }
                        } while (c.moveToNext());
                        System.out.println("logcat:5:ok");
                        //dbregnote.saveNote(myButton.getText().toString(),myButton.getTag().toString());
                    }
                    stopManagingCursor(c);
                    dbregnote.close();
                    //140522 end

                    Intent intent = new Intent(RouteSearchActivity.this, ResultOfRouteSearchActivity.class);
                    //start();
                    intent.putExtra("keyword", 0);
                    startActivity(intent);
                    System.out.println("logcat:runbuttonstart");
                }
            }
        });

	    if (uriStr.equals(""))
            System.out.println("logcat:2:start:" + uriStr);
        else if (uriStr.equals("AUTORUN")) {
            //Button runButton = (Button) findViewById(R.id.run_button);
            setFinishOnTouchOutside(false);
            runButton.performClick();
            finish();
        } else {
	    	System.out.println("logcat:2:start:" + uriStr);

//	    	if (BasicModel.getTwitterFlag() == 3) {
//	    		System.out.println("logcat:2:twitter1");
//	    		BasicModel.setTwitterFlag(1);
//	    		finish();
//	    	}

	    	String date = "";
	    	String hour = "";
	    	String minute = "";
	    	String destination = "";
	    	String month = "";
	    	String day = "";
	    	String destinationName = "";


	    	int indexOfHour = uriStr.indexOf("hour");
	    	hour = uriStr.substring(indexOfHour + 5, uriStr.indexOf("&min"));
	    	System.out.println("logcat:2:hour= " + hour);

	    	int indexOfMinute = uriStr.indexOf("min");
	    	minute = uriStr.substring(indexOfMinute + 4, uriStr.indexOf("&page"));
	    	System.out.println("logcat:2:minute= " + minute);

	    	int indexOfDestination = uriStr.indexOf("d_id");
	    	destination = uriStr.substring(indexOfDestination + 5, uriStr.indexOf("&per"));
	    	System.out.println("logcat:2:destination= " + destination);

	    	//ここの前後でエラー
	    	int indexOfDate = uriStr.indexOf("date");
	    	//

	    	if (uriStr.indexOf("&preHour") != -1) {

	    		System.out.println("logcat:uriStr:" + uriStr);

	    		if (uriStr.indexOf("&preHour") < indexOfDate + 5) {
	    			System.out.println("logcat:こっち1");

	    			if (uriStr.indexOf("&dir=backward&date") != -1) {
	    				uriStr = uriStr.replaceAll("&dir=backward&date", "&date");
	    				indexOfDate = uriStr.indexOf("date");
	    			}

	    			date = uriStr.substring(indexOfDate + 5, uriStr.indexOf("&dir"));
	    			System.out.println("logcat:2:date= " + date);
	    		} else {
	    			System.out.println("logcat:こっち2");
	    			date = uriStr.substring(indexOfDate + 5, uriStr.indexOf("&preHour"));
	    			System.out.println("logcat:2:date= " + date);
	    		}
	    	} else {
	    		System.out.println("logcat:こっち3");
	    		date = uriStr.substring(indexOfDate + 5, uriStr.indexOf("&dir"));
	    		System.out.println("logcat:2:date= " + date);

	    	}

	    	String[] dateAry = date.split(",");
	    	month = dateAry[0];
	    	day = dateAry[1];

	    	BasicModel.resetModel();

	    	Button departurePointButton = (Button) findViewById(R.id.departure_point_button);
			Button destinationPointButton = (Button) findViewById(R.id.destination_point_button);
			Button timeAndRouteButton = (Button) findViewById(R.id.time_and_route_button);

			if (isInteger(destination) == true) {

				InputStream is = null;
				BufferedReader br = null;

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
									if (Integer.valueOf(destination) == id) {
										hit = 1;
									}
								} else if (i == 2) {
									name = st.nextToken();
								} else if (i == 3) {
									yomi = st.nextToken();
								}else if (i == 4) {
									longitude = Integer.valueOf(st.nextToken());
								} else if (i == 5) {
									latitude = Integer.valueOf(st.nextToken());
								} else if (i == 6) {
									frequency = Integer.valueOf(st.nextToken());

									if (hit == 1) {
										PointModel destinationModel = new PointModel(id, name, yomi, longitude, latitude, frequency);
										BasicModel.setDestination(destinationModel);
										destinationName = name;
										break;
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
			} else {
				BasicModel.resetDestination();

				String[] destinationGmapdAry = destination.split(":");
		    	int gmapdLongitude = Integer.valueOf(destinationGmapdAry[1]);
		    	int gmapdLatitude = Integer.valueOf(destinationGmapdAry[2]);

				PointModel destinationPointModel = new PointModel(0, getResources().getString(R.string.get_your_destination),
                        "", gmapdLatitude, gmapdLongitude, 0);
				destinationName = getResources().getString(R.string.get_your_destination);
				BasicModel.setDestination(destinationPointModel);
			}

			departurePointButton.setText("必須");

			String place = destinationName;
			float size = destinationPointButton.getTextSize();
			System.out.println("logcat:10:size=" + size);
			if (place.indexOf("(") != -1 && screenWidth < 1111) {
				int newLine = place.indexOf("(");
				destinationPointButton.setText(place.substring(0, newLine) + "\n" + place.substring(newLine));
				destinationPointButton.setTextSize(size / 2);
				destinationPointButton.setGravity(Gravity.CENTER);
			} else {
				destinationPointButton.setText(destinationName);
				if (place.length() > 9 && screenWidth < 1111) {
					destinationPointButton.setTextSize(size / 2);
				}
			}


			if (minute.equals("0")) {
				timeAndRouteButton.setText(month + "月" + day + "日" + hour + "時" + "00" + "分までに到着");
				BasicModel.setSetting(month + "月" + day + "日" + hour + "時" + "00" + "分までに到着");
			} else {
				timeAndRouteButton.setText(month + "月" + day + "日" + hour + "時" + minute + "分までに到着");
				BasicModel.setSetting(month + "月" + day + "日" + hour + "時" + minute + "分までに到着");
			}

			BasicModel.setDay(Integer.valueOf(day));
			BasicModel.setMonth(Integer.valueOf(month));
			BasicModel.setHour(Integer.valueOf(hour));
			BasicModel.setMinute(Integer.valueOf(minute));
			BasicModel.setTabNumber(2);
			BasicModel.setForwardOrBackward("&dir=backward");
			BasicModel.setTime("&hour=" + Integer.valueOf(hour) + "&min=" + Integer.valueOf(minute));

			System.out.println("logcat:2:setpara1");
			BasicModel.setParameter(1);

	    }

	      //uriStr = "http://bus.ike.tottori-u.ac.jp/bus/index.cgi?device=xml&page=r" + uriStr.substring(index + 1);

		Button departurePointButton = (Button) findViewById(R.id.departure_point_button);
		Button destinationPointButton = (Button) findViewById(R.id.destination_point_button);
		Button timeAndRouteButton = (Button) findViewById(R.id.time_and_route_button);
		if (BasicModel.getSetting() != "") {
			timeAndRouteButton.setText(BasicModel.getSetting());
		}

		Button resetButton = (Button) findViewById(R.id.reset_button);
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//BasicModel.resetModel();
                BasicModel.resetDestination();
				Button departurePointButton = (Button) findViewById(R.id.departure_point_button);
				Button destinationPointButton = (Button) findViewById(R.id.destination_point_button);
				Button timeAndRouteButton = (Button) findViewById(R.id.time_and_route_button);

				//departurePointButton.setText("未指定");
				destinationPointButton.setText(getResources().getString(R.string.unspecified));
				timeAndRouteButton.setText(getResources().getString(R.string.now));
				destinationPointButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, siz);
			}
		});

		Button homeButton = (Button) findViewById(R.id.home_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RouteSearchActivity.this,
						BusnetActivity.class);
				startActivity(intent);
			}
		});

        //record and swap
        Button recordButton = (Button) findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastString = "";
                if (BasicModel.getDeparture().getName().equals("") || BasicModel.getDestination().getName().equals("")) {

                    if (BasicModel.getDeparture().getName().equals("")) {
                        if (BasicModel.getDestination().getName().equals("")) {
                            toastString = "出発地，目的地を入力して下さい．";
                        } else {
                            toastString = "出発地を入力して下さい．";
                        }
                    } else {
                        toastString = "目的地を入力して下さい．";
                    }

                    Toast toast = Toast.makeText(RouteSearchActivity.this, toastString, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    final EditText inputtext = new EditText(RouteSearchActivity.this);
                    //inputtext.setFocusable(true);
                    inputtext.setFocusableInTouchMode(true);
                    inputtext.setSingleLine(true);
                    inputtext.setText((String)(BasicModel.getDeparture().getName() + "——" + BasicModel.getDestination().getName())
                                    .replaceAll("[\\t\\n\\r]", "")); //regex 削除

                    AlertDialog.Builder builder = new AlertDialog.Builder(RouteSearchActivity.this);
                    builder.setTitle("名前を入力して下さい．")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(inputtext)
                            .setNegativeButton(getResources().getString(R.string.cancel), null)
                            .setPositiveButton(getResources().getString(R.string.decision), //.setNeutralButton
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String inputname = inputtext.getText().toString();
                                            if (!inputname.equals("")) {
                                                dbregnote.open();
                                                dbregnote.saveMark(inputname, BasicModel.getDeparture(), BasicModel.getDestination());
                                                dbregnote.close();
                                                Toast.makeText(RouteSearchActivity.this, "記録を追加しました．", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                            });
                    builder.show();
                  }
            }
        });

		Button swapButton = (Button) findViewById(R.id.swap_button);
		swapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                String toastString = "";
				if (BasicModel.getDeparture().getName().equals("") || BasicModel.getDestination().getName().equals("")) {

					if (BasicModel.getDeparture().getName().equals("")) {
						if (BasicModel.getDestination().getName().equals("")) {
							toastString = "出発地，目的地を入力して下さい．";
						} else {
							toastString = "出発地を入力して下さい．";
						}
					} else {
						toastString = "目的地を入力して下さい．";
					}

					Toast toast = Toast.makeText(RouteSearchActivity.this, toastString, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					int id = 0;
					String name = "";
					String yomi = "";
					int longitude = 0;
					int latitude = 0;
					int frequency = 0;

					id = BasicModel.getDeparture().getId();
					name = BasicModel.getDeparture().getName();
					yomi = BasicModel.getDeparture().getYomi();
					longitude = BasicModel.getDeparture().getLongitude();
					latitude = BasicModel.getDeparture().getLatitude();
					frequency = BasicModel.getDeparture().getFrequency();

					BasicModel.getDeparture().setId(BasicModel.getDestination().getId());
					BasicModel.getDeparture().setName(BasicModel.getDestination().getName());
					BasicModel.getDeparture().setYomi(BasicModel.getDestination().getYomi());
					BasicModel.getDeparture().setLongitude(BasicModel.getDestination().getLongitude());
					BasicModel.getDeparture().setLatitude(BasicModel.getDestination().getLatitude());
					BasicModel.getDeparture().setFrequency(BasicModel.getDestination().getFrequency());

					BasicModel.getDestination().setId(id);
					BasicModel.getDestination().setName(name);
					BasicModel.getDestination().setYomi(yomi);
					BasicModel.getDestination().setLongitude(longitude);
					BasicModel.getDestination().setLatitude(latitude);
					BasicModel.getDestination().setFrequency(frequency);

					Button departurePointButton = (Button) findViewById(R.id.departure_point_button);
					Button destinationPointButton = (Button) findViewById(R.id.destination_point_button);

					String place = BasicModel.getDeparture().getName();
					if (place.indexOf("(") != -1 && screenWidth < 1111) {
						int newLine = place.indexOf("(");
						departurePointButton.setText(place.substring(0, newLine) + "\n" + place.substring(newLine));
						departurePointButton.setTextSize(siz / 2);
						departurePointButton.setGravity(Gravity.CENTER);
					} else {
						departurePointButton.setText(BasicModel.getDeparture().getName());
						if (BasicModel.getDeparture().getName().length() > 9 && screenWidth < 1111) {
							departurePointButton.setTextSize(siz / 2);
						}
					}

					String place2 = BasicModel.getDestination().getName();
					if (place2.indexOf("(") != -1 && screenWidth < 1111) {
						int newLine = place2.indexOf("(");
						destinationPointButton.setText(place2.substring(0, newLine) + "\n" + place2.substring(newLine));
						destinationPointButton.setTextSize(siz / 2);
						destinationPointButton.setGravity(Gravity.CENTER);
					} else {
						destinationPointButton.setText(BasicModel.getDestination().getName());
						if (BasicModel.getDestination().getName().length() > 9 && screenWidth < 1111) {
							destinationPointButton.setTextSize(siz / 2);
						}
					}
				}
			}
		});

		PointModel departure = new PointModel();
		departure = BasicModel.getDeparture();
		if (departure.getName() != "") {
			String place = departure.getName();
			float size = departurePointButton.getTextSize();
			if (place.indexOf("(") != -1 && screenWidth < 1111) {
				int newLine = place.indexOf("(");
				departurePointButton.setText(place.substring(0, newLine) + "\n" + place.substring(newLine));
				departurePointButton.setTextSize(size / 2);
				departurePointButton.setGravity(Gravity.CENTER);
			} else {
				departurePointButton.setText(BasicModel.getDeparture().getName());
				if (BasicModel.getDeparture().getName().length() > 9 && screenWidth < 1111) {
					departurePointButton.setTextSize(size / 2);
				}
			}

		}

		departurePointButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RouteSearchActivity.this,
						SpecifyPointActivity.class);
				intent.putExtra("keyword", "departure");
				startActivity(intent);
			}
		});

        System.out.println("logcat:55555555555:size=" + screenWidth);
        //取得目標アドレス
		PointModel destination = new PointModel();
		destination = BasicModel.getDestination();
		if (destination.getName() != "") {
			String place2 = destination.getName();
			float size2 = destinationPointButton.getTextSize();
			if (place2.indexOf("(") != -1 && screenWidth < 1111) {
				int newLine = place2.indexOf("(");
				destinationPointButton.setText(place2.substring(0, newLine) + "\n" + place2.substring(newLine));
				destinationPointButton.setTextSize(size2 / 2);
				destinationPointButton.setGravity(Gravity.CENTER);
			} else {
				destinationPointButton.setText(destination.getName());
				if (destination.getName().length() > 9 && screenWidth < 1111) {
					destinationPointButton.setTextSize(size2 / 2);
				}
			}

			//destinationPointButton.setText(destination.getName());
		}

		destinationPointButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RouteSearchActivity.this,
						SpecifyPointActivity.class);
				intent.putExtra("keyword", "destination");
				startActivity(intent);
			}
		});


		timeAndRouteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RouteSearchActivity.this,
						SetTimeAndRouteActivity.class);
				startActivity(intent);
			}
		});

//20140519 mod by wyq move to head
/*		Button runButton = (Button) findViewById(R.id.run_button);
		runButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (BasicModel.getDeparture().getName().equals("") || BasicModel.getDestination().getName().equals("")) {
					String toastString = "";

					if (BasicModel.getDeparture().getName().equals("")) {
						if (BasicModel.getDestination().getName().equals("")) {
							toastString = "出発地，目的地を入力して下さい．";
						} else {
							toastString = "出発地を入力して下さい。";
						}

					} else {
						toastString = "目的地を入力して下さい。";
					}

					Toast toast = Toast.makeText(RouteSearchActivity.this, toastString, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				else if (BasicModel.getDeparture().getName().equals(BasicModel.getDestination().getName())) {
					String toastString = "";

					toastString = "出発地と目的地が同じです。";

					Toast toast = Toast.makeText(RouteSearchActivity.this, toastString, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					waitDialog.setMessage("経路探索中");
					waitDialog.setCancelable(true);
					waitDialog.show();

					Toast toast = Toast.makeText(RouteSearchActivity.this, "SNSを使って\n経路を共有することができます", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.BOTTOM, 0, 0);
					toast.show();

					Intent intent = new Intent(RouteSearchActivity.this,
							ResultOfRouteSearchActivity.class);

					//start();
					intent.putExtra("keyword", 0);
					startActivity(intent);
					System.out.println("logcat:runbuttonstart");
				}
			}
		});*/

		Button departureHistoryOrRegistrationButton = (Button)findViewById(R.id.departure_history_or_registration_button);
		Button destinationHistoryOrRegistrationButton = (Button)findViewById(R.id.destination_history_or_registration_button);

		departureHistoryOrRegistrationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RouteSearchActivity.this, HistoryOrRegistrationActivity.class);
				intent.putExtra("keyword", "departure");
				startActivity(intent);
			}
		});

		destinationHistoryOrRegistrationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                //System.out.println("logcat:1111:size=" + screenHeight);
				Intent intent = new Intent(RouteSearchActivity.this, HistoryOrRegistrationActivity.class);
				intent.putExtra("keyword", "destination");
				startActivity(intent);
			}
		});
	}

//	private void start() {
//	    // プログレスダイアログを開く処理を呼び出す。
//	    setWait();
//	}
//
//	private void setWait() {
//	    // プログレスダイアログの設定
//	    waitDialog = new ProgressDialog(this);
//	    // プログレスダイアログのメッセージを設定します
//	    waitDialog.setMessage("経路探索中");
//	    // 円スタイル（くるくる回るタイプ）に設定します
//	    waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//	    // プログレスダイアログを表示
//	    waitDialog.show();
//
//	    thread = new Thread(this);
//	    /* show()メソッドでプログレスダイアログを表示しつつ、
//	     * 別スレッドを使い、裏で重い処理を行う。
//	     */
//	    thread.start();
//
//	}

	@Override
	public void run() {
	    try {
	        Thread.sleep(3000);
	    } catch (InterruptedException e) {
	    	System.out.println("logcat:6:error:" + e);
	    }
	    handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
	    public void handleMessage(Message msg) {

	        waitDialog.dismiss();
	        waitDialog = null;
	       // loading.dismiss();
	    }
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();

			if (!uriStr.equals("")) {
				finish();
			}
			return true;
		}
		return false;
	}

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
