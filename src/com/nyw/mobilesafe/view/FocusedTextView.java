package com.nyw.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * ��ȡ�����TextView
 * 
 * @author ning
 * 
 */
public class FocusedTextView extends TextView {

	// ��style��ʽ�Ļ�,�ߴ˷���
	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	// �����Ե�ʱ��,�ߴ˷���
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// �ô���new����ʱ,�ߴ˷���
	public FocusedTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * ��ʾ��û�л�ȡ����
	 * 
	 * �����Ҫ��������Ҫ���д˺����ж��Ƿ��н���,��true�Ļ�,����ƲŻ���Ч��
	 * ���Բ���ʵ����textView��û�н���,��ǿ�Ʒ���true,��������н���
	 */
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {

		return true;
	}
}
