package com.nyw.mobilesafe.view;

import com.nyw.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �������ĵ��Զ�����Ͽؼ�
 * 
 * @author ning
 * 
 */
public class SettingItemView extends RelativeLayout {
	private static final String NAMESPCE = "http://schemas.android.com/apk/res/com.nyw.mobilesafe";
	private TextView tvTitle;
	private CheckBox cbStatus;
	private TextView tvDesc;
	private String mTitle;
	private String mDescOn;
	private String mDescOff;

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTitle = attrs.getAttributeValue(NAMESPCE, "title");// �����������ƻ�ȡ���Ե�ֵ
		mDescOn = attrs.getAttributeValue(NAMESPCE, "desc_on");
		mDescOff = attrs.getAttributeValue(NAMESPCE, "desc_off");
		initView();

		// int attributeCount = attrs.getAttributeCount();
		// for (int i = 0; i < attributeCount; i++) {
		// String attributeName = attrs.getAttributeName(i);
		// String attributeValue = attrs.getAttributeValue(i);
		// System.out.println(attributeName + "=" + attributeValue);
		// }

	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}

	/**
	 * ��ʼ������
	 */
	private void initView() {
		// ���Զ���õĲ����ļ����ø���ǰ��SettingItemView
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		cbStatus = (CheckBox) findViewById(R.id.cb_status);

		setTitle(mTitle);// ���ñ���
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	public void setDesc(String desc) {
		tvDesc.setText(desc);
	}

	/**
	 * �жϵ�ǰ��CheckBox�Ƿ�ѡ��ѡ
	 * 
	 * @return
	 */
	public boolean isChecked() {
		return cbStatus.isChecked();
	}

	public void setChecked(boolean check) {
		cbStatus.setChecked(check);

		// ����ѡ���״̬,�����ı�����
		if (check) {
			setDesc(mDescOn);
		} else {
			setDesc(mDescOff);
		}
	}
}
