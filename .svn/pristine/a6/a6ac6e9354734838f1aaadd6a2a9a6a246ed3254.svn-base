package jp.ikisaki.www;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class PlatformActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.platform);

    int station = 0;
    int num = 0;

    Button closeButton = (Button)findViewById(R.id.close_button);
    closeButton.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	});

    Intent intent = getIntent();
    int platform[] = intent.getIntArrayExtra("key");

	station = platform[0];
	num = platform[1];

    ImageView imageView = (ImageView)findViewById(R.id.platform_imageView);

    int resId = 0;

    if(station == 1791){
    	resId = getResources().getIdentifier("tottori" + num, "drawable", getPackageName());
    }
    else if(station == 20422){
    	resId = getResources().getIdentifier("yonago" + num, "drawable", getPackageName());
    }
    else if(station == 20431){
    	resId = getResources().getIdentifier("kurayoshi" + num, "drawable", getPackageName());
    }

    imageView.setImageBitmap(
    		BitmapFactory.decodeResource(getResources(),
    				resId));

  }
}