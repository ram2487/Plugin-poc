package com.plugin.services;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class LoaderServices {

	@Autowired
	private final ApplicationContext context = new AnnotationConfigApplicationContext();

	private String pluginServiceName;

	private String pluginClassName;

	private File pluginFile;

	private URL pluginUrl;

	private boolean pluginFileExists;

	private boolean isModified;

	private URLClassLoader classLoader;

	private List<Map<String, Object>> pluginMethodMetadaList;

	private Map<String, Object> pluginMetadaMap;

	@SuppressWarnings("unchecked")
	public void initPluginMetadata(Map<String, Object> pluginMetada, boolean isModifiedPlugin)
			throws MalformedURLException {

		pluginMetadaMap = pluginMetada;

		pluginServiceName = (String) pluginMetadaMap.get("pluginServiceName");
		pluginClassName = (String) pluginMetadaMap.get("pluginClassName");

		pluginMethodMetadaList = (List<Map<String, Object>>) pluginMetadaMap.get("pluginMethodMetaData");

		isModified = isModifiedPlugin;

		pluginFile = new File((String) pluginMetadaMap.get("pluginFilePath"));

		loadOrExecute();

	}

	private void loadOrExecute() throws MalformedURLException {
		pluginFileExists = pluginFile.exists() ? true : false;

		if (pluginFileExists) {
			pluginUrl = pluginFile.toURI().toURL();
			if (isModified) {

				loadModifiedJar(pluginFile);
			} else {

				executeJar();
			}
		}
	}

	private void loadModifiedJar(File pluginFile) {

		try {
			isChanged(pluginFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeJar() {
		try {
			classLoader = new URLClassLoader(new URL[] { pluginUrl }, this.getClass().getClassLoader());

			Class<?> serviceClass = Class.forName(pluginClassName, true, classLoader);

			registerClassToContext(serviceClass);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Need to implement version so that each method in class will have own method
	 * version which is needed to be implemented by using an interface.So that by
	 * getting the version number u can check whether the method has been overridden
	 * or not. If not try something like showing a message or unregister the bean
	 * and re initiate it.
	 */
	private void registerClassToContext(Class<?> clazz)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

		if (!isModified) {
			((GenericApplicationContext) context).registerBean(pluginServiceName, clazz);

			// ((AbstractApplicationContext) context).refresh();
		}

		executeClass(pluginServiceName, clazz);
	}

	private void executeClass(String serviceName, Class<?> clazz)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		if (isModified) {
			beanRegisterReload(clazz);

		}

		for (Map<String, Object> methodMetaData : pluginMethodMetadaList) {
			String methodName = (String) methodMetaData.get("methodName");
			Map<String, Object> paramResult = getMethodParams(methodMetaData);

			executeMethod(serviceName, methodName, paramResult);
		}

	}

	private Map<String, Object> getMethodParams(Map<String, Object> methodMetaData) {

		List<Object> paramObjectList = (List<Object>) methodMetaData.get("paramList");
		Object[] paramList = paramObjectList.toArray();

		List<Class<?>> paramClassTypeList = new ArrayList<Class<?>>();

		for (Object param : paramList) {
			if (param instanceof Map) {
				paramClassTypeList.add(Map.class);
			} else if (param instanceof List) {
				paramClassTypeList.add(List.class);

			} else if (param instanceof Double) {
				paramClassTypeList.add(Double.TYPE);

			} else if (param instanceof Integer) {
				paramClassTypeList.add(Integer.TYPE);

			} else {
				paramClassTypeList.add(String.class);

			}

		}

		Class<?>[] paramClassType = new Class[paramList.length];
		paramClassType = paramClassTypeList.toArray(paramClassType);

		Map<String, Object> paramResult = new HashMap<String, Object>();
		paramResult.put("classes", paramClassType);
		paramResult.put("params", paramList);

		return paramResult;

	}

	private void executeMethod(String serviceName, String MethodName, Map<String, Object> paramAndClasses)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Object pluginServiceInstance = this.context.getBean(serviceName);
		try {

			Method pluginServiceMethod = pluginServiceInstance.getClass().getDeclaredMethod(MethodName,
					(Class<?>[]) paramAndClasses.get("classes"));
			pluginServiceMethod.invoke(pluginServiceInstance, (Object[]) paramAndClasses.get("params"));

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println();
		}
	}

	private void beanRegisterReload(Class<?> clazz) {

		try {
			ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) this.context;
			SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
			((BeanDefinitionRegistry) beanRegistry).removeBeanDefinition(pluginServiceName);
			((GenericApplicationContext) this.context).registerBean(pluginServiceName, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void isChanged(File jarFile) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {

		invokeAddUrlClassLoader(jarFile);

		refreshClassPath(jarFile);

		try {
			executeJar();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void refreshClassPath(File jarFile) throws IOException {
		String classPath = System.getProperty("java.class.path");
		if (!classPath.contains(jarFile.getCanonicalPath())) {
			classPath += File.pathSeparatorChar + jarFile.getCanonicalPath();
			System.out.println();
		}
		System.getProperties().setProperty("java.class.path", classPath);
	}

	private void invokeAddUrlClassLoader(File jarFile)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, MalformedURLException {
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(classLoader, jarFile.toURI().toURL());
	}

	public ApplicationContext getContext() {
		return context;
	}

}
