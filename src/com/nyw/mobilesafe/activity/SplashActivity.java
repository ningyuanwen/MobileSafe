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
	protected static final int CODE_ENTER_HONE = 4;// ������ҳ��

	private TextView tvVersion;// �汾��չʾ
	private TextView tvProgress;// ���ؽ���չʾ

	// ���������ص���Ϣ
	private String mVersionName; // �汾��
	private int mVersionCode;// �汾��
	private String mDesc;// �汾����
	private String mDownloadUrl;// ��������

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_UPDATE_DIALOG:
				showUpdateDailog();
				break;
			case CODE_URL_ERROR:
				Toast.makeText(SplashActivity.this, "URL����", 0).show();
				enterHome();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this, "�������", 0).show();
				enterHome();
				break;
			case CODE_JSON_ERROR:
				Toast.makeText(SplashActivity.this, "���ݽ�������", 0).show();
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
	private RelativeLayout rlRoot;// ������

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("�汾��" + getVersionName());
		tvProgress = (TextView) findViewById(R.id.tv_progress);// Ĭ������

		rlRoot = (RelativeLayout) findViewById(R.id.rL_root);

		mPref = getSharedPreferences("config", MODE_PRIVATE);

		copyDB("address.db");// ���������ز�ѯ���ݿ�

		// �ж��Ƿ���Ҫ�Զ�����
		boolean autoUpdate = mPref.getBoolean("auto_update", true);
		if (autoUpdate) {
			checkVersion();
		} else {
			mHandler.sendEmptyMessageDelayed(CODE_ENTER_HONE, 2000);// ��ʱ2�������Ϣ
		}

		// �ø����ֵĳ����н���Ч��
		AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
		anim.setDuration(2000);
		rlRoot.startAnimation(anim);
	}

	/**
	 * ��ȡ����APP�汾����
	 * 
	 * @return
	 */
	private String getVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);// ��ȡ������Ϣ

			String versionName = packageInfo.versionName;

			return versionName;

		} catch (NameNotFoundException e) {
			// û���ҵ�������ʱ����ߴ��쳣
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * ��ȡ����APP�汾��
	 * 
	 * @return
	 */
	private int getVersionCode() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);// ��ȡ������Ϣ

			int versionCode = packageInfo.versionCode;
			return versionCode;

		} catch (NameNotFoundException e) {
			// û���ҵ�������ʱ����ߴ��쳣
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * �ӷ�������ȡ�汾��Ϣ����У��
	 */
	private void checkVersion() {
		final long startTime = System.currentTimeMillis();
		// �������߳�ȥ�첽��������
		new Thread() {

			public void run() {

				Message msg = Message.obtain();
				HttpURLConnection conn = null;
				try {
					// ������ַ��localhost�����������ģ�������ر�����ַʱ,������IP(10.0.2.2)���滻
					URL url = new URL(""); // json����·��
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");// �������󷽷�
					conn.setConnectTimeout(5000);// �������ӳ�ʱ
					conn.setReadTimeout(5000);// ������Ӧ��ʱ
					conn.connect();// ���ӷ�����

					int responseCode = conn.getResponseCode();
					if (responseCode == 200) {
						InputStream inpputStream = conn.getInputStream();
						String result = StreamUtils
								.readFromStream(inpputStream);

						System.out.println("���緵��" + result);

						// ����json
						JSONObject jo = new JSONObject(result);// ר����������json�����JSONObject

						mVersionName = jo.getString("versionName");
						mVersionCode = jo.getInt("versionCode");
						mDesc = jo.getString("description");
						mDownloadUrl = jo.getString("dowlloadUrl");

						System.out.println("��ӡ����json");

						if (mVersionCode > getVersionCode()) {// �Աȷ������ͱ��ص�versionCode,�ж��Ƿ���Ҫ����APP
							// ����������İ汾���ڱ��صİ汾��
							// ˵���и���,���������Ի���
							msg.what = CODE_UPDATE_DIALOG;
							showUpdateDailog();
						} else {
							// û�а汾����
							msg.what = CODE_ENTER_HONE;
						}

					}

				} catch (MalformedURLException e) {
					// url������쳣
					msg.what = CODE_URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// ��������쳣
					msg.what = CODE_NET_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// json����ʧ���쳣
					msg.what = CODE_JSON_ERROR;
					e.printStackTrace();
				} finally {
					long endTime = System.currentTimeMillis();

					long timeUsed = endTime - startTime;// �������绨�ѵ�ʱ��
					if (timeUsed < 2000) {
						// ǿ������һ��ʱ��,��֤����ҳ��չʾ������
						try {
							Thread.sleep(2000 - timeUsed);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					mHandler.sendMessage(msg);
					if (conn != null) {
						conn.disconnect();// �ر���������
					}
				}
			}
		}.start();
	}

	/**
	 * ���������Ի���
	 */
	protected void showUpdateDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("���°汾" + mVersionName);
		builder.setMessage(mDesc);
		// builder.setCancelable(false);//����ȡ�������Ի���,�û�����̫��,������Ҫ��

		builder.setPositiveButton("��������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				System.out.println("��������");
				download();
			}
		});

		builder.setNegativeButton("�´���˵", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				enterHome();
			}
		});

		// ����"ȡ����"�ļ���,���û�������ؼ�ʱ�ᴥ��
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				enterHome();
			}
		});

		builder.show();
	}

	/**
	 * ����apk�ļ�
	 */
	protected void download() {
		// �ж��ֻ��Ƿ���SD��
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// ������Ҫ��дSD��,����SD���Ķ�дȨ��

			tvProgress.setVisibility(View.VISIBLE);// ��ʾ���ؽ���

			String target = Environment.getExternalStorageDirectory()
					+ "/update.apk";// ��ȡSD���ĸ�Ŀ¼

			// XUtils���
			HttpUtils utils = new HttpUtils();
			utils.download(mDownloadUrl, target, new RequestCallBack<File>() {

				// ��ʾ�ļ������ؽ���
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {// total��ʾ�����ļ��Ĵ�С,current��ʾ��ǰ���ص�����,isUploading��ʾ�����Ƿ������ϴ�
					super.onLoading(total, current, isUploading);
					// System.out.println("���ؽ���" + current + "/" + total);
					tvProgress.setText("���ؽ���" + current * 100 / total + "%");
				}

				// ���سɹ�
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					System.out.println("���سɹ�");

					// ��ת��ϵͳ������ҳ��
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addCategory(intent.CATEGORY_DEFAULT);
					intent.setDataAndType(Uri.fromFile(arg0.result),
							"application/vnd.android.package-archive");
					// startActivity(intent);
					startActivityForResult(intent, 0);// ����û�ȡ����װ�Ļ�,�᷵�ؽ��,�ص�����onActivityResult
				}

				// ����ʧ��
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Toast.makeText(SplashActivity.this, "����ʧ��",
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(SplashActivity.this, "û�з���SD��", Toast.LENGTH_SHORT)
					.show();
		}

	}

	/**
	 * ����û�ȡ����װ,startActivityForResult��ص��˷���
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ������ҳ��
	 */
	private void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * �������ݿ�
	 */
	private void copyDB(String dbName) {
		// File fileDir = getFilesDir();//��ӡ���Ա������ݵ�·��
		// System.out.println("·��:" + fileDir.getAbsolutePath());

		File destFile = new File(getFilesDir(), dbName);// Ҫ������Ŀ���ַ

		if (destFile.exists()) {// �ж����ݿ��Ƿ��Ѿ�����
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
