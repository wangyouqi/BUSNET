package jp.ikisaki.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

public class TimeAndRouteTab2Activity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		setContentView(R.layout.time_and_route_tab2);

		final NumberPicker hourNumberPicker = (NumberPicker) findViewById(R.id.time_and_route_tab2_hour_numberPicker);
		final NumberPicker minuteNumberPicker = (NumberPicker) findViewById(R.id.time_and_route_tab2_minute_numberPicker);

		hourNumberPicker.setMaxValue(23);
		hourNumberPicker.setMinValue(0);

		minuteNumberPicker.setMaxValue(59);
		minuteNumberPicker.setMinValue(0);

		hourNumberPicker.setValue(0);
		minuteNumberPicker.setValue(5);

		hourNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);
		minuteNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);

		if(BasicModel.getTabNumber() == 1){
			minuteNumberPicker.setValue(BasicModel.getAfter() % 60);
			hourNumberPicker.setValue(BasicModel.getAfter() / 60);
	    }

		Button decisionButton = (Button)findViewById(R.id.decision_button);
        decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BasicModel.setTime("&after=" + (hourNumberPicker.getValue() * 60 + minuteNumberPicker.getValue()));
				BasicModel.setAfter(hourNumberPicker.getValue() * 60 + minuteNumberPicker.getValue());
				BasicModel.setForwardOrBackward("");
				BasicModel.setTabNumber(1);

				if(BasicModel.getAfter() / 60 >= 1){
					BasicModel.setSetting("今から" + (hourNumberPicker.getValue()) + "時間" + (minuteNumberPicker.getValue()) + "分後に出発");
				}
				else{
					BasicModel.setSetting("今から" + (minuteNumberPicker.getValue()) + "分後に出発");
				}

				Intent intent = new Intent(TimeAndRouteTab2Activity.this,
						RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				startActivity(intent);
			}
		});
	}
}
