package jp.ikisaki.www;

//import android.support.v4.app.FragmentActivity;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.graphics.Color;
import android.location.*;
import android.os.Bundle;

import android.os.*;
import android.preference.*;
import android.text.*;
import android.text.style.ForegroundColorSpan;
import android.view.*;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.*;
import com.j256.ormlite.stmt.*;;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.io.*;
import java.util.*;
import jp.ikisaki.www.db.*;

public class SetMapActivity extends OrmLiteBaseActivity<DataHelper>
        implements OnCameraChangeListener, OnMapClickListener, OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener {
    GoogleMap mMap = null;
    DecimalFormat df = new DecimalFormat("#.00000");
    private static LatLng SuZhou = new LatLng(31.2653514, 120.7365586);
    private static Circle circle;
    private Marker desMarker;
    private String checkpoint = "";

    private int gmapdLatitude = 0;
    private int gmapdLongitude = 0;

    static RegistrationDBAdapter registrationdbAdapter;
    private List<Marker> lmList =new ArrayList();
    //private Map<String,String[]> lmMap = new HashMap();
    private Map<String,Markerinfo> mmMap = new HashMap();

    class Markerinfo {
        public int id;
        public String type;
        public String figure;
        public String link;

        //初期値
        Markerinfo (int id, String type, String figure, String link) {
            this.id = id;
            this.type = type;
            this.figure = figure;
            this.link = link;
        }
    }

    //141015 add by wyq CustomInfoWindowAdapter
    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements InfoWindowAdapter {
        //private final RadioGroup mOptions;

        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        //private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // This means that getInfoContents will be called.
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            // Use the equals() method on a Marker to check for equals.  Do not use ==.
            if (lmList.contains(marker)) {
                //infowindow の写真、nophoto
                if ((mmMap.get(marker.getId()).figure == null?0:mmMap.get(marker.getId()).figure.length()) > 1) {//figure
                    //((ImageView) view.findViewById(R.id.badge)).setImageDrawable
                    //        (Common.getLocalFile(SetMapActivity.this, "photo/" + lmMap.get(marker.getId())[1]));
                    //((ImageView) view.findViewById(R.id.badge)).setImageBitmap(BitmapFactory.decodeStream(bis));
                    //Create thread for load url image
                    Common.strCommon = mmMap.get(marker.getId()).figure;
                    Thread thread = new Thread(Common.raUrlImage);
                    thread.start();
                    while (thread.isAlive()) {
                        try {
                            Thread.sleep(100L);

                            //Field field=R.drawable.class.getField("ic_hotel");
                            //反射 nouse
                            Field field=Class.forName(getApplication().getPackageName() + ".R$drawable").getField("ic_hotel");
                            int i= field.getInt(field);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    ((ImageView) view.findViewById(R.id.badge)).setImageBitmap(Common.bmInfoWindow);
                }else
                    ((ImageView) view.findViewById(R.id.badge)).setImageResource(R.drawable.nophoto);
            } else {
                // Passing 0 to setImageResource will clear the image view.
                //Toast.makeText(SetMapActivity.this, getClass().toString(), Toast.LENGTH_LONG).show();
                ((ImageView) view.findViewById(R.id.badge)).setImageResource(android.R.color.transparent);
            }
            System.out.println(Thread.currentThread().getName());


            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet()==null ? "":marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet == null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText(Html.fromHtml(snippet));
                snippetUi.setMaxWidth(400);
            }
        }
    }

    @Override
    public void onCameraChange(final CameraPosition position) {
        Iterator it = mmMap.entrySet().iterator();
        String in = " and int_landmark_id not in (0";
        RadioGroup rg=(RadioGroup)findViewById(R.id.landmark_options);
        int checkid = rg.getCheckedRadioButtonId();

        //141010 add by wyq load landmark from database
        while (it.hasNext()) {
        Map.Entry<String,Markerinfo> entry = (Map.Entry)it.next();
            if (entry.getValue().id > 0)
                in = in + "," + entry.getValue().id;
            //for (Iterator it : mmMap.) {
            break; //NOUSE
        }
        in = in + ")";

        //150717 ランドマーク全部リロード
        in = " and f_landmark_fix < " + position.zoom;
        lmList.clear();
        mMap.clear();
        //150717 end

        double latitude = position.target.latitude * 1E6 * 0.36;
        double longitude = position.target.longitude * 1E6 * 0.36;

        if (checkid != R.id.landmark_none) {
            rg.check(-1); //lost focus
        try {
            //距離計算：s/40000*360/cos(lat) * 360000
            double rlat = 1000;
            double rlng = 3000;
            rlat = Common.RADIUS * 3.24;
            rlng = Common.RADIUS * 3.24 / Math.cos(position.target.latitude * Math.PI/180);
            //System.out.println(Math.cos(Math.toRadians(60)) );

            // get our dao
            Dao<Landmark, Integer> landmarkDao = getHelper().getDaoLandmark();
            // query for all of the data objects in the database
            QueryBuilder<Landmark, Integer> builder = landmarkDao.queryBuilder();
            builder.where().raw("int_landmark_lat BETWEEN " + (int)(latitude - rlat) + " AND " + (int)(latitude + rlat) +
                    " and int_landmark_lng BETWEEN " + (int)(longitude - rlng) + " AND " + (int)(longitude + rlng) + in);
            //.gt("length(vc_landmark_type)", "1");

            long a = new Date().getTime();
            for (Landmark lmRowset : builder.query()) { //landmarkDao.queryForAll()
                //llList.add( new LatLng(35.4936256408691, 134.228317260742));
                int iconid = R.drawable.landmark; //最初のCHAR数字なら
                if (lmRowset.get_landmark_type().length() > 0 && Character.isDigit(lmRowset.get_landmark_type().charAt(0))) {
                    iconid = getResources().getIdentifier("ic_" + lmRowset.get_landmark_type(), "drawable", this.getPackageName());
                } else {
                    //iconid = R.drawable.landmark;
                }

                LatLng ll = (new LatLng((double)(lmRowset.get_landmark_lat() / 360000.0),
                        (double) (lmRowset.get_landmark_lng() / 360000.0)));
                lmList.add(mMap.addMarker(new MarkerOptions()
                                        .position(ll)
                                        .title(lmRowset.get_landmark_name())
                                        .snippet(lmRowset.get_landmark_name() + "<br>" +
                                                lmRowset.get_landmark_addr() + "<br>" +
                                                lmRowset.get_landmark_comment())
                                        .icon(BitmapDescriptorFactory.fromResource(iconid)).visible(false)
                                        //.visible(checkid == R.id.landmark_all ? true : false)
                        )
                );
                //lmList.get(lmList.size()-1).setIcon(BitmapDescriptorFactory.fromResource(iconid));

                //create landmark map
                //lmMap.put(lmList.get(lmList.size()-1).getId(), lminfo);
                mmMap.put(lmList.get(lmList.size()-1).getId(), new Markerinfo(lmRowset.get_landmark_id(), lmRowset.get_landmark_type(), lmRowset.get_landmark_figure(), lmRowset.get_landmark_link()) );
            }

            long b = new Date().getTime();
            System.out.println("logcat:55555555:count=" + (double)((b - a) / 1000.0));
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            rg.check(checkid);
        }   //141010 end

        if (circle != null) circle.remove();
        //reset BasicModel
        if (checkpoint.equals("出発地")) {
            BasicModel.resetDeparture();
        } else {
            BasicModel.resetDestination();
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        //lmlist 動作
        if (lmList.contains(marker)) {
            if (desMarker != null) desMarker.remove();
            // This causes the marker at Perth to bounce into position when it is clicked.
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long duration = 1500;

            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = Math.max(1 - interpolator
                            .getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + 2 * t);

                    if (t > 0.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });

            if (checkpoint.equals("出発地")) {
                BasicModel.resetDeparture();
                PointModel departurePointModel = new PointModel(mmMap.get(marker.getId()).id, marker.getTitle(), "",
                        (int)(marker.getPosition().longitude * 1E6 * 0.36), (int)(marker.getPosition().latitude * 1E6 * 0.36), 0);
                BasicModel.setDeparture(departurePointModel);
            } else {
                BasicModel.resetDestination();
                PointModel destinationModel = new PointModel(mmMap.get(marker.getId()).id, marker.getTitle(), "", 0, 0, 0);
                BasicModel.setDestination(destinationModel);
            }

            marker.showInfoWindow();
            return true;
        }

        //地図に円を描く
        circle = mMap.addCircle(new CircleOptions()
                .center(marker.getPosition())
                .radius(Common.RADIUS * 1.1)
                .strokeWidth(5)
                .strokeColor(Color.BLACK)
                .fillColor(Color.TRANSPARENT));

        //camara 中心に移り
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (!lmList.contains(marker)) return;
        View view = null;

        if (mmMap.get(marker.getId()).link == null) {
            //marker location in list
            SuZhou = new LatLng(lmList.get(lmList.indexOf(marker)).getPosition().latitude,
                    lmList.get(lmList.indexOf(marker)).getPosition().longitude);

            //open street view
            LayoutInflater inflater
                    = LayoutInflater.from(SetMapActivity.this);
            view = inflater.inflate(R.layout.custom_map_street, null);

            StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);

            streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                    new OnStreetViewPanoramaReadyCallback() {
                        @Override
                        public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                            // Only set the panorama to SYDNEY on startup (when no panoramas have been
                            // loaded which is when the savedInstanceState is null).
                            //if (savedInstanceState == null) {
                            panorama.setPosition(SuZhou);
                            panorama.setUserNavigationEnabled(false);
                            panorama.animateTo(
                                    new StreetViewPanoramaCamera.Builder()
                                            .zoom(panorama.getPanoramaCamera().zoom)
                                            .tilt(panorama.getPanoramaCamera().tilt)
                                            .bearing(panorama.getPanoramaCamera().bearing)
                                            .build(), 5000);
                            //}
                        }
                    });

            //150522 add by wyq 2度open streetview　使用不能の解決
            if (streetViewPanoramaFragment != null) {
                getFragmentManager().beginTransaction().addToBackStack(null).commit();
                getFragmentManager().beginTransaction().remove(streetViewPanoramaFragment).commit();
                //getFragmentManager().beginTransaction().replace(R.id.streetviewpanorama,streetViewPanoramaFragment).commit();
            }
            //150522 end
        } else {
            //open url
            Toast.makeText(getBaseContext(), "しばらくお待ちください！", Toast.LENGTH_SHORT).show();
            LayoutInflater inflater
                    = LayoutInflater.from(SetMapActivity.this);
            view = inflater.inflate(R.layout.share_route_fragment_tab2, null);
            WebView webview = (WebView) view.findViewById(R.id.route_sharing_webView);
            //webview.setWebViewClient(new WebViewClient());
            //webview.setWebChromeClient(new WebChromeClient());
            webview.getSettings().setJavaScriptEnabled(true);
            //"http://www.agoda.com/ja-jp/tottori-washington-hotel-plaza/hotel/tottori-jp.html"
            webview.loadUrl(mmMap.get(marker.getId()).link);
            webview.getSettings().setSupportZoom(true);
            webview.getSettings().setBuiltInZoomControls(true);

            webview.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //no jump alaws
                    view.loadUrl(url);
                    return true;
                }
            });
        }

        new AlertDialog.Builder(SetMapActivity.this)
                //.setTitle("登録名を入力して下さい。")
                .setView(view)
                .setPositiveButton(
                        getResources().getString(R.string.close),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.cancel();
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //mTopText.setText("onMarkerDragStart");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //mTopText.setText("onMarkerDragEnd");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //mTopText.setText("onMarkerDrag.  Current Position: " + marker.getPosition());
    }
    //141015 end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_map);

        setUpMapIfNeeded();
        initLocation();
        Spinner spinner = (Spinner) findViewById(R.id.sp_maptype);
        //将可?内容与ArrayAdapter?接起来
        //ArrayAdapter<String> adapter;
        //adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,stringarray);
        //?置下拉列表的?格
        //adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        //将adapter 添加到spinner中
        //spinner.setAdapter(adapter);

        //添加事件Spinner事件?听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        spinner.setSelection(0, true);

        RadioGroup.OnCheckedChangeListener oncheckchanged = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radiogroup, int checkedId) {
                // reset position
                if (radiogroup.getCheckedRadioButtonId() == R.id.landmark_none) {
                    CameraPosition cameraposition = new CameraPosition.Builder()
                            .target(mMap.getCameraPosition().target)
                            .zoom(17.5f)
                            .bearing(0).tilt(0).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraposition));
                }

                Markerinfo lminfo;
                RadioButton rb=(RadioButton)findViewById(checkedId);
                out: for(Marker marker : lmList) {
                    lminfo = null;
                    System.out.println("--->" + marker.getId());
                    lminfo = mmMap.get(marker.getId());
                    if (lminfo == null || checkedId < 0)  break out;

                    switch (checkedId) {
                        case R.id.landmark_all:
                            marker.setVisible(true);
                            break;
                        case R.id.landmark_none:
                            marker.setVisible(false);
                            break;
                        default:
                            if (lminfo.type.equals(rb.getTag().toString())) {
                                marker.setVisible(true);
                            } else {
                                marker.setVisible(false);
                            }
                            break;
                    }
                }
                //for (Map.Entry<String, String[]> entry : lmMap.entrySet())
                //GroundOverlayOptions option = new GroundOverlayOptions();
                //mMap.addGroundOverlay(option);
            }
        };

        //150510 add by wyq load typecode from database
        RadioGroup rg=(RadioGroup)findViewById(R.id.landmark_options);
        try {
            // get our dao
            Dao<Classcode, Integer> classcodeDao = getHelper().getDaoClasscode();
            // query for all of the data objects in the database
            QueryBuilder<Classcode, Integer> builder = classcodeDao.queryBuilder();
            //builder.where().raw("id<10111");
            builder.groupByRaw("sector_ID");
            //System.out.println("logcat:55555555:count=" + builder.countOf());

            for (Classcode ccRowset : builder.query()) { //classcodeDao.queryForAll()
                RadioButton rb = new RadioButton(this);
                rb.setText(ccRowset.getClasscode_name());
                rb.setTextSize(12);
                //rb.setTextColor(getResources().getColor(R.drawable.btn_text));
                rb.setButtonDrawable(android.R.drawable.btn_radio);
                rb.setTag(ccRowset.getSector_ID());
                rb.setId(new Random().nextInt(100000000));

                //rb.setFocusable(true);
                rb.setFocusableInTouchMode(true);
                rb.setSingleLine(true);
                rb.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                rb.setMarqueeRepeatLimit(3); //3回
                //rb[i].setLayoutParams(new LinearLayout.LayoutParams(60,30));
                rg.addView(rb,  RadioGroup.LayoutParams.MATCH_PARENT, 26);
             }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //150510 end
        rg.invalidate();
        rg.setOnCheckedChangeListener(oncheckchanged);

        registrationdbAdapter = new RegistrationDBAdapter(this);
        Intent intent = getIntent();
        if (intent.getStringExtra("keyword").equals("departure")) {
            checkpoint = "出発地";
        } else {
            checkpoint = "目的地";
        }

        Toast toast = Toast.makeText(SetMapActivity.this, "設定したい" + checkpoint
                + "を中心に合わせて下さい", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapview)).getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                MapsInitializer.initialize(getApplicationContext());
                setUpMap();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.clear();
        /*String title = "経度" + df.format(SuZhou.latitude) + "緯度" + df.format(SuZhou.longitude);
        CameraPosition cameraposition = new CameraPosition.Builder().target(SuZhou).zoom(15.5f).bearing(300).tilt(50).build();
        mMap.addMarker(new MarkerOptions().position(SuZhou).title(title));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraposition));*/
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);

        // UiSettings
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        //mMap.getUiSettings().setRotateGesturesEnabled(false);

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
    }

    private void initLocation() {    //現在地処理
        // Enabling MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);
        //mMap.setLocationSource();

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        provider = locationManager.PASSIVE_PROVIDER;
        //locationManager.GPS_PROVIDER; locationManager.NETWORK_PROVIDER;

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        // location 即ち 現在地の経緯度、一部端末のlocationがなし
        if (location != null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (sp.getInt("START_ID", -1) >= 0) {
                location.setLatitude(sp.getInt("START_LATITUDE", 0) / 360000.0);
                location.setLongitude(sp.getInt("START_LONGITUDE", 0) / 360000.0);
            }
            onLocationChanged(location);
        } else {
            location = new Location("LongPressLocationProvider");
            location.setLatitude(SuZhou.latitude);
            location.setLongitude(SuZhou.longitude);
            location.setAccuracy(100);
            onLocationChanged(location);
        }
        //locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }

    private void onLocationChanged(Location location) {
//		LatLng latlng = new LatLng(location.getLatitude(),
//				location.getLongitude());
//		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lalLng,
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.5f));*/
        CameraUpdate posicionInicial = CameraUpdateFactory.newLatLngZoom(latlng, 17.5f);
        mMap.moveCamera(posicionInicial);

        SuZhou = new LatLng(location.getLatitude(), location.getLongitude());
        //Toast.makeText(this,  "" + location.getLatitude(), Toast.LENGTH_LONG).show();
        String title = getResources().getString(R.string.departure);// + "\n 経度：" + df.format(SuZhou.latitude) + "\n 緯度："+ df.format(SuZhou.longitude);
        //CameraPosition cameraposition = new CameraPosition.Builder().target(SuZhou).zoom(15.5f).bearing(300).tilt(50).build();
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraposition));
        mMap.addMarker(new MarkerOptions().position(SuZhou).title(title)).showInfoWindow();
    }

    @Override
    public void onMapClick(LatLng point) {
        if (desMarker != null) desMarker.remove();
        Geocoder gcoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        double lat = point.latitude;
        double lon = point.longitude;
        DecimalFormat df = new DecimalFormat("#.00000");
        String title = "経度：" + df.format(lat) + "\n 緯度：" + df.format(lon);
        try {
            addresses = gcoder.getFromLocation(point.latitude, point.longitude, 3);
            System.out.println("logcat:55555555555:size=" + addresses);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(1);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    if (i > 0) break; //強制運行１回
                    strReturnedAddress.append(returnedAddress.getMaxAddressLineIndex()>1?
                            returnedAddress.getAddressLine(1):returnedAddress.getLocality()).append("\n");
                    if (returnedAddress.getSubLocality()!= null && !returnedAddress.getSubLocality().equals("null"))
                        strReturnedAddress.append(returnedAddress.getSubLocality()).append("\n");
                    //strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    //strReturnedAddress.append(returnedAddress.getLocality()).append("\n");
                    if (returnedAddress.getPostalCode()!= null && !returnedAddress.getPostalCode().equals(""))
                        strReturnedAddress.append("〒").append(returnedAddress.getPostalCode()).append("\n");
                    //builder.append(address.getCountryName())
                    //0行=国名
                }
                title = strReturnedAddress.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Marker center point as desMarker;
        desMarker= mMap.addMarker(new MarkerOptions().position(point).title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
                .draggable(true) );

        System.out.println("logcat:point:" + point.latitude +"中心位置緯度" + BasicModel.getDeparture().getLatitude() / 360000.0);
        System.out.println("logcat:point:" + point.longitude +"中心位置緯度" + BasicModel.getDeparture().getLongitude() / 360000.0);
        // Polylines are useful for marking paths and routes on the map.
        ///mMap.addPolyline(new PolylineOptions().geodesic(true)
                //.add(new LatLng(BasicModel.getDeparture().getLongitude() / 360000.0,BasicModel.getDeparture().getLatitude() / 360000.0))
        ///        .add(SuZhou)
        ///        .add(point)
                //.add(new LatLng(-18.142, 178.431))  // Fiji
                //.add(new LatLng(21.291, -157.821))  // Hawaii
        ///);
    }

//busnet method
protected boolean isRouteDisplayed() {
    return false;
}

    public void onCancel(View view) {
        finish();
    }

public void onGetCenter(View view) {
    //GeoPoint gpo = mview.getMapCenter();
    CameraPosition gpo = mMap.getCameraPosition();
    double latitude = gpo.target.latitude;
    double longitude = gpo.target.longitude;
    // Toast.makeText(this, "中心位置\n緯度" + latitude + "\n経度:" + longitude,
    // Toast.LENGTH_LONG).show();
    //System.out.println("logcat:point:" + "中心位置緯度" + gpo.getLatitudeE6() + "経度:" + gpo.getLongitudeE6());

    //	変更	latitude = la + la * 0.00010696 - ln * 0.000017467 - 0.0046020;
    //	longitude = ln + la * 0.000046047 + ln * 0.000083049 - 0.010041;

    gmapdLatitude = (int) (latitude * 1E6 * 0.36);
    gmapdLongitude = (int) (longitude * 1E6 * 0.36);

    if (checkpoint.equals("出発地")) {
        // リセット出発地
        if (BasicModel.getDeparture().getId() == 0) {
            BasicModel.resetDeparture();
            PointModel departurePointModel = new PointModel(-1, getResources().getString(R.string.get_your_departure), "",
                    gmapdLongitude, gmapdLatitude, 0);
            BasicModel.setDeparture(departurePointModel);
        }

        Common.setLocation(this);
    } else {
        // リセット目的地
        if (BasicModel.getDestination().getId() == 0) {
            BasicModel.resetDestination();
            PointModel destinationPointModel = new PointModel(-1, getResources().getString(R.string.get_your_destination), "",
                    gmapdLongitude, gmapdLatitude, 0);
            BasicModel.setDestination(destinationPointModel);
        }
    }

    if (Common.CHKSETTING == true) finish();
    else {
        Intent intent = new Intent(SetMapActivity.this, RouteSearchActivity.class);
        intent.putExtra("keyword", "");
        startActivity(intent); }
}

    public void onRegistration(View view) {
        LayoutInflater inflater = LayoutInflater.from(SetMapActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog, null);
        final EditText editText = (EditText) dialogView
                .findViewById(R.id.dialog_editText);

        new AlertDialog.Builder(SetMapActivity.this).setTitle("登録名を入力して下さい。")

                .setView(dialogView)
                .setPositiveButton(getResources().getString(R.string.decision), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //GeoPoint gpo = mview.getMapCenter();
                        CameraPosition gpo = mMap.getCameraPosition();
                        double latitude = gpo.target.latitude;
                        double longitude = gpo.target.longitude;
                        // Toast.makeText(SetMapActivity.this, "中心位置\n緯度" + latitude + "\n経度:" + longitude,
                        // Toast.LENGTH_LONG).show();

                        double la = latitude;
                        double ln = longitude;

//			変更			latitude = la + la * 0.00010696 - ln * 0.000017467 - 0.0046020;
//						longitude = ln + la * 0.000046047 + ln * 0.000083049 - 0.010041;

                        gmapdLatitude = (int) (latitude * 1E6 * 0.36);
                        gmapdLongitude = (int) (longitude * 1E6 * 0.36);

                        if (checkpoint.equals("出発地")) {
                            BasicModel.resetDeparture();
                            PointModel departurePointModel = new PointModel(0, editText.getText().toString(), "",
                                    gmapdLongitude, gmapdLatitude, 0);
                            BasicModel.setDeparture(departurePointModel);
                        } else {
                            BasicModel.resetDestination();
                            PointModel destinationPointModel = new PointModel(0, editText.getText().toString(), "",
                                    gmapdLongitude, gmapdLatitude, 0);
                            BasicModel.setDestination(destinationPointModel);
                        }

                        System.out.println("logcat:7:"
                                + editText.getText().toString() + ":" + gmapdLongitude + ":" + gmapdLatitude);

                        int noName = 0;

                        if (editText.getText().toString().equals("")) {
                            noName = 1;
                        }

                        int flag = 1;

                        registrationdbAdapter.open();
                        Cursor c = registrationdbAdapter.getAllNotes();
                        if (c.getCount() == 0 && noName == 0) {
                            registrationdbAdapter.saveNote(editText.getText().toString(), gmapdLongitude + "," + gmapdLatitude);
                            flag = 1;
                        }
                        startManagingCursor(c);
                        if (c.moveToFirst()) {
                            do {
                                Note note = new Note(c.getInt(c
                                        .getColumnIndex(DBAdapter.COL_ID)), c.getString(c
                                        .getColumnIndex(DBAdapter.COL_NOTE)), c.getString(c
                                        .getColumnIndex(DBAdapter.COL_LASTUPDATE)));
                                if (note.getNote().equals(editText.getText().toString())) {
                                    //registrationdbAdapter.deleteNote(note.getId());
                                    flag = 0;
                                }
                            } while (c.moveToNext());
                            if (flag == 1 && noName == 0) {
                                registrationdbAdapter.saveNote(editText.getText().toString(), gmapdLongitude + "," + gmapdLatitude);
                            }

                        }
                        stopManagingCursor(c);
                        registrationdbAdapter.close();


                        if (noName == 1) {
                            Toast toast = Toast.makeText(SetMapActivity.this, "文字が入力されていません。" + "\n" + "名称を入力して再登録して下さい。", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else if (flag == 1) {
                            Toast toast = Toast.makeText(SetMapActivity.this, "「"
                                    + editText.getText().toString() + "」" + "\n"
                                    + "を登録しました。", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            Intent intent = new Intent(SetMapActivity.this,
                                    RouteSearchActivity.class);
                            intent.putExtra("keyword", "");
                            startActivity(intent);
                        } else {
                            Toast toast = Toast.makeText(SetMapActivity.this, "既にその名前は使われています。" + "\n" + "別の名前を入力して下さい。", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                }).show();
    }

    //地図種類の選択
    public class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case 1:
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;
                case 2:
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
                case 3:
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                default:
                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                    break;
            }
            //Toast.makeText(SetMapActivity.this, "分享", Toast.LENGTH_SHORT).show();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    //140528 add by wyq add actionbar event
    public boolean onCreateOptionsMenu(Menu menu) {
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
}
