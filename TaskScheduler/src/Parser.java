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
	
	private static final String SEPARATOR = "||";
	private static final String TIME_SEPARATOR = ":";
	private static final String DURATION_SEPARATOR = "\\.";
	
	private static final String MESSAGE_ERROR_NO_INPUT = "No input is entered.";
	private static final String MESSAGE_ERROR_INVALID_INPUT = "Input entered is invalid.";
	private static final String MESSAGE_ERROR_TASK_NAME_NOT_ENTERED = "Task name not entered";
	private static final String MESSAGE_ERROR_TASK_TIME_OR_SEPARATOR_NOT_ENTERED = "Task time or separtor not entered";
	private static final String MESSAGE_ERROR_TASK_DATE_INVALID = "Task date entered is invalid";
	private static final String MESSAGE_ERROR_TASK_DATE_ALREADY_PASSED = "Date entered already passed";
	private static final String MESSAGE_ERROR_TASK_TIME_INVALID = "Time entered is invalid (hh:mm)";
	private static final String MESSAGE_ERROR_TASK_DURATION_INVALID = "Task duration entered is invalid";
	private static final String MESSAGE_ERROR_TASK_TIME_OUT_OF_BOUND = "Task time entered is out of bound";
	private static final String MESSAGE_ERROR_TASK_DATE_NOT_ENTERED = "Task date is not entered.";
	private static final String MESSAGE_ERROR_TASK_INDEX_INVALID = "Task index entered is invalid.";
	private static final String MESSAGE_ERROR_NO_ARGUMENT = "No argument entered";
	private static final String MESSAGE_ERROR_TOO_MANY_ARGUMENT = "Too many arguments are entered.";
	
	private static final int DAY_TOMORROW = 0;
	private static final int DAY_INVALID = -1;
	private static final int ONE_HOUR_IN_MINUTE = 60;
	
	
	/**
	 * Get the command of input , which is the command corresponding to the first word.
	 * @param input User input.
	 * @return Command type or invalid command.
	 */
	public static Command getCommand(String input) {
		if (input == null) {
			throw new Error(MESSAGE_ERROR_NO_INPUT);
		}
		String[] tokens = divideTokens(input);
		return categorizeCommand(tokens[0]);
	}
	
	/**
	 * Get the task for adding from the input (THE METHOD ASSUME FIRST WORD IS Add).
	 * @param input User input.
	 * @return Task to be added.
	 */
	public static Task getTaskForAdding(String input) {
		
		if (tryGettingTask(input) == null) {
			throw new Error(MESSAGE_ERROR_INVALID_INPUT);
		} else {
			return tryGettingTask(input);
		}
	}
	
	/**
	 * Get the task to send for editing from the input (THIS METHOD ASSUMES THE FIRST WORD IS Edit).
	 * @param input User input.
	 * @return TaskToEdit to be edited.
	 */
	public static TaskToEdit getTaskForEditing(String input) {
		
		String[] tokens = divideTokens(input);
		
		
		// Get Index
		int index = Integer.valueOf(tokens[1]);
		TaskToEdit toReturn = new TaskToEdit(index);
		
		// Get Arguments
		switch (tokens[2]) {

			// For Duration Edit Case, simply get the duration from the input and add it.
			case "duration":
				int duration = Integer.valueOf(tokens[3]);
				toReturn.setDuration(duration);
				break;
			// For Name Edit Case, simply get the name from the input and add it.
			case "name":
				String name = getArgumentForEditing(input);
				toReturn.setName(name);
				break;
			// For DateTime Edit Case, get date, then time, then compile date and add.
			case "datetime":
				String datetime = getArgumentForEditing(input);
				Date date = getExactDate(datetime.split(" ")[0]);
				
				String timeTokens[] = getTimeStringToken(datetime.split(" ")[1]);
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
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.set(Calendar.HOUR, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.add(Calendar.HOUR_OF_DAY, hr);
				calendar.add(Calendar.MINUTE, min);
				date = calendar.getTime();
				
				toReturn.setTimeStart(date);
				
				break;
			default:
				throw new Error(MESSAGE_ERROR_INVALID_INPUT);
		}
		
		// Return Task to Edit
		return toReturn;
		
	}
	
	/**
	 * This method helps to find the argument of the task when creating a task for editing from the user input.
	 * @param input User Input
	 * @return String with the Argument of the task.
	 */
	public static String getArgumentForEditing(String input) {
		
		String[] tokens = input.split(" ", 4);
		return tokens[3];
		
	}
	
	/**
	 * Get task index for deleting.
	 * @param input the user input (Assume the first word is delete)
	 * @return The index to be deleted.
	 */
	public static int getTaskIndexForDeleting(String input) {
		String[] tokens = divideTokens(input);
		int index;
		if (tokens.length == 1) {
			throw new Error(MESSAGE_ERROR_NO_ARGUMENT);
		}
		if (tokens.length > 2) {
			throw new Error(MESSAGE_ERROR_TOO_MANY_ARGUMENT);
		}
		try {
			index = Integer.parseInt(tokens[1]);
		} catch (NumberFormatException e) {
			throw new Error(MESSAGE_ERROR_TASK_INDEX_INVALID);
		}
		return index;
	}
	
	
	
	/**
	 * Try to get a task generated from user input.
	 * @param input User input.
	 * @return Task object.
	 */
	protected static Task tryGettingTask(String input) {
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
		
		// Extract the name from input
		while (i < tokens.length && !tokens[i].equalsIgnoreCase(SEPARATOR)) {
			if (i < tokens.length - 1 && !tokens[i+1].equalsIgnoreCase(SEPARATOR)) {
				name = name.concat(tokens[i] + " ");
			} else {
				name = name.concat(tokens[i]);
			}
			i++;
		}
				
		if (i == tokens.length) {
			throw new Error(MESSAGE_ERROR_TASK_TIME_OR_SEPARATOR_NOT_ENTERED);
		}
		
		// Try to get date if format is correct till this stage
		i++;
		if (i == tokens.length) {
			throw new Error(MESSAGE_ERROR_TASK_DATE_NOT_ENTERED);
		}
		date = getExactDate(tokens[i++]);
		
		// Return a new task if duration and exact time is not specified
		if (i == tokens.length) {
			exactTime = false;
			return new Task(name, date, exactTime, duration);
		}
		
		// Try to get the exact time if specified
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
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.add(Calendar.HOUR_OF_DAY, hr);
		calendar.add(Calendar.MINUTE, min);
		date = calendar.getTime();
		
		// Return the task if the exact time is valid
		exactTime = true;
		if (i == tokens.length) {
			return new Task(name, date, exactTime, duration);
		}
		
		// Try to get the duration if specified
		try {
			timeTokens = getDurationStringToken(tokens[i]);
			duration = getTotalMin(timeTokens);
		} catch (InvalidTimeException e) {
			throw new Error(MESSAGE_ERROR_TASK_DURATION_INVALID);
		} catch (Error e) {
			throw new Error(MESSAGE_ERROR_TASK_DURATION_INVALID);
		}
		
		// All input valid, return a fully defined task
		return new Task(name, date, exactTime, duration);
		
	}
	/**
	 * Return whether hour is out of bound.
	 * @param hr Hour.
	 * @return Whether hour is out of bound.
	 */
	private static boolean hrOutOfBound(int hr) {
		return hr < 0 || hr > 24;
	}
	
	/**
	 * Return whether minute is out of bound.
	 * @param min Minute.
	 * @return Whether minute is out of bound.
	 */
	private static boolean minOutOfBound(int min) {
		return min < 0 || min > 60;
	}
	
	/**
	 * Get the total minutes from hh:mm format.
	 * @param timeTokens String tokens that contain time.
	 * @return Total minutes.
	 * @throws InvalidTimeException Time input is invalid.
	 */
	private static int getTotalMin(String[] timeTokens) throws InvalidTimeException {
		int hr, min;
		hr = getTimeElement(timeTokens[0]);
		min = getTimeElement(timeTokens[1]);
		if (hr < 0 || min < 0) {
			throw new Error(MESSAGE_ERROR_TASK_TIME_INVALID);
		}
		return ONE_HOUR_IN_MINUTE * hr + min;
	}
	
	/**
	 * Get the time token for processing.
	 * @param time Time string in hh:mm format.
	 * @return A time token.
	 */
	private static String[] getTimeStringToken(String time) {
		String tokens[] = time.split(TIME_SEPARATOR);
		if (tokens.length != 2 || tokens.length == 2 && tokens[0].equals("")) {
			throw new Error(MESSAGE_ERROR_TASK_TIME_INVALID);
		}
		return tokens;
	}
	
	/**
	 * Get the duration token for processing.
	 * @param duration Duration string in hh.mm format.
	 * @return A duration token.
	 */
	private static String[] getDurationStringToken(String duration) {
		String tokens[] = duration.split(DURATION_SEPARATOR);
		if (tokens.length != 2 || tokens.length == 2 && tokens[0].equals("")) {
			throw new Error(MESSAGE_ERROR_TASK_DURATION_INVALID);
		}
		return tokens;
	}
	
	
	/**
	 * Parse a string to integer for processing as time element (hour or minute).
	 * @param time Time string.
	 * @return Integer representing hour or minute.
	 * @throws InvalidTimeException Time input is invalid.
	 */
	private static int getTimeElement(String time) throws InvalidTimeException {
		try {
			return Integer.parseInt(time);
		} catch (NumberFormatException e) {
			throw new InvalidTimeException();
		}
	}
	/**
	 * Return if task name is entered.
	 * @param taskName Task name, which is part of the user input (the second word till the separator)
	 * @return Whether task name is entered.
	 */
	private static boolean taskNameIsEntered(String taskName) {
		return !taskName.equalsIgnoreCase(SEPARATOR);
	}
	
	/**
	 * Get the exact date from a date string in format dd/mm/yyyy.
	 * @param dateString Date string.
	 * @return A date object.
	 */
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
				if (dayAlreadyPassed(day) && day != DAY_TOMORROW) {
					throw new Error(MESSAGE_ERROR_TASK_DATE_ALREADY_PASSED);
				} else {
					date = getDateInThisWeek(day);
				}
			}
		}
		return date;
	}
	
	
	/**
	 * Parse a date string into a date object.
	 * @param date Date string.
	 * @return A date object.
	 * @throws ParseException Date cannot be parsed.
	 */
	private static Date dateParse(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		df.setLenient(false);
		return df.parse(date);
	}
	
	/**
	 * Return if a day is already passed.
	 * @param day Day object indicated in the Calendar class.
	 * @return Whether the day has already passed.
	 */
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
	
	/**
	 * Get the date object specified by an input day.
	 * @param day Day object indicated in the Calendar class.
	 * @return A date object.
	 */
	private static Date getDateInThisWeek(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(calendar.getTime());
    	int dayOfWeek = getDayOfTheWeek();
    	int daysInterval;
    	Date result;
    	
    	if (day == DAY_TOMORROW) {
    		calendar.add(Calendar.DAY_OF_YEAR, 1);
    		return calendar.getTime();
    	}
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
	
	/**
	 * Get the day of today.
	 * @return An integer representing a day specified in Calendar class.
	 */
	private static int getDayOfTheWeek() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * Return a string token split by space.
	 * @param commandString Command string.
	 * @return A string token split by space.
	 */
	private static String[] divideTokens(String commandString) {
		return commandString.split(" ");
	}
	
	/**
	 * Get the command type from user input.
	 * @param command User input.
	 * @return Command type.
	 */
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
	
	/**
	 * Get the day from user input.
	 * @param day Day string.
	 * @return Day integer specified in Calendar class.
	 */
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


