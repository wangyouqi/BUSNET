package jp.ikisaki.www;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.*;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;

public class TimetableSearchActivity extends Activity{
	public static ProgressDialog waitDialog;
	public static ProgressDialog progressDialog;
	private Thread thread;
	/** Called when the activity is first created. */
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
		setContentView(R.layout.timetable_search);
		waitDialog = new ProgressDialog(this);

		progressDialog = new ProgressDialog(this);
		new TimetableModel();

		final Button destinationHistoryOrRegistrationButton = (Button)findViewById(R.id.destination_history_or_registration_button);

		final Button alightingBusStopButton = (Button) findViewById(R.id.alighting_bus_stop_button);
		alightingBusStopButton.setEnabled(false);
		destinationHistoryOrRegistrationButton.setEnabled(false);

		System.out.println("logcat:start");
		String data = "";
		if (!TimetableModel.getStartName().equals("") && TimetableModel.getDestinationName().equals("")) {
			data = "keyword";
		}
		System.out.println("logcat:" + data);

		Button homeButton = (Button) findViewById(R.id.home_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimetableSearchActivity.this,
						BusnetActivity.class);
				startActivity(intent);
			}
		});

		Button resetButton = (Button) findViewById(R.id.reset_button);
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TimetableModel.resetTimetableModel();
				System.out.println("logcat:resetmodel");

				Button rideBusStopButton = (Button) findViewById(R.id.ride_bus_stop_button);
				Button alightingBusStopButton = (Button) findViewById(R.id.alighting_bus_stop_button);

				rideBusStopButton.setText("バス停(駅)を入力");
				alightingBusStopButton.setText("バス停(駅)を入力");
				alightingBusStopButton.setEnabled(false);
				destinationHistoryOrRegistrationButton.setEnabled(false);
			}
		});

		Button swapButton = (Button) findViewById(R.id.swap_button);
		swapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TimetableModel.getDestinationName().equals("")){
					if(TimetableModel.getStartName().equals("")){
						Toast toast = Toast.makeText(
								TimetableSearchActivity.this,
								"乗車バス停、下車バス停を指定してください。", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					else{
						Toast toast = Toast.makeText(
								TimetableSearchActivity.this,
								"下車バス停を指定してください。", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				}
				else if(TimetableModel.getStartName().equals("")){
					Toast toast = Toast.makeText(
							TimetableSearchActivity.this,
							"乗車バス停を指定してください。", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				else{
					int id = 0;
					String name = "";

					id = TimetableModel.getStartId();
					name = TimetableModel.getStartName();

					TimetableModel.setStartId(TimetableModel.getDestinationId());
					TimetableModel.setStartName(TimetableModel.getDestinationName());

					TimetableModel.setDestinationId(id);
					TimetableModel.setDestinationName(name);

					Button rideBusStopButton = (Button) findViewById(R.id.ride_bus_stop_button);
					Button alightingBusStopButton = (Button) findViewById(R.id.alighting_bus_stop_button);
					rideBusStopButton.setText(TimetableModel.getStartName());
					alightingBusStopButton.setText(TimetableModel.getDestinationName());
				}
			}
		});

		Button selectBusButton = (Button) findViewById(R.id.select_bus_button);
		selectBusButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimetableSearchActivity.this,
						SelectBusActivity.class);
				startActivity(intent);
			}
		});

		Button rideBusStopButton = (Button) findViewById(R.id.ride_bus_stop_button);
		//Button alightingBusStopButton = (Button) findViewById(R.id.alighting_bus_stop_button);
		if (!TimetableModel.getStartName().equals("")) {
			rideBusStopButton.setText(TimetableModel.getStartName());
			rideBusStopButton.setEnabled(true);
		}

		Button departureHistoryOrRegistrationButton = (Button)findViewById(R.id.departure_history_or_registration_button);




		final Handler handler = new Handler();
		if (data.equals("keyword")) {
			System.out.println("logcat:handler");
			new Thread(new Runnable() {
				@Override
				public void run() {
					handler.post(new Runnable() {

						@Override
						public void run() {
							alightingBusStopButton.setText("お待ちください");
							alightingBusStopButton.setEnabled(false);
							destinationHistoryOrRegistrationButton.setEnabled(false);
						}
					});
					loadBusstops();
					handler.post(new Runnable() {
						@Override
						public void run() {
							alightingBusStopButton.setText("バス停もしくは駅を入力");
							alightingBusStopButton.setEnabled(true);
							destinationHistoryOrRegistrationButton.setEnabled(true);
							System.out.println("logcat:5:ans:" + TimetableModel.getBusstopsModel().size());
						}
					});
				}
			}).start();
		}

		rideBusStopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimetableSearchActivity.this,
						SelectARideBusStopActivity.class);
				startActivity(intent);
			}
		});

		// alightingBusStopButton.setEnabled(false);
		if(!TimetableModel.getDestinationName().equals("")){
			alightingBusStopButton.setText(TimetableModel.getDestinationName());
			alightingBusStopButton.setEnabled(true);
			destinationHistoryOrRegistrationButton.setEnabled(true);
		}
		alightingBusStopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimetableSearchActivity.this,
						SelectAnAlightingBusStopActivity.class);
				startActivity(intent);
			}
		});


		Button timetableSearchButton = (Button) findViewById(R.id.timetable_search_button);
		timetableSearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TimetableModel.getDestinationName().equals("")){
					if(TimetableModel.getStartName().equals("")){
						Toast toast = Toast.makeText(
								TimetableSearchActivity.this,
								"乗車バス停、下車バス停を指定してください。", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					else{
						Toast toast = Toast.makeText(
								TimetableSearchActivity.this,
								"下車バス停を指定してください。", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				}
				else if(TimetableModel.getStartName().equals("")){
					Toast toast = Toast.makeText(
							TimetableSearchActivity.this,
							"乗車バス停を指定してください。", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				else{
//					new Thread(new Runnable() {
//						@Override
//						public void run() {
//							handler.post(new Runnable() {
//								@Override
//								public void run() {
//									System.out.println("logcat:parun1");
//								}
//							});
//							 waitDialog.setMessage("時刻表検索中");
//							    // 円スタイル（くるくる回るタイプ）に設定します
//							 waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//							    // プログレスダイアログを表示
//							 waitDialog.show();
//							handler.post(new Runnable() {
//								@Override
//								public void run() {
//									System.out.println("logcat:parunfin1");
//								}});
//						}
//					}).start();


					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setMessage("時刻表検索中");
					progressDialog.setCancelable(true);
					progressDialog.show();

					Intent intent = new Intent(TimetableSearchActivity.this,
							ResultOfTimetableSearchActivity.class);
					//start();
					startActivity(intent);
				}
			}
		});


		departureHistoryOrRegistrationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimetableSearchActivity.this, HistoryOrRegistrationActivity.class);
				intent.putExtra("keyword", "timetableDeparture");
				startActivity(intent);
			}
		});

		destinationHistoryOrRegistrationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TimetableSearchActivity.this, HistoryOrRegistrationActivity.class);
				intent.putExtra("keyword", "timetableDestination");
				startActivity(intent);
			}
		});


	}

	private void loadBusstops() {
		try {
			XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance()
					.newPullParser();
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.permitAll().build());
			URL url;

			url = new URL(
					//"http://www.ikisaki.jp/index.cgi?device=xml&page=get_off_busstop_search&s_id=870&keyword=&date=6,24&timetype=last&perpage=0");
					"http://bus.ike.tottori-u.ac.jp/bus/index.cgi?device=xml&page=get_off_busstop_search&s_id=" + TimetableModel.getStartId() + "&keyword=&timetype=last&perpage=0");

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			connection.setRequestMethod("GET");
			xmlPullParser.setInput(connection.getInputStream(), "UTF-8");
			ArrayList<BusstopsModel> busstopsArrayList = new ArrayList<BusstopsModel>();

			int eventType;
			int counter = 0;
			while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
				String str = "";

				if (eventType == XmlPullParser.START_TAG
						&& "Landmark".equals(xmlPullParser.getName())) {
					BusstopsModel busstopsModel = new BusstopsModel();
					int busstopsEventType = xmlPullParser.next();
					while (!(busstopsEventType == XmlPullParser.END_TAG && "Landmark"
							.equals(xmlPullParser.getName()))) {
						if (eventType == XmlPullParser.START_TAG
								&& "Name".equals(xmlPullParser.getName())) {
							str = xmlPullParser.nextText();
							busstopsModel.setName(str);
						} else if (eventType == XmlPullParser.START_TAG
								&& "Id".equals(xmlPullParser.getName())) {
							str = xmlPullParser.nextText();
							busstopsModel.setId(Integer.valueOf(str));
						} else if (eventType == XmlPullParser.START_TAG
								&& "Yomi".equals(xmlPullParser.getName())) {
							str = xmlPullParser.nextText();
							busstopsModel.setYomi(str);
						}
						busstopsEventType = xmlPullParser.next();
					}
					busstopsArrayList.add(counter++, busstopsModel);
				}

			}
			TimetableModel.setBusstopsModel(busstopsArrayList);
		} catch (Exception e) {
			System.out.println("logcat:(errore)→" + e);
		}

	}

//	private void start(){
//	    // プログレスダイアログを開く処理を呼び出す。
//	    setWait();
//	}
//
//	private void setWait(){
//	    // プログレスダイアログの設定
//	    waitDialog = new ProgressDialog(this);
//	    // プログレスダイアログのメッセージを設定します
//	    waitDialog.setMessage("時刻表検索中");
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
//	}
//
//	@Override
//	public void run() {
//	    try {
//	        Thread.sleep(3000);
//	    } catch (InterruptedException e) {
//
//	    }
//	    handler.sendEmptyMessage(0);
//	}

//	private Handler handler = new Handler() {
//	    public void handleMessage(Message msg){
//
//	        waitDialog.dismiss();
//	        waitDialog = null;
//	       // loading.dismiss();
//	    }
//	};

	@Override
	protected void onPause(){

		super.onPause();
		System.out.println("logcat:pause");
	}

}