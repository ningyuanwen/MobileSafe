package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ��3��������ҳ
 * 
 * @author ning
 * 
 */
public class Setup3Activity extends BaseSetupActivity {

	private EditText etPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup3);

		etPhone = (EditText) findViewById(R.id.et_phone);

		String phone = mPref.getString("safe_phone", "");
		etPhone.setText(phone);
	}

	/**
	 * չʾ��һҳ
	 */
	@Override
	public void showPreviousPage() {
		startActivity(new Intent(this, Setup2Activity.class));
		finish();

		// ��������֮����л�����
		overridePendingTransition(R.anim.tran_previous_in,
				R.anim.tran_previous_out);// ���붯�����˳�����
	}

	/**
	 * չʾ��һҳ
	 */
	@Override
	public void showNextPage() {

		String phone = etPhone.getText().toString().trim(); // ���˿ո�
		if (TextUtils.isEmpty(phone)) {
			// ToastUtils���Զ����Toast������
			ToastUtils.showToast(this, "��ȫ���벻��Ϊ��");
			return;
		}

		mPref.edit().putString("safe_phone", phone).commit();// ���氲ȫ����

		startActivity(new Intent(this, Setup4Activity.class));
		finish();

		// ��������֮����л�����
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// ���붯�����˳�����
	}

	/**
	 * ѡ����ϵ��
	 * 
	 * @param view
	 */
	public void selectContact(View view) {
		Intent intent = new Intent(this, ContactActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			String phone = data.getStringExtra("phone");
			phone = phone.replaceAll("-", "").replaceAll(" ", "");// �滻�绰����Ķ̺��

			etPhone.setText(phone);// �ѵ绰�������ø������
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
