package jp.ikisaki.www;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class SetTimeAndRouteActivity extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getWindow().setSoftInputMode(
//	            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		setContentView(R.layout.set_time_and_route);

		Button homeButton = (Button) findViewById(R.id.close_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetTimeAndRouteActivity.this, RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				startActivity(intent);
			}
		});

		Button setRouteButton = (Button)findViewById(R.id.set_route_button);
        setRouteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			Intent intent = new Intent(SetTimeAndRouteActivity.this, SetTheLineActivity.class);
			startActivity(intent);
			}
		});

		initTabs();

	}

	protected void initTabs() {

		//Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		// Tab1
		intent = new Intent().setClass(this, TimeAndRouteTab1Activity.class);
		spec = tabHost.newTabSpec("Tab1").setIndicator("現在\n時刻")
				.setContent(intent);
		tabHost.addTab(spec);

		// Tab2
		intent = new Intent().setClass(this, TimeAndRouteTab2Activity.class);
		spec = tabHost.newTabSpec("Tab2").setIndicator("~分後")
				.setContent(intent);
		tabHost.addTab(spec);

		// Tab3
		intent = new Intent().setClass(this, TimeAndRouteTab3Activity.class);
		spec = tabHost.newTabSpec("Tab3").setIndicator("日時\n指定")
				.setContent(intent);
		tabHost.addTab(spec);

		// Tab4
		intent = new Intent().setClass(this, TimeAndRouteTab4Activity.class);
		spec = tabHost.newTabSpec("Tab4").setIndicator("朝一番")
				.setContent(intent);
		tabHost.addTab(spec);

		// Tab5
		intent = new Intent().setClass(this, TimeAndRouteTab5Activity.class);
		spec = tabHost.newTabSpec("Tab5").setIndicator("最終便")
				.setContent(intent);
		tabHost.addTab(spec);

		// Set Default Tab - zero based index
		if(BasicModel.getTabNumber() == 5){
			tabHost.setCurrentTab(2);
		}else{
			tabHost.setCurrentTab(BasicModel.getTabNumber());
		}
	}

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
