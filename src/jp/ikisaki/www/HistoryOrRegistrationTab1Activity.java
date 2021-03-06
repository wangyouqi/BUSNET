package jp.ikisaki.www;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryOrRegistrationTab1Activity extends Activity implements OnClickListener {
	static final int MENUITEM_ID_DELETE = 1;
	ListView itemListView;
	EditText noteEditText;
	Button saveButton;
	static DBAdapter dbAdapter;
	static NoteListAdapter listAdapter;
	static List<Note> noteList = new ArrayList<Note>();
	static String type = "";

	ArrayList<BusstopsModel> busstopsModels = null;
	//TimetableModel.getBusstopsModel();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.history_or_registration_tab1);

		findViews();
		setListeners();

		dbAdapter = new DBAdapter(this);
		listAdapter = new NoteListAdapter();
		itemListView.setAdapter(listAdapter);

		Intent intent = getIntent();
        type = intent.getStringExtra("keyword");

        if (type.equals("timetableDestination")) {
        	System.out.println("logcat:5:" + type + " " + TimetableModel.getBusstopsModel().size());
        	busstopsModels = TimetableModel.getBusstopsModel();
        }
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
		dbAdapter.open();
		Cursor c = dbAdapter.getAllNotes();

		startManagingCursor(c);

		if (c.moveToFirst()) {

			do {
				Note note = new Note(c.getInt(c
						.getColumnIndex(DBAdapter.COL_ID)), c.getString(c
						.getColumnIndex(DBAdapter.COL_NOTE)), c.getString(c
						.getColumnIndex(DBAdapter.COL_LASTUPDATE)));

				if (type.equals("departure") || type.equals("destination")) {
					noteList.add(0, note);
				} else {
					if ((note.getNote().indexOf("駅(") != -1) || (note.getNote().indexOf("バス停") != -1)) {
						if (type.equals("timetableDeparture")) {
							noteList.add(0, note);
						} else if (type.equals("timetableDestination")) {

							System.out.println("logcat:5:timetableDestination:size:" + busstopsModels.size());
							for (int i = 0; i < busstopsModels.size(); i++) {
								System.out.println("logcat:5:timetableDestination:" + busstopsModels.get(i).getName());

								if (note.getNote().equals(busstopsModels.get(i).getName())) {
									noteList.add(0, note);
								}
							}
						}
					}
				}
			} while (c.moveToNext());
		}

		stopManagingCursor(c);
		dbAdapter.close();

		listAdapter.notifyDataSetChanged();
	}

	protected void saveItem() {
		dbAdapter.open();
		dbAdapter.saveNote(noteEditText.getText().toString(), "kon");
		dbAdapter.close();
		noteEditText.setText("");
		loadNote();
	}

	protected void setListeners() {
		//saveButton.setOnClickListener(this);

		itemListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.add(0, MENUITEM_ID_DELETE, 0, "削除");
					}
				});

		itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	            if (type.equals("departure")) {
					PointModel departureModel = new PointModel(Integer.valueOf(noteList.get(position).getLastupdate()), noteList.get(position).getNote(), "", 0, 0, 0);

					BasicModel.setDeparture(departureModel);
				} else if (type.equals("destination")) {
					PointModel destinationModel = new PointModel(Integer.valueOf(noteList.get(position).getLastupdate()), noteList.get(position).getNote(), "", 0, 0, 0);

					BasicModel.setDestination(destinationModel);
				} else if (type.equals("timetableDeparture")) {
					TimetableModel.setStartId(Integer.valueOf(noteList.get(position).getLastupdate()));
					TimetableModel.setStartName(noteList.get(position).getNote());
					TimetableModel.setDestinationId(0);
					TimetableModel.setDestinationName("");
				} else if (type.equals("timetableDestination")) {
					TimetableModel.setDestinationId(Integer.valueOf(noteList.get(position).getLastupdate()));
					TimetableModel.setDestinationName(noteList.get(position).getNote());
				}

	            System.out.println("logcat:5:start");
                //add 検索記録
	            String tempNote = noteList.get(position).getNote();
	            String tempLastupdate = noteList.get(position).getLastupdate();
	            dbAdapter.open();
	            dbAdapter.deleteNote(noteList.get(position).getId());
	            dbAdapter.close();
	            System.out.println("logcat:5:goal");

	            dbAdapter.open();
	            dbAdapter.saveNote(tempNote, tempLastupdate);
	            dbAdapter.close();

	            if (type.equals("departure") || type.equals("destination")) {
	            	Intent intent = new Intent(HistoryOrRegistrationTab1Activity.this,
	            			RouteSearchActivity.class);
	            	intent.putExtra("keyword", "");
	            	startActivity(intent);
	            } else {
	            	Intent intent = new Intent(HistoryOrRegistrationTab1Activity.this,
	            			TimetableSearchActivity.class);
	            	intent.putExtra("keyword", "keyword");
	            	startActivity(intent);
	            }
	        }
	    });
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case MENUITEM_ID_DELETE:
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			Note note = noteList.get(menuInfo.position);
			final int noteId = note.getId();

			new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("本当に削除してもよろしいですか?")
					.setPositiveButton("はい",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dbAdapter.open();
									if (dbAdapter.deleteNote(noteId)) {
										Toast.makeText(getBaseContext(), "The note was successfully deleted.", Toast.LENGTH_SHORT);
										loadNote();
									}
									dbAdapter.close();
								}
							}).setNegativeButton("いいえ", null).show();

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
				lastupdateTextView = (TextView) v
						.findViewById(R.id.lastupdateTextView);
				noteTextView.setText(note.getNote());
				lastupdateTextView.setText(note.getLastupdate());
				//変えたv.setTag(R.id.noteTextView, note);
			}
			return v;
		}
	}
}
