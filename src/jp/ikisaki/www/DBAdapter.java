package jp.ikisaki.www;

import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	static final String DATABASE_NAME = "mynote.db";
	static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "notes";
	public static final String COL_ID = "_id";
	public static final String COL_NOTE = "note";
	public static final String COL_LASTUPDATE = "lastupdate";
    public static final String COL_FREQUENCY = "frequency";

	protected final Context context;
	protected DatabaseHelper dbHelper;
	protected SQLiteDatabase db;

	public DBAdapter(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(this.context);
	}

	//
	// SQLiteOpenHelper
	//

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_NOTE + " TEXT NOT NULL," + COL_LASTUPDATE + " TEXT NOT NULL," + COL_FREQUENCY + " INTEGER NOT NULL DEFAULT 0);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	//
	// Adapter Methods
	//

	public DBAdapter open() {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	//
	// App Methods
	//

	public boolean deleteAllNotes() {
		return db.delete(TABLE_NAME, null, null) > 0;
	}

	public boolean deleteNote(int id) {
		return db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
	}

	public Cursor getAllNotes() {
        return db.query(TABLE_NAME, null, null, null, null, null, "frequency desc");
	}

	public void saveNote(String note, String id) {
		Date dateNow = new Date();
		ContentValues values = new ContentValues();
		values.put(COL_NOTE, note);
		values.put(COL_LASTUPDATE, id);
		// カーソルに一旦入れてから，デリートして再構築
		db.insertOrThrow(TABLE_NAME, null, values);
	}

    public Cursor getSpecNotes(String id) {
        return db.rawQuery("select * from "+ TABLE_NAME +" where lastupdate = ?", new String[]{id});
    }

    public void updateNote(int id) {
        Date dateNow = new Date();
        ContentValues values = new ContentValues();
        values.put(COL_FREQUENCY, 110);
        // カーソルに一旦入れてから，デリートして再構築
        db.execSQL("update "+ TABLE_NAME +" set frequency=frequency+1 where _id=" + id);
        //db.update(TABLE_NAME, values, COL_ID + "=" + id, null);
    }
}
