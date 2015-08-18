package jp.ikisaki.www;
/**
 * Created by wangyouqi on 2014/05/13.
 */
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.*;
import com.j256.ormlite.stmt.*;
import jp.ikisaki.www.db.*;
import java.sql.SQLException;

public class AddressSelect extends OrmLiteBaseActivity<DataHelper> {
    private static RegistrationDBAdapter dbregnote;
    private Button[] arrbtn;
    float size; //= getResources().getDimension(android.R.dimen.app_icon_size);

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

    View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View v) {
            Toast.makeText(AddressSelect.this, "しばらくお待ちください！", Toast.LENGTH_SHORT).show();
            Button myButton = (Button)v;

            PointModel destinationModel;
            int index = myButton.getTag().toString().indexOf(",");
            if (index == -1) {
                destinationModel = new PointModel(Integer.valueOf(myButton.getTag().toString()), myButton.getText().toString(), "", 0, 0, 0);
            } else {
                int longitude = 0;
                int latitude = 0;

                longitude = Integer.valueOf(myButton.getTag().toString().substring(0, index));
                latitude = Integer.valueOf(myButton.getTag().toString().substring(index + 1));

                destinationModel = new PointModel(0, myButton.getText().toString(), "", longitude, latitude, 0);
            }
            BasicModel.setDestination(destinationModel);

            Intent intent = new Intent(AddressSelect.this, RouteSearchActivity.class);
            intent.putExtra("keyword", "AUTORUN");
            startActivity(intent);
                /*直接結果
                Intent intent = new Intent(AddressSelect.this, ResultOfRouteSearchActivity.class);
                intent.putExtra("keyword", 0);
                startActivity(intent);*/
            System.out.println("logcat:runbuttonstart");

            //save address log
            dbregnote = new RegistrationDBAdapter(AddressSelect.this);
            dbregnote.open();
            Cursor c = dbregnote.getSpecNotes(myButton.getTag().toString());
            if (c.getCount() == 0) {
                dbregnote.saveNote(myButton.getText().toString(), myButton.getTag().toString());
            }
            startManagingCursor(c);
            if (c.moveToFirst()) {
                do {
                    Note note = new Note(c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL_ID)),
                            c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_NOTE)),
                            c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_LASTUPDATE)) );
                    if (note.getNote().equals(myButton.getText().toString()) ) {
                        dbregnote.countNote(note.getId());
                        //dbregnote.deleteNote(note.getId());
                    }
                } while (c.moveToNext());
                System.out.println("logcat:5:ok");
                //dbregnote.saveNote(myButton.getText().toString(),myButton.getTag().toString());
            }
            stopManagingCursor(c);
            dbregnote.close();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_select);

        //150530 add by wyq 特定端末(端末ID限定)の出発地の初期化
        try {
            // get our dao
            Dao<RegDevice, Integer> regdeviceDao = getHelper().getDaoRegDevice();
            // query for all of the data objects in the database
            QueryBuilder<RegDevice, Integer> builder = regdeviceDao.queryBuilder();
            //builder.where().raw("vc_device_serial is not null");
            builder.where().eq("vc_device_serial", android.os.Build.SERIAL);
            builder.orderByRaw("int_start_id");

            RegDevice rdRowset = builder.queryForFirst();
            if (builder.countOf() == 1) {
                final PointModel departureModel = new PointModel(rdRowset.getInt_start_id(), //Integer.valueOf(getResources().getString(R.string.start_posion_id)),
                        rdRowset.getVc_start_name(), "", 33, 44, 0);
                BasicModel.setDeparture(departureModel);
            }

            // test code
            //builder.prepareStatementString() == query.getStatement() //sql string
            //builder.prepare() == query.toString() //sql object
            PreparedQuery<RegDevice> query = regdeviceDao.queryBuilder().setCountOf(true)
                    .where().eq("vc_device_serial", "GPKDBJA001213").prepare();
            //query = builder.prepare();

            if (regdeviceDao.countOf(query) == 1) { //rdRowset : builder.query()  error???
                for (RegDevice rdRowset0 : regdeviceDao.queryForEq("vc_device_serial", "GPKDBJA001213")) { //regdeviceDao.queryForAll()
                    System.out.println("logcat:55555555555:" + "GPKDBJA001213");
                    break;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //150530 end

        //自分で決める
        Button btnAddressSelect = (Button) findViewById(R.id.address_select_self);
        size = btnAddressSelect.getTextSize();
        btnAddressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressSelect.this, SpecifyPointActivity.class);
                intent.putExtra("keyword", "destination");
                startActivity(intent);
            }
        });

        //button array
        int i = 10;
        arrbtn=new Button[i];
        arrbtn[0]=(Button) findViewById(R.id.address_select_00);
        arrbtn[1]=(Button) findViewById(R.id.address_select_01);
        arrbtn[2]=(Button) findViewById(R.id.address_select_02);
        arrbtn[3]=(Button) findViewById(R.id.address_select_03);
        arrbtn[4]=(Button) findViewById(R.id.address_select_04);
        arrbtn[5]=(Button) findViewById(R.id.address_select_05);
        arrbtn[6]=(Button) findViewById(R.id.address_select_06);
        arrbtn[7]=(Button) findViewById(R.id.address_select_07);
        arrbtn[8]=(Button) findViewById(R.id.address_select_08);
        arrbtn[9]=(Button) findViewById(R.id.address_select_09);

        onResume();
        for (i=1;i<arrbtn.length;i++) {
            if (!arrbtn[i].getText().equals(""))
            arrbtn[i].setOnClickListener(listener);
        };

        //test
        final Button btnStartlocation = (Button) findViewById(R.id.address_select_00);
        btnStartlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStartlocation.getText().equals("")) {
                    if (1!=0) return ;
                    //finish();
                } else {
                    Intent intent = new Intent(AddressSelect.this, RouteSearchActivity.class);
                    intent.putExtra("keyword", "");
                    startActivity(intent);
                    //btnRun.performClick(); //callOnClick()
/*                    btnRun.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnRun.setText("eeeee");
                            //tv1.setText("已?被点??了");
                        }
                    });*/
                    //btnRun.performClick(); //callOnClick()
                }
            }
        });

        //changeViewSize(ccc,1000,800);
    }

    public void onResume() {
        super.onResume();
        //140909 add by wyq testdataを差し込む
        dbregnote = new RegistrationDBAdapter(AddressSelect.this);
        dbregnote.open();
        Cursor c = dbregnote.getAllNotes();

        startManagingCursor(c);
        if (!c.moveToFirst()) {
            dbregnote.saveNote("鳥取砂丘","1457");
            dbregnote.saveNote("鳥取駅(バス停)","1791");
            dbregnote.saveNote("倉吉駅(バス停)","20431");
            dbregnote.saveNote("鳥商前(バス停)","783");
            dbregnote.saveNote("安蔵森林公園","198");
        } else {
            dbregnote.deleteNull();
        }

        for (int i=1; i<(c.getCount()<arrbtn.length?c.getCount()+1:arrbtn.length); i++) {
            Note note = new Note(c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL_ID)),
                    c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_NOTE)),
                    c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_LASTUPDATE)) );
            arrbtn[i].setText(note.getNote());
            arrbtn[i].setTag(note.getLastupdate());

            if (arrbtn[i].getText().length() > 16)
                arrbtn[i].setTextSize(size * 2/3);
            else
                arrbtn[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

            c.moveToNext();
        }
        stopManagingCursor(c);
        dbregnote.close();

        //150615 add by wyq XMLから出発地を読みます
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getInt("START_ID", -1) >= 0) {
            final PointModel departureModel = new PointModel(sp.getInt("START_ID", 0), sp.getString("START_NAME", "..."),
                    "", sp.getInt("START_LONGITUDE", 0), sp.getInt("START_LATITUDE", 0), 0);
            BasicModel.setDeparture(departureModel);
        }
        arrbtn[0].setText(getResources().getString(R.string.departure) + ":" + BasicModel.getDeparture().getName());
        //150615 end
    }


    //重置字体 nouse
    public static void changeViewSize(ViewGroup viewGroup,int screenWidth,int screenHeight) {//?入Activity??Layout,屏幕?,屏幕高
        int adjustFontSize = adjustFontSize(screenWidth,screenHeight);
        for (int i = 0; i<viewGroup.getChildCount(); i++ ) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                changeViewSize((ViewGroup)v,screenWidth,screenHeight);
            } else if (v instanceof Button) {//按?加大?个一定要放在TextView上面，因?Button也?承了TextView
                ((Button)v).setTextSize(adjustFontSize+2);
            } else if (v instanceof TextView) {
                if (v.getId()== R.id.address_select_self) {//?部??
                    ((TextView)v).setTextSize(adjustFontSize+4);
                } else {
                    ((TextView)v).setTextSize(adjustFontSize);
                }
            }
        }
    }

    //字体大小
    public static int adjustFontSize(int screenWidth, int screenHeight) {
        screenWidth=screenWidth>screenHeight?screenWidth:screenHeight;
        /**
         * 1. 在??的 onsizechanged里?取???度，一般情况下默??度是320，所以?算一个?放比率
         rate = (float) w/320   w是???度
         2.然后在?置字体尺寸? paint.setTextSize((int)(8*rate));   8是在分辨率??320 下需要?置的字体大小
         ??字体大小 = 默?字体大小 x  rate
         */
        int rate = (int)(5*(float) screenWidth/320); //我自己???个倍数比??合，当然?可以??后再修改
        return rate<15?15:rate; //字体太小也不好看的
    }
}
