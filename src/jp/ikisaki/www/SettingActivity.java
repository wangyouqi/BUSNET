package jp.ikisaki.www;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

@SuppressWarnings("deprecation")
public class SettingActivity extends TabActivity {
	/** Called when the activity is first created. */
    //140528 add by wyq add actionbar event
    public boolean onCreateOptionsMenu(Menu menu) {
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
		setContentView(R.layout.setting);

		Button homeButton = (Button) findViewById(R.id.home_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

        //150615 add by wyq 出発地設定
        Button btnStartlocation = (Button) findViewById(R.id.start_location);
        btnStartlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SpecifyPointActivity.class);
                intent.putExtra("keyword", "departure");
                startActivity(intent);
            }
        });
        //150615 end

		initTabs();
	}

		protected void initTabs() {

			TabHost tabHost = getTabHost();
			TabHost.TabSpec spec;
			Intent intent;

            if ("setting".equals(getIntent().getStringExtra("keyword")) ) {
                intent = new Intent().setClass(this, SettingTab1Activity.class);
                spec = tabHost.newTabSpec("Tab1").setIndicator(getResources().getString(R.string.registration_point))
                        .setContent(intent);
                tabHost.addTab(spec);

                intent = new Intent().setClass(this, SettingTab2Activity.class);
                spec = tabHost.newTabSpec("Tab2").setIndicator(getResources().getString(R.string.management_point))
                        .setContent(intent);
                tabHost.addTab(spec);
            }

            intent = new Intent().setClass(this, SettingTab3Activity.class);
            spec = tabHost.newTabSpec("Tab3").setIndicator(getResources().getString(R.string.bookmark))
                    .setContent(intent);
            tabHost.addTab(spec);
		}
}

