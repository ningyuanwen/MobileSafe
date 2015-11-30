package com.nyw.mobilesafe.activity;

import java.net.ServerSocket;

import com.nyw.mobilesafe.R;
import com.nyw.mobilesafe.service.AddressService;
import com.nyw.mobilesafe.utils.ServiceStatusUtils;
import com.nyw.mobilesafe.view.SettingClickView;
import com.nyw.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * ��������
 * 
 * @author ning
 * 
 */
public class SettingActivity extends Activity {
	private SettingItemView sivUpdate;// �������ĵ�����ѡ��
	private SettingItemView sivAddress;// �������ĵ������������ʾѡ��
	private SettingClickView scvAddressStyle;// ��������ʾ������ʾ���
	private SettingClickView scvAddressLocation;// ��������ʾ����λ��

	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		initUpdateView();// ��ʼ���Զ����¿���
		initAddressView();// ��ʼ�������ؿ���
		initAddressStyle();// ��ʼ���޸Ĺ�������ʾ������ʾ���
		initAddressLocation();// ��ʼ���޸Ĺ�������ʾ���λ��
	}

	/**
	 * ��ʼ���Զ����¿���
	 */
	private void initUpdateView() {
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		// sivUpdate.setTitle("�Զ���������");

		// �ж��Ƿ���Ҫ�Զ�����
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {
			// sivUpdate.setDesc("�Զ������Ѿ�����");
			sivUpdate.setChecked(true);
		} else {
			// sivUpdate.setDesc("�Զ������Ѿ��ر�");
			sivUpdate.setChecked(false);
		}

		sivUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// �жϵ�ǰCheckBox�Ĺ�ѡ״̬
				if (sivUpdate.isChecked()) {
					// ���óɲ���ѡ
					sivUpdate.setChecked(false);
					// sivUpdate.setDesc("�Զ������Ѿ��ر�");

					// ����SharedPreferences
					mPref.edit().putBoolean("auto_update", false).commit();
				} else {
					// ���óɹ�ѡ
					sivUpdate.setChecked(true);
					// sivUpdate.setDesc("�Զ������Ѿ�����");

					// ����SharedPreferences
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
	}

	/**
	 * ��ʼ�������ؿ���
	 */
	private void initAddressView() {

		sivAddress = (SettingItemView) findViewById(R.id.siv_address);

		// ������������ط����Ƿ�����������CheckBox
		boolean serviceRunning = ServiceStatusUtils.isServiceRunning(this,
				"com.nyw.mobilesafe.service.AddressService");

		if (serviceRunning) {
			sivAddress.setChecked(true);
		} else {
			sivAddress.setChecked(false);
		}

		sivAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (sivAddress.isChecked()) {
					// ���óɲ���ѡ
					sivAddress.setChecked(false);

					stopService(new Intent(SettingActivity.this,
							AddressService.class));// ֹͣ�����ز�ѯ����
				} else {
					// ���óɹ�ѡ
					sivAddress.setChecked(true);
					startService(new Intent(SettingActivity.this,
							AddressService.class));// ���������ز�ѯ����
				}
			}
		});
	}

	final String[] items = new String[] { "��͸��", "��ʺ��", "��ʿ��", "������", "ƻ����" };

	/**
	 * ��ʼ���޸Ĺ�������ʾ������ʾ���
	 */
	private void initAddressStyle() {
		scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
		scvAddressStyle.setTitle("��������ʾ����");// ���õ�ѡ�����

		int style = mPref.getInt("address_style", 0);// ��ȡ�������ʾ����
		scvAddressStyle.setDesc(items[style]);

		scvAddressStyle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showSingleChoseDailog();
			}
		});
	}

	/**
	 * ����ѡ����ʾ�����ĵ�ѡ��
	 */
	protected void showSingleChoseDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("��������ʾ����");

		int style = mPref.getInt("address_style", 0);// ��ȡ�������ʾ����

		builder.setSingleChoiceItems(items, style,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPref.edit().putInt("address_style", which).commit();// ����ѡ��ķ��
						dialog.dismiss();// �õ�ѡ����ʧ
						scvAddressStyle.setDesc(items[which]);// ������ѡ����Ͽؼ���������Ϣ
					}
				});
		builder.setNegativeButton("ȡ��", null);
		builder.show();
	}

	/**
	 * ��ʼ���޸Ĺ�������ʾ���λ��
	 */
	private void initAddressLocation() {
		scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
		scvAddressLocation.setTitle("��������ʾ���λ��");
		scvAddressLocation.setDesc("���ù�������ʾ�����ʾλ��");

		scvAddressLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(SettingActivity.this,
						DragViewActivity.class));
			}
		});
	}
}
