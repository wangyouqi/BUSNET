package jp.ikisaki.www;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class SocialMediaTab1Activity extends Activity {

	WebView snsWebView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		snsWebView = new WebView(this);

		final ProgressBar bar = new ProgressBar(this);

		final ProgressDialog loading = new ProgressDialog(this) {
			public void onBackPressed() {
				snsWebView.stopLoading();
				snsWebView.goBack();
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


		snsWebView.setWebViewClient(client);
		snsWebView.getSettings().setJavaScriptEnabled(true);
		snsWebView.setWebChromeClient(chrome);

//		try {
//			routeShareString = URLEncoder.encode(urlString, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//
//		String hashString = "";
//		try {
//			hashString = URLEncoder.encode("#busnet_tottori", "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

		final String urlString = "https://mobile.twitter.com/search?q=%23busnet_tottori&s=typd";

		snsWebView
				.loadUrl(urlString);

		layout.addView(snsWebView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		setContentView(layout);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && snsWebView.canGoBack()) {
			snsWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
