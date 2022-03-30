package net.mykea.logger;

import java.io.File;

public class Logger {
	private boolean debugEnabled = false;
	private final String caller;
	private final Thread callerThread;

	private Logger(final String caller, final Thread callerThread) {
		this.caller = caller;
		this.callerThread = callerThread;
	}

	public static void init(final File data) {
		FileLogger.init(data);
	}

	public static void close() {
		FileLogger.close();
	}

	private void writePrimitive(final LogPrefix prefix, String text, final boolean logToFile) {
		System.out.println(prefix.getPrefix(caller, callerThread) + text);
		if (logToFile) FileLogger.writePrimitive(prefix.getPrefix(caller, callerThread), text);

	}

	static String format(String message, final Object... args) {
		for (int i = 0; i < args.length; i++) message = message.replace("{" + i + "}", args[i].toString());
		return message;
	}

	public void enableDebug() {
		debugEnabled = true;
	}

	public void disableDebug() {
		debugEnabled = false;
	}

	public static Logger getLogger() {
		return new Logger(Thread.currentThread().getStackTrace()[2].getClassName(), Thread.currentThread());
	}


	public void writePrimitive(final LogPrefix prefix, final String write) {
		writePrimitive(prefix, write, true);
	}

	public void debug(final String write) {
		if (debugEnabled) writePrimitive(LogPrefix.DEBUG, write, true);
	}

	public void info(final String write) {
		writePrimitive(LogPrefix.INFO, write, true);
	}

	public void error(final String write) {
		writePrimitive(LogPrefix.ERROR, write, false);
		FileLogger.write(FileLogger.errors, LogPrefix.ERROR.getPrefix(caller, callerThread), write);
	}

	public void warn(final String write) {
		writePrimitive(LogPrefix.WARN, write, false);
		FileLogger.write(FileLogger.warns, LogPrefix.WARN.getPrefix(caller, callerThread), write);
	}

	public void debug(final String write, final Object... objects) {
		writePrimitive(LogPrefix.DEBUG, format(write, objects), true);
	}

	public void info(final String write, final Object... objects) {
		writePrimitive(LogPrefix.INFO, format(write, objects), true);
	}

	public void error(final String write, final Object... objects) {
		writePrimitive(LogPrefix.ERROR, format(write, objects), false);
		FileLogger.write(FileLogger.errors, LogPrefix.ERROR.getPrefix(caller, callerThread), format(write, objects));
	}

	public void warn(final String write, final Object... objects) {
		writePrimitive(LogPrefix.WARN, format(write, objects), false);
		FileLogger.write(FileLogger.warns, LogPrefix.WARN.getPrefix(caller, callerThread), format(write, objects));
	}
}
