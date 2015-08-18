package jp.ikisaki.www;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class HistoryOrRegistrationActivity extends TabActivity {

	private String type = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.history_or_registration);

		if(type.equals("timetableDestination")){
			System.out.println("logcat:5:" + type + " tab " + TimetableModel.getBusstopsModel().size());
		}

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
			Intent intent = new Intent(HistoryOrRegistrationActivity.this, BusnetActivity.class);
			startActivity(intent);
			}
		});

        Intent intent = getIntent();
        type = intent.getStringExtra("keyword");

		initTabs();

	}

	protected void initTabs() {

		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, HistoryOrRegistrationTab1Activity.class).putExtra("keyword", type);
		spec = tabHost.newTabSpec("Tab1").setIndicator("履歴")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, HistoryOrRegistrationTab2Activity.class).putExtra("keyword", type);
		spec = tabHost.newTabSpec("Tab2").setIndicator("登録地")
				.setContent(intent);
		tabHost.addTab(spec);

	}

}
