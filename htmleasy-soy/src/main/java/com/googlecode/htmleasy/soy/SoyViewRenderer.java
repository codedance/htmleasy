/*
* (c) Copyright 1999-2011 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.googlecode.htmleasy.soy;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

public class SoyViewRenderer {
	private final ConfigurationManager configuration;
	private Map<String, SoyTofu> cachedFileSets = new HashMap<String, SoyTofu>();
	
	public SoyViewRenderer() {
		this(ConfigurationManager.getConfigurationManager());
	}
	
	public SoyViewRenderer(ConfigurationManager configuration) {
		this.configuration = configuration;
	}
	
	public String render(String templateName, Object model) {
		try {
			Map<String, Object> data = createViewModel(model);
			String namespace = getSoyNamespace(templateName);
			
			// Check for cached version
			if (cachedFileSets.containsKey(namespace) && configuration.isCacheCompiledFileSets()) {
				return cachedFileSets.get(namespace).render(templateName, data, null);
			}
			
			// No cache, build new file set
			SoyFileSet.Builder sfsBuilder = new SoyFileSet.Builder();
			sfsBuilder = addSoyDirectory(sfsBuilder, configuration.getTemplateSharedPath());
			
			if (!namespace.equalsIgnoreCase(configuration.getTemplateSharedPath())) {
				sfsBuilder = addSoyDirectory(sfsBuilder, namespace);
			}
			
			// Compile and store file set
			SoyTofu compiledFileSet = sfsBuilder.build().compileToJavaObj();
			
			if (configuration.isCacheCompiledFileSets()) {
				cachedFileSets.put(namespace, compiledFileSet);
			}
			
			return compiledFileSet.render(templateName, data, null);
		} catch (Exception e) {
			return "Error renderering template: " + e.getMessage();
		}
	}
	
	private Map<String, Object> createViewModel(Object model) throws Exception {
		Map<String, Object> viewModel = new HashMap<String, Object>();
		
		viewModel.put("model", mapProperties(model));
		
		return viewModel;
	}
	
	static String getSoyNamespace(String templateName) throws Exception {
		String[] dotSplit = templateName.split("\\.");
		
		if (dotSplit.length < 2) {
			throw new Exception("Invalid template name " + templateName);
		}
		
		return dotSplit[0];
	}
	
	private SoyFileSet.Builder addSoyDirectory(SoyFileSet.Builder sfsBuilder, String ... directories) throws Exception {
		for (String directory : directories) {
			File namespaceDirectory;
			try {
				namespaceDirectory = new File(String.format("%s%s", configuration.getTemplateFilePath(), directory));
				
				Collection<File> namespaceFiles = FileUtils.listFiles(namespaceDirectory, new String[]{"soy"}, true);
				for (File f : namespaceFiles) {
					sfsBuilder.add(f);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Could not find specified directory: " + directory);
			}
		}
		
		return sfsBuilder;
	}
	
	private static Map<String, Object> mapProperties(Object pojo) throws Exception {
	    Map<String, Object> properties = new HashMap<String, Object>();
	    for (Method method : pojo.getClass().getDeclaredMethods()) {
	        if (Modifier.isPublic(method.getModifiers())
	            && method.getParameterTypes().length == 0
	            && method.getReturnType() != void.class
	            && method.getName().matches("^(get|is).+")
	        ) {
	            String name = method.getName().replaceAll("^(get|is)", "");
	            name = Character.toLowerCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "");
	            Object value = method.invoke(pojo);
	            properties.put(name, value);
	        }
	    }
	    return properties;
	}
}
