package jp.ikisaki.www;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class ShareRouteActivity extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getWindow().setSoftInputMode(
//	            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		setContentView(R.layout.share_route);

		Button closeButton = (Button) findViewById(R.id.close_button);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		Button homeButton = (Button)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			Intent intent = new Intent(ShareRouteActivity.this, BusnetActivity.class);
			startActivity(intent);
			}
		});

		initTabs();

	}

	protected void initTabs() {

		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, ShareRouteTab1FragmentActivity.class);
		spec = tabHost.newTabSpec("Tab1").setIndicator("友達に" + "\n" + "教える")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ShareRouteTab2FragmentActivity.class);
		spec = tabHost.newTabSpec("Tab2").setIndicator("　友達と" + "\n" + "一緒に行く")
				.setContent(intent);
		tabHost.addTab(spec);

	}

}
