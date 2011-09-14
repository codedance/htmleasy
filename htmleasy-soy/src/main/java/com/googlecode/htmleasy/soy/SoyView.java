/*
* (c) Copyright 1999-2011 PaperCut Software Int. Pty. Ltd.
* $Id$
*/
package com.googlecode.htmleasy.soy;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

import com.google.common.base.Preconditions;
import com.googlecode.htmleasy.Viewable;

public class SoyView implements Viewable {
	private static SoyViewRenderer globalRenderer = new SoyViewRenderer();
	
	private final SoyViewRenderer renderer;
	private final String templateName;
	private final Object model;
	
	public SoyView(String templateName, Object model) {
		this(globalRenderer, templateName, model);
	}
	
	public SoyView(SoyViewRenderer renderer, String templateName, Object model) {
		Preconditions.checkNotNull(renderer);
		Preconditions.checkNotNull(templateName);
		
		this.renderer = renderer;
		this.templateName = templateName;
		this.model = model;
	}
	
	public void render(HttpServletRequest arg0, HttpServletResponse arg1)
			throws IOException, ServletException, WebApplicationException {
		arg1.setContentType("text/html");
		arg1.setStatus(HttpServletResponse.SC_OK);
		
		PrintWriter writer = arg1.getWriter();
		writer.write(renderer.render(templateName, model));
	}

}
