package jp.ikisaki.www;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TestActivity extends Activity {
    public GoogleMap mMap;
    private ArrayAdapter<String> adapter;
    private static final String[] m={"A型","B型","O型","AB型","其他"};
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.test);

		TextView text = (TextView)findViewById(R.id.output);
		Intent intent = getIntent();
		String data = intent.getStringExtra("keyword");
		text.setText(data);


        Spinner spinner = (Spinner) findViewById(R.id.Spinner01);
        //将可?内容与ArrayAdapter?接起来
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);
        //?置下拉列表的?格
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);

        //添加事件Spinner事件?听
        //spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        spinner.setSelection(0, true);

        if (1==1) {

            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            MapsInitializer.initialize(this);
            LatLng sydney = new LatLng(31.2653514, 120.7365586);

            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, (float)10.0));

            mMap.addMarker(new MarkerOptions()
                    .title("Sydney")
                    .snippet("The most populous city in Australia.")
                    .position(sydney)
                    .draggable(true)
                    //.icon(BitmapDescriptorFactory.fromBitmap(getBitMap("成都市")))
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.XX))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }
	}

}
