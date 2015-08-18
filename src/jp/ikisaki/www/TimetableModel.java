package jp.ikisaki.www;

import java.util.ArrayList;

public class TimetableModel {
	private static int month = 0;
	private static int day = 0;
	private static int startId = 0;
	private static int destinationId = 0;
	private static String startName = "";
	private static String destinationName = "";
	private static String line = "";
	private static int hinomaru = 1;
	private static int nikko = 1;
	private static ArrayList<BusstopsModel> busstopsModel;

	public static void resetTimetableModel(){
		TimetableModel.setMonth(0);
		TimetableModel.setDay(0);
		TimetableModel.setStartId(0);
		TimetableModel.setDestinationId(0);
		TimetableModel.setStartName("");
		TimetableModel.setDestinationName("");
		TimetableModel.setLine("");
		TimetableModel.setHinomaru(1);
		TimetableModel.setNikko(1);
	}

	public static int getMonth() {
		return month;
	}
	public static void setMonth(int month) {
		TimetableModel.month = month;
	}
	public static int getDay() {
		return day;
	}
	public static void setDay(int day) {
		TimetableModel.day = day;
	}
	public static int getStartId() {
		return startId;
	}
	public static void setStartId(int startId) {
		TimetableModel.startId = startId;
	}
	public static int getDestinationId() {
		return destinationId;
	}
	public static void setDestinationId(int destinationId) {
		TimetableModel.destinationId = destinationId;
	}
	public static String getStartName() {
		return startName;
	}
	public static void setStartName(String startName) {
		TimetableModel.startName = startName;
	}
	public static String getDestinationName() {
		return destinationName;
	}
	public static void setDestinationName(String destinationName) {
		TimetableModel.destinationName = destinationName;
	}
	public static String getLine() {
		return line;
	}
	public static void setLine(String line) {
		TimetableModel.line = line;
	}
	public static int getHinomaru() {
		return hinomaru;
	}
	public static void setHinomaru(int hinomaru) {
		TimetableModel.hinomaru = hinomaru;
	}
	public static int getNikko() {
		return nikko;
	}
	public static void setNikko(int nikko) {
		TimetableModel.nikko = nikko;
	}

	public static void setBusstopsModel(ArrayList<BusstopsModel> busstopsModel) {
		TimetableModel.busstopsModel = busstopsModel;
	}

	public static ArrayList<BusstopsModel> getBusstopsModel() {
		return busstopsModel;
	}
}
