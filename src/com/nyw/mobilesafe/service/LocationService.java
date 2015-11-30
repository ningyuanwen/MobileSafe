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
 * ��ȡ��γ�������Service
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
		// List<String> allProviders = lm.getAllProviders();// ��ȡ����λ���ṩ��
		// System.out.println(allProviders);

		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);// �Ƿ�������,����ʹ��3G���綨λ
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = lm.getBestProvider(criteria, true);// ��ȡ��ѵ�λ���ṩ��

		listener = new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, listener);// ����1λ���ṩ��,����2��̵ĸ���ʱ��,����3��̵ĸ��¾���,����4
	}

	class MyLocationListener implements LocationListener {

		// λ�÷����仯ʱ
		@Override
		public void onLocationChanged(Location location) {
			// ����ȡ�ľ�γ�ȱ�����sp��
			mPref.edit()
					.putString(
							"location",
							"longitude" + location.getLongitude() + ";"
									+ "latitude:" + location.getLatitude())
					.commit();
			stopSelf();// ͣ��service
		}

		// λ���ṩ��״̬�����仯ʱ
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			System.out.println("onStatusChanged");

		}

		// �û���GPSʱ
		@Override
		public void onProviderEnabled(String provider) {
			System.out.println("onProviderEnabled");
		}

		// �û��ر�GPSʱ
		@Override
		public void onProviderDisabled(String provider) {
			System.out.println("onProviderDisabled");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);// ��activity����ʱ,ֹͣ����λ��,ʡ��
	}

}
