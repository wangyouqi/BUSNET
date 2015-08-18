package jp.ikisaki.www;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * マップ上に独自アイコンを表示するためのオーバレイ
 *
 */
public class UserIconsOverlay extends ItemizedOverlay<OverlayItem> {

	private Drawable nikko = null;
	private Drawable hinomaru = null;
	private Drawable other = null;
	private ArrayList<OverlayItem> overlay_items = new ArrayList<OverlayItem>();
	private Context context;

	/* --------- 基本 -------- */

	public UserIconsOverlay(Drawable otherMarker, Drawable nikkoMarker, Drawable hinomaruMarker, Context context) {
		super(boundCenterBottom(otherMarker));
		this.context = context;

		nikko = nikkoMarker;
		hinomaru = hinomaruMarker;
		other = otherMarker;

		// コンストラクタ内でpopulate() しないと，NullPointerExceptionになるので。
		// http://developmentality.wordpress.com/2009/10/19/android-itemizedoverlay-arrayindexoutofboundsexception-nullpointerexception-workarounds/
		// populate()がない場合，ユーザ画面からホーム画面に戻って最初の数秒の
		// まだGPS情報が取得されるよりも前の時点でタップすると落ちた。
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlay_items.get(i);
	}

	@Override
	public int size() {
		return overlay_items.size();
	}

	/* --------- アイコンの描画 -------- */

	// アイテムの追加
	public void addOverlayItem(OverlayItem overlayItem) {
		//System.out.println("logcat:3:name:" + overlayItem.getSnippet());
		if(overlayItem.getSnippet().indexOf("日ノ丸") != -1){
			overlayItem.setMarker(boundCenterBottom(hinomaru));
		}
		else if((overlayItem.getSnippet().indexOf("日本交通") != -1) || (overlayItem.getTitle().indexOf("日交") != -1)){
			overlayItem.setMarker(boundCenterBottom(nikko));
		}
		else{
			overlayItem.setMarker(boundCenterBottom(other));
		}
		overlay_items.add(overlayItem);
		populate();
	}

	public void removeOverlayItem() {
		overlay_items.removeAll(overlay_items);
	}

	// ダミーアイコンを描画
	public void draw_dummy_icons(
			ArrayList<BusLocationModel> busLocationArrayList) {
		// int lat = g.getLatitudeE6();
		// int lng = g.getLongitudeE6();

		// addOverlayItem( new OverlayItem(
		// new GeoPoint(lat + 1000, lng + 1000),
		// "ダミー",
		// "ダミーですよ！"
		// ) );
		//
		// addOverlayItem( new OverlayItem(
		// new GeoPoint(lat - 3000, lng - 3000),
		// lat + "",
		// lng + ""
		// ) );

		removeOverlayItem();

		for (int i = 0; i < busLocationArrayList.size(); i++) {
			int latitude, longitude;

			double la = Double.valueOf(busLocationArrayList.get(i)
					.getLatitude()) / 1E6 / 0.36;
			double ln = Double.valueOf(busLocationArrayList.get(i)
					.getLongitude()) / 1E6 / 0.36;

			// latitude = (int)((la + la * 0.00010695 - ln * 0.000017464 -
			// 0.0046017) * 1E6);
			// longitude = (int)((ln + la * 0.000046038 + ln * 0.000083043 -
			// 0.010040) * 1E6);

			latitude = (int) ((la) * 1E6);
			longitude = (int) ((ln) * 1E6);

			addOverlayItem(new OverlayItem(new GeoPoint(latitude, longitude),
					busLocationArrayList.get(i).getRosenName() + " "
							+ busLocationArrayList.get(i).getDestination()
							+ "行き", busLocationArrayList.get(i).getHour() + "時"
							+ busLocationArrayList.get(i).getMinute() + "分現在 (" + busLocationArrayList.get(i).getCompanyName() + ")"
							+ "\n"
							+ busLocationArrayList.get(i).getDelayMinute()
							+ "分遅れ"));

			// System.out.println("logcat:3:" + i + " " + latitude + ":"
			// + longitude + ":"
			// + busLocationArrayList.get(i).getDestination());
		}

		System.out.println("logcat:3:dasyutu");

	}

	// アイコンをタップ時
	@Override
	protected boolean onTap(int index) {

		OverlayItem item = overlay_items.get(index);

		// AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		// dialog.setTitle(item.getTitle());
		// dialog.setMessage(item.getSnippet());
		// dialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
		// {
		// public void onClick(DialogInterface dialog, int which) {
		// // OKを押した場合の処理
		//
		// }
		// });
		// dialog.show();

		Toast toast = Toast.makeText(context, item.getTitle() + "\n" + item.getSnippet(), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		return true;
	}

}
