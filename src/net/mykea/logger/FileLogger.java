package net.mykea.logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class FileLogger {
	private static File logsFolder;
	private static boolean init = false;
	protected static File logsFile;
	protected static File warnsFile;
	protected static File errorsFile;
	protected static BufferedWriter logs;
	protected static BufferedWriter warns;
	protected static BufferedWriter errors;

	static void init(final File dataFolder) {
		try {
			dataFolder.mkdirs();
			logsFolder = new File(dataFolder + File.separator + "logs");
			logsFolder.mkdirs();
			logsFile = new File(logsFolder, "logs.txt");
			warnsFile = new File(logsFolder, "warns.txt");
			errorsFile = new File(logsFolder, "errors.txt");
			final Thread current = Thread.currentThread();
			final String info = LogPrefix.INFO.getPrefix(current.getStackTrace()[2].getClassName(), current);
			if (logsFile.createNewFile()) System.out.println(info + "Log file has been created");
			if (warnsFile.createNewFile()) System.out.println(info + "Warns file has been created");
			if (errorsFile.createNewFile()) System.out.println(info + "Errors file has been created");
			logs = new BufferedWriter(new FileWriter(new File(logsFolder, "logs.txt"), true));
			warns = new BufferedWriter(new FileWriter(new File(logsFolder, "warns.txt"), true));
			errors = new BufferedWriter(new FileWriter(new File(logsFolder, "errors.txt"), true));
			init = true;
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	static void writePrimitive(String prefix, final String write) {
		if (!init) return;
		try {
			if (prefix == null)
				prefix = LogPrefix.INFO.getPrefix(Thread.currentThread().getStackTrace()[2].getClassName(), Thread.currentThread());
			logs.append(prefix).append(write).append("\n");
		} catch (final IOException e) {
			System.err.println("[LOGGER-LOG] We had some problems to write line in file. File: " + logsFolder.getName());
			e.printStackTrace();
		}
	}

	static void write(final BufferedWriter writer, String prefix, final String write) {
		if (!init) return;
		if (prefix == null) {
			prefix = LogPrefix.INFO.getPrefix(Thread.currentThread().getStackTrace()[2].getClassName(), Thread.currentThread());
		}
		try {
			writer.append(prefix).append(write).append("\n");
		} catch (final IOException e) {
			System.err.println("[LOGGER-LOG] We had some problems to write line in file");
			e.printStackTrace();
		}
	}

	static void saveToArchive() throws IOException {
		logs.flush();
		warns.flush();
		errors.flush();

		final StringBuilder logsBuilder = new StringBuilder();
		final BufferedReader logsReader = new BufferedReader(new FileReader(logsFile));
		for (String line; (line = logsReader.readLine()) != null; ) logsBuilder.append(line).append("\n");

		final StringBuilder warnsBuilder = new StringBuilder();
		final BufferedReader warnsReader = new BufferedReader(new FileReader(warnsFile));
		for (String line; (line = warnsReader.readLine()) != null; ) warnsBuilder.append(line).append("\n");

		final StringBuilder errorsBuilder = new StringBuilder();
		final BufferedReader errorsReader = new BufferedReader(new FileReader(errorsFile));
		for (String line; (line = errorsReader.readLine()) != null; ) errorsBuilder.append(line).append("\n");

		//zip file initialization
		final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd_hh.mm");
		final LocalDateTime now = LocalDateTime.now();
		final File file = new File(logsFolder + File.separator + format.format(now) + ".zip");
		final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));

		final ZipEntry logsEntry = new ZipEntry("logs.txt");
		out.putNextEntry(logsEntry);
		final byte[] data = logsBuilder.toString().getBytes();
		out.write(data, 0, data.length);
		out.closeEntry();

		final ZipEntry warnsEntry = new ZipEntry("warns.txt");
		out.putNextEntry(warnsEntry);
		final byte[] warnsData = warnsBuilder.toString().getBytes();
		out.write(warnsData, 0, warnsData.length);
		out.closeEntry();

		final ZipEntry errorsEntry = new ZipEntry("errors.txt");
		out.putNextEntry(errorsEntry);
		final byte[] errorsData = errorsBuilder.toString().getBytes();
		out.write(errorsData, 0, errorsData.length);
		out.closeEntry();

		out.close();
	}

	static void close() {
		if (!init) return;
		Logger.getLogger().info("Closing logger...");
		try {
			saveToArchive();
			logs.write("");
			logs.close();
			warns.write("");
			warns.close();
			errors.write("");
			errors.close();
			init = false;
			System.out.println("[LOGGER-LOG] Logger closed");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}