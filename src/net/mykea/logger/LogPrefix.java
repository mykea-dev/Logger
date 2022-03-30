package net.mykea.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

enum LogPrefix {
	INFO("INFO]: ", ""),
	DEBUG("DEBUG]: ", "\u001B[34m"),
	WARN("WARN]: ", "\u001B[33m"),
	ERROR("ERROR]: ", "\u001B[31m");

	private final String prefix;
	private final String color;

	LogPrefix(final String prefix, final String color) {
		this.prefix = prefix;
		this.color = color;
	}

	private String getCurrentTime() {
		final DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
		final LocalDateTime now = LocalDateTime.now();
		return format.format(now);
	}

	String getPrefix(String caller, final Thread thread) {
		caller = caller.split("\\.")[caller.split("\\.").length - 1];
		return Logger.format("{0}[{1}] [Thread: {2}] [{3}/{4}", color, getCurrentTime(), thread.getName(), caller, prefix);
	}
}
