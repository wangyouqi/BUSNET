package jp.ikisaki.www;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;

import java.util.*;
import java.io.*;
import java.net.*;
import org.json.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by wangyouqi on 2014/05/28.
 */
public class Common extends Application {
    public static List<Activity> mActivityList = new ArrayList<Activity>();
    public static Bitmap bmInfoWindow = null;
    public static String strCommon = "";
    public static int RADIUS = 500; //meter
    public static int DBVER = 20010101;
    public static String APPVER = "1.0";
    public static boolean CHKFULL = false;
    public static boolean CHKUPDATE = false;
    public static boolean CHKSETTING = false;
    public static final String DBNAME = "busnet.db";
    public static final String DBPATH ="/data/data/jp.ikisaki.www/databases/";
    public static Map UPINFO = new HashMap<String, Object>();
    public ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

    public static void actionbartool(ActionBar actionbar) {
        //hilde()和show()控制actionbar隐藏和表示
        //ActionBar actionbar = this.getActionbar();
        //actionbar.hide().show();
        actionbar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);

        //160622 add by wyq fullscreen
        int num = mActivityList.size();
        if (mActivityList.get(num-1) != null & CHKFULL) {
            mActivityList.get(num-1).getWindow().getDecorView().setSystemUiVisibility(
                    //View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                    //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | 0x00001000 //| View.SYSTEM_UI_FLAG_IMMERSIVE
            );  actionbar.hide();
        }    //160622 end
    }

    public static boolean menu(MenuItem item, Context context) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                Intent mainActivityIntent = new Intent(context, BusnetActivity.class);
                mainActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent);
                //finish();
                System.out.println("logcat:10:" + "Click Home！");
                return true;

            case R.id.action_start:
                //Instrumentation inst=new Instrumentation();
                //inst.sendKeyDownUpSync(1001);

                Intent startActivityIntent = new Intent(context, AddressSelect.class);
                startActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startActivityIntent);
                return true;

            case R.id.action_back:
                //Instrumentation inst=new Instrumentation(KeyEvent.KEYCODE_BACK);
                //inst.sendKeyDownUpSync(1001);
                int num = mActivityList.size();
                for (int i = num; i == num; i--) { //closed the last activity onetime
                    if (mActivityList.get(i-1) != null) {
                        mActivityList.get(i-1).finish();
                        mActivityList.remove(i-1);
                    }
                }
                return true;

            case R.id.action_settings:
                Intent settingsActivityIntent = new Intent(context, SettingsActivity.class);
                context.startActivity(settingsActivityIntent);
                return true;

            case R.id.action_snapshot:
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(new File(takeScreenShot(mActivityList.get(mActivityList.size() - 1)))) );
                context.sendBroadcast(scanIntent);

                Toast.makeText(context, "アルバムに保存しました！", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_record:
                Intent recordActivityIntent = new Intent(context, SettingActivity.class);
                context.startActivity(recordActivityIntent);
                return true;

            case 1111:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //削除cache
                context.startActivity(intent);
                System.exit(0);

            default:
                break;
        }
        return false;
        //20140416 test by wyq
        //return super.onOptionsItemSelected(item);
        //return Common.menu(item, this);
    }

    public static void enterLightsOutMode(Window window) {
        //Common.enterLightsOutMode(getWindow());
        //hide bottom bar
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        //params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.setAttributes(params);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //スクリーンショット
    public static String takeScreenShot(Activity activity) {
        String filepath = Environment.getExternalStorageDirectory() + File.separator +
                "DCIM" + File.separator + UUID.randomUUID().toString() + ".PNG" ;
        //FileUtils.getInstance().getStorePicFile(activity);
        View rootView = activity.getWindow().getDecorView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();
        Bitmap bitmap = rootView.getDrawingCache();
        File imagePath = new File(filepath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
        } finally {
            try {
                fos.close();
                bitmap.recycle();
                bitmap = null;
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            rootView.destroyDrawingCache();
            rootView.setDrawingCacheEnabled(false);
        }
        return filepath;
    }

    //////////////////////////////////////////////////////////////
    //初期化　JSONとデータベース
     public static void init(Context context) {
         //UIthread can use network
         if (android.os.Build.VERSION.SDK_INT > 14) {
             StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
             StrictMode.setThreadPolicy(policy);
         }

         //get version
         SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
         //SharedPreferences sharedPreferences = context.getSharedPreferences("setting", 0);
         CHKUPDATE = sharedPreferences.getBoolean("UPDATE_ENABLED", CHKUPDATE);
         CHKFULL = sharedPreferences.getBoolean("FULL", CHKFULL);
         DBVER = sharedPreferences.getInt("dbVersion", DBVER);
         RADIUS = sharedPreferences.getInt("RADIUS", RADIUS) * 10;
         String UPSRV ="http://192.168.1.10/";

         ApplicationInfo appinfo; PackageInfo pkginfo;
         try {
             appinfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
             pkginfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
             UPSRV = appinfo.metaData.getString("upsrv");
             APPVER = pkginfo.versionName;
             //PACKAGENAME = appinfo.packageName;
         } catch (PackageManager.NameNotFoundException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }

         //update app
         if (CHKUPDATE == false) {
             UPINFO.put("dbup", false);
             return;
         }

         String strJSON = urlText(UPSRV + "update.txt");
         if (strJSON.length() > 0) {
             UPINFO = parseUpdateJSON(strJSON);
             if (Integer.decode(UPINFO.get("dbver").toString()) != Math.abs(DBVER)) {
                 UPINFO.put("dbup", true);
                 DBVER = ((Integer)UPINFO.get("dbver")).intValue();
             } else
                UPINFO.put("dbup", false);
         } else {
             //can not load JSON
             DBVER = DBVER * -1;
             UPINFO.put("dbup", false);
             UPINFO.put("dbtip", "サーバーに接続できません");
         }

         //database update
         if (((Boolean)UPINFO.get("dbup")).booleanValue()) {
             try {
                 File file = new File(DBPATH + DBNAME);
                 OutputStream dbOut = new FileOutputStream(DBPATH + DBNAME);
                 InputStream dbIn = null;

                 if (!file.exists()) {
                     //create file
                     file.createNewFile();
                     file.setLastModified(111111111);
                     //file.mkdirs();

                     dbIn = context.getAssets().open(DBNAME);
                 } else {
                     dbIn = Common.urlStream(Common.UPINFO.get("dburl").toString());
                 }

                 byte[] buffer = new byte[1024];
                 int length = 0;
                 while ((length = dbIn.read(buffer)) > 0) {
                     dbOut.write(buffer, 0, length);
                 }

                 dbOut.flush();
                 dbOut.close();
                 dbIn.close();

                 //update config
                 sharedPreferences.edit().putInt("dbVersion", DBVER).commit();
                 //sharedPreferences.edit().commit();
             } catch (IOException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
         }
    }

     private static Map<String, Object> parseUpdateJSON(String JSONString) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            JSONObject person = new JSONObject(JSONString);
            resultMap.put("dbver", person.getInt("dbver"));
            resultMap.put("dburl", person.getString("dburl"));
            resultMap.put("dbtip", person.getString("dbtip"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultMap;
    }

    //get text from url
    public static String urlText(String strUrl) {
        // HttpGet object
        String strResult = "";
        HttpGet httpRequest = new HttpGet(strUrl);
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setIntParameter("http.connection.timeout", 5000);
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // return text
                strResult = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            strResult=new String(strResult.getBytes("8859_1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            //return "strResult";
        }
        return strResult;
    }

    //get file from url getInternetStream
    public static InputStream urlStream(String strURL) throws IOException {
        InputStream inputstream = null;
        URL url = new URL(strURL);
        if (url != null) {
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(5000);  //timeout
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();   //get server code
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputstream = httpURLConnection.getInputStream();
            }
        }
        return inputstream;
    }

    //get bitmap from url
    public static Bitmap urlBitMap(String url) {
        URL myUrl = null;
        Bitmap bitmap = null;
        try {
            myUrl = new URL(url);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    //get local file
    public static Bitmap getLocalBitmap(String strPath) {
        try {
            InputStream fis = new FileInputStream(strPath);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    //get assest file
    public static Drawable getLocalFile(Context context,String strURL) {
        try {
            //InputStream is = (InputStream) new URL(strURL).getContent();
            InputStream is = context.getResources().getAssets().open(strURL);
            return Drawable.createFromStream(is, "src name");
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("logcat:11:" + e.getMessage());
            return null;
        }
    }

    public static boolean setLocation(Context context) {
        //SharedPreferencesから出発地の設定を保存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt("START_ID", BasicModel.getDeparture().getId()).commit();
        sp.edit().putString("START_NAME", BasicModel.getDeparture().getName()).commit();
        sp.edit().putInt("START_LATITUDE", BasicModel.getDeparture().getLatitude()).commit();
        sp.edit().putInt("START_LONGITUDE", BasicModel.getDeparture().getLongitude()).commit();
        return true;
    }

    //Thread and Handler
    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            System.out.println("logcat:11:" + val);
        }
    };

    public static Runnable raUrlImage = new Runnable() {
        @Override
        public void run() {
            // TODO: http request.
            bmInfoWindow = urlBitMap(strCommon);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "Send OK to Handler");
            msg.setData(data);
            handler.sendMessage(msg);
            //handler.sendEmptyMessage(0);
        }
    };
}
