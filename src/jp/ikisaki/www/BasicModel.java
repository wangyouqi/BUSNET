package jp.ikisaki.www;

public class BasicModel {

	private static PointModel departure = new PointModel();
	private static PointModel destination = new PointModel();

	private static int year = 0;
	private static int month = 0;
	private static int day = 0;
	private static int hour = 0;
	private static int minute = 0;
	private static int after = 0;
	private static int tabNumber = 5;

	private static String date = "";
	private static String time = "";
	private static String vehicle = "";
	private static String setting = "";
	private static String forwardOrBackward = "";
	private static String line = "";

	private static String routeShareUrl = "";
	private static String destinationShareUrl = "";

	private static int shareHour = 0;
	private static int shareMinute = 0;

	private static int parameter = 0;


	public BasicModel(){
		BasicModel.departure.setId(0);
		BasicModel.departure.setLatitude(0);
		BasicModel.departure.setLongitude(0);
		BasicModel.departure.setName("");
		BasicModel.departure.setYomi("");
		BasicModel.departure.setFrequency(0);
		BasicModel.destination.setId(0);
		BasicModel.destination.setLatitude(0);
		BasicModel.destination.setLongitude(0);
		BasicModel.destination.setName("");
		BasicModel.destination.setYomi("");
		BasicModel.destination.setFrequency(0);
		BasicModel.date = "";
		BasicModel.time = "";
		BasicModel.vehicle = "";
		BasicModel.forwardOrBackward = "";
		BasicModel.tabNumber = 5;
		BasicModel.setParameter(0);
	}

	public static void resetDeparture(){
		BasicModel.departure.setId(0);
		BasicModel.departure.setLatitude(0);
		BasicModel.departure.setLongitude(0);
		BasicModel.departure.setName("");
		BasicModel.departure.setYomi("");
		BasicModel.departure.setFrequency(0);
	}

	public static void resetDestination(){
		BasicModel.destination.setId(0);
		BasicModel.destination.setLatitude(0);
		BasicModel.destination.setLongitude(0);
		BasicModel.destination.setName("");
		BasicModel.destination.setYomi("");
		BasicModel.destination.setFrequency(0);
	}

	public static void resetModel(){
		BasicModel.departure.setId(0);
		BasicModel.departure.setLatitude(0);
		BasicModel.departure.setLongitude(0);
		BasicModel.departure.setName("");
		BasicModel.departure.setYomi("");
		BasicModel.departure.setFrequency(0);
		BasicModel.destination.setId(0);
		BasicModel.destination.setLatitude(0);
		BasicModel.destination.setLongitude(0);
		BasicModel.destination.setName("");
		BasicModel.destination.setYomi("");
		BasicModel.destination.setFrequency(0);

		BasicModel.year = 0;
		BasicModel.month = 0;
		BasicModel.day = 0;
		BasicModel.hour = 0;
		BasicModel.minute = 0;
		BasicModel.after = 0;
		BasicModel.date = "";
		BasicModel.time = "";
		BasicModel.vehicle = "";
		BasicModel.setting = "";
		BasicModel.forwardOrBackward = "";
		BasicModel.line = "";
		BasicModel.tabNumber = 5;

		BasicModel.setParameter(0);
	}

	public static PointModel getDeparture() {
		return departure;
	}
	public static void setDeparture(PointModel departure) {
		BasicModel.departure = departure;
	}
	public static PointModel getDestination() {
		return destination;
	}
	public static void setDestination(PointModel destination) {
		BasicModel.destination = destination;
	}
	public static int getMonth() {
		return month;
	}
	public static void setMonth(int month) {
		BasicModel.month = month;
	}
	public static String getDate() {
		return date;
	}
	public static void setDate(String date) {
		BasicModel.date = date;
	}
	public static int getDay() {
		return day;
	}
	public static void setDay(int date) {
		BasicModel.day = date;
	}
	public static int getHour() {
		return hour;
	}
	public static void setHour(int hour) {
		BasicModel.hour = hour;
	}
	public static int getMinute() {
		return minute;
	}
	public static void setMinute(int minute) {
		BasicModel.minute = minute;
	}
	public static String getTime() {
		return time;
	}
	public static void setTime(String time) {
		BasicModel.time = time;
	}
	public String getVehicle() {
		return vehicle;
	}
	public static void setVehicle(String vehicle) {
		BasicModel.vehicle = vehicle;
	}
	public static String getSetting() {
		return setting;
	}
	public static void setSetting(String setting) {
		BasicModel.setting = setting;
	}
	public static String getForwardOrBackward() {
		return forwardOrBackward;
	}
	public static void setForwardOrBackward(String forwardOrBackward) {
		BasicModel.forwardOrBackward = forwardOrBackward;
	}
	public static int getTabNumber() {
		return tabNumber;
	}
	public static void setTabNumber(int tabNumber) {
		BasicModel.tabNumber = tabNumber;
	}
	public static int getYear() {
		return year;
	}
	public static void setYear(int year) {
		BasicModel.year = year;
	}
	public static int getAfter() {
		return after;
	}

	public static void setAfter(int after) {
		BasicModel.after = after;
	}
	public static String getLine() {
		return line;
	}

	public static void setLine(String line) {
		BasicModel.line = line;
	}
	public static String getRouteShareUrl() {
		return routeShareUrl;
	}

	public static void setRouteShareUrl(String routeShareUrl) {
		BasicModel.routeShareUrl = routeShareUrl;
	}

	public static String getDestinationShareUrl() {
		return destinationShareUrl;
	}

	public static void setDestinationShareUrl(String destinationShareUrl) {
		BasicModel.destinationShareUrl = destinationShareUrl;
	}
	public static int getShareHour() {
		return shareHour;
	}

	public static void setShareHour(int shareHour) {
		BasicModel.shareHour = shareHour;
	}

	public static int getShareMinute() {
		return shareMinute;
	}

	public static void setShareMinute(int shareMinute) {
		BasicModel.shareMinute = shareMinute;
	}

	public static void setParameter(int parameter) {
		BasicModel.parameter = parameter;
	}

	public static int getParameter() {
		return parameter;
	}

}
