package jp.ikisaki.www;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.*;

import java.io.*;
import java.util.*;

public class SettingTab2Activity  extends Activity implements OnClickListener {
	static final int MENUITEM_ID_DELETE = 1;
	ListView itemListView;
	EditText noteEditText;
	static RegistrationDBAdapter registrationdbAdapter;
	static NoteListAdapter listAdapter;
	static List<Note> noteList = new ArrayList<Note>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_tab2);

		Toast toast = Toast.makeText(SettingTab2Activity.this, "地図から登録した点は青文字で表示しています。", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

		Toast toast2 = Toast.makeText(SettingTab2Activity.this, "登録地点を削除したい場合は項目を長押しして下さい。", Toast.LENGTH_SHORT);
		toast2.setGravity(Gravity.BOTTOM, 0, 0);
		toast2.show();

		findViews();
		setListeners();

		registrationdbAdapter = new RegistrationDBAdapter(this);
		listAdapter = new NoteListAdapter();
		itemListView.setAdapter(listAdapter);

		loadNote();
	}

	protected void findViews() {
		itemListView = (ListView) findViewById(R.id.itemListView);
//		noteEditText = (EditText) findViewById(R.id.memoEditText);
//		saveButton = (Button) findViewById(R.id.saveButton);
	}

	protected void loadNote() {
		noteList.clear();

		// Read
		registrationdbAdapter.open();
		Cursor c = registrationdbAdapter.getAllNotes();
		startManagingCursor(c);

		if (c.moveToFirst()) {
			do {
                Note note = new Note(c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL_ID)),
                        c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_NOTE)),
                        c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_LASTUPDATE)) );

					noteList.add(0, note);

			} while (c.moveToNext());
		}

		stopManagingCursor(c);
		registrationdbAdapter.close();
		listAdapter.notifyDataSetChanged();
	}

	protected void saveItem() {
		registrationdbAdapter.open();
		registrationdbAdapter.saveNote(noteEditText.getText().toString(), "kon");
		registrationdbAdapter.close();
		noteEditText.setText("");
		loadNote();
	}

	protected void setListeners() {
        itemListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.add(0, MENUITEM_ID_DELETE, 0, getResources().getString(R.string.delete));
            }
        });

		itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

	            final String tempNote = noteList.get(position).getNote();
	            final String tempLastupdate = noteList.get(position).getLastupdate();
//	            registrationdbAdapter.open();
//	            registrationdbAdapter.deleteNote(noteList.get(position).getId());
//	            registrationdbAdapter.close();
//
//	            registrationdbAdapter.open();
//	            registrationdbAdapter.saveNote(tempNote, tempLastupdate);
//	            registrationdbAdapter.close();
	            System.out.println("logcat:8:" + noteList.get(position).getNote() + ":" + noteList.get(position).getLastupdate());

	            if (noteList.get(position).getLastupdate().indexOf(",") != -1) {
	            	new AlertDialog.Builder(SettingTab2Activity.this)
	            	 .setMessage(noteList.get(position).getNote())
	            	 .setCancelable(false)
	            	 .setPositiveButton(getResources().getString(R.string.change_name), new DialogInterface.OnClickListener() {
                         @Override
	            	     public void onClick(DialogInterface dialog, int id) {
	            	    	LayoutInflater inflater = LayoutInflater.from(SettingTab2Activity.this);
	     	            	View dialogView = inflater.inflate(R.layout.dialog, null);
	     	            	final EditText editText = (EditText) dialogView.findViewById(R.id.dialog_editText);
	     	            	editText.setText(tempNote);

	     	            	new AlertDialog.Builder(SettingTab2Activity.this)
                            .setTitle("新しい登録名を入力して下さい。")
	     	            	.setView(dialogView)
	     	            	.setPositiveButton(getResources().getString(R.string.decision), new DialogInterface.OnClickListener() {
	     	            		@Override
	     	            		public void onClick(DialogInterface dialog, int which) {
	     	            			int flag = 0;
	     	            			registrationdbAdapter.open();
	     	            			Cursor c = registrationdbAdapter.getAllNotes();
//	     							if (c.getCount() == 0) {
//	     							registrationdbAdapter.saveNote(editText.getText().toString(), gmapdLongitude + "," + gmapdLatitude);
//	     							flag = 1;
//	     						}
	     	            			startManagingCursor(c);
	     	            			if (c.moveToFirst()) {
	     	            				do {
                                            Note note = new Note(c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL_ID)),
                                                    c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_NOTE)),
                                                    c.getString(c.getColumnIndex(RegistrationDBAdapter.COL_LASTUPDATE)) );

	     	            					if (!note.getNote().equals(editText.getText().toString()) ) {
                                                registrationdbAdapter.updateNote(noteList.get(position).getId(), editText.getText().toString());
                                                //registrationdbAdapter.deleteNote(noteList.get(position).getId());
                                                //registrationdbAdapter.saveNote(editText.getText().toString(), tempLastupdate);
	     	            						//registrationdbAdapter.deleteNote(note.getId());
	     	            						flag = 1;
	     	            					}
	     	            				} while (c.moveToNext());
	     	            			}
	     	            			stopManagingCursor(c);
	     	            			registrationdbAdapter.close();

	     	            			if (flag == 1) {
	     	            				Toast toast = Toast.makeText(SettingTab2Activity.this, "登録名を「"
	     	            						+ editText.getText().toString() + "」" + "\n"
	     	            						+ "に変更しました。", Toast.LENGTH_SHORT);
	     	            				toast.setGravity(Gravity.CENTER, 0, 0);
	     	            				toast.show();
	     	            				loadNote();
	     	            			} else {
	     	            				Toast toast = Toast.makeText(SettingTab2Activity.this, "既にその名前は使われています。" + "\n" + "別の名前を入力して下さい。", Toast.LENGTH_SHORT);
	     	            				toast.setGravity(Gravity.CENTER, 0, 0);
	     	            				toast.show();
	     	            			}
	     	            		}
	     	            	}).show();
	            	    }
	            	})
                    .setNegativeButton(getResources().getString(R.string.cancel), null)
	            	.setNeutralButton(getResources().getString(R.string.showmap), new DialogInterface.OnClickListener() {
                        @Override
	            	    public void onClick(DialogInterface dialog, int id) {
	            	    	Intent intent = new Intent(SettingTab2Activity.this, ShowMapByMapActivity.class);
	    	            	intent.putExtra("keyword", tempNote + "$" + tempLastupdate);
	    	            	startActivity(intent);
                        }
                    }).show();
                } else {
	            	int landmarkLatitude = 0;
	            	int landmarkLongitude = 0;

                    //150730 add by wyq csv読み2がdatabaseに替わる

                    //150730 end

	        		System.out.println("logcat:2: longitude : latitude : " + landmarkLongitude + " : " + landmarkLatitude);
	        		Intent intent = new Intent(SettingTab2Activity.this, ShowMapActivity.class);
	            	intent.putExtra("keyword", tempNote + "$" + landmarkLatitude + "," + landmarkLongitude);
	            	startActivity(intent);
	            }
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
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("本当に削除してもよろしいですか?")
					.setPositiveButton(getResources().getString(R.string.YES),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									registrationdbAdapter.open();
									if (registrationdbAdapter.deleteNote(noteId)) {
										Toast.makeText(getBaseContext(), "The note was successfully deleted.", Toast.LENGTH_SHORT);
										loadNote();
									}
									registrationdbAdapter.close();
								}
							})
                    .setNegativeButton(getResources().getString(R.string.NO), null)
                    .show();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.saveButton:
//			saveItem();
//			break;
//		}
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
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.row, null);
            }
            TextView noteTextView = (TextView) v.findViewById(R.id.noteTextView);
            TextView lastupdateTextView = (TextView) v.findViewById(R.id.lastupdateTextView);

            Note note = (Note) getItem(position);
            if (note != null) {
                noteTextView.setText(note.getNote());
                System.out.println("logcat:8:inside");

                if (note.getLastupdate().indexOf(",") != -1) {
                    noteTextView.setTextColor(Color.BLUE);
                } else {
                    noteTextView.setTextColor(Color.BLACK);
                }

                lastupdateTextView.setText(note.getLastupdate());
                //変えたv.setTag(R.id.noteTextView, note);
            }
            return v;
        }
    }
}
