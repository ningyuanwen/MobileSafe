package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;
import com.nyw.mobilesafe.db.dao.AddressDao;

import android.app.Activity;
import android.location.Address;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 归属地查询页面
 * 
 * @author ning
 * 
 */
public class AddressActivity extends Activity {

	private EditText etNumber;
	private TextView tvResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_address);
		etNumber = (EditText) findViewById(R.id.et_number);
		tvResult = (TextView) findViewById(R.id.tv_result);

		// 监听EditText的变化
		etNumber.addTextChangedListener(new TextWatcher() {

			// 文本框的文字发生变化时的回调
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String address = AddressDao.getAddress(s.toString());
				tvResult.setText(address);
			}

			// 文本框的文字变化前的回调
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			// 文本框的文字变化结束之后的回调
			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	/**
	 * 开始查询
	 * 
	 * @param view
	 */
	public void query(View view) {
		String number = etNumber.getText().toString().trim();

		if (!TextUtils.isEmpty(number)) {
			System.out.println("开始查询");
			String address = AddressDao.getAddress(number);
			tvResult.setText(address);
			System.out.println("查询完毕");
		} else {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			etNumber.startAnimation(shake);
			vibrate();
		}
	}

	/**
	 * 手机震动
	 */
	private void vibrate() {
		// 需要加权限
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		// vibrator.vibrate(2000);// 震动两秒
		vibrator.vibrate(new long[] { 0, 300, 100, 300, 100, 300 }, -1);// vibrate:参数1表示等待1秒震动2秒等待1秒震动3秒;参数2表示从第几个位置开始循环,-1表示只执行一次不循环
		// vibrator.cancel();// 取消震动
	}
}
