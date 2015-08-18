package jp.ikisaki.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.Time;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class SocialMediaTab2Activity extends Activity {

	private WindowManager wm;
	private Display disp;
	private int width;
	private int height;

	public EditText edtText;

	public Button nameButton;
	public Button dateButton;
	public Button timeButton;
	public EditText eventEditText;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		disp = wm.getDefaultDisplay();
		width = disp.getWidth();
		height = disp.getHeight();

		final Time time = new Time("Asia/Tokyo");
		time.setToNow();

		System.out.println("logcat:10:time:" + time.year + time.month);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout eventLayout = new LinearLayout(this);
		eventLayout.setOrientation(LinearLayout.HORIZONTAL);

		TextView eventTextView = new TextView(this);
		eventTextView.setText("イベント名称:");
		eventTextView.setHeight(height / 10);
		eventTextView.setWidth(width / 4);
		eventTextView.setTextColor(Color.BLACK);


		eventEditText = new EditText(this);
		eventEditText.setHint("イベント名称を入力して下さい。");
		eventEditText.setHeight(height / 10);
		eventEditText.setWidth(3 * height / 4);
		eventEditText.setBackgroundColor(Color.rgb(255, 204, 255));
		eventEditText.setTextColor(Color.BLACK);
		if(!SnsModel.getEventName().equals("")){
			eventEditText.setText(SnsModel.getEventName());
		}

		eventEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					SnsModel.setEventName(eventEditText.getText().toString());
			InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return true;
			}
		});

		eventLayout.addView(eventTextView);
		eventLayout.addView(eventEditText);

		layout.addView(eventLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView dateTextView = new TextView(this);
		dateTextView.setText("日時:");
		dateTextView.setHeight(height / 12);
		dateTextView.setWidth(width / 4);
		dateTextView.setTextColor(Color.BLACK);
		dateTextView.setGravity(Gravity.CENTER);

		dateButton = new Button(this);
		dateButton.setText("日付指定");
		dateButton.setHeight(height / 12);
		dateButton.setWidth(3 * width / 8);
		dateButton.setTextColor(Color.BLACK);
		dateButton.setBackgroundColor(Color.rgb(204, 255, 255));
		dateButton.setGravity(Gravity.CENTER);
		dateButton.setPadding(0, 0, width / 50, 0);
		if(SnsModel.getMonth() != 0){
			dateButton.setText(SnsModel.getYear() + "年" + SnsModel.getMonth() + "月" + SnsModel.getMonthDay() + "日");
		}

		dateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(SocialMediaTab2Activity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								if (year < time.year || year == time.year
										&& monthOfYear < time.month || year == time.year && monthOfYear == time.month
										&& dayOfMonth < time.monthDay) {
									Toast toast = Toast.makeText(SocialMediaTab2Activity.this, "過去の日付を指定することはできません。", Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								} else {
									SnsModel.setYear(year);
									SnsModel.setMonth(monthOfYear + 1);
									SnsModel.setMonthDay(dayOfMonth);
									dateButton.setText(SnsModel.getYear() + "年" + "\n" + SnsModel.getMonth() + "月" + SnsModel.getMonthDay() + "日");
								}
							}
						}, time.year, time.month, time.monthDay).show();
			}
		});

		timeButton = new Button(this);
		timeButton.setText("時刻指定");
		timeButton.setHeight(height / 12);
		timeButton.setWidth(3 * width / 8);
		timeButton.setTextColor(Color.BLACK);
		timeButton.setBackgroundColor(Color.rgb(204, 204, 255));
		timeButton.setPadding(width / 50, 0, 0, 0);
		if(SnsModel.getHour() != 0 || SnsModel.getMinute() != 0){
			timeButton.setText(SnsModel.getHour() + "時" + SnsModel.getMinute() + "分");
			if(SnsModel.getMinute() == 0){
				timeButton.setText(SnsModel.getHour() + "時" + "00" + "分");
			}
		}

		timeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				time.setToNow();

				new TimePickerDialog(SocialMediaTab2Activity.this,
						new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								SnsModel.setHour(hourOfDay);
								SnsModel.setMinute(minute);
								timeButton.setText(SnsModel.getHour() + "時" + SnsModel.getMinute() + "分");
								if(SnsModel.getMinute() == 0){
									timeButton.setText(SnsModel.getHour() + "時" + "00" + "分");
								}
							}
						}, time.hour, time.minute, true).show();
			}
		});

		LinearLayout dateLayout = new LinearLayout(this);
		dateLayout.setOrientation(LinearLayout.HORIZONTAL);
		dateLayout.setPadding(0, height / 100, 0, 0);
		dateLayout.setBackgroundColor(Color.WHITE);
		dateLayout.setGravity(Gravity.CENTER);

		dateLayout.addView(dateTextView);
		dateLayout.addView(dateButton);
		dateLayout.addView(timeButton);

		layout.addView(dateLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		final TextView placeTextView = new TextView(this);
		placeTextView.setText("開催地:");
		placeTextView.setHeight(height / 10);
		placeTextView.setWidth(width / 4);
		placeTextView.setTextColor(Color.BLACK);
		placeTextView.setGravity(Gravity.CENTER);

		//Button nameButton = new Button(this);
		nameButton = new Button(this);
		nameButton.setText("未指定");
		nameButton.setHeight(height / 10);
		nameButton.setWidth(3 * height / 4);
		nameButton.setBackgroundColor(Color.rgb(255, 204, 255));
		nameButton.setTextColor(Color.BLACK);
		nameButton.setGravity(Gravity.CENTER);

		if(!SnsModel.getPlaceName().equals("")){
			nameButton.setText(SnsModel.getPlaceName());
		}



		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		nameButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SnsModel.setEventName(eventEditText.getText().toString());


				alertDialogBuilder.setTitle("開催地指定");
				alertDialogBuilder.setMessage("指定方法を選んで下さい");
				alertDialogBuilder.setPositiveButton("ランドマークから指定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								final EditText editText = new EditText(
										SocialMediaTab2Activity.this);

								editText.setHint("入力して下さい");
								editText.setBackgroundColor(Color.WHITE);
								editText.setTextColor(Color.BLACK);
								editText.setInputType(InputType.TYPE_CLASS_TEXT);

								new AlertDialog.Builder(
										SocialMediaTab2Activity.this)

										//.setIcon(R.drawable.icon)

										.setTitle("ランドマーク検索")

										.setView(editText)


										.setPositiveButton(
												"キャンセル",
												new DialogInterface.OnClickListener() {

													public void onClick(
															DialogInterface dialog,
															int id) {

														dialog.cancel();

													}

												})

										.setNegativeButton(
												"検索",
												new DialogInterface.OnClickListener() {

													public void onClick(
															DialogInterface dialog,
															int id) {
														//ここの実装からスタート

														String exploreString = editText.getText().toString();

														examinLandmark(exploreString);
														dialog.cancel();

													}

												}).show();

							}
						});
				alertDialogBuilder.setNegativeButton("地図から指定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										SocialMediaTab2Activity.this,
										ByMapActivity.class);
								intent.putExtra("keyword", "");
								startActivity(intent);

							}
						});
				alertDialogBuilder.setCancelable(true);
				AlertDialog alertDialog = alertDialogBuilder.create();

				alertDialog.show();

			}
		});

		LinearLayout placeLayout = new LinearLayout(this);
		placeLayout.setOrientation(LinearLayout.HORIZONTAL);
		placeLayout.setPadding(0, height / 100, 0, 0);
		placeLayout.setGravity(Gravity.CENTER);

		placeLayout.addView(placeTextView);
		placeLayout.addView(nameButton);

		layout.addView(placeLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		//
		LinearLayout wordLayout = new LinearLayout(this);
		wordLayout.setOrientation(LinearLayout.HORIZONTAL);
		wordLayout.setPadding(0, height / 100, 0, 0);
		wordLayout.setGravity(Gravity.CENTER);

		TextView wordTextView = new TextView(this);
		wordTextView.setText("一言:");
		wordTextView.setHeight(height / 10);
		wordTextView.setWidth(width / 4);
		wordTextView.setTextColor(Color.BLACK);
		wordTextView.setGravity(Gravity.CENTER);

		final EditText wordEditText = new EditText(this);
		wordEditText.setHint("イベントに関する情報を入力して下さい(任意)");
		wordEditText.setHeight(height / 10);
		wordEditText.setWidth(3 * height / 4);
		wordEditText.setBackgroundColor(Color.rgb(255, 204, 255));
		wordEditText.setTextColor(Color.BLACK);
		wordEditText.setGravity(Gravity.CENTER);
		wordEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
			InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
			imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return true;
			}
		});

		wordLayout.addView(wordTextView);
		wordLayout.addView(wordEditText);

//		layout.addView(wordLayout, new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.WRAP_CONTENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT));

		//
		LinearLayout linkLayout = new LinearLayout(this);
		linkLayout.setOrientation(LinearLayout.HORIZONTAL);
		linkLayout.setPadding(0, height / 100, 0, 0);
		linkLayout.setGravity(Gravity.CENTER);

		final TextView linkTextView = new TextView(this);
		linkTextView.setText("参考URL:");
		linkTextView.setHeight(height / 10);
		linkTextView.setWidth(width / 4);
		linkTextView.setTextColor(Color.BLACK);
		linkTextView.setGravity(Gravity.CENTER);

		final EditText linkEditText = new EditText(this);
		linkEditText.setHint("リンクがある場合は入力して下さい(任意)");

		if(SnsModel.getLinkFlag() == 1){
			linkEditText.setText(SnsModel.getLink());
		}

		linkEditText.setHeight(height / 10);
		linkEditText.setWidth(3 * height / 4);
		linkEditText.setBackgroundColor(Color.rgb(255, 204, 255));
		linkEditText.setTextColor(Color.BLACK);
		linkEditText.setGravity(Gravity.CENTER);
		linkEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					SnsModel.setLink(linkEditText.getText().toString());
				}
				return true;
			}
		});

		linkLayout.addView(linkTextView);
		linkLayout.addView(linkEditText);

		layout.addView(linkLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		LinearLayout okLayout = new LinearLayout(this);
		okLayout.setOrientation(LinearLayout.HORIZONTAL);
		okLayout.setPadding(0, height / 100, 0, 0);
		okLayout.setGravity(Gravity.CENTER);

		Button resetButton = new Button(this);
		resetButton.setText("リセ" + "\n" + "ット");
		resetButton.setBackgroundColor(Color.GRAY);
		resetButton.setTextColor(Color.BLACK);
		resetButton.setHeight(height / 10);
		resetButton.setWidth(width / 4);
		resetButton.setGravity(Gravity.CENTER);

		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				eventEditText.setText("");
				nameButton.setText("未指定");
				linkEditText.setText("");
				wordEditText.setText("");
				dateButton.setText("日付指定");
				timeButton.setText("時刻指定");

				SnsModel.setEventName("");
				SnsModel.setPlaceId("");
				SnsModel.setPlaceName("");
				SnsModel.setLink("");
				SnsModel.setWord("");
				SnsModel.setYear(0);
				SnsModel.setMonth(0);
				SnsModel.setMonthDay(0);
				SnsModel.setHour(0);
				SnsModel.setMinute(0);
				SnsModel.setLinkFlag(0);
			}
		});

		Button oKButton = new Button(this);
		oKButton.setText("OK");
		oKButton.setBackgroundColor(Color.BLUE);
		oKButton.setTextColor(Color.WHITE);
		oKButton.setHeight(height / 10);
		oKButton.setWidth(3 * width / 4);
		oKButton.setGravity(Gravity.CENTER);

		oKButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SnsModel.setEventName(eventEditText.getText().toString());

				String caution = "";
				if(eventEditText.getText().toString().equals("")){
					caution += "「イベント名称入力」";
				}
				if(SnsModel.getYear() == 0){
					caution += "「日付指定」";
				}
				if(SnsModel.getHour() == 0 && SnsModel.getMinute() == 0){
					caution += "「時刻指定」";
				}
				if(nameButton.getText().equals("未指定")){
					caution += "「開催地指定」";
				}
				if(!caution.equals("")){
					caution += "を行って下さい";
				}
				System.out.println("logcat:10: " + caution);

				if(!caution.equals("")){
					Toast toast = Toast.makeText(
							SocialMediaTab2Activity.this,
							caution, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				else{
//					Toast toast = Toast.makeText(
//							SocialMediaTab2Activity.this,
//							wordEditText.getText().toString() + "," + linkEditText.getText().toString(), Toast.LENGTH_LONG);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();

					String checkString = "";
					if(!linkEditText.getText().toString().equals("")){
						checkString = linkEditText.getText().toString();
					}

					if(checkString.startsWith("http") || checkString.startsWith("https")){
						System.out.println("logcat:3:check");
						SnsModel.setLink(linkEditText.getText().toString());
						SnsModel.setLinkFlag(1);
					}

					Intent intent = new Intent(SocialMediaTab2Activity.this, EventActivity.class);
					startActivity(intent);

				}
			}
		});

		okLayout.addView(resetButton);
		okLayout.addView(oKButton);

		layout.addView(okLayout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		setContentView(layout);

	}

	public void examinLandmark(String exploreString) {
		InputStream is = null;
		BufferedReader br = null;

		final ArrayList<LandmarkModel> landmarkArray = new ArrayList<LandmarkModel>();

		final StringBuilder sb = new StringBuilder();
		try {
			try {
				is = getAssets().open("landmark.csv");
				br = new BufferedReader(new InputStreamReader(is));

				String str;
				while ((str = br.readLine()) != null) {
					sb.append(str + "\n");

					StringTokenizer st = new StringTokenizer(str, ",");

					int i = 0;
					int hit = 0;

					int id = 0;
					String name = "";
					String yomi = "";
					int longitude = 0;
					int latitude = 0;
					int frequency = 0;

					while (st.hasMoreTokens()) {
						i++;
						if (i == 1) {
							id = Integer.valueOf(st.nextToken());
						} else if (i == 2) {
							name = st.nextToken();

							if (name.indexOf(exploreString) != -1) {

								hit = 1;

								yomi = st.nextToken();

							} else {
								yomi = st.nextToken();

								if (yomi.indexOf(exploreString) != -1) {

									hit = 1;
								}
							}
						} else if (i == 4) {
							longitude = Integer.valueOf(st.nextToken());
						} else if (i == 5) {
							latitude = Integer.valueOf(st.nextToken());
						} else if (i == 6) {
							frequency = Integer.valueOf(st.nextToken());

							if (hit == 1) {
								LandmarkModel landmarkModel = new LandmarkModel(
										id, name, yomi, longitude, latitude,
										frequency);

								if (landmarkArray.size() == 0) {
									landmarkArray.add(landmarkModel);
								} else {
									int k = landmarkArray.size();
									for (int j = 0; j < k; j++) {

										if (landmarkModel.getFrequency() > landmarkArray
												.get(j).getFrequency()) {
											landmarkArray.add(j, landmarkModel);
											hit = 0;
											break;
										}
									}
									if (hit == 1) {
										landmarkArray.add(landmarkModel);
									}
								}
							}

							i = 0;
							hit = 0;
						}
					}
				}

			} finally {
				if (br != null) {
					br.close();
				}
			}
		} catch (IOException e) {

		}

		String landmarkOfTheCandidate[] = new String[landmarkArray.size()];
		for (int i = 0; i < landmarkArray.size(); i++) {
			landmarkOfTheCandidate[i] = landmarkArray.get(i).getName();
		}

		if (landmarkArray.size() != 0) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("候補から選択して下さい。");
			alert.setItems(landmarkOfTheCandidate,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int idx) {

							 System.out.println("logcat:10:" +
							 landmarkArray.get(idx).getName() + " : " +
							 landmarkArray.get(idx).getId());

							 SnsModel.setPlaceName(landmarkArray.get(idx).getName());
							 SnsModel.setPlaceId(String.valueOf(landmarkArray.get(idx).getId()));


							 nameButton.setText(SnsModel.getPlaceName());

						}
					});
			alert.show();
		} else {
			Toast toast = Toast.makeText(SocialMediaTab2Activity.this,
					"該当するランドマークが存在しません．", Toast.LENGTH_LONG);

			toast.setGravity(Gravity.TOP, 0, 250);

			toast.show();
		}
	}
}
