package jp.ikisaki.www.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import jp.ikisaki.www.Common;


public class DataHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = Common.DBNAME;
	private static final int DATABASE_VERSION = 1;
	private Dao<Landmark, Integer> daoLandmark = null;
    private Dao<Classcode, Integer> daoClasscode = null;
    private Dao<RegDevice, Integer> daoRegDevice = null;

	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Landmark.class);
		} catch (SQLException e) {
			Log.e(DataHelper.class.getName(), "Can't create database", e);
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int arg2, int arg3) {
		try {
			TableUtils.dropTable(connectionSource, Landmark.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DataHelper.class.getName(), "Can't up database", e);
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		super.close();
		daoLandmark = null;
        daoClasscode = null;
        daoRegDevice = null;
	}

    //landmark table
	public Dao<Landmark, Integer> getDaoLandmark() throws SQLException {
		if (daoLandmark == null) {
			daoLandmark = getDao(Landmark.class);
		}
		return daoLandmark;
	}

    //classcode table
    public Dao<Classcode, Integer> getDaoClasscode() throws SQLException {
        if ( daoClasscode == null) {
            daoClasscode = getDao(Classcode.class);
        }
        return daoClasscode;
    }

    //regdevice table
    public Dao<RegDevice, Integer> getDaoRegDevice() throws SQLException {
        if ( daoRegDevice == null) {
            daoRegDevice = getDao(RegDevice.class);
        }
        return daoRegDevice;
    }
}