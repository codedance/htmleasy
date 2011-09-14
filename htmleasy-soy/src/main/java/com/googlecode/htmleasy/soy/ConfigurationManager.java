/*
* (c) Copyright 1999-2011 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.googlecode.htmleasy.soy;


public class ConfigurationManager {
	private static ConfigurationManager configurationManager;
	public synchronized static ConfigurationManager getConfigurationManager() {
		if (configurationManager == null)
			configurationManager = new ConfigurationManager();
		
		return configurationManager;
	}
	
	private String templateFilePath  = ConfigurationManager.class.getResource("/soy/").getPath();
	private String templateSharedPath = "shared";
	private boolean cacheCompiledFileSets = true;
	
	public String getTemplateFilePath() {
		return templateFilePath;
	}
	public void setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
	}
	public String getTemplateSharedPath() {
		return templateSharedPath;
	}
	public void setTemplateSharedPath(String templateSharedPath) {
		this.templateSharedPath = templateSharedPath;
	}
	public boolean isCacheCompiledFileSets() {
		return cacheCompiledFileSets;
	}
	public void setCacheCompiledFileSets(boolean cacheCompiledFileSets) {
		this.cacheCompiledFileSets = cacheCompiledFileSets;
	}
}
