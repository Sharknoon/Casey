/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.settings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author frank
 */
public class Logger {

    //The user-set Loglevel, default is ERROR
    private static LogLevel logLevel = LogLevel.WARNING;
    //The output of the Logger
    private static LogMessageConsumer logMessageConsumer = (LogMessage message) -> {
        if (message.errorLevel == LogLevel.OFF) {
            return;
        }
        if (message.errorLevel.compareTo(LogLevel.WARNING) >= 0) {
            System.out.println(message.description);
        } else {
            System.err.println(message.description);
        }
    };

    public interface LogMessageConsumer {

        public void log(LogMessage message);
    }

    public enum LogLevel {
        OFF(0), FATALERROR(1), ERROR(2), WARNING(3), INFO(4), DEBUG(5);
        /**
         * The weight of the LogLevel, 0 means the worst and 5 the best
         */
        public final int level;

        private LogLevel(int level) {
            this.level = level;
        }
    }

    /**
     * Logs a message as a debug log-message. Debug means it is only intended to
     * be used in development usecases.
     *
     * @param message The message to be written to the log
     */
    public static void debug(String message) {
        debug(message, null);
    }

    /**
     * Logs a message as a debug log-message. Debug means it is only intended to
     * be used in development usecases.
     *
     * @param exception The Exception to be written to the log
     */
    public static void debug(Exception exception) {
        debug(null, exception);
    }

    /**
     * Logs a message as a debug log-message. Debug means it is only intended to
     * be used in development usecases.
     *
     * @param message The message to be written to the log
     * @param exception The Exception to be written to the log
     */
    public static void debug(String message, Exception exception) {
        log(LogLevel.DEBUG, message, exception);
    }

    /**
     * Logs a message as a info log-message. Info means it informs the user
     * about something.
     *
     * @param message The message to be written to the log
     */
    public static void info(String message) {
        info(message, null);
    }

    /**
     * Logs a message as a info log-message. Info means it informs the user
     * about something.
     *
     * @param exception The Exception to be written to the log
     */
    public static void info(Exception exception) {
        info(null, exception);
    }

    /**
     * Logs a message as a info log-message. Info means it informs the user
     * about something.
     *
     * @param message The message to be written to the log
     * @param exception The Exception to be written to the log
     */
    public static void info(String message, Exception exception) {
        log(LogLevel.INFO, message, exception);
    }

    /**
     * Logs a message as a warning log-message. Warning means that something
     * <b>should</b> be changed, but it isn't a error (yet)
     *
     * @param message The message to be written to the log
     */
    public static void warning(String message) {
        warning(message, null);
    }

    /**
     * Logs a message as a warning log-message. Warning means that something
     * <b>should</b> be changed, but it isn't a error (yet)
     *
     * @param exception The Exception to be written to the log
     */
    public static void warning(Exception exception) {
        warning(null, exception);
    }

    /**
     * Logs a message as a warning log-message. Warning means that something
     * <b>should</b> be changed, but it isn't a error (yet)
     *
     * @param message The message to be written to the log
     * @param exception The Exception to be written to the log
     */
    public static void warning(String message, Exception exception) {
        log(LogLevel.WARNING, message, exception);
    }

    /**
     * Logs a message as a error log-message. Error means that something failed,
     * but it isn't system critical.
     *
     * @param message The message to be written to the log
     */
    public static void error(String message) {
        error(message, null);
    }

    /**
     * Logs a message as a error log-message. Error means that something failed,
     * but it isn't system critical.
     *
     * @param exception The Exception to be written to the log
     */
    public static void error(Exception exception) {
        error(null, exception);
    }

    /**
     * Logs a message as a error log-message. Error means that something failed,
     * but it isn't system critical.
     *
     * @param message The message to be written to the log
     * @param exception The Exception to be written to the log
     */
    public static void error(String message, Exception exception) {
        log(LogLevel.ERROR, message, exception);
    }

    /**
     * Logs a message as a fatalerror log-message. Fatalerror means that
     * something system-critical failed.
     *
     * @param message The message to be written to the log
     */
    public static void fatalError(String message) {
        fatalError(message, null);
    }

    /**
     * Logs a message as a fatalerror log-message. Fatalerror means that
     * something system-critical failed.
     *
     * @param exception The Exception to be written to the log
     */
    public static void fatalError(Exception exception) {
        fatalError(null, exception);
    }

    /**
     * Logs a message as a fatalerror log-message. Fatalerror means that
     * something system-critical failed.
     *
     * @param message The message to be written to the log
     * @param exception The Exception to be written to the log
     */
    public static void fatalError(String message, Exception exception) {
        log(LogLevel.FATALERROR, message, exception);
    }

    /*
    * Logs the message, message and/or exception can be null
     */
    private static void log(LogLevel level, String message, Exception exception) {
        if (level.level > logLevel.level || logLevel.equals(LogLevel.OFF)) {
            return;
        }
        message = message == null ? "" : message;
        if (exception != null) {
            message += message.isEmpty() ? "" : "\n";
            if (LogLevel.DEBUG.equals(logLevel)) {
                StringWriter writer = new StringWriter();
                exception.printStackTrace(new PrintWriter(writer));
                message += writer.toString();
            } else {
                message += exception.getLocalizedMessage();
            }
        }
        String[] linesToPrint = message.split("\\R+");
        if (linesToPrint.length > 20) {
            linesToPrint = Arrays.copyOf(linesToPrint, 20);
        }
        message = "";
        String header = getHeader(level);
        String headerBlanks = linesToPrint.length > 1 ? getBlanks(header.length()) : "";
        for (int i = 0; i < linesToPrint.length; i++) {
            linesToPrint[i] = linesToPrint[i].length() > 500 ? linesToPrint[i].substring(0, 499) + "... line is too long to be logged" : linesToPrint[i];
            message += (i == 0 ? header : headerBlanks) + linesToPrint[i] + (i + 1 < linesToPrint.length ? System.lineSeparator() : "");
        }
        logMessageConsumer.log(LogMessage.of(level, message));
    }

    /**
     * Sets the output of the logger
     * @param logMessageConsumer 
     */
    public static void setLogMessageConsumer(LogMessageConsumer logMessageConsumer) {
        Logger.logMessageConsumer = logMessageConsumer;
    }

    private static int maxLengthOfClassName = 0;

    //Header: Date + Class#MethodName + Level
    private static String getHeader(LogLevel level) {
        String result = getDateString() + " ";
        String className = getClassName();
        int currentLengthOfClassName = className.length();
        maxLengthOfClassName = Math.max(currentLengthOfClassName, maxLengthOfClassName);
        result += getBlanks((maxLengthOfClassName - currentLengthOfClassName) / 2);
        result += "[" + className + "] ";
        result += getBlanks((maxLengthOfClassName - currentLengthOfClassName) / 2);
        result += (maxLengthOfClassName - currentLengthOfClassName) % 2 != 0 ? " " : "";
        result += getLevelString(level) + " ";
        return result;
    }

    private static int maxLengthOfErrorLevel = 0;

    //Returns the Level as a formatted String
    private static String getLevelString(LogLevel level) {
        String errorLevel = level.name();
        int currentLengthOfErrorLevel = errorLevel.length();
        maxLengthOfErrorLevel = Math.max(currentLengthOfErrorLevel, maxLengthOfErrorLevel);
        String result = getBlanks((maxLengthOfErrorLevel - currentLengthOfErrorLevel) / 2);
        result += "[" + errorLevel + "]";
        result += getBlanks((maxLengthOfErrorLevel - currentLengthOfErrorLevel) / 2);
        result += (maxLengthOfErrorLevel - currentLengthOfErrorLevel) % 2 != 0 ? " " : "";
        return result;
    }

    //Returns the Date as a formatted String
    private static String getDateString() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + "]";
    }

    private static final HashMap<Integer, String> SPACES = new HashMap<>();

    //Fast Method to get a amount of blanks as a String
    private static String getBlanks(int amount) {
        if (SPACES.containsKey(amount)) {
            return SPACES.get(amount);
        }
        char[] blanks = new char[amount];
        Arrays.fill(blanks, ' ');
        String res = new String(blanks);
        SPACES.put(amount, res);
        return res;
    }

    //Returns the classname + # + methodname of the caller of the log method
    private static String getClassName() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        for (StackTraceElement elem : stack) {
            try {
                if (!Logger.class.isAssignableFrom(Class.forName(elem.getClassName()))) {
                    return cleanClassName(elem.getClassName()) + " #" + elem.getMethodName();
                }
            } catch (ClassNotFoundException ex) {
            }
        }
        return stack.length > 0 ? stack[0].getClass().getSimpleName() + " #" + stack[0].getMethodName() : Logger.class.getSimpleName();
    }

    //Cuts of the package path to save some space
    private static String cleanClassName(String className) {
        className = className.substring(className.lastIndexOf(".") + 1);
        return className;
    }

    /**
     * This class is responsible for transporting the messages to the
     * user-specified output. it contains a loglevel and a description.
     */
    public static class LogMessage {

        public final LogLevel errorLevel;
        public final String description;

        /**
         * Creates a new LogMessage
         *
         * @param errorLevel The desired LogLevel, from OFF to DEBUG
         * @param description The desired description, to sxplain what happened
         * @return The new instance of the {@link LogMessage}
         */
        public static LogMessage of(LogLevel errorLevel, String description) {
            return new LogMessage(errorLevel, description);
        }

        /**
         * Creates a new LogMessage
         *
         * @param errorLevel The desired LogLevel, from OFF to DEBUG
         * @param description The desired description, to sxplain what happened
         */
        public LogMessage(LogLevel errorLevel, String description) {
            this.errorLevel = errorLevel != null ? errorLevel : LogLevel.INFO;
            this.description = description != null ? description : "";
        }

        /**
         * Returns the error-level of this logMessage
         *
         * @return The level as a {@link LogLevel} object
         */
        public LogLevel getErrorLevel() {
            return errorLevel;
        }

        /**
         * Returns the description of the LogMessage
         *
         * @return The description what happened as a String
         */
        public String getDescription() {
            return description;
        }

        /**
         * Returns the description, so you can easily write in the parser:
         * <p>
         * System.out::println</p>
         *
         * @return Returns the description of this logMessage
         */
        @java.lang.Override
        public String toString() {
            return description;
        }

    }
}
