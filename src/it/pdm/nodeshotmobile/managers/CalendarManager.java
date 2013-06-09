package it.pdm.nodeshotmobile.managers;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarManager {

	
	public CalendarManager() {
	
	}
	
	static public Calendar getCalendar() {
		final Calendar c = Calendar.getInstance();
		return c;
 
	}
	
	/* parte di parsing stringa datetime in formato YYYY/MM/DD HH:MM:SS */
	
	/**
     * Returns the year of a given datetime
     * @param		datetime strings that contains YYYY/MM/DD HH:MM:SS
     * @return      the year
     */
	static String getYear(String datetime){
		String[] pieces = datetime.split(" "); //divido data da time
		String data=pieces[0];//prelevo solo la data
		
		pieces = data.split("/"); //prelevo le singole parti della data
		return pieces[0]; //restituisco l'anno
	}
	
	/**
     * Returns the month of a given datetime
     * @param		datetime strings that contains YYYY/MM/DD HH:MM:SS
     * @return      the month
     */
	static String getMonth(String datetime){
		String[] pieces = datetime.split(" "); //divido data da time
		String data=pieces[0];//prelevo solo la data
		
		pieces = data.split("/"); //prelevo le singole parti della data
		return pieces[1]; //restituisco il mese
	}
	
	/**
     * Returns the day of a given datetime
     * @param		datetime strings that contains YYYY/MM/DD HH:MM:SS
     * @return      the day
     */
	static String getDay(String datetime){
		String[] pieces = datetime.split(" "); //divido data da time
		String data=pieces[0];//prelevo solo la data
		
		pieces = data.split("/"); //prelevo le singole parti della data
		return pieces[2]; //restituisco il giorno
	}
	
	/**
     * Returns the hour of a given datetime
     * @param		datetime strings that contains YYYY/MM/DD HH:MM:SS
     * @return      the hour
     */
	static String getHour(String datetime){
		String[] pieces = datetime.split(" "); //divido data da time
		String data=pieces[1];//prelevo solo l'ora
		
		pieces = data.split(":"); //prelevo le singole parti della data
		return pieces[0]; //restituisco le ore
	}
	
	/**
     * Returns the minute of a given datetime
     * @param		datetime strings that contains YYYY/MM/DD HH:MM:SS
     * @return      the minute
     */
	static String getMinute(String datetime){
		String[] pieces = datetime.split(" "); //divido data da time
		String data=pieces[1];//prelevo solo l'ora
		
		pieces = data.split(":"); //prelevo le singole parti della data
		return pieces[1]; //restituisco i minuti
	}
	
	/**
     * Returns the seconds of a given datetime
     * @param		datetime strings that contains YYYY/MM/DD HH:MM:SS
     * @return      the seconds
     */
	static String getSecond(String datetime){
		String[] pieces = datetime.split(" "); //divido data da time
		String data=pieces[1];//prelevo solo l'ora
		
		pieces = data.split(":"); //prelevo le singole parti della data
		return pieces[2]; //restituisco i secondi
	}
	
	
	
	/**
     * Returns a string that contains the system date (DD/MM/YYYY)
     * @return      system date
     */
    static String getCurrentDate(){
		
    	final Calendar c = getCalendar();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
    	String result= day+"/"+(month+1)+"/"+year;
		
		return result;
		
	}
    
    /**
     * Returns a string that contains the system hour (HH:MM:SS)
     * @return      the hour
     */
    static String getCurrentTime(){
		
    	final Calendar c = getCalendar();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		
    	String result= hour+":"+minute+":"+second;
		
		return result;
		
	}
    
  //restituisce una stringa rappresentante la data e l'ora di sistema
    /**
     * Returns a string that contains date and hour (DD/MM/YYYY HH:MM:SS)
     * @return      date and hour
     */
    public static String getCurrentDateTime(){
    	
    	String result=getCurrentDate();
		
    	result+=" "+getCurrentTime();
    	
    	return result;
		
	}
	
    //formatta la stringa rappresentante la data in formato YYYY/MM/DD
    /**
     * Returns the date in a format that can be used in the database (YYYY/MM/DD) 
     * @param		date strings that contains the date (DD/MM/YYYY)
     * @return      the formatted date
     */
	static String formatDateForDatabase(String date){
		
		String[] pieces = date.split("/");
		
		String day= pieces[0];
		String month= pieces[1];
		String year= pieces[2];
		
		String result= year+"/"+month+"/"+day;
		
		return result;
		
	}
	
	/**
     * Returns the time in format HH:MM:SS
     * @param		datetime strings that contains HH:MM:SS
     * @return      the time
     * 
     */
	static String formatTimeForDatabase(String time){
		
		String[] pieces = time.split(":");
		
		String second= pieces[2];
		String minute= pieces[1];
		String hour= pieces[0];
		
		String result= hour+":"+minute+":"+second;
		
		return result;
		
	}
	
	//formatta la stringa rappresentante la data e l'ora in formato YYYY/MM/DD HH:MM:SS
	/**
     * Returns a string that contains date and time (YYYY/MM/DD HH:MM:SS)
     * @param		datetime strings that contains YYYY/MM/DD HH:MM:SS
     * @return      the date and the time
     */
	public static String formatDateTimeForDatabase(String datetime){
		
		String[] date_time = datetime.split(" ");
		
		String date= date_time[0];
		String time= date_time[1];
		
		date=formatDateForDatabase(date);
		time=formatTimeForDatabase(time);
		
		String result= date+" "+time;
		
		return result;
		
	}
	
	//trasforma la stringa rappresentante la data l'ora da formato YYYY/MM/DD HH:MM:SS in DD/MM/YYYY HH:MM:SS
	/**
     * Returns a string that contains date and time (DD/MM/YYYY HH:MM:SS)
     * @param		datetime strings that contains YYYY/MM/DD HH:MM:SS
     * @return      the date and the time
     */
	static String readDateTime(String datetime){
		
		
		String hour= getHour(datetime);
		String minute= getMinute(datetime);
		String second= getSecond(datetime);
		String year= getYear(datetime);
		String month= getMonth(datetime);
		String day= getDay(datetime);
		
		month=("0"+month).substring(("0"+month).length()-2);
		day=("0"+day).substring(("0"+day).length()-2);
		hour=("0"+hour).substring(("0"+hour).length()-2);
		minute=("0"+minute).substring(("0"+minute).length()-2);
		second=("0"+second).substring(("0"+second).length()-2);
		
		String result= day+"/"+month+"/"+year+" "+hour+":"+minute+":"+second;
		
		return result;
		
	}
	
	/**
     * Compares the given datetimes.
     * @param		datetime1 a datetime
     * @param		datetime2 a datetime
     * @return      "equals" if datetimes are egual, if datetime1 is greater returns '>', otherwise '<'.
     */
	static String compareDateTimes(String datetime1,String datetime2){
		
		/*return 'equal' if they are equal, return '>' if the first datetime is greater 
		 * than second, return '<' otherwise*/
	
		
		
		if(datetime1.equals(datetime2)){
			return "equal";
		}else{
			if(datetime1.compareTo(datetime2)>0){
				return ">";
			}else{
				return "<";
			}
		}
		
	}
	
	//calcola i giorni di differenza tra due date
	/**
     * Computes the average days between two dates.
     * @param		date1 a date
     * @param		date2 a date
     * @return      a double rappresenting the average day between two dates.
     */
	public static double differenceDates(String date1,String date2){
		
		Integer hour= Integer.parseInt(getHour(date1));
		Integer minute= Integer.parseInt(getMinute(date1));
		Integer second= Integer.parseInt(getSecond(date1));
		Integer year= Integer.parseInt(getYear(date1));
		Integer month= Integer.parseInt(getMonth(date1));
		Integer day= Integer.parseInt(getDay(date1));
		
		GregorianCalendar cal1 = new GregorianCalendar(year, month, day, hour, minute, second);
		
		hour= Integer.parseInt(getHour(date2));
		minute= Integer.parseInt(getMinute(date2));
		second= Integer.parseInt(getSecond(date2));
		year= Integer.parseInt(getYear(date2));
		month= Integer.parseInt(getMonth(date2));
		day= Integer.parseInt(getDay(date2));
		
		GregorianCalendar cal2 = new GregorianCalendar(year, month, day, hour, minute, second);
		
		long date1Millis = cal1.getTimeInMillis();
		long date2Millis = cal2.getTimeInMillis();
		
		long difference = date1Millis - date2Millis;
		
		//1 giorno medio = 1000*60*60*24 ms
		// = 86400000 ms
		double giorniFraDueDate = Math.round( difference / 86400000.0 );
		
		return giorniFraDueDate;
	}
	
}
