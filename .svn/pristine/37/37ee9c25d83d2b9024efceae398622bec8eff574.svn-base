//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.StringTokenizer;
//
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.speech.RecognizerIntent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//@Override
//	public void onLocationChanged(Location location) {
//		currentLatitude = location.getLatitude();
//		currentLongitude = location.getLongitude();
//
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//					}
//				});
//				loadGps();
//				handler.post(new Runnable() {
//					@Override
//					public void run()
//						Toast.makeText(SpecifyPointActivity.this, landmarkArray.get(0).getName(), Toast.LENGTH_LONG).show();
//					}
//				});
//			}
//		}).start();
//	}
//
//
//
//
//	@Override
//	protected void onPause(){
//		if(manager != null){
//			manager.removeUpdates(this);
//		}
//		super.onPause();
//	}
//
//	@Override
//	protected void onResume(){
//		System.out.println("logcat::onresume");
//		if(manager != null){
//			System.out.println("logcat::null");
//			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);
//		}
//		super.onResume();
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//	}
//
//
//
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//	}
//
//	private void loadGps() {
//		double currentJapLongitude = currentLongitude;
//		double currentJapLatitude = currentLatitude;
//
//		currentJapLatitude = currentJapLatitude + currentJapLatitude * 0.00010696 - currentJapLongitude * 0.000017467 - 0.0046020;
//		currentJapLongitude = currentJapLongitude + currentJapLatitude * 0.000046047 + currentJapLongitude * 0.000083049 - 0.010041;
//
//		InputStream is = null;
//		BufferedReader br = null;
//		ArrayList<LandmarkModel> aroundLandmarkArray = new ArrayList<LandmarkModel>();
//
//		final StringBuilder sb = new StringBuilder();
//		try {
//			try {
//				is = getAssets().open("busstops.csv");
//				br = new BufferedReader(new InputStreamReader(is));
//
//				String str;
//				while ((str = br.readLine()) != null) {
//					sb.append(str + "\n");
//
//					StringTokenizer st = new StringTokenizer(str, ",");
//
//
//
//					int i = 0;
//
//					int id = 0;
//					String name = "";
//					String yomi = "";
//					int longitude = 0;
//					int latitude = 0;
//					int frequency = 0;
//					double lng = 0.0;
//					double lat = 0.0;
//
//
//
//					while (st.hasMoreTokens()) {
//						i++;
//
//						if (i == 1) {
//							id = Integer.valueOf(st.nextToken());
//						} else if (i == 2) {
//							name = st.nextToken();
//						} else if (i == 3) {
//							yomi = st.nextToken();
//						} else if (i == 4) {
//							lng = Double.valueOf(st.nextToken());
//
//						} else if (i == 5) {
//							lat = Double.valueOf(st.nextToken());
//
//						} else if (i == 6) {
//							frequency = Integer.valueOf(st.nextToken());
//							longitude = (int)(lng * 1E6 * 0.36);
//							latitude = (int)(lat * 1E6 * 0.36);
//
//							LandmarkModel aroundLandmarkModel = new LandmarkModel(
//									id, name, yomi, longitude, latitude,
//									frequency);
//
//
//
//							if (aroundLandmarkArray.size() == 0) {
//								System.out.println("logcat:0");
//								aroundLandmarkArray
//										.add(aroundLandmarkModel);
//							} else {
//								int k = aroundLandmarkArray.size();
//
//								for (int j = 0; j < k && j < 5; j++) {
//									if (aroundLandmarkModel.getFrequency() > 10) {
//										float[] preResult = new float[3];
//
//										Location.distanceBetween(
//												(double) aroundLandmarkArray
//														.get(j)
//														.getLongitude() / 1E6 / 0.36,
//												(double) aroundLandmarkArray
//														.get(j)
//														.getLatitude() / 1E6 / 0.36,
//												currentJapLatitude,
//												currentJapLongitude,
//												preResult);
//
//										float[] currentResult = new float[3];
//										Location.distanceBetween(
//												(double) aroundLandmarkModel
//														.getLongitude() / 1E6 / 0.36,
//												(double) aroundLandmarkModel
//														.getLatitude() / 1E6 / 0.36,
//												currentJapLatitude,
//												currentJapLongitude,
//												currentResult);
//
//										if (currentResult[0] < preResult[0] && !aroundLandmarkArray.get(j).getName().equals(aroundLandmarkModel.getName())) {
//
//
//											aroundLandmarkArray.add(j,
//													aroundLandmarkModel);
//											i = 0;
//											j = k;
//										}
//									}
//								}
//								i = 0;
//							}
//						}
//					}
//				}
//				for(int k = 0; k < 5; k++){
//					aroundString[k] = aroundLandmarkArray.get(k).getName();
//					aroundId[k] = aroundLandmarkArray.get(k).getId();
//				}
//			}
//
//			finally {
//				if (br != null) {
//					br.close();
//				}
//			}
//		} catch (IOException e) {
//
//		}
//	}