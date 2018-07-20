/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sharknoon.casey.ide.utils.settings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 *
 * @author frank
 */
public class Logger {

    //The user-set Loglevel, default is WARNING
    private static LogLevel logLevel = LogLevel.WARNING;
    //The output of the Logger
    private static Consumer<LogMessage> logMessageConsumer = (LogMessage message) -> {
        if (message.errorLevel == LogLevel.OFF) {
            return;
        }
        if (message.errorLevel.compareTo(LogLevel.WARNING) >= 0) {
            System.out.println(message.description);
        } else {
            System.err.println(message.description);
        }
    };

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
     * Sets the LogLevel of the logger, default is WARNING
     *
     * @param logLevel The new LogLevel
     * @return The old LogLevel
     */
    public static LogLevel setLogLevel(LogLevel logLevel) {
        var oldLogLevel = Logger.logLevel;
        Logger.logLevel = logLevel;
        return oldLogLevel;
    }

    /**
     * Returns the current LogLevel, default is WARNING
     *
     * @return The current LogLevel
     */
    public static LogLevel getLogLevel() {
        return Logger.logLevel;
    }

    /**
     * Logs a message as a debug log-message. Debug means it is only intended to
     * be used in development usecases.
     *
     * @param throwable The Throwable to be written to the log
     */
    public static void debug(Throwable throwable) {
        debug(null, throwable);
    }

    /**
     * Logs a message as a debug log-message. Debug means it is only intended to
     * be used in development usecases.
     *
     * @param message The message to be written to the log
     * @param throwable The Throwable to be written to the log
     */
    public static void debug(String message, Throwable throwable) {
        log(LogLevel.DEBUG, message, throwable);
    }
    
    /**
     * Logs a message as a info log-message. Info means it informs the user
     * about something.
     *
     * @param throwable The Throwable to be written to the log
     */
    public static void info(Throwable throwable) {
        info(null, throwable);
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
     * @param message The message to be written to the log
     * @param throwable The Throwable to be written to the log
     */
    public static void info(String message, Throwable throwable) {
        log(LogLevel.INFO, message, throwable);
    }
    
    /**
     * Logs a message as a warning log-message. Warning means that something
     * <b>should</b> be changed, but it isn't a error (yet)
     *
     * @param throwable The Throwable to be written to the log
     */
    public static void warning(Throwable throwable) {
        warning(null, throwable);
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
     * @param message The message to be written to the log
     * @param throwable The Throwable to be written to the log
     */
    public static void warning(String message, Throwable throwable) {
        log(LogLevel.WARNING, message, throwable);
    }
    
    /**
     * Logs a message as a error log-message. Error means that something failed,
     * but it isn't system critical.
     *
     * @param throwable The Throwable to be written to the log
     */
    public static void error(Throwable throwable) {
        error(null, throwable);
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
     * @param message The message to be written to the log
     * @param throwable The Throwable to be written to the log
     */
    public static void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }
    
    /**
     * Logs a message as a fatalerror log-message. Fatalerror means that
     * something system-critical failed.
     *
     * @param throwable The Throwable to be written to the log
     */
    public static void fatalError(Throwable throwable) {
        fatalError(null, throwable);
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
     * @param message The message to be written to the log
     * @param throwable The Throwable to be written to the log
     */
    public static void fatalError(String message, Throwable throwable) {
        log(LogLevel.FATALERROR, message, throwable);
    }

    /*
     * Logs the message, message and/or throwable can be null
     */
    private static void log(LogLevel level, String message, Throwable throwable) {
        if (level.level > logLevel.level || logLevel.equals(LogLevel.OFF)) {
            return;
        }
        message = message == null ? "" : message;
        if (throwable != null) {
            message += message.isEmpty() ? "" : "\n";
            if (LogLevel.DEBUG.equals(logLevel)) {
                StringWriter writer = new StringWriter();
                throwable.printStackTrace(new PrintWriter(writer));
                message += writer.toString();
            } else {
                message += throwable.toString();
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
        logMessageConsumer.accept(LogMessage.of(level, message));
    }
    
    /**
     * The weight of the LogLevel, 0 means the worst and 5 the best
     */
    public enum LogLevel {
        OFF(0), FATALERROR(1), ERROR(2), WARNING(3), INFO(4), DEBUG(5);
        public final int level;
        
        LogLevel(int level) {
            this.level = level;
        }
    }

    /**
     * Sets the output of the logger
     *
     * @param logMessageConsumer
     */
    public static void setLogMessageConsumer(Consumer<LogMessage> logMessageConsumer) {
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

    //Fast Method to iconToNodeProperty a amount of blanks as a String
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
                    return cleanClassName(elem.getClassName()) + " #" + elem.getMethodName() + ":" + elem.getLineNumber();
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
    public static final class LogMessage {

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
