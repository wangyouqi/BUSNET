package jp.ikisaki.www;

import android.app.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Dialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.*;
import android.view.*;
import android.widget.*;
import android.util.Log;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
//import android.support.v4.app.FragmentActivity;

import java.text.DecimalFormat;
import java.util.*;
import java.net.*;

public class OperationStatusActivity extends Activity implements LocationListener {
    ArrayList<BusLocationModel> busLocationArrayList = new ArrayList<BusLocationModel>();
    Handler handler = new Handler();
    private int flag = 0;
    private int resume = 0;
    private GoogleMap mMap;
    private LocationSource.OnLocationChangedListener mListener;

    //private UserIconsOverlay users_overlay;
    private String TAG = "MainActivity";
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    //140528 add by wyq add actionbar event
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar, menu);

        ActionBar actionbar = this.getActionBar();
        Common.mActivityList.add(this);
        Common.actionbartool(actionbar);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        return Common.menu(item, this);
    }
    //140528 end

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_status);
        mLocationSource = new LongPressLocationSource();

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status != ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else {
            setUpMapIfNeeded();
            initLocation();
    }

        //	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //	locationManager.requestLocationUpdates(
        //			LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); // You
        // can
        // also
        // use
        // LocationManager.GPS_PROVIDER
        // and
        // LocationManager.PASSIVE_PROVIDER

        //busnet code
        Button homeButton = (Button) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//		Button tottoriButton = (Button) findViewById(R.id.home_button);
//		tottoriButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				GeoPoint tottori = new GeoPoint(35690921, 139700258);
//				mMapController.animateTo(tottori);
//			}
//		});

        flag = 0;
        resume = 0;

        // メモ：これで落ちるようだと，つぎはこのスレッドの非同期処理自体をwhile文で繰り返す(タイマー)がいいかも

        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        //
        // handler.post(new Runnable() {
        // @Override
        // public void run() {
        // }
        // });
        //
        // while(flag == 0){
        // checkBusLocation();
        // users_overlay.draw_dummy_icons(busLocationArrayList);
        // try {
        // Thread.sleep(5000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // System.out.println("logcat:3:error");
        // }
        // }
        //
        // handler.post(new Runnable() {
        // @Override
        // public void run() {
        // }
        // });
        // }
        // }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (flag == 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    });
                                    checkBusLocation();

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            System.out.println("logcat:3:runn");
                                            //users_overlay.draw_dummy_icons(busLocationArrayList);

                                            setBusOverlay(busLocationArrayList);
                                            System.out.println("logcat:3:final");
                                        }
                                    });
                                }
                            }).start();
                        }
                    });
                    try {
                        Thread.sleep(80000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("logcat:3:error");
                    }

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

//      mLocationSource.onResume();
//		if(resume == 1){
//			System.out.println("logcat:3:resumein");
//
//
//
//			myloc_overlay.disableMyLocation();
//			map.getOverlays().remove(myloc_overlay);
//			map.getOverlays().remove(users_overlay);
//			map.invalidate();
//		}


        Toast toast = Toast.makeText(OperationStatusActivity.this, "鳥取県内を運行中のバス" + "\n" + "・バスをタップすると詳しい情報を表示", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        //users_overlay.removeOverlayItem();

        super.onResume();
        System.out.println("logcat:3:onrefin");
    }

        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the
            // map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((MapFragment) getFragmentManager()
                        .findFragmentById(R.id.operation_status_mapview)).getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    MapsInitializer.initialize(getApplicationContext());
                    setUpMap();
                }
            }
        }

        public void initLocation()
        {
            // Enabling MyLocation Layer of Google Map
            mMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
            provider = locationManager.PASSIVE_PROVIDER;

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if(location != null){
                onLocationChanged(location);
            }
            //locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }

    private void setUpMap() {
        mMap.setLocationSource(mLocationSource);
        mMap.setOnMapLongClickListener(mLocationSource);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    /**
     * A {@link LocationSource} which reports a new location whenever a user
     * long presses the map at the point at which a user long pressed the map.
     */
    //150505 add by wyq longpressで自定義現在地
    private static class LongPressLocationSource implements LocationSource,
            OnMapLongClickListener {
        private OnLocationChangedListener mListener;

        /**
         * Flag to keep track of the activity's lifecycle. This is not strictly
         * necessary in this case because onMapLongPress events don't occur
         * while the activity containing the map is paused but is included to
         * demonstrate best practices (e.g., if a background service were to be
         * used).
         */

        @Override
        public void activate(OnLocationChangedListener listener) {
            mListener = listener;
        }

        @Override
        public void deactivate() {
            mListener = null;
        }

        @Override
        public void onMapLongClick(LatLng point) {
            if (mListener != null ) {
                Location location = new Location("LongPressLocationProvider");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                location.setAccuracy(100);
                mListener.onLocationChanged(location);
            }
        }
    }  //150505 end

    private LongPressLocationSource mLocationSource;

    @Override
    protected void onPause() {
        super.onPause();
        // mLocationSource.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
//		LatLng latLng = new LatLng(location.getLatitude(),
//				location.getLongitude());
//		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
//				10);
//		Log.i(TAG, "onLocationChanged");
//
//		mMap.animateCamera(cameraUpdate);
//		locationManager.removeUpdates(this);

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latlng = new LatLng(latitude, longitude);

        //150522 del by wyq android 4.2 以後動かない move api
/*        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14.5f));*/

        CameraUpdate posicionInicial = CameraUpdateFactory.newLatLngZoom(latlng, 14.5f);
        mMap.moveCamera(posicionInicial);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    //busnet code
    private void checkBusLocation() {
        busLocationArrayList.clear();
        // currentPointArrayList.clear();

        try {
            XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance()
                    .newPullParser();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build());
            URL url;

            url = new URL(
                    "http://www.ikisaki.jp/index.cgi?device=xml&page=all_bus_location_map");

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            //connection.setRequestProperty("User-Agent", "Android/Busnet");
            connection.setRequestMethod("GET");

            xmlPullParser.setInput(connection.getInputStream(), "UTF-8");

            int eventType;
            while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
                String str = "";

                String latitude = "";
                String longitude = "";
                String companyName = "";
                String rosenName = "";
                String destination = "";
                String hour = "";
                String minute = "";
                String delayMinute = "";

                if (eventType == XmlPullParser.START_TAG
                        && "Bus".equals(xmlPullParser.getName())) {
                    int subEventType;

                    BusLocationModel model = new BusLocationModel();

                    subEventType = xmlPullParser.next();

                    while (!(subEventType == XmlPullParser.END_TAG && "Bus"
                            .equals(xmlPullParser.getName()))) {
                        if (eventType == XmlPullParser.START_TAG
                                && "Latitude".equals(xmlPullParser.getName())) {
                            latitude = xmlPullParser.nextText();
                            model.setLatitude(latitude);
                        } else if (eventType == XmlPullParser.START_TAG
                                && "Longitude".equals(xmlPullParser.getName())) {
                            longitude = xmlPullParser.nextText();
                            model.setLongitude(longitude);
                        } else if (eventType == XmlPullParser.START_TAG
                                && "Name".equals(xmlPullParser.getName())) {
                            companyName = xmlPullParser.nextText();
                            model.setCompanyName(companyName);
                        } else if (eventType == XmlPullParser.START_TAG
                                && "RosenName".equals(xmlPullParser.getName())) {
                            rosenName = xmlPullParser.nextText();
                            model.setRosenName(rosenName);
                        } else if (eventType == XmlPullParser.START_TAG
                                && "Destination"
                                .equals(xmlPullParser.getName())) {
                            destination = xmlPullParser.nextText();
                            model.setDestination(destination);
                        } else if (eventType == XmlPullParser.START_TAG
                                && "DelayMinute"
                                .equals(xmlPullParser.getName())) {
                            delayMinute = xmlPullParser.nextText();
                            model.setDelayMinute(delayMinute);
                        } else if (eventType == XmlPullParser.START_TAG
                                && "Hour".equals(xmlPullParser.getName())) {
                            hour = xmlPullParser.nextText();
                            model.setHour(hour);
                        } else if (eventType == XmlPullParser.START_TAG
                                && "Minute".equals(xmlPullParser.getName())) {
                            minute = xmlPullParser.nextText();
                            model.setMinute(minute);
                        }
                        subEventType = xmlPullParser.next();
                    }
                    System.out.println("logcat:3:" + model.getDestination());
                    busLocationArrayList.add(model);
                    LatLng geo = new LatLng(Integer.valueOf(model
                            .getLatitude()), Integer.valueOf(model.getLongitude()));
                    // currentPointArrayList.add(geo);

                }
            }

        } catch (Exception e) {
            System.out.println("logcat:(errore)→" + e);
        }
    }

    private void setBusOverlay(ArrayList<BusLocationModel> busLocationArrayList) {
        mMap.clear();
        CheckBox chk_hinomaru = (CheckBox) findViewById(R.id.my_location0);
        CheckBox chk_nikko = (CheckBox) findViewById(R.id.my_location1);
        //chk_hinomaru.setChecked(true);
        //chk_nikko.setChecked(true);
        // アイコンを表示するオーバレイ

        // リサイズしない場合
        // Drawable drawable_icon =
        // this.getResources().getDrawable(R.drawable.icon);

        // リサイズする場合
        Resources res = this.getResources();
        Bitmap bmp_orig = BitmapFactory.decodeResource(res, R.drawable.otherbus);
        Bitmap bmp_resized = Bitmap.createScaledBitmap(bmp_orig, 50, 30, false);
        Drawable drawable_icon = new BitmapDrawable(bmp_resized);

        Resources res2 = this.getResources();
        Bitmap bmp_orig2 = BitmapFactory.decodeResource(res2, R.drawable.nikko);
        Bitmap bmp_resized2 = Bitmap.createScaledBitmap(bmp_orig2, 60, 30, false);
        Drawable drawable_icon2 = new BitmapDrawable(bmp_resized2);

        Resources res3 = this.getResources();
        Bitmap bmp_orig3 = BitmapFactory.decodeResource(res3, R.drawable.hinomaru);
        Bitmap bmp_resized3 = Bitmap.createScaledBitmap(bmp_orig3, 50, 30, false);
        Drawable drawable_icon3 = new BitmapDrawable(bmp_resized3);

        // for (int i = 0; i < busLocationArrayList.size(); i++) {
        for (BusLocationModel aBusLocationArrayList : busLocationArrayList) {
            int latitude, longitude;
            double la = Double.valueOf(aBusLocationArrayList
                    .getLatitude()) / 1E6 / 0.36;
            double ln = Double.valueOf(aBusLocationArrayList
                    .getLongitude()) / 1E6 / 0.36;

            // latitude = (int)((la + la * 0.00010695 - ln * 0.000017464 -
            // 0.0046017) * 1E6);
            // longitude = (int)((ln + la * 0.000046038 + ln * 0.000083043 -
            // 0.010040) * 1E6);

            latitude = (int) ((la) * 1E6);
            longitude = (int) ((ln) * 1E6);

            Marker busmarker;
            String title = aBusLocationArrayList.getRosenName() + " "
                    + aBusLocationArrayList.getDestination() + "行き";
            String snippet = aBusLocationArrayList.getHour() + "時"
                    + aBusLocationArrayList.getMinute() + "分現在 (" + aBusLocationArrayList.getCompanyName() + ")"
                    + "\n" + aBusLocationArrayList.getDelayMinute() + "分遅れ";
            busmarker = mMap.addMarker(new MarkerOptions().position(new LatLng(la, ln))
                            .title(title).snippet(snippet)
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    //.anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
            );
            if (busmarker.getSnippet().indexOf("日ノ丸") != -1) {
                if (chk_hinomaru.isChecked() == true)
                    busmarker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp_resized3));
                else
                    busmarker.remove();
            } else if ((busmarker.getSnippet().indexOf("日本交通") != -1) || (busmarker.getTitle().indexOf("日交") != -1)) {
                if (chk_nikko.isChecked() == true)
                    busmarker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp_resized2));
                else
                    busmarker.remove();
            } else {
                busmarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            }

            // System.out.println("logcat:3:" + i + " " + latitude + ":"
            // + longitude + ":"
            // + busLocationArrayList.get(i).getDestination());
        }

        System.out.println("logcat:3:dasyutu");
    }

    /**
     * Called when the Traffic checkbox is clicked.
     */
    public void onTrafficToggled(View view) {
        CheckBox mTrafficCheckbox = (CheckBox) findViewById(R.id.traffic);
        mMap.setTrafficEnabled(mTrafficCheckbox.isChecked());
    }

    public void onMyLocationToggled(View view) {
        setBusOverlay(busLocationArrayList);
    }

}
