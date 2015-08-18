package jp.ikisaki.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SetTheLineActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.set_the_line);

		Button setTheTimeButton = (Button) findViewById(R.id.set_the_time_button);
		setTheTimeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetTheLineActivity.this, SetTimeAndRouteActivity.class);
				startActivity(intent);			}
		});

		Button routeSearchButton = (Button)findViewById(R.id.route_search_button);
        routeSearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			Intent intent = new Intent(SetTheLineActivity.this, RouteSearchActivity.class);
			intent.putExtra("keyword", "");
			startActivity(intent);
			}
		});


        final CheckBox hinomaruCheckBox = (CheckBox)findViewById(R.id.hinomaru_checkBox);
        final CheckBox nikkoCheckBox = (CheckBox)findViewById(R.id.nikko_checkBox);
        final CheckBox jrCheckBox = (CheckBox)findViewById(R.id.jr_checkBox);

        if(!BasicModel.getLine().equals("")){
        	hinomaruCheckBox.setChecked(false);
        	nikkoCheckBox.setChecked(false);
        	jrCheckBox.setChecked(false);

        	String line = BasicModel.getLine();
        	if(line.indexOf("hinomaru") != -1){
        		hinomaruCheckBox.setChecked(true);
        	}
        	if(line.indexOf("nikkou") != -1){
        		nikkoCheckBox.setChecked(true);
        	}
        	if(line.indexOf("with") != -1){
        		jrCheckBox.setChecked(true);
        	}
        }
        else{
        	hinomaruCheckBox.setChecked(true);
        	nikkoCheckBox.setChecked(true);
        	jrCheckBox.setChecked(true);
        }


        Button decisionButton = (Button)findViewById(R.id.decision_button);

        decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hinomaruCheckBox.isChecked() == true || nikkoCheckBox.isChecked() == true){
					String line = "";
					if(hinomaruCheckBox.isChecked() == true){
						line += "&bus=hinomaru";
					}
					if(nikkoCheckBox.isChecked() == true){
						line += "&bus=nikkou";
					}
					if(jrCheckBox.isChecked() == true){
						line += "&train=with";
					}
					else{
						line += "&train=without";
					}

					BasicModel.setLine(line);

					Intent intent = new Intent(SetTheLineActivity.this, RouteSearchActivity.class);

					intent.putExtra("keyword", "");

					startActivity(intent);
				}
				else{
					Toast toast = Toast.makeText(SetTheLineActivity.this, "少なくともバス会社1つはチェックして下さい．", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});
	}
}
