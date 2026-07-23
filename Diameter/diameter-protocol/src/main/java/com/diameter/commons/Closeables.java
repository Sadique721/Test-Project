package com.diameter.commons;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;


/**
 * A set of utilities for all {@link Closeable}s
 * 
 */
public class Closeables {

	/**
	 * Closes the {@code closeable} passed ignoring any I/O exception by logging.
	 * Does nothing if {@code closeable} is {@code null}.
	 * 
	 * @param closeable closeable to be closed, does nothing if {@code null}
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		
		try {
			closeable.close();
		} catch (IOException e) {
			LogManager.getLogger().trace(e);
		}
	}
	
	/**
	 * Closes all the {@code closeables} passed ignoring any I/O exceptions by logging.
	 * Skips any {@code null}s that occur.
	 * 
	 * @param closeables all the closeables to be closed, does nothing if {@code null} is passed
	 */
	public static void closeQuietly(Closeable... closeables){
		if (closeables == null) {
			return;
		}
		
		for (Closeable closeable : closeables) {
			closeQuietly(closeable);
		}
		
	}

	/**
	 * Closes the {@code serverSocket} passed, ignoring any I/O exception by logging.
	 * Does nothing if {@code serverSocket} is {@code null}.
	 * 
	 * @param serverSocket a nullable server socket to be closed, does nothing if null
	 */
	public static void closeQuietly(ServerSocket serverSocket) {
		if (serverSocket == null) {
			return;
		}
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			LogManager.getLogger().trace(e);
		}
	}
}
