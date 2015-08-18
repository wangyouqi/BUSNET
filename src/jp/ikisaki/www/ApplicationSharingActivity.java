package jp.ikisaki.www;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class ApplicationSharingActivity extends Activity {
	WebView shareApplicationWebView;

	private WindowManager wm;
	private Display disp;
	private int width;
	private int height;

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
		setContentView(R.layout.application_sharing);


		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();

		Button homeButton = (Button) findViewById(R.id.home_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		final ProgressBar bar = new ProgressBar(this);
        final ProgressDialog loading = new ProgressDialog(this){
        	public void onBackPressed(){
        		shareApplicationWebView.stopLoading();
        		shareApplicationWebView.goBack();
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

        	public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
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


		shareApplicationWebView = (WebView) findViewById(R.id.application_sharing_webView);
		shareApplicationWebView.setWebViewClient(client);
        shareApplicationWebView.setWebChromeClient(chrome);
		//shareApplicationWebView.setWebViewClient(new WebViewClient());

        String hashString = "";
		try {
			hashString = URLEncoder.encode("#busnet_tottori", "UTF-8");
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


		String urlString = "";
		try{
			urlString = URLEncoder.encode("http://bus.ike.tottori-u.ac.jp/android_app/", "UTF-8");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}

		shareApplicationWebView.loadUrl("https://twitter.com/intent/tweet?text= Android用バスネットアプリ(鳥取) " + hashString + " "+ hashString2 + " " + hashString3 + " &url=" + urlString);
		shareApplicationWebView.getSettings().setJavaScriptEnabled(true);

		final String tweetAppString = "Android用バスネットアプリ(鳥取) " +  "http://bus.ike.tottori-u.ac.jp/android_app/"+ "　#busnet_tottori #鳥取 #tottori";

		final String lineString = urlString;

		Button tweetButton = (Button)findViewById(R.id.tweet_button);
		tweetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tweetIntent = new Intent(Intent.ACTION_SEND);
				tweetIntent.putExtra(Intent.EXTRA_TEXT, tweetAppString);
				tweetIntent.setType("text/plain");
				startActivity(tweetIntent);
			}
		});

		Button lineButton = (Button)findViewById(R.id.line_button);
		lineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("logcat:2:line");
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("line://msg/text/" + "Android用バスネットアプリ(鳥取) " + lineString));
				startActivity(intent);
			}
		});



	}
}
