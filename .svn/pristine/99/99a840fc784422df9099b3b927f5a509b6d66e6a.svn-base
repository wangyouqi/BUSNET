package jp.ikisaki.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SpecifyDestinationPointActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specify_point);


        Button homeButton = (Button)findViewById(R.id.close_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

        TextView selectARideBusStopText = (TextView)findViewById(R.id.specify_point_textView);
        selectARideBusStopText.setText(R.string.destination);

        Button gpsMapButton = (Button)findViewById(R.id.gps_map_button);
        gpsMapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SpecifyDestinationPointActivity.this,
						CurrentPositionActivity.class);
				startActivity(intent);
			}
		});

        EditText exploreEditText = (EditText)findViewById(R.id.specify_point_edittext);

    }
}