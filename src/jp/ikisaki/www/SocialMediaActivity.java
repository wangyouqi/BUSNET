package jp.ikisaki.www;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

@SuppressWarnings("deprecation")
public class SocialMediaActivity extends TabActivity {
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

		setContentView(R.layout.social_media);

		Button closeButton = (Button) findViewById(R.id.close_button);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SocialMediaActivity.this, BusnetActivity.class);
				startActivity(intent);
			}
		});

		Button homeButton = (Button)findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SocialMediaActivity.this, BusnetActivity.class);
				startActivity(intent);
			}
		});

        if(Intent.ACTION_SEND.equals(getIntent().getAction())){
        	SnsModel.setLinkFlag(1);
        	SnsModel.setLink(getIntent().getExtras().getCharSequence(Intent.EXTRA_TEXT).toString());
        }



		initTabs();

	}

	protected void initTabs() {

		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, SocialMediaTab2Activity.class);
		spec = tabHost.newTabSpec("Tab2").setIndicator("イベント" + "\n" + "情報発信")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, SocialMediaTab1Activity.class);
		spec = tabHost.newTabSpec("Tab1").setIndicator("リアルタイム")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, SocialMediaTab3Activity.class);
		spec = tabHost.newTabSpec("Tab3").setIndicator("つぶやき数")
				.setContent(intent);
		tabHost.addTab(spec);

	}

}
