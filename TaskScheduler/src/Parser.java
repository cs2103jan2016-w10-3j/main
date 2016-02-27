import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Parser class used to give return a task or command
 *
 */

public class Parser {
	
	private static final String MESSAGE_ERROR_NO_INPUT = "No input is entered.";
	private static final String MESSAGE_ERROR_INVALID_INPUT = "Input entered is invalid.";
	private static final String MESSAGE_ERROR_TASK_NAME_NOT_ENTERED = "Task name not entered";
	private static final String MESSAGE_ERROR_TASK_TIME_OR_BY_NOT_ENTERED = "Task time or by not entered";
	private static final String MESSAGE_ERROR_TASK_DATE_INVALID = "Task date entered is invalid";
	private static final String MESSAGE_ERROR_TASK_DATE_ALREADY_PASSED = "Date entered already passed";
	private static final String MESSAGE_ERROR_TASK_TIME_INVALID = "Time entered is invalid (hh:mm)";
	private static final String MESSAGE_ERROR_TASK_DURATION_INVALID = "Task duration entered is invalid";
	private static final String MESSAGE_ERROR_TASK_TIME_OUT_OF_BOUND = "Task time entered is out of bound";
	private static final String MESSAGE_ERROR_NO_ARGUMENT = "No argument entered";
	
	private static final int DAY_TOMORROW = 0;
	private static final int DAY_INVALID = -1;
	private static final int ONE_HOUR_IN_MINUTE = 60;
	
	public static Command getCommand(String input) {
		if (input == null) {
			throw new Error(MESSAGE_ERROR_NO_INPUT);
		}
		String[] tokens = divideTokens(input);
		return categorizeCommand(tokens[0]);
	}
	
	public static Task generateTask(String input) {
		
		if (processInputForAdding(input) == null) {
			throw new Error(MESSAGE_ERROR_INVALID_INPUT);
		} else {
			return processInputForAdding(input);
		}
	}
	// The method assume the input starts with add - in order to return a task object
	protected static Task processInputForAdding(String input) {
		String[] tokens = divideTokens(input);
		if (tokens.length == 1) {
			throw new Error(MESSAGE_ERROR_NO_ARGUMENT);
		}
		int i = 1; // Skip the first word
		String name = "";
		Date date;
		int duration = 0;
		boolean exactTime;
		if (!taskNameIsEntered(tokens[i])) {
			throw new Error(MESSAGE_ERROR_TASK_NAME_NOT_ENTERED);
		}
		
		while (i < tokens.length && !tokens[i].equalsIgnoreCase("by")) {
			if (i < tokens.length - 1 && !tokens[i+1].equalsIgnoreCase("by")) {
				name = name.concat(tokens[i] + " ");
			} else {
				name = name.concat(tokens[i]);
			}
			i++;
		}
		
		if (i + 1 == tokens.length) {
			throw new Error(MESSAGE_ERROR_TASK_TIME_OR_BY_NOT_ENTERED);
		}
		
		i++;
		date = getExactDate(tokens[i++]);

		if (i == tokens.length) {
			exactTime = false;
			return new Task(name, date, exactTime, duration);
		}
		
		String timeTokens[] = getTimeStringToken(tokens[i++]);
		int hr, min;
		
		try {
			hr = getTimeElement(timeTokens[0]);
			min = getTimeElement(timeTokens[1]);
		} catch (InvalidTimeException e) {
			throw new Error(MESSAGE_ERROR_TASK_TIME_INVALID);
		}
		
		if (hrOutOfBound(hr) || minOutOfBound(min)) {
			throw new Error(MESSAGE_ERROR_TASK_TIME_OUT_OF_BOUND);
		}
		
		// Try to add time to the old date 
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 12);
		calendar.set(Calendar.MINUTE, 0);
		calendar.add(Calendar.HOUR_OF_DAY, hr);
		calendar.add(Calendar.MINUTE, min);
		date = calendar.getTime();
		
		exactTime = true;
		if (i == tokens.length) {
			return new Task(name, date, exactTime, duration);
		}
		
		timeTokens = getTimeStringToken(tokens[i]);
		try {
			duration = getTotalMin(timeTokens);
		} catch (InvalidTimeException e) {
			throw new Error(MESSAGE_ERROR_TASK_DURATION_INVALID);
		}
		
		return new Task(name, date, exactTime, duration);
		
	}
	
	private static boolean hrOutOfBound(int hr) {
		return hr < 0 || hr > 24;
	}
	
	private static boolean minOutOfBound(int min) {
		return min < 0 || min > 60;
	}
	
	private static int getTotalMin(String[] timeTokens) throws InvalidTimeException {
		int hr, min;
		hr = getTimeElement(timeTokens[0]);
		min = getTimeElement(timeTokens[1]);
		if (hr < 0 || min < 0) {
			throw new Error(MESSAGE_ERROR_TASK_TIME_INVALID);
		}
		return ONE_HOUR_IN_MINUTE * hr + min;
	}
	
	private static String[] getTimeStringToken(String time) {
		String tokens[] = time.split(":");
		if (tokens.length != 2 || tokens.length == 2 && tokens[0].equals("")) {
			throw new Error(MESSAGE_ERROR_TASK_TIME_INVALID);
		}
		return tokens;
	}
	
	private static int getTimeElement(String time) throws InvalidTimeException {
		try {
			return Integer.parseInt(time);
		} catch (NumberFormatException e) {
			throw new InvalidTimeException();
		}
	}
	
	private static boolean taskNameIsEntered(String taskName) {
		return !taskName.equalsIgnoreCase("by");
	}
	
	
	private static Date getExactDate(String dateString) {
		Date date;
		try {
			date = dateParse(dateString);
			if (date.before(new Date())) {
				throw new Error(MESSAGE_ERROR_TASK_DATE_ALREADY_PASSED);
			}
		} catch (ParseException e) {
			if (categorizeDay(dateString) == DAY_INVALID) {
				throw new Error(MESSAGE_ERROR_TASK_DATE_INVALID);
			} else {
				// Exact day not entered by day entered e.g. Monday
				int day = categorizeDay(dateString);
				if (dayAlreadyPassed(day)) {
					throw new Error(MESSAGE_ERROR_TASK_DATE_ALREADY_PASSED);
				} else {
					date = getDateInThisWeek(day);
				}
			}
		}
		return date;
	}
	
	private static Date dateParse(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.parse(date);
	}
	
	
	private static boolean dayAlreadyPassed(int day) {
    	int dayOfWeek = getDayOfTheWeek();
    	if (day == dayOfWeek || dayOfWeek == Calendar.MONDAY) {
    		return true;
    	} else if (dayOfWeek == Calendar.SUNDAY) {
    		return true;
    	} else if (dayOfWeek > day && day != Calendar.SUNDAY) {
    		return true;
    	}
    	return false;
	}
	
	private static Date getDateInThisWeek(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(calendar.getTime());
    	int dayOfWeek = getDayOfTheWeek();
    	int daysInterval;
    	Date result;
    	// It's checked days entered must be the same or ahead of the day of today
    	if (day == dayOfWeek) {
    		daysInterval = 0;
    	} else if (day == Calendar.SUNDAY) {
    		daysInterval = 8 - dayOfWeek;
    	} else {
    		daysInterval = day - dayOfWeek;
    	}
    	calendar.add(Calendar.DAY_OF_YEAR, daysInterval);
    	result = calendar.getTime();
    	return result;
	}
	
	private static int getDayOfTheWeek() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	
	private static String[] divideTokens(String commandString) {
		return commandString.split(" ");
	}
	
	private static Command categorizeCommand(String command) {
		if (command.toLowerCase().equals("add")) {
			return Command.ADD;
		} else if (command.toLowerCase().equals("delete")) {
			return Command.DELETE;
		} else if (command.toLowerCase().equals("edit")){
			return Command.EDIT;
		} else {
			return Command.INVALID;
		}
	}
	
	private static int categorizeDay(String day) {
		if (day.equalsIgnoreCase("tomorrow")) {
			return DAY_TOMORROW;
		} else if (day.equalsIgnoreCase("monday")) {
			return Calendar.MONDAY;
		} else if (day.equalsIgnoreCase("tuesday")) {
			return Calendar.TUESDAY;
		} else if (day.equalsIgnoreCase("wednesday")) {
			return Calendar.WEDNESDAY;
		} else if (day.equalsIgnoreCase("thursday")) {
			return Calendar.THURSDAY;
		} else if (day.equalsIgnoreCase("friday")) {
			return Calendar.FRIDAY;
		} else if (day.equalsIgnoreCase("saturday")) {
			return Calendar.SATURDAY;
		} else if (day.equalsIgnoreCase("sunday")) {
			return Calendar.SUNDAY;
		} else {
			return DAY_INVALID;
		}
	}
	
	public static class InvalidTimeException extends Exception {
		public InvalidTimeException() {
			super(MESSAGE_ERROR_TASK_TIME_INVALID);
		}

		private static final long serialVersionUID = 1L;
	}
}

