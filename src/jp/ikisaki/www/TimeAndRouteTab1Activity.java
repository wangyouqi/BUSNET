package jp.ikisaki.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimeAndRouteTab1Activity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_and_route_tab1);

        TextView currentTimeTextView = (TextView)findViewById(R.id.current_time_textView);
        final Time time = new Time("Asia/Tokyo");
        time.setToNow();
        String date = time.year + "年" + (time.month + 1) + "月" + time.monthDay + "日 " + time.hour + "時" + time.minute + "分";
        currentTimeTextView.setText(date);

        Button decisionButton = (Button)findViewById(R.id.decision_button);
        decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BasicModel.setMonth(time.month + 1);
				BasicModel.setDay(time.monthDay);
				BasicModel.setDate("");
				BasicModel.setTime("");
				BasicModel.setForwardOrBackward("");
				BasicModel.setSetting("現在時刻に出発");
				BasicModel.setTabNumber(0);

				Intent intent = new Intent(TimeAndRouteTab1Activity.this,
						RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				startActivity(intent);
			}
		});
    }
}