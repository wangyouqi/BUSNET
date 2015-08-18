package jp.ikisaki.www;

public class BusstopsModel {
	private String name = "";
	private int id = 0;
	private String yomi = "";

	BusstopsModel(){
		name = "";
		id = 0;
		yomi = "";
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getYomi() {
		return yomi;
	}
	public void setYomi(String yomi) {
		this.yomi = yomi;
	}
}
