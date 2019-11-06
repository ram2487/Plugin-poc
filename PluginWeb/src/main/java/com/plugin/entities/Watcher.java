package com.plugin.entities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Watcher {

	public String watchPath;
	public WatcherStatus watcherStatus;
	public Path watchDirectory;
	public List<String> filesInDirectory;

	public Watcher(String fileWatcherPath, String watcherStatus) {

		this.watchPath = fileWatcherPath;
		this.watcherStatus = WatcherStatus.fromString(watcherStatus);
		this.watchDirectory = Paths.get(this.watchPath);
	}

	public List<String> getAllFilePathInDirc() {
		try (Stream<Path> walk = Files.walk(watchDirectory)) {

			filesInDirectory = walk.map(x -> x.toString()).filter(f -> f.endsWith(".jar")).collect(Collectors.toList());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return filesInDirectory;
	}

	public String getFileWatcherPath() {
		return watchPath;
	}

	public void setFileWatcherPath(String fileWatcherPath) {
		this.watchPath = fileWatcherPath;
	}

	public String getStatus() {
		return watcherStatus.toString();
	}

	public void setStatus(String status) {
		watcherStatus = WatcherStatus.fromString(status);
	}

	public Path getDestFolder() {
		return watchDirectory;
	}

	public void setDestFolder(Path destFolder) {
		this.watchDirectory = destFolder;
	}
}
