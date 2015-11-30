package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;

/*
 * ��������ҳ�ĸ���(����)
 * ����Ҫ���嵥�ļ�(AndroidManifest.xml)��ע��,��Ϊ�ý��治��Ҫչʾ
 */
public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mDectector;
	public SharedPreferences mPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPref = getSharedPreferences("config", MODE_PRIVATE);
		
		// ����ʶ����
		mDectector = new GestureDetector(this, new SimpleOnGestureListener() {
			// �������ƻ����¼�
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) { // onFling�Ĳ���e1��ʾ��������㣬e2��ʾ�������յ㣬velocityX��ʾˮƽ����Ļ����ٶȣ�velocityY��ʾ��ֱ����Ļ����ٶ�

				if(Math.abs(velocityX) < 150)
				{
					Toast.makeText(BaseSetupActivity.this, "����̫�������л�ҳ��  �������",
							Toast.LENGTH_SHORT).show();
					return true;
				}
				
				// �ж����򻬶��Ƿ����,�������̫�󲻻��л�����
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
					Toast.makeText(BaseSetupActivity.this, "Ҫ���Ż�  �������",
							Toast.LENGTH_SHORT).show();
					return true;
				}

				// ���һ�,������һҳ
				if (e2.getRawX() - e1.getRawX() > 200) {
					showPreviousPage();
					return true;
				}

				// ����,������һҳ
				if (e1.getRawX() - e2.getRawX() > 200) {
					showNextPage();
					return true;
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});

	}

	/**
	 * չʾ��һҳ,��Ϊ���󷽷�,�������ʵ��
	 */
	public abstract void showPreviousPage();

	/**
	 * չʾ��һҳ,��Ϊ���󷽷�,�������ʵ��
	 */
	public abstract void showNextPage();

	/**
	 * �����"��һ��"��ť
	 * 
	 */
	public void next(View view) {
		showNextPage();
	}

	/**
	 * �����"��һ��"��ť
	 * 
	 */
	public void previous(View view) {
		showPreviousPage();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDectector.onTouchEvent(event);// ί������ʶ�����������¼�
		return super.onTouchEvent(event);
	}
}
