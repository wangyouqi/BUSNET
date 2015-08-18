package jp.ikisaki.www;

import java.io.*;
import java.util.UUID;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.net.http.SslError;
import android.os.*;
import android.preference.PreferenceManager;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class BusnetActivity extends Activity {

	public static final int PREFERENCE_INIT = 0;
	public static final int PREFERENCE_BOOTED = 1;
	public static final int MENU_SELECT_CLEAR = 0;

	private WindowManager wm;
	private Display disp;
	private int width;
	private int height;

	//データ保存
	private void setState(int state) {
	    // SharedPreferences設定を保存
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	    sp.edit().putInt("InitState", state).commit();

	    //ログ表示
	    output( String.valueOf(state) );
	}

	//データ読み出し
	private int getState() {
	    // 読み込み
	    int state;
	    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
	    state = sp.getInt("InitState", PREFERENCE_INIT);

	    //ログ表示
	    output( String.valueOf(state) );
	    return state;
	}

	//データ表示
	private void output(String string){
	    System.out.println("logcat:busnet:Preference: InitState is " + string);

	}

	//ダイアログ表示
	@Override
	public void onResume(){
        super.onResume();
	    //super.onPause();

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(BusnetActivity.this);
	    // ダイアログの設定
	    //alertDialog.setTitle("初回アンケート");          //タイトル
	    alertDialog.setMessage("「初回アンケート」が表示されるまでしばらくお待ちください");      //内容
	    alertDialog.setIcon(R.drawable.icon);   //アイコン設定

	    wm = (WindowManager)getSystemService(WINDOW_SERVICE);
		disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();

	    WebView wv = new WebView(BusnetActivity.this);
//	    wv.setWebViewClient(new CustomWebViewClient());
//	    wv.setWebChromeClient(new WebChromeClient());
        wv.getSettings().setJavaScriptEnabled(true);
//        wv.loadUrl("http://d.hatena.ne.jp/ats337/20110329/1301418778");
//        wv.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view,
//                    String url) {
//                view.loadUrl(url);
//
//                return true;
//            }
//        });

        wv.setWebViewClient(new WebViewClient(){
			public void onReceivedSslError(WebView view, SslErrorHandler handler,
		            SslError error) {
		        handler.proceed();
		    }
		});
        wv.loadUrl("https://docs.google.com/forms/d/176zKKUVFq2e17h3VdRIa9s5w4IrVhW1aTY28fQ_LHtM/viewform");

        Dialog d = alertDialog.setView(wv).create();
        //d.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes(lp);

	    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

	        public void onClick(DialogInterface dialog, int which) {
	            //初回表示完了
	            setState(PREFERENCE_BOOTED);
	        }
	    });

	    // ダイアログの作成と表示
	    if(PREFERENCE_INIT == getState() ) {
	        //初回起動時のみ表示する
	        alertDialog.create();
	        alertDialog.show();
	        for(int i = 0; i < 4; i++){
	        	Toast toast = Toast.makeText(BusnetActivity.this, "「はい」・「いいえ」のどちらかを選択し\n「送信」ボタンを押して下さい\n(※初回のみのアンケートです)", Toast.LENGTH_SHORT);
	        	toast.setGravity(Gravity.BOTTOM, 0, 0);
				toast.show();
			}

	        final Context context = this;

	        BufferedWriter out = null;
	        try{
	        	FileOutputStream file = context.openFileOutput("sample.txt", Context.MODE_PRIVATE);
	        	out = new BufferedWriter(new OutputStreamWriter(file));
	        	String id = UUID.randomUUID().toString();
	        	out.write(id);
	        	out.flush();
	        	out.close();
	        	System.out.println("logcat:10:test");
	        }catch(IOException e){
	        	e.printStackTrace();
	        }

            //0 is the first run
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putInt("dbVersion", Common.DBVER).commit();
            sp.edit().putBoolean("UPDATE_ENABLED", true).commit();
        }

        //150202 add by wyq update
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setIcon(android.R.drawable.ic_input_get);// 设置提示的title的图标，默认是没有的
        dialog.setTitle(getResources().getString(R.string.update));
        dialog.setMax(100);
        dialog.setMessage(getResources().getString(R.string.dbupdate));

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Common.init(BusnetActivity.this);
                    Button btnVer = (Button) findViewById(R.id.rrr);
                    btnVer.setText("" + Common.DBVER);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                dialog.dismiss();

                if (((Boolean)Common.UPINFO.get("dbup")).booleanValue() || Common.DBVER < 0) {
                    Toast toast = Toast.makeText(BusnetActivity.this, Common.UPINFO.get("dbtip").toString(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                Looper.loop();
            }
        }).start();

        dialog.show();
        //150202 end
	}

//	public void onReceivedSslError(WebView view, SslErrorHandler handler,
//			SslError error) {
//		handler.cancel();
//	}

	class CustomWebViewClient extends WebViewClient {
	    public CustomWebViewClient() {
	        super();
	    }

	    @Override
	    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
	        handler.proceed();
	    }
	}


	//メニュー作成
	public boolean onCreateOptionsMenu(Menu menu){
	    //Clearボタンの追加
		//今から編集
//	    menu.add(0, MENU_SELECT_CLEAR, 0, "Clear")
//	    .setIcon(android.R.drawable.ic_menu_close_clear_cancel);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ab_menu, menu);

        ActionBar actionbar = this.getActionBar();
        Common.actionbartool(actionbar);
        return true;
	}

	//メニュー実行時の処理
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                Intent parentActivityIntent = new Intent(this, BusnetActivity.class);
                parentActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                finish();
                System.out.println("logcat:10:" +"Click Home！");
                return true;
                //break;

            case MENU_SELECT_CLEAR:
	        //状態を忘れる
	        setState(PREFERENCE_INIT);
//	        Intent intent = new Intent(BusnetActivity.this,
//					InformationActivity.class);
//			startActivity(intent);


//	        final Context context = this;
//
//	        BufferedReader in = null;
//	        try{
//	        	FileInputStream file = context.openFileInput("sample.txt");
//	        	in = new BufferedReader(new InputStreamReader(file));
//	        	System.out.println("logcat:10:" + in.readLine());
//	        	in.close();
//	        }catch(IOException e){
//	        	e.printStackTrace();
//	        }
            return true;
            default:
                break;
	    }
        //20140416 test by wyq
        return super.onOptionsItemSelected(item);
	    //return false;
	}

	String title = "アプリを終了しますか?";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//new BasicModel();
        Button btnRun = (Button) findViewById(R.id.rrr);
        btnRun.getBackground().setAlpha(10);//0~255透明度?
        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusnetActivity.this, HelloOrmliteActivity.class);
                intent.putExtra("keyword", "bus888");
                startActivity(intent);
            }
        });

	    ImageButton routeSearchImageButton = (ImageButton) findViewById(R.id.route_search_imgbtn);
		routeSearchImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent intent;
                if (getResources().getString(R.string.mode).equals("tablet"))
                    intent = new Intent(BusnetActivity.this, AddressSelect.class);
                else
                    intent = new Intent(BusnetActivity.this, RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				//startActivity(intent);
                startActivityForResult(intent,0);
                //finish();
			}
		});

		ImageButton timetableSearchImageButton = (ImageButton) findViewById(R.id.timetable_imgbtn);
		timetableSearchImageButton
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(BusnetActivity.this,
								TimetableSearchActivity.class);
						startActivity(intent);
					}
				});

		ImageButton shareImageButton = (ImageButton) findViewById(R.id.share_imgbtn);
		shareImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BusnetActivity.this,
						ApplicationSharingActivity.class);
				startActivity(intent);
			}
		});

		ImageButton settingImageButton = (ImageButton) findViewById(R.id.setting_imgbtn);
		settingImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BusnetActivity.this,
						SettingsActivity.class);
				//startActivity(intent);
                startActivityForResult(intent, 0);
			}
		});

		ImageButton operationImageButton = (ImageButton) findViewById(R.id.operation_status_imgbtn);
		operationImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BusnetActivity.this,
						OperationStatusActivity.class);
				startActivity(intent);
			}
		});

		ImageButton busGuideImageButton = (ImageButton) findViewById(R.id.bus_guide_imgbtn);
		busGuideImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BusnetActivity.this,
						SocialMediaActivity.class);
				startActivity(intent);
			}
		});

		ImageButton informationImageButton = (ImageButton) findViewById(R.id.information);
		informationImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BusnetActivity.this,
						InformationActivity.class);
				startActivity(intent);
			}
		});

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		ImageButton inquiryImageButton = (ImageButton) findViewById(R.id.inquiry);
		inquiryImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

		        alertDialogBuilder.setTitle("バスネットに関するお問い合わせです");
		        alertDialogBuilder.setMessage("問い合わせ方法を選択して下さい");
		        alertDialogBuilder.setPositiveButton("メール",
		                new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                    	Intent intent = new Intent();

		                    	intent.setAction(Intent.ACTION_SENDTO);

                                intent.setData(Uri.parse("mailto:webmaster@ikisaki.jp"));   //busnetandroid@gmail.com

		                    	intent.putExtra(Intent.EXTRA_SUBJECT, "バスネットに関する問い合わせ");

		                    	intent.putExtra(Intent.EXTRA_TEXT, "");

		                    	startActivity(intent);
		                    }
		                });


		        alertDialogBuilder.setNegativeButton("Twitter",
		                new DialogInterface.OnClickListener() {
		                    @Override
		                    public void onClick(DialogInterface dialog, int which) {
		                    	final String tweetAppString = "@busnet_tottori"+ "        　\n#busnet_tottori #鳥取 #tottori";

		                    	Intent tweetIntent = new Intent(Intent.ACTION_SEND);
		        				tweetIntent.putExtra(Intent.EXTRA_TEXT, tweetAppString);
		        				tweetIntent.setType("text/plain");
		        				startActivity(tweetIntent);
		                    }
		                });

		        alertDialogBuilder.setCancelable(true);
		        AlertDialog alertDialog = alertDialogBuilder.create();

		        alertDialog.show();
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			 AlertDialog.Builder ad = new AlertDialog.Builder(this);
			 ad.setTitle(title);
             ad.setNegativeButton(getResources().getString(R.string.NO), null);
			 ad.setPositiveButton(getResources().getString(R.string.YES),new DialogInterface.OnClickListener(){
				 @Override
				 public void onClick(DialogInterface dialog, int which) {
					 //finish();
					 moveTaskToBack(true);
				 }
			 });
			 ad.create();
			 ad.show();

			return true;
		}
		return false;
	}

}
