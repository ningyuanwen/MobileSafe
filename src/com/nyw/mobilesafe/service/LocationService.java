package com.nyw.mobilesafe.service;

import java.util.List;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * 获取经纬度坐标的Service
 * 
 * @author ning
 * 
 */
public class LocationService extends Service {

	private LocationManager lm;
	private MyLocationListener listener;
	private SharedPreferences mPref;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPref = getSharedPreferences("config", MODE_PRIVATE);
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// List<String> allProviders = lm.getAllProviders();// 获取所有位置提供者
		// System.out.println(allProviders);

		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);// 是否允许付费,比如使用3G网络定位
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = lm.getBestProvider(criteria, true);// 获取最佳的位置提供者

		listener = new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, listener);// 参数1位置提供者,参数2最短的更新时间,参数3最短的更新距离,参数4
	}

	class MyLocationListener implements LocationListener {

		// 位置发生变化时
		@Override
		public void onLocationChanged(Location location) {
			// 将获取的经纬度保存在sp中
			mPref.edit()
					.putString(
							"location",
							"longitude" + location.getLongitude() + ";"
									+ "latitude:" + location.getLatitude())
					.commit();
			stopSelf();// 停掉service
		}

		// 位置提供者状态发生变化时
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			System.out.println("onStatusChanged");

		}

		// 用户打开GPS时
		@Override
		public void onProviderEnabled(String provider) {
			System.out.println("onProviderEnabled");
		}

		// 用户关闭GPS时
		@Override
		public void onProviderDisabled(String provider) {
			System.out.println("onProviderDisabled");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);// 当activity销毁时,停止更新位置,省电
	}

}
