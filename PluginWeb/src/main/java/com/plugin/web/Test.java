//package com.plugin.web;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.util.Map;
//
//import org.springframework.util.ResourceUtils;
//
//import com.google.gson.Gson;
//
//public class Test {
//
//	public static void main(String[] args) throws FileNotFoundException {
//		// TODO Auto-generated method stub
//
//		File file = ResourceUtils.getFile("classpath:filesMetadata.json");
//
//		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
//
//		Map<String, Object> pluginMetadataMap = new Gson().fromJson(bufferedReader, Map.class);
//		System.out.println(pluginMetadataMap.toString());
//	}
//
//}
