package com.nyw.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * 获取焦点的TextView
 * 
 * @author ning
 * 
 */
public class FocusedTextView extends TextView {

	// 有style样式的话,走此方法
	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	// 有属性的时候,走此方法
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// 用代码new对象时,走此方法
	public FocusedTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 表示有没有获取焦点
	 * 
	 * 跑马灯要运行首先要运行此函数判断是否有焦点,是true的话,跑马灯才会有效果
	 * 所以不管实际上textView有没有焦点,都强制返回true,让跑马灯有焦点
	 */
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {

		return true;
	}
}
