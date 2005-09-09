/*
*Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
*
*This program is free software; you can redistribute it and/or modify
*it under the terms of the GNU General Public License as published by
*the Free Software Foundation; either version 2 of the License, or
*(at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this program; if not, write to the Free Software
*Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
*USA
*
*http://www.gnu.org/licenses/gpl.txt
*/
package org.lamsfoundation.lams.tool.sbmt.dao;

import java.util.List;

import org.lamsfoundation.lams.tool.sbmt.Learner;
import org.lamsfoundation.lams.tool.sbmt.SbmtBaseTestCase;
import org.lamsfoundation.lams.tool.sbmt.SubmissionDetails;

public class TestSubmissionDetailsDAO extends SbmtBaseTestCase {

	protected ISubmissionDetailsDAO submissionDetailsDAO;

	/*
	 * @see TestCase#setUp()
	 */
	public void setUp() throws Exception {
		super.setUp();
		submissionDetailsDAO = (ISubmissionDetailsDAO)context.getBean("submissionDetailsDAO");
		assertNotNull("submitFilesContentDAO",submissionDetailsDAO);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for TestSubmissionDetailsDAO.
	 * @param arg0
	 */
	public TestSubmissionDetailsDAO(String arg0) {
		super(arg0);
	}

	public void testGetSubmissionDetailsByID() {
		SubmissionDetails details = submissionDetailsDAO.getSubmissionDetailsByID(TEST_SUBMISSION_ID);
		assertNotNull("details", details);
		assertEquals(details.getFilePath(),TEST_FILE_NAME);
	}

	public void testGetSubmissionDetailsByContentID() {
		List list = submissionDetailsDAO.getSubmissionDetailsByContentID(TEST_CONTENT_ID);
		assertEquals("Expect only 1 submission", list.size(), 1);
		SubmissionDetails details = (SubmissionDetails) list.iterator().next();
		assertNotNull("details", details);
		assertEquals(details.getSubmissionID(),TEST_SUBMISSION_ID);
		assertEquals(details.getFilePath(),TEST_FILE_NAME);
	}

	public void testGetSubmissionDetailsBySession() {
		List list = submissionDetailsDAO.getSubmissionDetailsBySession(TEST_SESSION_ID);
		assertEquals("Expect only 1 submission", list.size(), 1);
		SubmissionDetails details = (SubmissionDetails) list.iterator().next();
		assertNotNull("details", details);
		assertEquals(details.getSubmissionID(),TEST_SUBMISSION_ID);
		assertEquals(details.getFilePath(),TEST_FILE_NAME);
	}

	public void testGetUsersForSession() {
		List list = submissionDetailsDAO.getUsersForSession(TEST_SESSION_ID);
		assertEquals("Expect only 1 learner", list.size(), 1);
		Long userId = (Long) list.iterator().next();
		assertNotNull("userId", userId);
	}

}
