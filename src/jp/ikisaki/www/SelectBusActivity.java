package jp.ikisaki.www;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

public class SelectBusActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_bus);

		Time time = new Time("Asia/Tokyo");
		time.setToNow();

		final DatePicker date = (DatePicker)findViewById(R.id.datePicker);

		 Date minDate = new Date(time.year - 1900, time.month, time.monthDay);
	     date.setMinDate(minDate.getTime());

		Button homeButton = (Button) findViewById(R.id.close_button);
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SelectBusActivity.this, TimetableSearchActivity.class);
				startActivity(intent);
			}
		});

		final CheckBox hinomaruCheckBox = (CheckBox)findViewById(R.id.hinomaru_checkBox);
		final CheckBox nikkoCheckBox = (CheckBox)findViewById(R.id.nikko_checkBox);
		hinomaruCheckBox.setChecked(true);
		nikkoCheckBox.setChecked(true);

		if(TimetableModel.getHinomaru() == 0){
			hinomaruCheckBox.setChecked(false);
		}
		if(TimetableModel.getNikko() == 0){
			nikkoCheckBox.setChecked(false);
		}


		if(TimetableModel.getDay() != 0 || TimetableModel.getDay() != 0){
			date.updateDate(time.year, TimetableModel.getMonth() - 1, TimetableModel.getDay());
		}

		Button decisionButton = (Button)findViewById(R.id.decision_button);
		decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if((hinomaruCheckBox.isChecked() == false) && (nikkoCheckBox.isChecked() == false)){
					Toast toast = Toast.makeText(
							SelectBusActivity.this,
							"少なくとも一方はチェックして下さい", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();

				}
				else{
					String line = "";
					if(hinomaruCheckBox.isChecked() == false){
						TimetableModel.setHinomaru(0);
					}
					else{
						TimetableModel.setHinomaru(1);
						line += "&bus=hinomaru";
					}
					if(nikkoCheckBox.isChecked() == false){
						TimetableModel.setNikko(0);
					}
					else{
						TimetableModel.setNikko(1);
						line += "&bus=nikkou";
					}
					TimetableModel.setLine(line);


					TimetableModel.setMonth(date.getMonth() + 1);
					TimetableModel.setDay(date.getDayOfMonth());

					Intent intent = new Intent(SelectBusActivity.this, TimetableSearchActivity.class);
					startActivity(intent);
				}
			}
		});

	}

}
