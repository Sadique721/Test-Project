package com.diameter.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.diameter.config.AuditProperties;

public class CsvFileRotator {
	private final AuditProperties properties;
	private BufferedWriter writer;
	private Path currentFile;
	private int currentHour = -1;
	private final DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	public CsvFileRotator(AuditProperties properties) {
		this.properties = properties;
	}

	public BufferedWriter getWriter() throws IOException {
		rotateIfRequired();
		return writer;
	}

	private void rotateIfRequired() throws IOException {
		LocalDateTime now = LocalDateTime.now();
		boolean rotate = false;
		
		if (writer == null) {
			rotate = true;
		} else if (currentHour != now.getHour()) {
			rotate = true;
		} else if (Files.size(currentFile) >= properties.getRotateSizeMb() * 1024L * 1024L) {
			rotate = true;
		}
		if (!rotate) {
			return;
		}
		if (writer != null) {
			writer.flush();
			writer.close();
		}
		Files.createDirectories(Paths.get(properties.getDirectory()));
		String fileName = "audit_" + now.format(formatter) + ".csv";
		currentFile = Paths.get(properties.getDirectory(), fileName);
		boolean newFile = Files.notExists(currentFile);
		writer = Files.newBufferedWriter(currentFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
				StandardOpenOption.APPEND);
		writer = new BufferedWriter(writer, properties.getBufferSize());
		if (newFile) {
			writer.write(CsvFormatter.HEADER);
			writer.newLine();
			writer.flush();
		}
		currentHour = now.getHour();
	}

	public void close() throws IOException {
		if (writer != null) {
			writer.flush();
			writer.close();
		}
	}
}
