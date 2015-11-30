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
 * 设置中心
 * 
 * @author ning
 * 
 */
public class SettingActivity extends Activity {
	private SettingItemView sivUpdate;// 设置中心的升级选项
	private SettingItemView sivAddress;// 设置中心的来电归属地显示选项
	private SettingClickView scvAddressStyle;// 归属地提示窗的显示风格
	private SettingClickView scvAddressLocation;// 归属地提示窗的位置

	private SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		initUpdateView();// 初始化自动更新开关
		initAddressView();// 初始化归属地开关
		initAddressStyle();// 初始化修改归属地提示窗的显示风格
		initAddressLocation();// 初始化修改归属地提示框的位置
	}

	/**
	 * 初始化自动更新开关
	 */
	private void initUpdateView() {
		sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
		// sivUpdate.setTitle("自动更新设置");

		// 判断是否需要自动更新
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {
			// sivUpdate.setDesc("自动更新已经开启");
			sivUpdate.setChecked(true);
		} else {
			// sivUpdate.setDesc("自动更新已经关闭");
			sivUpdate.setChecked(false);
		}

		sivUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 判断当前CheckBox的勾选状态
				if (sivUpdate.isChecked()) {
					// 设置成不勾选
					sivUpdate.setChecked(false);
					// sivUpdate.setDesc("自动更新已经关闭");

					// 更新SharedPreferences
					mPref.edit().putBoolean("auto_update", false).commit();
				} else {
					// 设置成勾选
					sivUpdate.setChecked(true);
					// sivUpdate.setDesc("自动更新已经开启");

					// 更新SharedPreferences
					mPref.edit().putBoolean("auto_update", true).commit();
				}
			}
		});
	}

	/**
	 * 初始化归属地开关
	 */
	private void initAddressView() {

		sivAddress = (SettingItemView) findViewById(R.id.siv_address);

		// 根据来电归属地服务是否运行来更新CheckBox
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
					// 设置成不勾选
					sivAddress.setChecked(false);

					stopService(new Intent(SettingActivity.this,
							AddressService.class));// 停止归属地查询服务
				} else {
					// 设置成勾选
					sivAddress.setChecked(true);
					startService(new Intent(SettingActivity.this,
							AddressService.class));// 开启归属地查询服务
				}
			}
		});
	}

	final String[] items = new String[] { "半透明", "狗屎橙", "卫士蓝", "金属灰", "苹果绿" };

	/**
	 * 初始化修改归属地提示窗的显示风格
	 */
	private void initAddressStyle() {
		scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);
		scvAddressStyle.setTitle("归属地提示框风格");// 设置单选框标题

		int style = mPref.getInt("address_style", 0);// 读取保存的提示框风格
		scvAddressStyle.setDesc(items[style]);

		scvAddressStyle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showSingleChoseDailog();
			}
		});
	}

	/**
	 * 弹出选择提示窗风格的单选框
	 */
	protected void showSingleChoseDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("归属地提示框风格");

		int style = mPref.getInt("address_style", 0);// 读取保存的提示框风格

		builder.setSingleChoiceItems(items, style,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mPref.edit().putInt("address_style", which).commit();// 保存选择的风格
						dialog.dismiss();// 让单选框消失
						scvAddressStyle.setDesc(items[which]);// 更新已选中组合控件的描述信息
					}
				});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 初始化修改归属地提示框的位置
	 */
	private void initAddressLocation() {
		scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
		scvAddressLocation.setTitle("归属地提示框的位置");
		scvAddressLocation.setDesc("设置归属地提示框的显示位置");

		scvAddressLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(SettingActivity.this,
						DragViewActivity.class));
			}
		});
	}
}
