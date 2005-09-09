/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
package org.lamsfoundation.lams.tool.sbmt.web;

import java.io.File;

/**
 * This test is invalid. You can't assign a string to the 
 * filePath parameter - its of type org.apache.struts.upload.FormFile!
 * FM Sept 05
 * 
 * @author Manpreet Minhas
 */
public class TestLearnerAction extends SbmtBaseTestAction {
	
	public TestLearnerAction(String name){
		super(name);
	}
	
	public void testUploadFile(){		
		setConfigFile("/WEB-INF/struts/struts-config.xml");
		setRequestPathInfo("/learner");
		addRequestParameter("method","uploadFile");
		
		addRequestParameter("contentID","1");		
		String filePath = "c:" + File.separator + "mminhas.txt";
		addRequestParameter("filePath",filePath);		
		addRequestParameter("fileDescription","Mock file description ");
		addRequestParameter("userID","1");
		
		actionPerform();
		verifyForward("upload");
		verifyNoActionErrors();
	}

}
