package jp.ikisaki.www;

import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RegistrationDBAdapter {
	static final String DATABASE_NAME = "regnote.db";
	static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "notemark";
	public static final String COL_ID = "_id";
	public static final String COL_NOTE = "note";
	public static final String COL_LASTUPDATE = "lastupdate";
    public static final String COL_FREQUENCY = "frequency";

    public static final String TABLE1_NAME = "bookmark";
    public static final String COL1_ID = "_id";
    public static final String COL1_ROUTENAME = "routename";
    public static final String COL1_DEPARTID = "departid";
    public static final String COL1_DEPARTNAME = "departname";
    public static final String COL1_DEPARTLONG = "departlong";
    public static final String COL1_DEPARTLAT = "departlat";
    public static final String COL1_DESTID = "destid";
    public static final String COL1_DESTNAME = "destname";
    public static final String COL1_DESTLONG = "destlong";
    public static final String COL1_DESTLAT = "destlat";
    public static final String COL1_CREATEDATE = "createdate";

	protected final Context context;
	protected DatabaseHelper dbHelper;
	protected SQLiteDatabase db;

	public RegistrationDBAdapter(Context context) {
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

            db.execSQL("CREATE TABLE " + TABLE1_NAME + " (" + COL1_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL1_ROUTENAME + " TEXT NOT NULL,"
                    + COL1_DEPARTID + " INTEGER NOT NULL," + COL1_DEPARTNAME + " TEXT NOT NULL," + COL1_DEPARTLONG + " INTEGER NOT NULL," + COL1_DEPARTLAT + " INTEGER NOT NULL,"
                    + COL1_DESTID + " INTEGER NOT NULL," + COL1_DESTNAME + " TEXT NOT NULL," + COL1_DESTLONG + " INTEGER NOT NULL," + COL1_DESTLAT + " INTEGER NOT NULL,"
                    + COL1_CREATEDATE + " TEXT DEFAULT CURRENT_TIMESTAMP);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
			onCreate(db);
		}
	}

	//
	// Adapter Methods
	//

	public RegistrationDBAdapter open() {
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

    public boolean deleteNull() {
        return db.delete(TABLE_NAME, COL_LASTUPDATE + "=0" , null) > 0;
    }

	public Cursor getAllNotes() {
        return db.query(TABLE_NAME, null, null, null, null, null, "frequency desc");
    }

    public Cursor getAllRegMarks() {
        return db.rawQuery("select * from " + TABLE_NAME + " where lastupdate like '%,%'", null);
    }

	public void saveNote(String note, String id) {
		ContentValues values = new ContentValues();
		values.put(COL_NOTE, note);
		values.put(COL_LASTUPDATE, id);
		// カーソルに一旦入れてから，デリートして再構築
		db.insertOrThrow(TABLE_NAME, null, values);
	}

    public Cursor getSpecNotes(String id) {
        return db.rawQuery("select * from " + TABLE_NAME + " where lastupdate = ?", new String[]{id});
    }

    public void updateNote(int id, String notename) {
        ContentValues values = new ContentValues();
        values.put(COL_NOTE, notename);
        // カーソルに一旦入れてから，デリートして再構築
        db.update(TABLE_NAME, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void countNote(int id) {
        ContentValues values = new ContentValues();
        values.put(COL_FREQUENCY, 110);
        // カーソルに一旦入れてから，デリートして再構築
        db.execSQL("update " + TABLE_NAME + " set frequency=frequency+1 where _id=" + id);
        //db.update(TABLE_NAME, values, COL_ID + "=" + id, null);
    }

    //160707 add by wyq save bookmark
    public Cursor getAllMarks() {
        return db.query(TABLE1_NAME, null, null, null, null, null, "createdate desc");
    }

    public Cursor getSpecMarks(String id) {
        return db.rawQuery("select * from " + TABLE1_NAME + " where _id = ?", new String[]{id});
    }

    public void updateMark(int id, String routename) {
        ContentValues values = new ContentValues();
        values.put(COL1_ROUTENAME, routename);
        // カーソルに一旦入れてから，デリートして再構築
        db.update(TABLE1_NAME, values, COL1_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public boolean deleteMark(int id) {
        return db.delete(TABLE1_NAME, COL_ID + "=" + id, null) > 0;
    }

    public void saveMark(String routename , PointModel departModel, PointModel destModel) {
        Date dateNow = new Date();
        ContentValues values = new ContentValues();
        values.put(COL1_ROUTENAME, routename);
        values.put(COL1_DEPARTID, departModel.getId());
        values.put(COL1_DEPARTNAME, departModel.getName());
        values.put(COL1_DEPARTLONG, departModel.getLongitude());
        values.put(COL1_DEPARTLAT, departModel.getLatitude());
        values.put(COL1_DESTID, destModel.getId());
        values.put(COL1_DESTNAME, destModel.getName());
        values.put(COL1_DESTLONG, destModel.getLongitude());
        values.put(COL1_DESTLAT, destModel.getLatitude());
        // カーソルに一旦入れてから，デリートして再構築
        db.insertOrThrow(TABLE1_NAME, null, values);
    }
    //160707 end

    //160707 add by wyq load bookmark
    public PointModel loadMark(String id, boolean departdest) {
        ContentValues values = new ContentValues();
        PointModel mark = new PointModel();

        if (departdest) {
            mark.setId(values.getAsInteger(COL1_DEPARTID));
            mark.setName(values.getAsString(COL1_DEPARTNAME));
            mark.setLongitude(values.getAsInteger(COL1_DEPARTLONG));
            mark.setLatitude(values.getAsInteger(COL1_DEPARTLAT));
        } else {
            mark.setId(values.getAsInteger(COL1_DESTID));
            mark.setName(values.getAsString(COL1_DESTNAME));
            mark.setLongitude(values.getAsInteger(COL1_DESTLONG));
            mark.setLatitude(values.getAsInteger(COL1_DESTLAT));
        }

        return mark;
    }
    //160707 end
}
