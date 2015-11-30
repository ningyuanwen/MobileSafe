package com.nyw.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.nyw.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * ��ȡ�ֻ���ϵ��
 * 
 * @author ning
 * 
 */
public class ContactActivity extends Activity {
	private ListView lvList;
	private ArrayList<HashMap<String, String>> readContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contact);

		lvList = (ListView) findViewById(R.id.lv_list);
		readContact = readContact();
		// System.out.println(readContact);
		lvList.setAdapter(new SimpleAdapter(this, readContact(),
				R.layout.contact_list_item, new String[] { "name", "phone" },
				new int[] { R.id.tv_name, R.id.tv_phone }));

		// ������ϵ���б�ļ����¼�
		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int postion, long id) {
				String phone = readContact.get(postion).get("phone");// ��ȡ��ǰitem���ֻ���
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(Activity.RESULT_OK, intent);// �����ݷ���intent�з��ظ���һ��ҳ��

				finish();

			}
		});
	}

	private ArrayList<HashMap<String, String>> readContact() {

		Uri rawContactsUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		// ���ȴ�raw_contacts���ж�ȡ��ϵ�˵�ID("contact_id")
		Cursor rawContactsCursor = getContentResolver().query(rawContactsUri,
				new String[] { "contact_id" }, null, null, null);

		if (rawContactsCursor != null) {

			while (rawContactsCursor.moveToNext()) {
				String contactId = rawContactsCursor.getString(0);

				// ���,����contacts_id��data����data1�ֶβ�����ϵ�˵����ƺ��ֻ���,ʵ���ϲ�ѯ������ͼview_data
				Cursor dataCursor = getContentResolver().query(dataUri,
						new String[] { "data1", "mimetype" }, "contact_id=?",
						new String[] { contactId }, null);

				if (dataCursor != null) {
					HashMap<String, String> map = new HashMap<String, String>();
					while (dataCursor.moveToNext()) {
						String data1 = dataCursor.getString(0);
						String mimetype = dataCursor.getString(1);

						// ���,����mimetype��������ϵ�˺��ֻ���
						if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
							map.put("phone", data1);
						} else if ("vnd.android.cursor.item/name"
								.equals(mimetype)) {
							map.put("name", data1);
						}
					}
					list.add(map);
					dataCursor.close();
				}
			}
			rawContactsCursor.close();
		}
		return list;
	}
}
