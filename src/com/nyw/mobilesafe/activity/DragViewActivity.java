package com.nyw.mobilesafe.activity;

import com.nyw.mobilesafe.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 修改归属地显示位置
 * 
 * @author ning
 * 
 */
public class DragViewActivity extends Activity {

	private TextView tvTop;
	private TextView tvBottom;
	private ImageView ivDrag;

	private int startX;
	private int startY;
	private SharedPreferences mPref;
	long[] mHits = new long[2];// 数组长度表示连击次数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		tvTop = (TextView) findViewById(R.id.tv_top);
		tvBottom = (TextView) findViewById(R.id.tv_bottom);
		ivDrag = (ImageView) findViewById(R.id.iv_drag);

		int lastX = mPref.getInt("lastX", MODE_PRIVATE);
		int lastY = mPref.getInt("lastY", MODE_PRIVATE);

		final int winWidth = getWindowManager().getDefaultDisplay().getWidth();
		final int winHeight = getWindowManager().getDefaultDisplay()
				.getHeight();

		// 根据图片位置,决定提示框显示
		if (lastY > winHeight / 2) {// 上面显示,下面隐藏
			tvTop.setVisibility(View.VISIBLE);
			tvBottom.setVisibility(View.INVISIBLE);
		} else {// 上面隐藏,下面显示
			tvTop.setVisibility(View.INVISIBLE);
			tvBottom.setVisibility(View.VISIBLE);
		}
		// 绘制一个界面的步骤:1.onMeasure(测量view),2.onLayout(安放位置),3.inDraw(绘制)
		// ivDrag.layout(lastX, lastY, lastX + ivDrag.getWidth(),lastY +
		// ivDrag.getHeight());//不能用这个方法,因为还没有测量完成,就不能安放位置

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag
				.getLayoutParams();// 获取布局对象
		layoutParams.leftMargin = lastX;// 设置边距
		layoutParams.topMargin = lastY;// 设置边距

		ivDrag.setLayoutParams(layoutParams);// 重新设置位置

		// 双击事件
		ivDrag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();// SystemClock.uptimeMillis()表示手机的开机后持续时间
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 把来电提示框居中
					ivDrag.layout(winWidth / 2 - ivDrag.getWidth() / 2,
							ivDrag.getTop(), winWidth / 2 + ivDrag.getWidth()
									/ 2, ivDrag.getBottom());
				}
			}
		});

		// 设置触摸监听
		ivDrag.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int endX = (int) event.getRawX();
					int endY = (int) event.getRawY();

					// 计算移动偏移量
					int dx = endX - startX;
					int dy = endY - startY;

					// 更新左右上下距离
					int l = ivDrag.getLeft() + dx;
					int r = ivDrag.getRight() + dx;
					int t = ivDrag.getTop() + dy;
					int b = ivDrag.getBottom() + dy;

					// 判断是否超出屏幕的边界
					if (l < 0 || r > winWidth || t < 0 || b > winHeight - 20) {
						break;
					}

					if (t > winHeight / 2) {// 上面显示,下面隐藏
						tvTop.setVisibility(View.VISIBLE);
						tvBottom.setVisibility(View.INVISIBLE);
					} else {// 上面隐藏,下面显示
						tvTop.setVisibility(View.INVISIBLE);
						tvBottom.setVisibility(View.VISIBLE);
					}

					// 更新界面
					ivDrag.layout(l, t, r, b);

					// 重新初始化起点坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					// 手指松开,记录下坐标点
					Editor edit = mPref.edit();
					edit.putInt("lastX", ivDrag.getLeft());
					edit.putInt("lastY", ivDrag.getTop());
					edit.commit();
					break;

				default:
					break;
				}

				return false;// 事件要往下传递,让onClick(双击事件)可以响应.写成true无法传递
			}
		});
	}
}
