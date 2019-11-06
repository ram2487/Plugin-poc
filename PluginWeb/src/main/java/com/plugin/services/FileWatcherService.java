package com.plugin.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.plugin.entities.PluginMetaData;
import com.plugin.entities.Watcher;

@Service("fileWatcherService")
public class FileWatcherService {

	private Watcher watcher;

	private List<String> filesPaths;

	@Autowired
	private LoaderServices loader;

	private PluginMetaData pluginMetaData;

	public void init(Map<String, Object> pluginMetadata) {

		pluginMetaData = new PluginMetaData(pluginMetadata);
		this.watcher = new Watcher(pluginMetaData.getPluginBaseFolder(), "INITIATED");
		initLoad();
		runWatcher();
	}

	private void initLoad() {

		for (Map<String, Object> plugin : pluginMetaData.getPluginMetadaList()) {
			try {
				loader.initPluginMetadata(plugin, false);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void runWatcher() {
		try {
			Path destFolder = watcher.getDestFolder();
			WatchService watchService = FileSystems.getDefault().newWatchService();
			destFolder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY);

			boolean valid = true;

			setFilesPaths(watcher.getAllFilePathInDirc());

			while (true) {
				WatchKey watchKey = watchService.take();

				for (WatchEvent<?> event : watchKey.pollEvents()) {
					String fileName = event.context().toString();
					if (fileName.contains(".jar")) {
						boolean isModified = false;

						if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {

							System.out.println("FILE CREATED: " + fileName);

							setFilesPaths(watcher.getAllFilePathInDirc());

						}
						if (StandardWatchEventKinds.ENTRY_MODIFY.equals(event.kind())) {

							System.out.println("FILE MODIFIED" + fileName);

							isModified = true;

						}

						// fileName = String.format("%s/%s", watcher.getFileWatcherPath(), fileName);

						loader.initPluginMetadata(pluginMetaData.getPluginMetadata(fileName), isModified);
					}
				}
				valid = watchKey.reset();
			}

		} catch (IOException | InterruptedException e) {
			System.out.println("Watcher IO problem in loading");

		}
	}

	public Watcher getWatcher() {
		return watcher;
	}

	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}

	public List<String> getFilesPaths() {
		return filesPaths;
	}

	public void setFilesPaths(List<String> filesPaths) {
		this.filesPaths = filesPaths;
	}

	public String getStatus() {
		// TODO Auto-generated method stub
		return watcher.getStatus();
	}

	public void loadFilesFromDirectory() {

	}
}
