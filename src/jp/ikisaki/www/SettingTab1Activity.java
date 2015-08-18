package jp.ikisaki.www;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.*;
import com.j256.ormlite.stmt.*;
import jp.ikisaki.www.db.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;


public class SettingTab1Activity extends OrmLiteBaseActivity<DataHelper> implements
		TextView.OnEditorActionListener, OnClickListener {

	static RegistrationDBAdapter registrationdbAdapter;
	static final int MENUITEM_ID_DELETE = 1;
	ListView itemListView;

	static NoteListAdapter listAdapter;
	static List<Note> noteList = new ArrayList<Note>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_tab1);

		Toast toast = Toast.makeText(SettingTab1Activity.this, "ランドマーク名を検索して地点登録を行なって下さい。", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		findViews();
		setListeners();

		registrationdbAdapter = new RegistrationDBAdapter(this);
		listAdapter = new NoteListAdapter();
		itemListView.setAdapter(listAdapter);

		System.out.println("logcat:7:year");

		final EditText editText = (EditText) findViewById(R.id.memoEditText);
		editText.setImeOptions(DEFAULT_KEYS_SEARCH_LOCAL);
		editText.setOnEditorActionListener(this);
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				});

		Button searchButton = (Button) findViewById(R.id.saveButton);

		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String exploreString = editText.getText().toString();
				examinLandmark(exploreString);
			}
		});

		System.out.println("logcat:7:loadmae");
		loadNote();
		System.out.println("logcat:7:loadgo");
	}

	public void examinLandmark(String exploreString) {
        final ArrayList<LandmarkModel> landmarkArray = new ArrayList<LandmarkModel>();
        //final StringBuilder sb = new StringBuilder();

        //150730 add by wyq csv読み１がdatabaseに替わる
        try {
            // get our dao
            Dao<Landmark, Integer> landmarkDao = getHelper().getDaoLandmark();
            // query for all of the data objects in the database
            QueryBuilder<Landmark, Integer> builder = landmarkDao.queryBuilder();
            builder.where().raw("vc_landmark_name||vc_landmark_yomi like '%" + exploreString + "%'");
            builder.orderByRaw("int_landmark_id");

            for (Landmark lmRowset : builder.query()) { //landmarkDao.queryForAll()
                LandmarkModel landmarkModel = new LandmarkModel(lmRowset.get_landmark_id(), lmRowset.get_landmark_name(), lmRowset.get_landmark_yomi(),
                        lmRowset.get_landmark_lng(), lmRowset.get_landmark_lat(), 0);
                landmarkArray.add(landmarkModel);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //150730 end

		String landmarkOfTheCandidate[] = new String[landmarkArray.size()];
		for (int i = 0; i < landmarkArray.size(); i++) {
			landmarkOfTheCandidate[i] = landmarkArray.get(i).getName();
		}

		if (landmarkArray.size() != 0) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("候補から選択して下さい。");
			alert.setItems(landmarkOfTheCandidate,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int idx) {

							registrationdbAdapter.open();
							Cursor c = registrationdbAdapter.getAllNotes();
							if (c.getCount() == 0) {
								registrationdbAdapter.saveNote(landmarkArray.get(idx).getName(),
										String.valueOf(landmarkArray.get(idx).getId()));
								System.out.println("logcat:7:ko");
							}
							startManagingCursor(c);
							int reg = 0;
							String regName = "";
							if (c.moveToFirst()) {
								do {
									Note note = new Note(
											c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL_ID)),
											c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_NOTE)),
											c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_LASTUPDATE)));
									if (note.getNote().equals(
											landmarkArray.get(idx).getName())) {
										registrationdbAdapter.deleteNote(note
												.getId());
										System.out.println("logcat:7:delete");
										regName = note.getNote();

										Toast toast = Toast.makeText(SettingTab1Activity.this,
												"「" + regName + "」は既に登録されています。", Toast.LENGTH_LONG);
										toast.setGravity(Gravity.CENTER, 0, 0);
										toast.show();
										reg = 1;
									}
								} while (c.moveToNext());
								System.out.println("logcat:7:ok");

								if (reg == 0) {
									Toast toast = Toast.makeText(SettingTab1Activity.this,
											"「" + landmarkArray.get(idx).getName() + "」を登録しました。", Toast.LENGTH_LONG);

									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}

								registrationdbAdapter.saveNote(landmarkArray.get(idx).getName(),
										String.valueOf(landmarkArray.get(idx).getId()));
							}
							stopManagingCursor(c);
							registrationdbAdapter.close();

							loadNote();

						}
					});
			alert.show();
		} else {
			Toast toast = Toast.makeText(SettingTab1Activity.this,
					"該当するランドマークが存在しません。", Toast.LENGTH_LONG);

			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	public boolean examineLandmark(int actionId, KeyEvent event) {
		if (event == null && actionId == 6 || event != null
				&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			EditText exploreEditText = (EditText) findViewById(R.id.memoEditText);
			String exploreString = exploreEditText.getText().toString();
			examinLandmark(exploreString);
		}
		return false;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		examineLandmark(actionId, event);
		return false;
	}

	protected void findViews() {
		itemListView = (ListView) findViewById(R.id.itemListView);
		//noteEditText = (EditText) findViewById(R.id.memoEditText);
		// saveButton = (Button) findViewById(R.id.saveButton);
	}

	protected void loadNote() {
		noteList.clear();

		// Read
		registrationdbAdapter.open();
		Cursor c = registrationdbAdapter.getAllNotes();

		startManagingCursor(c);

		if (c.moveToFirst()) {

			do {
				Note note = new Note(c.getInt(c
						.getColumnIndex(RegistrationDBAdapter.COL_ID)), c.getString(c
						.getColumnIndex(RegistrationDBAdapter.COL_NOTE)), c.getString(c
						.getColumnIndex(RegistrationDBAdapter.COL_LASTUPDATE)));

				noteList.add(0, note);

			} while (c.moveToNext());
		}

		stopManagingCursor(c);
		registrationdbAdapter.close();
		listAdapter.notifyDataSetChanged();
	}

//	protected void saveItem(String name, String id) {
//		dbAdapter.open();
//		dbAdapter.saveNote(name, id);
//		dbAdapter.close();
//		noteEditText.setText("");
//		loadNote();
//	}

	protected void setListeners() {
		// saveButton.setOnClickListener(this);

		itemListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                //menu.add(0, MENUITEM_ID_DELETE, 0, "削除");
            }
        });

		itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                String tempNote = noteList.get(position).getNote();
                String tempLastupdate = noteList.get(position).getLastupdate();
                registrationdbAdapter.open();
                registrationdbAdapter.deleteNote(noteList.get(position).getId());
                registrationdbAdapter.close();
                System.out.println("logcat:5:goal");

                Toast toast = Toast.makeText(SettingTab1Activity.this, "登録地点の「削除」・「地図表示」・「名称変更」については。\n[登録地管理]で行ってください。", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();


                registrationdbAdapter.open();
                registrationdbAdapter.saveNote(tempNote, tempLastupdate);
                registrationdbAdapter.close();

            }
        });
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENUITEM_ID_DELETE:
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

			Note note = noteList.get(menuInfo.position);
			final int noteId = note.getId();

			new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("本当に削除してもよろしいですか?")
					.setPositiveButton(getResources().getString(R.string.YES),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									registrationdbAdapter.open();
									if (registrationdbAdapter.deleteNote(noteId)) {
										Toast.makeText(getBaseContext(), "The note was successfully deleted.", Toast.LENGTH_SHORT);
										loadNote();
									}
									registrationdbAdapter.close();
								}
							}).setNegativeButton(getResources().getString(R.string.NO), null).show();

			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.saveButton:
		// saveItem();
		// break;
		// }


	}

	private class NoteListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return noteList.size();
		}

		@Override
		public Object getItem(int position) {
			return noteList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView noteTextView;
			TextView lastupdateTextView;
			View v = convertView;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.row, null);
			}
			Note note = (Note) getItem(position);
			if (note != null) {
				noteTextView = (TextView) v.findViewById(R.id.noteTextView);
				lastupdateTextView = (TextView) v.findViewById(R.id.lastupdateTextView);
				noteTextView.setText(note.getNote());
				lastupdateTextView.setText(note.getLastupdate());
				// 変えたv.setTag(R.id.noteTextView, note);
			}
			return v;
		}
	}
}
