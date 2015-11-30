package com.nyw.mobilesafe.receiver;

import com.nyw.mobilesafe.db.dao.AddressDao;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * ����ȥ��Ĺ㲥������ ��ҪȨ��:android.permission.PROCESS_OUTGOING_CALLS
 * 
 * @author ning
 * 
 */
public class OutCallReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String number = getResultData();
		String address = AddressDao.getAddress(number);
		Toast.makeText(context, address, Toast.LENGTH_LONG).show();
	}

}
