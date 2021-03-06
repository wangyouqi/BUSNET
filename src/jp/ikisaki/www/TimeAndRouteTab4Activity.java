package jp.ikisaki.www;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class TimeAndRouteTab4Activity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_and_route_tab4);

        Time time = new Time("Asia/Tokyo");
        time.setToNow();

        final DatePicker datePicker = (DatePicker)findViewById(R.id.time_and_route_tab4_datePicker);

        datePicker.setDescendantFocusability(DatePicker.FOCUS_AFTER_DESCENDANTS);

        Date minDate = new Date(time.year - 1900, time.month, time.monthDay);
        datePicker.setMinDate(minDate.getTime());

        if(BasicModel.getTabNumber() == 3){
        	datePicker.updateDate(BasicModel.getYear(), BasicModel.getMonth() - 1, BasicModel.getDay());
        }

        Button decisionButton = (Button)findViewById(R.id.decision_button);
        decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BasicModel.setTime("&timetype=first");
				BasicModel.setDate("&date=" + (datePicker.getMonth() + 1) + "," + datePicker.getDayOfMonth());
				BasicModel.setForwardOrBackward("");

				BasicModel.setSetting((datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日の朝一番");
				BasicModel.setTabNumber(3);

				BasicModel.setMonth(datePicker.getMonth() + 1);
				BasicModel.setDay(datePicker.getDayOfMonth());
				BasicModel.setYear(datePicker.getYear());

				Intent intent = new Intent(TimeAndRouteTab4Activity.this,
						RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				startActivity(intent);
			}
		});
    }
}