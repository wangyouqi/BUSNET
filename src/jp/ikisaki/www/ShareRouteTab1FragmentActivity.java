package jp.ikisaki.www;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.R.string;
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

public class ShareRouteTab1FragmentActivity extends Activity {

	WebView routeSharingWebView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		routeSharingWebView = new WebView(this);
		// setContentView(R.layout.share_route_fragment_tab1);

		final ProgressBar bar = new ProgressBar(this);

		final ProgressDialog loading = new ProgressDialog(this) {
			public void onBackPressed() {
				routeSharingWebView.stopLoading();
				routeSharingWebView.goBack();
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


		routeSharingWebView.setWebViewClient(client);
		routeSharingWebView.getSettings().setJavaScriptEnabled(true);
		routeSharingWebView.setWebChromeClient(chrome);


		String urlString = BasicModel.getRouteShareUrl().replaceAll("経路探索",
		"%e7\\%b5\\%8c\\%e8\\%b7\\%af\\%e6\\%8e\\%a2\\%e7\\%b4\\%a2");

		urlString += "&share=route";

		String routeShareString = "";
		try {
			routeShareString = URLEncoder.encode(urlString + "&notiframe=1", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String hashString = "";
		try {
			hashString = URLEncoder.encode("#busnet_tottori #鳥取 #tottori", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		final String tweetString = "https://twitter.com/intent/tweet?text= " + "この経路で移動します：「" + BasicModel.getDeparture().getName() + "」" + "→" + "「" + BasicModel.getDestination().getName() + "」" + " " + hashString + " &url=" + routeShareString;
		final String tweetAppString = "この経路で移動します：「" + BasicModel.getDeparture().getName() + "」" + "→" + "「" + BasicModel.getDestination().getName() + "」" + " " + urlString + " #busnet_tottori #鳥取 #tottori";

		//final String tweetAppString = "友達に教える。 " + urlString + " #busnet_tottori";

		routeSharingWebView
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

		layout.addView(routeSharingWebView, new LinearLayout.LayoutParams(
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
				intent.setData(Uri.parse("line://msg/text/" + "この経路で移動します：「" + BasicModel.getDeparture().getName() + "」" + "→" + "「" + BasicModel.getDestination().getName() + "」" + " " + lineString));
				startActivity(intent);
			}
		});


		setContentView(layout);

	}
}
