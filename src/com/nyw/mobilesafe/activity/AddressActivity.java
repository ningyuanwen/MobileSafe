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
 * �����ز�ѯҳ��
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

		// ����EditText�ı仯
		etNumber.addTextChangedListener(new TextWatcher() {

			// �ı�������ַ����仯ʱ�Ļص�
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String address = AddressDao.getAddress(s.toString());
				tvResult.setText(address);
			}

			// �ı�������ֱ仯ǰ�Ļص�
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			// �ı�������ֱ仯����֮��Ļص�
			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	/**
	 * ��ʼ��ѯ
	 * 
	 * @param view
	 */
	public void query(View view) {
		String number = etNumber.getText().toString().trim();

		if (!TextUtils.isEmpty(number)) {
			System.out.println("��ʼ��ѯ");
			String address = AddressDao.getAddress(number);
			tvResult.setText(address);
			System.out.println("��ѯ���");
		} else {
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			etNumber.startAnimation(shake);
			vibrate();
		}
	}

	/**
	 * �ֻ���
	 */
	private void vibrate() {
		// ��Ҫ��Ȩ��
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		// vibrator.vibrate(2000);// ������
		vibrator.vibrate(new long[] { 0, 300, 100, 300, 100, 300 }, -1);// vibrate:����1��ʾ�ȴ�1����2��ȴ�1����3��;����2��ʾ�ӵڼ���λ�ÿ�ʼѭ��,-1��ʾִֻ��һ�β�ѭ��
		// vibrator.cancel();// ȡ����
	}
}
