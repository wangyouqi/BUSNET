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

public class SettingTab3Activity extends Activity implements OnClickListener {
	static final int MENUITEM_ID_DELETE = 1;
	static RegistrationDBAdapter registrationdbAdapter;
	static NoteListAdapter listAdapter;
	static List<Note> noteList = new ArrayList<Note>();
    ListView itemListView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_tab3);

        itemListView = (ListView) findViewById(R.id.itemListView);
		setListeners();

		registrationdbAdapter = new RegistrationDBAdapter(this);
		listAdapter = new NoteListAdapter();
		itemListView.setAdapter(listAdapter);

		loadNote();
    }

	protected void loadNote() {
		noteList.clear();

		// Read
		registrationdbAdapter.open();
		Cursor c = registrationdbAdapter.getAllMarks();
		startManagingCursor(c);

		if (c.moveToFirst()) {
			do {
                Note note = new Note(c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL1_ID)),
                        c.getString(c.getColumnIndex(RegistrationDBAdapter.COL1_ROUTENAME)),
                        c.getString(c.getColumnIndex(RegistrationDBAdapter.COL1_CREATEDATE)) );

					noteList.add(0, note);

			} while (c.moveToNext());
		}

		stopManagingCursor(c);
		registrationdbAdapter.close();
		listAdapter.notifyDataSetChanged();
	}

/*	protected void saveItem() {
		registrationdbAdapter.open();
		registrationdbAdapter.saveNote(noteEditText.getText().toString(), "kon");
		registrationdbAdapter.close();
		noteEditText.setText("");
		loadNote();
	}*/

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

                    new AlertDialog.Builder(SettingTab3Activity.this)
	            	 .setMessage(noteList.get(position).getNote())
	            	 .setCancelable(false)
	            	 .setPositiveButton(getResources().getString(R.string.change_name), new DialogInterface.OnClickListener() {
                         @Override
	            	     public void onClick(DialogInterface dialog, int id) {
	            	        LayoutInflater inflater = LayoutInflater.from(SettingTab3Activity.this);
	     	            	View dialogView = inflater.inflate(R.layout.dialog, null);
	     	            	final EditText editText = (EditText) dialogView.findViewById(R.id.dialog_editText);
	     	            	editText.setText(tempNote);

                            new AlertDialog.Builder(SettingTab3Activity.this)
                            .setTitle("新しい登録名を入力して下さい。")
	     	            	.setView(dialogView)
	     	            	.setPositiveButton(getResources().getString(R.string.decision), new DialogInterface.OnClickListener() {
	     	            		@Override
	     	            		public void onClick(DialogInterface dialog, int which) {
	     	            			int flag = 0;
	     	            			registrationdbAdapter.open();
	     	            			Cursor c = registrationdbAdapter.getSpecMarks("" + noteList.get(position).getId());
//	     							if (c.getCount() == 0) {
//	     							registrationdbAdapter.saveNote(editText.getText().toString(), gmapdLongitude + "," + gmapdLatitude);
//	     							flag = 1;
//	     						}
	     	            			startManagingCursor(c);
	     	            			if (c.moveToFirst()) {
                                        Note note = new Note(c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL1_ID)),
                                                c.getString(c.getColumnIndex(RegistrationDBAdapter.COL1_ROUTENAME)),
                                                c.getString(c.getColumnIndex(RegistrationDBAdapter.COL1_CREATEDATE)) );

                                        if (!note.getNote().equals(editText.getText().toString()) ) {
                                            registrationdbAdapter.updateMark(noteList.get(position).getId(), editText.getText().toString());
                                            //registrationdbAdapter.saveNote(editText.getText().toString(), tempLastupdate);
                                            flag = 1;
                                        }
	     	            			}
	     	            			stopManagingCursor(c);
	     	            			registrationdbAdapter.close();

	     	            			if (flag == 1) {
	     	            				Toast toast = Toast.makeText(SettingTab3Activity.this, "登録名を「"
	     	            						+ editText.getText().toString() + "」" + "\n"
	     	            						+ "に変更しました。", Toast.LENGTH_SHORT);
	     	            				toast.setGravity(Gravity.CENTER, 0, 0);
	     	            				toast.show();
	     	            				loadNote();
	     	            			} else {
	     	            				Toast toast = Toast.makeText(SettingTab3Activity.this, "既にその名前は使われています。" + "\n" + "別の名前を入力して下さい。", Toast.LENGTH_SHORT);
	     	            				toast.setGravity(Gravity.CENTER, 0, 0);
	     	            				toast.show();
	     	            			}
	     	            		}
	     	            	}).show();
	            	    }
	            	})
                    .setNegativeButton(getResources().getString(R.string.cancel), null)
	            	.setNeutralButton(getResources().getString(R.string.result_of_route_search), new DialogInterface.OnClickListener() {
                        @Override
	            	    public void onClick(DialogInterface dialog, int id) {
                            registrationdbAdapter.open();
                            Cursor c = registrationdbAdapter.getSpecMarks("" + noteList.get(position).getId());
                            startManagingCursor(c);

                            if (c.moveToFirst()) {
                                final PointModel departureModel = new PointModel(c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL1_DEPARTID)), c.getString(c.getColumnIndex(RegistrationDBAdapter.COL1_DEPARTNAME)),
                                        "", c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL1_DEPARTLONG)), c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL1_DEPARTLAT)), 0);
                                BasicModel.setDeparture(departureModel);

                                final PointModel destinationModel = new PointModel(c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL1_DESTID)), c.getString(c.getColumnIndex(RegistrationDBAdapter.COL1_DESTNAME)),
                                        "", c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL1_DESTLONG)), c.getInt(c.getColumnIndex(RegistrationDBAdapter.COL1_DESTLAT)), 0);
                                BasicModel.setDestination(destinationModel);

                                Intent intent = new Intent(SettingTab3Activity.this, RouteSearchActivity.class);
                                intent.putExtra("keyword", "AUTORUN");
                                //intent.putExtra("keyword", tempNote + "$" + tempLastupdate);
                                startActivity(intent);
                            }
                            stopManagingCursor(c);
                            registrationdbAdapter.close();
	            	    }
	            	}).show();
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
									if (registrationdbAdapter.deleteMark(noteId)) {
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

				lastupdateTextView.setText(note.getLastupdate());
                lastupdateTextView.setTextColor(Color.BLACK);
                v.setTag(note.getId());
				//変えたv.setTag(R.id.noteTextView, note);
			}
			return v;
		}
	}
}
