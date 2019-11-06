package com.plugin.entities;

public enum WatcherStatus {

	RUN("RUNNING"), STOP("STOPPED"), INIT("INITIATED");

	private String status;

	WatcherStatus(String status) {
		this.status = status;
	}

	public String url() {
		return status;
	}

	@Override
	public String toString() {
		return status;
	}

	public static WatcherStatus fromString(String status) {
		for (WatcherStatus b : WatcherStatus.values()) {
			if (b.toString().equalsIgnoreCase(status)) {
				return b;
			}
		}
		return null;
	}
}
