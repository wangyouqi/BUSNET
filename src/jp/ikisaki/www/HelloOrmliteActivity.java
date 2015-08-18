package jp.ikisaki.www;

import android.os.Bundle;
import android.widget.TextView;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import jp.ikisaki.www.db.*;

import java.sql.SQLException;
import java.util.List;

public class HelloOrmliteActivity extends OrmLiteBaseActivity<DataHelper> {

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		TextView tv = (TextView) this.findViewById(R.id.output);

		try {
			// 1.获取DAO
			Dao<Landmark, Integer> helloDao = getHelper().getDaoLandmark();

			// 添加数据:一次添加两条数据

			for (int i = 0; i < 2; i++) {
				Landmark landmark = new Landmark("Hello" + i);
				// Create a new row in the database from an object.
				// 更新hello中的某一表段中的内容
				helloDao.create(landmark);
			}

			tv.setText(tv.getText() + "\n" + "添加数据完成" + "\n");

			// 在TextView中添加查询到的数据
			List<Landmark> landmarks = helloDao.queryForAll();
			for (Landmark h : landmarks) {
				tv.setText(tv.getText() + h.toString() + "\n");
			}

			// 删除数据第一条数据
			tv.setText(tv.getText() + "\n" + "删除第一条数据n" + "\n");
			//helloDao.delete(hellos.get(0));

			// 重新查询数据
			tv.setText(tv.getText() + "\n" + "删除第一条数据后剩下的数据库的内容" + "\n");
			landmarks = helloDao.queryForAll();

			for (Landmark h : landmarks) {

				tv.setText(tv.getText() + h.toString() + "\n");

			}

			// 修改数据
			Landmark h1 = landmarks.get(0);
			h1.setWord("这是修改的数据");
			tv.setText(tv.getText() + "这是修改" + h1.getWord());

			helloDao.update(h1);

			// 重新查询数据

			landmarks = helloDao.queryForAll();

			for (Landmark h : landmarks) {
				tv.setText(tv.getText() + "\n" + h.toString());
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}