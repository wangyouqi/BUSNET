package jp.ikisaki.www;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class ShareRouteTab2FragmentActivity extends Activity {

	WebView destinationSharingWebView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		destinationSharingWebView = new WebView(this);

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
		layout.setOrientation(LinearLayout.VERTICAL);

		destinationSharingWebView.setWebViewClient(client);
		destinationSharingWebView.getSettings().setJavaScriptEnabled(true);
		destinationSharingWebView.setWebChromeClient(chrome);

		String urlString = BasicModel.getRouteShareUrl().replaceAll("hour", "preHour");
		urlString = urlString.replaceAll("min", "preMin");
		urlString = urlString.replaceAll("s_id", "pre_S_id");
		urlString = urlString.replaceAll("&bus", "&preBus");
		urlString = urlString.replaceAll("train", "preTrain");
		urlString = urlString.replaceAll("timetype", "preTimetype");
		urlString = urlString.replaceAll("after", "preAfter");
		urlString = urlString.replaceAll("&search=1", "");


		urlString = urlString.replaceAll("target=経路探索",
		"&hour=" + BasicModel.getShareHour() + "&min=" + BasicModel.getShareMinute());

		System.out.println("logcat:2: " + urlString + "&dir=backward");

		String destinationShareString = "";
		try {
			destinationShareString = URLEncoder.encode(urlString + "&dir=backward" + "&share=destination", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		String hashString = "";
		try {
			hashString = URLEncoder.encode("#busnet_tottori #鳥取 #tottori", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		final String tweetString = "https://twitter.com/intent/tweet?text= " + "「" + BasicModel.getDestination().getName() + "」" + "に一緒に行く。 " + hashString +  " &url=" + destinationShareString;

		final String tweetAppString = "「" + BasicModel.getDestination().getName() + "」" + "に一緒に行く。 " + urlString + "&dir=backward" + "&share=destination" + " #busnet_tottori #鳥取 #tottori";

		destinationSharingWebView
				.loadUrl(tweetString);

		Button twitterButton = new Button(this);
		twitterButton.setText("Twitterアプリでつぶやく");
		twitterButton.setTextColor(Color.WHITE);
		twitterButton.setBackgroundColor(Color.BLUE);
		twitterButton.setGravity(Gravity.CENTER);
		twitterButton.setPadding(2, 2, 2, 2);

		Button lineButton = new Button(this);
		lineButton.setText("LINEで送る");
		lineButton.setTextColor(Color.WHITE);
		lineButton.setBackgroundColor(Color.GREEN);
		lineButton.setGravity(Gravity.CENTER);
		lineButton.setPadding(2, 2, 2, 2);

		final String lineString = urlString;

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
				intent.setData(Uri.parse("line://msg/text/" + "「" + BasicModel.getDestination().getName() + "」" + "に一緒に行く。 " + lineString + "&dir=backward" + "&share=destination"));
				startActivity(intent);
			}
		});

		setContentView(layout);

	}
}
