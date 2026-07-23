package com.diameter.model;

public class AuditRoute {
	private boolean db;
	private boolean csv;

	public boolean isDb() {
		return db;
	}

	public void setDb(boolean db) {
		this.db = db;
	}

	public boolean isCsv() {
		return csv;
	}

	public void setCsv(boolean csv) {
		this.csv = csv;
	}
}
