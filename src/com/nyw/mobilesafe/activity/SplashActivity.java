package com.nyw.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nyw.mobilesafe.R;
import com.nyw.mobilesafe.utils.StreamUtils;

public class SplashActivity extends Activity {

	protected static final int CODE_UPDATE_DIALOG = 0;
	protected static final int CODE_URL_ERROR = 1;
	protected static final int CODE_NET_ERROR = 2;
	protected static final int CODE_JSON_ERROR = 3;
	protected static final int CODE_ENTER_HONE = 4;// 进到主页面

	private TextView tvVersion;// 版本号展示
	private TextView tvProgress;// 下载进度展示

	// 服务器返回的信息
	private String mVersionName; // 版本名
	private int mVersionCode;// 版本号
	private String mDesc;// 版本描述
	private String mDownloadUrl;// 下载链接

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDailog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL错误", 0).show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "网络错误", 0).show();
				enterHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "数据解析错误", 0).show();
				enterHome();
				break;
			case CODE_ENTER_HONE:
				enterHome();
				break;
			default:
				break;
			}

		};
	};
	private SharedPreferences mPref;
	private RelativeLayout rlRoot;// 根布局

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("版本名" + getVersionName());
		tvProgress = (TextView) findViewById(R.id.tv_progress);// 默认隐藏

		rlRoot = (RelativeLayout) findViewById(R.id.rL_root);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		copyDB("address.db");// 拷贝归属地查询数据库

		// 判断是否需要自动更新
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {
			checkVersion();
		} else {
			mHandler.sendEmptyMessageDelayed(CODE_ENTER_HONE, 2000);// 延时2秒后发送消息
		}

		// 让根布局的出现有渐变效果
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
		anim.setDuration(2000);
		rlRoot.startAnimation(anim);
	}

	/**
	 * 获取本地APP版本名称
	 * 
	 * @return
	 */
	private String getVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);// 获取包的信息

			String versionName = packageInfo.versionName;

			return versionName;

		} catch (NameNotFoundException e) {
			// 没有找到包名的时候会走此异常
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取本地APP版本号
	 * 
	 * @return
	 */
	private int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);// 获取包的信息

			int versionCode = packageInfo.versionCode;
			return versionCode;

		} catch (NameNotFoundException e) {
			// 没有找到包名的时候会走此异常
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 从服务器获取版本信息进行校验
	 */
	private void checkVersion() {
		final long startTime = System.currentTimeMillis();
		// 启动子线程去异步加载数据
		new Thread() {

			public void run() {

				Message msg = Message.obtain();
				HttpURLConnection conn = null;
				try {
					// 本机地址用localhost，但是如果用模拟器加载本机地址时,可以用IP(10.0.2.2)来替换
					URL url = new URL(""); // json串的路径
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");// 设置请求方法
					conn.setConnectTimeout(5000);// 设置连接超时
					conn.setReadTimeout(5000);// 设置响应超时
					conn.connect();// 连接服务器

					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream inpputStream = conn.getInputStream();
						String result = StreamUtils
								.readFromStream(inpputStream);

						System.out.println("网络返回" + result);

						// 解析json
						JSONObject jo = new JSONObject(result);// 专门用来解析json的类叫JSONObject

						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						mDownloadUrl = jo.getString("dowlloadUrl");

						System.out.println("打印测试json");

						if (mVersionCode > getVersionCode()) {// 对比服务器和本地的versionCode,判断是否需要更新APP
							// 如果服务器的版本大于本地的版本号
							// 说明有更新,弹出升级对话框
							msg.what = CODE_UPDATE_DIALOG;
							showUpdateDailog();
						} else {
							// 没有版本更新
							msg.what = CODE_ENTER_HONE;
						}

					}

				} catch (MalformedURLException e) {
					// url错误的异常
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// 网络错误异常
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// json解析失败异常
					msg.what = CODE_JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();

					long timeUsed = endTime - startTime;// 访问网络花费的时间
					if (timeUsed < 2000) {
						// 强制休眠一段时间,保证闪屏页面展示两秒钟
						try {
							Thread.sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					mHandler.sendMessage(msg);
					if (conn != null) {
						conn.disconnect();// 关闭网络连接
					}
				}
			}
		}.start();
	}

	/**
	 * 弹出升级对话框
	 */
	protected void showUpdateDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本" + mVersionName);
		builder.setMessage(mDesc);
		// builder.setCancelable(false);//不让取消升级对话框,用户体验太差,尽量不要用

		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				System.out.println("立即更新");
				download();
			}
		});

		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				enterHome();
			}
		});

		// 设置"取消键"的监听,当用户点击返回键时会触发
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				enterHome();
			}
		});

		builder.show();
	}

	/**
	 * 下载apk文件
	 */
	protected void download() {
		// 判断手机是否有SD卡
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 下载需要读写SD卡,加上SD卡的读写权限

			tvProgress.setVisibility(View.VISIBLE);// 显示下载进度

			String target = Environment.getExternalStorageDirectory()
					+ "/update.apk";// 获取SD卡的根目录

			// XUtils框架
			HttpUtils utils = new HttpUtils();
			utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

				// 表示文件的下载进度
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {// total表示整个文件的大小,current表示当前下载到了哪,isUploading表示现在是否正在上传
					super.onLoading(total, current, isUploading);
					// System.out.println("下载进度" + current + "/" + total);
					tvProgress.setText("下载进度" + current * 100 / total + "%");
				}

				// 下载成功
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					System.out.println("下载成功");

					// 跳转到系统的下载页面
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// startActivity(intent);
					startActivityForResult(intent, 0);// 如果用户取消安装的话,会返回结果,回调方法onActivityResult
				}

				// 下载失败
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Toast.makeText(SplashActivity.this, "下载失败",
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(SplashActivity.this, "没有发现SD卡", Toast.LENGTH_SHORT)
					.show();
		}

	}

	/**
	 * 如果用户取消安装,startActivityForResult会回调此方法
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 进入主页面
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 拷贝数据库
	 */
	private void copyDB(String dbName) {
		// File fileDir = getFilesDir();//打印测试保存数据的路径
		// System.out.println("路径:" + fileDir.getAbsolutePath());

		File destFile = new File(getFilesDir(), dbName);// 要拷贝的目标地址

		if (destFile.exists()) {// 判断数据库是否已经存在
			return;
		}

		FileOutputStream out = null;
		InputStream in = null;

		try {
			in = getAssets().open(dbName);
			out = new FileOutputStream(destFile);

			int len = 0;
			byte[] buffer = new byte[1024];

			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
