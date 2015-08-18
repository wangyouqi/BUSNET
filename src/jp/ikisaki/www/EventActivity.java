package jp.ikisaki.www;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EventActivity extends Activity {
	private WindowManager wm;
	private Display disp;
	private int width;
	private int height;

	WebView destinationSharingWebView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		destinationSharingWebView = new WebView(this);

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();

		final ProgressBar bar = new ProgressBar(this);
		final ProgressDialog loading = new ProgressDialog(this) {
			public void onBackPressed() {
				destinationSharingWebView.stopLoading();
				destinationSharingWebView.goBack();
				cancel();
				finish();
			}
		};
		loading.setTitle("しばらくお待ちください");
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		WebViewClient client = new WebViewClient() {
			public void onPageFinished(final WebView view, final String url) {
				if (loading.isShowing()) {
					loading.dismiss();
					Toast toast = Toast.makeText(EventActivity.this, "参考URL付きで発信するには\n「Twitterアプリ」または\n「LINEアプリ」を利用してください", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				bar.setVisibility(View.GONE);
			};

			public void onPageStarted(WebView view, String url,
					android.graphics.Bitmap favicon) {
				loading.show();
				bar.setVisibility(View.VISIBLE);
			};
		};

		WebChromeClient chrome = new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				loading.setProgress(progress);
				bar.setProgress(progress);
			}
		};

		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);
		layout.setOrientation(LinearLayout.VERTICAL);

		destinationSharingWebView.setWebViewClient(client);
		destinationSharingWebView.getSettings().setJavaScriptEnabled(true);
		destinationSharingWebView.setWebChromeClient(chrome);

		String urlString = "";

//		String urlString = BasicModel.getRouteShareUrl().replaceAll("hour", "preHour");
//		urlString = urlString.replaceAll("min", "preMin");
//		urlString = urlString.replaceAll("s_id", "pre_S_id");
//		urlString = urlString.replaceAll("&bus", "&preBus");
//		urlString = urlString.replaceAll("train", "preTrain");
//		urlString = urlString.replaceAll("timetype", "preTimetype");
//		urlString = urlString.replaceAll("after", "preAfter");
//
//		urlString = urlString.replaceAll("target=経路探索",
//		"&hour=" + BasicModel.getShareHour() + "&min=" + BasicModel.getShareMinute());

		urlString = "http://www.ikisaki.jp/search/search.cgi?" +  "&hour=" + SnsModel.getHour() + "&min=" + SnsModel.getMinute()+ "&page=route_search" + "pre_S_id=1092" + "&d_id=" + SnsModel.getPlaceId() + "&perpage=7"
			+ "&date=" + SnsModel.getMonth() + "," + SnsModel.getMonthDay()  +  "&preHour=0" ;

		System.out.println("logcat:2:, " + urlString + "&dir=backward");

		String destinationShareString = "";
		try {
			destinationShareString = URLEncoder.encode(urlString + "&dir=backward" + "&share=destination", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		String linkShareString = "";
		try {
			linkShareString = URLEncoder.encode("http://www.tottori-u.ac.jp", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		String hashString1 = "";
		try {
			hashString1 = URLEncoder.encode("#busnet_tottori", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String hashString2 = "";
		try {
			hashString2 = URLEncoder.encode("#鳥取", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String hashString3 = "";
		try {
			hashString3 = URLEncoder.encode("#tottori", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}



		String weekString = "";
		String timeString = "";

		if(SnsModel.getMinute() == 0){
			timeString = SnsModel.getHour() + ":" + "00～開催";
		}
		else{
			timeString = SnsModel.getHour() + ":" + SnsModel.getMinute() + "～開催";
		}

		System.out.println("logcat:3:" + SnsModel.getYear() + SnsModel.getMonth() + SnsModel.getMonthDay());

		Calendar cal = new GregorianCalendar(SnsModel.getYear(), SnsModel.getMonth() - 1, SnsModel.getMonthDay());

		System.out.println("logcat:3:" + cal.get(Calendar.DAY_OF_WEEK));

		switch(cal.get(Calendar.DAY_OF_WEEK)){
		case Calendar.SUNDAY:
			weekString = "日";
			break;
		case Calendar.MONDAY:
			weekString = "月";
			break;
		case Calendar.TUESDAY:
			weekString = "火";
			break;
		case Calendar.WEDNESDAY:
			weekString = "水";
			break;
		case Calendar.THURSDAY:
			weekString = "木";
			break;
		case Calendar.FRIDAY:
			weekString = "金";
			break;
		case Calendar.SATURDAY:
			weekString = "土";
			break;
		}

		String linkString = "";

		if(SnsModel.getLinkFlag() == 1){
			linkString = " 詳しくはこちら→" + SnsModel.getLink();
		}

		String eventString = SnsModel.getYear() + "年" + SnsModel.getMonth() + "月" + SnsModel.getMonthDay() + "日" + "(" + weekString + ")" + timeString  + "「" + SnsModel.getEventName() + "」" + "（@" + SnsModel.getPlaceName() + "）";

		final String tweetString = "https://twitter.com/intent/tweet?text= " + eventString + hashString1 + " " + hashString2 + " " + hashString3 + " &url=" + destinationShareString;

		final String tweetAppString = eventString + " 行き方→" + urlString + "&dir=backward" + "&share=destination" + linkString + "　#busnet_tottori #鳥取 #tottori";

		destinationSharingWebView
				.loadUrl(tweetString);

		Button twitterButton = new Button(this);
		twitterButton.setText("Twitterアプリでつぶやく");
		twitterButton.setTextColor(Color.WHITE);
		twitterButton.setBackgroundColor(Color.BLUE);

		twitterButton.setGravity(Gravity.CENTER);
		twitterButton.setPadding(2, 2, 2, 2);

		layout.setGravity(Gravity.CENTER_HORIZONTAL);

		twitterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tweetIntent = new Intent(Intent.ACTION_SEND);
				tweetIntent.putExtra(Intent.EXTRA_TEXT, tweetAppString);
				tweetIntent.setType("text/plain");
				startActivity(tweetIntent);
			}
		});

		Button lineButton = new Button(this);
		lineButton.setText("LINEで送る");
		lineButton.setTextColor(Color.WHITE);
		lineButton.setBackgroundColor(Color.GREEN);
		lineButton.setGravity(Gravity.CENTER);
		lineButton.setPadding(2, 2, 2, 2);

		final String line1String = eventString;
		final String line2String = urlString;
		final String line3String = linkString;

		////////////////////////
		Button closeButton = new Button(this);
		closeButton.setText("閉じる");
		closeButton.setHeight(height / 12);
		closeButton.setWidth(width / 3);
		closeButton.setTextColor(Color.WHITE);
		closeButton.setBackgroundColor(Color.BLUE);
		closeButton.setGravity(Gravity.CENTER);

		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventActivity.this, SocialMediaActivity.class);
				startActivity(intent);
			}
		});

		Button eventButton = new Button(this);
		eventButton.setText("イベント");
		eventButton.setHeight(height / 12);
		eventButton.setWidth(width / 3);
		eventButton.setTextColor(Color.BLACK);
		eventButton.setBackgroundColor(Color.rgb(204, 204, 204));
		eventButton.setGravity(Gravity.CENTER);

		Button homeButton = new Button(this);
		homeButton.setText("ホームへ");
		homeButton.setHeight(height / 12);
		homeButton.setWidth(width / 3);
		homeButton.setTextColor(Color.WHITE);
		homeButton.setBackgroundColor(Color.BLUE);
		homeButton.setGravity(Gravity.CENTER);

		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventActivity.this, BusnetActivity.class);
				startActivity(intent);
			}
		});

		LinearLayout headLayout = new LinearLayout(this);
		headLayout.setOrientation(LinearLayout.HORIZONTAL);
		headLayout.setBackgroundColor(Color.WHITE);
		headLayout.setGravity(Gravity.CENTER);

		headLayout.addView(closeButton);
		headLayout.addView(eventButton);
		headLayout.addView(homeButton);

		layout.addView(headLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		/////////



		layout.addView(destinationSharingWebView,
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));

		layout.addView(twitterButton, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		layout.addView(lineButton, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		lineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("logcat:2:line");
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("line://msg/text/" + line1String + " 行き方→" + line2String + "&dir=backward" + "&share=destination" + line3String));
				startActivity(intent);
			}
		});

		setContentView(layout);

	}
}
