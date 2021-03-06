package jp.ikisaki.www;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class TimeAndRouteTab5Activity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_and_route_tab5);

        Time time = new Time("Asia/Tokyo");
        time.setToNow();

        final DatePicker datePicker = (DatePicker)findViewById(R.id.time_and_route_tab5_datePicker);

        datePicker.setDescendantFocusability(DatePicker.FOCUS_AFTER_DESCENDANTS);

        Date minDate = new Date(time.year - 1900, time.month, time.monthDay);
        datePicker.setMinDate(minDate.getTime());

        if(BasicModel.getTabNumber() == 4){
        	datePicker.updateDate(BasicModel.getYear(), BasicModel.getMonth() - 1, BasicModel.getDay());
        }

        Button decisionButton = (Button)findViewById(R.id.decision_button);
        decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BasicModel.setTime("&timetype=last");
				BasicModel.setDate("&date=" + (datePicker.getMonth() + 1) + "," + datePicker.getDayOfMonth());
				BasicModel.setForwardOrBackward("");

				BasicModel.setSetting((datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日の最終便");
				BasicModel.setTabNumber(4);

				BasicModel.setMonth(datePicker.getMonth() + 1);
				BasicModel.setDay(datePicker.getDayOfMonth());
				BasicModel.setYear(datePicker.getYear());

				Intent intent = new Intent(TimeAndRouteTab5Activity.this,
						RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				startActivity(intent);
			}
		});
    }
}