package jp.ikisaki.www;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class InformationActivity extends Activity{
	WebView informationWebView;
	private WindowManager wm;
	private Display disp;
	private int width;
	private int height;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Toast toast = Toast.makeText(InformationActivity.this, "アプリに関するお知らせを表示します。\n定期的に参照して下さい。", Toast.LENGTH_LONG);
    	toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();

		informationWebView = new WebView(this);

		final ProgressBar bar = new ProgressBar(this);

		final ProgressDialog loading = new ProgressDialog(this) {
			public void onBackPressed() {
				informationWebView.stopLoading();
				informationWebView.goBack();
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


		informationWebView.setWebViewClient(client);
		informationWebView.getSettings().setJavaScriptEnabled(true);
		informationWebView.setWebChromeClient(chrome);


		//final String urlString = "https://twitter.com/search?q=%23busnet_tottori_android&src=typd";

		final String urlString = "https://mobile.twitter.com/search?q=%23busnet_tottori_android&s=typd";

		informationWebView
				.loadUrl(urlString);

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
				Intent intent = new Intent(InformationActivity.this, BusnetActivity.class);
				startActivity(intent);
			}
		});
		Button eventButton = new Button(this);
		eventButton.setText("お知らせ");
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
				Intent intent = new Intent(InformationActivity.this, BusnetActivity.class);
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

		layout.addView(informationWebView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		setContentView(layout);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && informationWebView.canGoBack()) {
			informationWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
