package com.plugin.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.plugin.services.FileWatcherService;

@RestController
@RequestMapping("plugin")
public class PluginAPI {

	@Autowired
	private FileWatcherService watcherService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/watcher/start", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	
	public String initWatcher(HttpServletRequest request) throws Exception {

		File pluginMetadatafile = ResourceUtils.getFile("classpath:filesMetadata.json");

		BufferedReader bufferedReader = new BufferedReader(new FileReader(pluginMetadatafile));

		Map<String, Object> pluginMetadataMap = new Gson().fromJson(bufferedReader, Map.class);
		
		watcherService.init(pluginMetadataMap);

		watcherService.getWatcher().setStatus("RUNNING");

		return "Ids Watcher Started";
	}

	@RequestMapping(value = "/watcher/reload/plugin/{pluginName}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	
	public String getWatcherStatus(@PathVariable String pluginName) {

		return watcherService.getWatcher().getStatus();
	}

	@RequestMapping(value = "/watcher/refresh", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public String refreshWatcher() {
		return "Refreshed Ids Watcher ";
	}

}
