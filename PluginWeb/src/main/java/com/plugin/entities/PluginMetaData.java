package com.plugin.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PluginMetaData {

	private String pluginBaseFolder;

	private List<Map<String, Object>> pluginMetadaList;

	@SuppressWarnings("unchecked")
	public PluginMetaData(Map<String, Object> pluginMetadataMap) {
		pluginBaseFolder = (String) pluginMetadataMap.get("pluginBaseFlderPath");
		pluginMetadaList = (List<Map<String, Object>>) pluginMetadataMap.get("pluginMetadaList");

	}

	@SuppressWarnings({ "unchecked" })
	public Map<String, Object> getPluginMetadata(String pluginName) {

		Map<String, Object> metadata = new HashMap<String, Object>();

		metadata = (Map<String, Object>) getSelected(pluginName, "pluginFileName");
		return metadata;

	}

	@SuppressWarnings({ "unchecked" })
	public List<Map<String, Object>> getMethodMetaData(String pluginName) {

		List<Map<String, Object>> pluginMethodMetaMap = new ArrayList<Map<String, Object>>();

		pluginMethodMetaMap = (List<Map<String, Object>>) getSelected(pluginName, "pluginMethodMetaData");

		return pluginMethodMetaMap;

	}

	private Object getSelected(String pluginFileName, String key) {
		Object metaData = null;

		for (Map<String, Object> pluginData : pluginMetadaList) {

			String pluginFile = (String) pluginData.get(key);

			if (pluginFile.equalsIgnoreCase(pluginFileName)) {
				metaData = pluginData;
				System.out.println();
			}
		}
		return metaData;
	}

	public String getPluginBaseFolder() {
		return pluginBaseFolder;
	}

	public void setPluginBaseFolder(String pluginBaseFolder) {
		this.pluginBaseFolder = pluginBaseFolder;
	}

	public List<Map<String, Object>> getPluginMetadaList() {
		return pluginMetadaList;
	}

	public void setPluginMetadaList(List<Map<String, Object>> pluginMetadaList) {
		this.pluginMetadaList = pluginMetadaList;
	}

}
