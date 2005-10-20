/***************************************************************************
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
 * ***********************************************************************/

package org.lamsfoundation.lams.tool.mc.service;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.contentrepository.AccessDeniedException;
import org.lamsfoundation.lams.contentrepository.FileException;
import org.lamsfoundation.lams.contentrepository.ICredentials;
import org.lamsfoundation.lams.contentrepository.ITicket;
import org.lamsfoundation.lams.contentrepository.IVersionedNode;
import org.lamsfoundation.lams.contentrepository.ItemExistsException;
import org.lamsfoundation.lams.contentrepository.ItemNotFoundException;
import org.lamsfoundation.lams.contentrepository.LoginException;
import org.lamsfoundation.lams.contentrepository.NodeKey;
import org.lamsfoundation.lams.contentrepository.RepositoryCheckedException;
import org.lamsfoundation.lams.contentrepository.WorkspaceNotFoundException;
import org.lamsfoundation.lams.contentrepository.service.IRepositoryService;
import org.lamsfoundation.lams.contentrepository.service.RepositoryProxy;
import org.lamsfoundation.lams.contentrepository.service.SimpleCredentials;
import org.lamsfoundation.lams.lesson.Lesson;
import org.lamsfoundation.lams.tool.BasicToolVO;
import org.lamsfoundation.lams.tool.ToolContentManager;
import org.lamsfoundation.lams.tool.ToolSessionExportOutputData;
import org.lamsfoundation.lams.tool.ToolSessionManager;
import org.lamsfoundation.lams.tool.exception.DataMissingException;
import org.lamsfoundation.lams.tool.exception.SessionDataExistsException;
import org.lamsfoundation.lams.tool.exception.ToolException;
import org.lamsfoundation.lams.tool.mc.McAppConstants;
import org.lamsfoundation.lams.tool.mc.McApplicationException;
import org.lamsfoundation.lams.tool.mc.McContent;
import org.lamsfoundation.lams.tool.mc.McOptsContent;
import org.lamsfoundation.lams.tool.mc.McQueContent;
import org.lamsfoundation.lams.tool.mc.McQueUsr;
import org.lamsfoundation.lams.tool.mc.McSession;
import org.lamsfoundation.lams.tool.mc.McUsrAttempt;
import org.lamsfoundation.lams.tool.mc.dao.IMcContentDAO;
import org.lamsfoundation.lams.tool.mc.dao.IMcOptionsContentDAO;
import org.lamsfoundation.lams.tool.mc.dao.IMcQueContentDAO;
import org.lamsfoundation.lams.tool.mc.dao.IMcSessionDAO;
import org.lamsfoundation.lams.tool.mc.dao.IMcUserDAO;
import org.lamsfoundation.lams.tool.mc.dao.IMcUsrAttemptDAO;
import org.lamsfoundation.lams.tool.service.ILamsToolService;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.springframework.dao.DataAccessException;



/**
 * The POJO implementation of Mc service. All business logics of survey tool
 * are implemented in this class. It translate the request from presentation
 * layer and perform approporiate database operation.
 * 
 * Two construtors are provided in this class. The constuctor with Hibernate
 * session object allows survey tool to handle long run application transaction.
 * The developer can store Hibernate session in http session and pass across
 * different http request. This implementation also make the testing out side 
 * JBoss container much easier.
 * 
 * Every method is implemented as a Hibernate session transaction. It open an
 * new persistent session or connect to existing persistent session in the 
 * begining and it close or disconnect to the persistent session in the end.
 * 
 * @author ozgurd
 *  
 */

public class McServicePOJO implements
                              IMcService, ToolContentManager, ToolSessionManager, McAppConstants
               
{
	static Logger logger = Logger.getLogger(McServicePOJO.class.getName());
	
	/*repository access related constants */
	private final String repositoryUser 		= "lamc11";
	private final char[] repositoryId 			= {'l','a','m','c','_','1', '1'}; 
	private final String repositoryWorkspace 	= "lamc11";
	private IRepositoryService repositoryService;
	private ICredentials cred;
	
	private IMcContentDAO			mcContentDAO; 
	private IMcQueContentDAO		mcQueContentDAO; 
	private IMcOptionsContentDAO 	mcOptionsContentDAO;	
	private IMcSessionDAO			mcSessionDAO;
	private IMcUserDAO				mcUserDAO;
	private IMcUsrAttemptDAO		mcUsrAttemptDAO;
	
    private IUserManagementService userManagementService;
    private ILamsToolService toolService;
    
    public McServicePOJO(){}
    
    public void configureContentRepository() throws McApplicationException {
    	logger.debug("retrieved repService: " + repositoryService);
        cred = new SimpleCredentials(repositoryUser, repositoryId);
        logger.debug("retrieved cred: "+ cred);
          try 
		  {
          	repositoryService.createCredentials(cred);
          	logger.debug("created credentails.");
          	repositoryService.addWorkspace(cred,repositoryWorkspace);
          	logger.debug("created workspace.");
          } catch (ItemExistsException ie) {
              logger.warn("Tried to configure repository but it "
  	        		+" appears to be already configured. Exception thrown by repository being ignored. ", ie);
          } catch (RepositoryCheckedException e) {
              String error = "Error occured while trying to configure repository."
  				+" Unable to recover from error: "+e.getMessage();
  		    logger.error(error, e);
  			throw new McApplicationException(error,e);
          }
      }
    
    
    public void createMc(McContent mcContent) throws McApplicationException
    {
        try
        {
        	mcContentDAO.saveMcContent(mcContent);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is loading mc content: "
                                                         + e.getMessage(),
														   e);
        }
    }
	
    
    public McContent retrieveMc(Long toolContentId) throws McApplicationException
    {
        try
        {
            return mcContentDAO.findMcContentById(toolContentId);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is loading mc content: "
                                                         + e.getMessage(),
														   e);
        }
    }

    
    public void createMcQue(McQueContent mcQueContent) throws McApplicationException
    {
        try
        {
        	mcQueContentDAO.saveMcQueContent(mcQueContent);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is creating mc que content: "
                                                         + e.getMessage(),
														   e);
        }
    }
    
    
    public void saveOrUpdateMcQueContent(McQueContent mcQueContent) throws McApplicationException
	{
    	try
        {
        	mcQueContentDAO.saveOrUpdateMcQueContent(mcQueContent);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is updating mc que content: "
                                                         + e.getMessage(),
														   e);
        }
	}
    
    
    public void removeQuestionContentByMcUid(final Long mcContentUid) throws McApplicationException
	{
    	try
        {
        	mcQueContentDAO.removeQuestionContentByMcUid(mcContentUid);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing mc que content by mc content id: "
                                                         + e.getMessage(),
														   e);
        }
	}
    
    public void resetAllQuestions(final Long mcContentUid) throws McApplicationException
	{
    	try
        {
        	mcQueContentDAO.resetAllQuestions(mcContentUid);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is resetting all questions: "
                                                         + e.getMessage(),
														   e);
        }
	}
    
    
    
    public void cleanAllQuestions(final Long mcContentUid) throws McApplicationException
	{
    	try
        {
        	mcQueContentDAO.cleanAllQuestions(mcContentUid);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is cleaning all questions: "
                                                         + e.getMessage(),
														   e);
        }
	}
    
  
    public void createMcSession(McSession mcSession) throws McApplicationException
    {
        try
        {
        	mcSessionDAO.saveMcSession(mcSession);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is creating mc session: "
                                                         + e.getMessage(),
														   e);
        }
    }

    public void createMcQueUsr(McQueUsr mcQueUsr) throws McApplicationException
    {
	   try
        {
	   		mcUserDAO.saveMcUser(mcQueUsr);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is creating mc QueUsr: "
                                                         + e.getMessage(),
														   e);
        }
    }
    
    
    public McQueUsr retrieveMcQueUsr(Long userId) throws McApplicationException
    {
	   try
        {
	   		McQueUsr mcQueUsr=mcUserDAO.findMcUserById(userId);
	   		return mcQueUsr;
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is retrieving McQueUsr: "
                                                         + e.getMessage(),
														   e);
        }
    }
    
    
    public void createMcUsrAttempt(McUsrAttempt mcUsrAttempt) throws McApplicationException
    {
        try
        {
        	mcUsrAttemptDAO.saveMcUsrAttempt(mcUsrAttempt);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is creating mc UsrAttempt: "
                                                         + e.getMessage(),
														   e);
        }
    }
    
    
	public void updateMcUsrAttempt(McUsrAttempt mcUsrAttempt) throws McApplicationException
    {
        try
        {
        	mcUsrAttemptDAO.updateMcUsrAttempt(mcUsrAttempt);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is updating mc UsrAttempt: "
                                                         + e.getMessage(),
														   e);
        }
    }
	
    
    
    public McQueContent retrieveMcQueContentByUID(Long uid) throws McApplicationException
    {
        try
        {
            return mcQueContentDAO.getMcQueContentByUID(uid);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is retrieving by uid  mc question content: "
                                                         + e.getMessage(),
														   e);
        }
    }
    
    
    public void removeMcQueContent(McQueContent mcQueContent) throws McApplicationException
	{
    	try
        {
            mcQueContentDAO.removeMcQueContent(mcQueContent);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing mc question content: "
                                                         + e.getMessage(),
														   e);
        }
	}
    
    public void removeMcOptionsContent(McOptsContent mcOptsContent) throws McApplicationException
    {
    	try
        {
            mcOptionsContentDAO.removeMcOptionsContent(mcOptsContent);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing"
                                                 + " the mc options content: "
                                                 + e.getMessage(),e);
        }
    }

    
    public McQueContent getQuestionContentByQuestionText(final String question, final Long mcContentId)
    {
        try
        {
            return mcQueContentDAO.getQuestionContentByQuestionText(question, mcContentId);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is retrieving question content by question text: "
                                                         + e.getMessage(),
														   e);
        }
    }
    
    
    public McSession retrieveMcSession(Long mcSessionId) throws McApplicationException
    {
    	try
        {
            return mcSessionDAO.findMcSessionById(mcSessionId);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is retrieving by id mc session : "
                                                         + e.getMessage(),
														   e);
        }
    }
    
    
    
    public McContent retrieveMcBySessionId(Long mcSessionId) throws McApplicationException
    {
        try
        {
        	return mcContentDAO.getMcContentBySession(mcSessionId);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is retrieving mc by session id: "
            								+ e.getMessage(),
                                              e);
        }
    }
    
    
    public void updateMc(McContent mc) throws McApplicationException
    {
        try
        {
            mcContentDAO.updateMcContent(mc);
        }
        catch(DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is updating"
                                                 + " the mc content: "
                                                 + e.getMessage(),e);
        }
    }

    
    public void updateMcSession(McSession mcSession) throws McApplicationException
    {
    	try
        {
            mcSessionDAO.updateMcSession(mcSession);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is updating mc session : "
                                                         + e.getMessage(),
														   e);
        }
    }
    
    public void deleteMc(McContent mc) throws McApplicationException
    {
    	try
        {
            mcContentDAO.removeMc(mc);
        }
        catch(DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing"
                                                 + " the mc content: "
                                                 + e.getMessage(),e);
        }
    }
    
    public void deleteMcById(Long mcId) throws McApplicationException
    {
    	try
        {
            mcContentDAO.removeMcById(mcId);
        }
        catch(DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing by id"
                                                 + " the mc content: "
                                                 + e.getMessage(),e);
        }
    }

    public void deleteMcSession(McSession mcSession) throws McApplicationException 
	{
		try
        {
			mcSessionDAO.removeMcSession(mcSession);
        }
		catch(DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is deleting"
                                                 + " the mc session: "
                                                 + e.getMessage(),e);
        }
	}
    
    
    public void removeAttempt (McUsrAttempt attempt) throws McApplicationException
	{
    	try
        {
    		mcUsrAttemptDAO.removeMcUsrAttempt(attempt);
        }
        catch(DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing"
                                                 + " the attempt: "
                                                 + e.getMessage(),e);
        }
	}
    
    
    public void deleteMcQueUsr(McQueUsr mcQueUsr) throws McApplicationException
    {
    	try
        {
    		mcUserDAO.removeMcUser(mcQueUsr);
        }
        catch(DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing"
                                                 + " the user: "
                                                 + e.getMessage(),e);
        }
    }

    public void saveMcContent(McContent mc) throws McApplicationException
    {
        try
        {
            mcContentDAO.saveMcContent(mc);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is saving"
                                                 + " the mc content: "
                                                 + e.getMessage(),e);
        }
    }
    
    
    public List findMcOptionsContentByQueId(Long mcQueContentId) throws McApplicationException
    {
    	try
        {
            List list=mcOptionsContentDAO.findMcOptionsContentByQueId(mcQueContentId);
            return list;
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is finding by que id"
                                                 + " the mc options: "
                                                 + e.getMessage(),e);
        }
    }
    
    public void saveMcOptionsContent(McOptsContent mcOptsContent) throws McApplicationException
	{
    	try
        {
            mcOptionsContentDAO.saveMcOptionsContent(mcOptsContent);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is saving"
                                                 + " the mc options content: "
                                                 + e.getMessage(),e);
        }
	}
    
    public McOptsContent getOptionContentByOptionText(final String option, final Long mcQueContentUid)
    {
    	try
        {
            return mcOptionsContentDAO.getOptionContentByOptionText(option, mcQueContentUid);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is returning the"
                                                 + " option by option text: "
                                                 + e.getMessage(),e);
        }
    }
    
    
    public void updateMcOptionsContent(McOptsContent mcOptsContent) throws McApplicationException
	{
    	try
        {
            mcOptionsContentDAO.updateMcOptionsContent(mcOptsContent);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is updating"
                                                 + " the mc options content: "
                                                 + e.getMessage(),e);
        }
	}

    
    public void deleteMcOptionsContent(McOptsContent mcOptsContent) throws McApplicationException
	{
    	try
        {
            mcOptionsContentDAO.removeMcOptionsContent(mcOptsContent);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing"
                                                 + " the mc options content: "
                                                 + e.getMessage(),e);
        }
	}
    
    
    public void removeMcOptionsContentByQueId(Long mcQueContentId) throws McApplicationException
    {
    	try
        {
            mcOptionsContentDAO.removeMcOptionsContentByQueId(mcQueContentId);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing by que id"
                                                 + " the mc options content: "
                                                 + e.getMessage(),e);
        }
    }
    
    
    public void deleteMcOptionsContentByUID(Long uid) throws McApplicationException
	{
    	try
        {
            mcOptionsContentDAO.removeMcOptionsContentByUID(uid);
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is removing by uid"
                                                 + " the mc options content: "
                                                 + e.getMessage(),e);
        }
	}

    
    public User getCurrentUserData(String username) throws McApplicationException
    {
        try
        {
        	logger.debug("getCurrentUserData: " + username);
        	/**
             * this will return null if the username not found
             */
        	User user=userManagementService.getUserByLogin(username);
        	if (user  == null)
        	{
        		logger.debug("No user with the username: "+ username +  " exists.");
        		throw new McApplicationException("No user with that username exists.");
        	}
        	return user;	 
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Unable to find current user information"
	                                             + " Root Cause: ["
	                                             + e.getMessage() + "]",
                                                 e);
        }
    }

    /**
     * 
     * Unused method
     * @param lessonId
     * @return
     * @throws McApplicationException
     */
    public Lesson getCurrentLesson(long lessonId) throws McApplicationException
    {
        try
        {
            /**this is a mock implementation to make the project compile and 
            work. When the Lesson service is ready, we need to switch to 
            real service implementation.
            */
            return new Lesson();
            /**return lsDAO.find(lsessionId); */
        }
        catch (DataAccessException e)
        {
            throw new McApplicationException("Exception occured when lams is loading"
                                                 + " learning session:"
                                                 + e.getMessage(),
                                                 e);
        }
    }

    
	/**
	 * checks the paramter content in the user responses table 
	 * @param qa
	 * @return
	 * @throws McApplicationException
	 */
	public boolean studentActivityOccurredGlobal(McContent mcContent) throws McApplicationException
	{
		Iterator questionIterator=mcContent.getMcQueContents().iterator();
        while (questionIterator.hasNext())
        {
        	McQueContent mcQueContent=(McQueContent)questionIterator.next(); 
        	Iterator attemptsIterator=mcQueContent.getMcUsrAttempts().iterator();
        	while (attemptsIterator.hasNext())
        	{
        		logger.debug("there is at least one attempt");
        		/**
        		 * proved the fact that there is at least one attempt for this content.
        		 */
        		return true;
        	}
        } 
        logger.debug("there is no response for this content");
		return false;
	}
	

	public int countIncompleteSession(McContent mc) throws McApplicationException
	{
		//int countIncompleteSession=mcSessionDAO.countIncompleteSession(mc);
		int countIncompleteSession=2;
		return countIncompleteSession;
	}
	
	/**
	 * checks the parameter content in the tool sessions table
	 * 
	 * find out if any student has ever used (logged in through the url  and replied) to this content
	 * return true even if you have only one content passed as parameter referenced in the tool sessions table
	 * @param qa
	 * @return boolean
	 * @throws McApplicationException
	 */
	public boolean studentActivityOccurred(McContent mc) throws McApplicationException
	{
		//int countStudentActivity=mcSessionDAO.studentActivityOccurred(mc);
		int countStudentActivity=2;
		
		if (countStudentActivity > 0)
			return true;
		return false;
	}
	
	
	/**
	 * 
	 * gets called ONLY when a lesson is being created in monitoring mode. 
	 * Should create the new content(toContent) based on what the author has created her content with. In q/a tool's case
	 * that is content + question's content but not user responses. The deep copy should go only as far as
	 * default content (or author created content) already goes.
	 * ToolContentManager CONTRACT
	 * 
	 * 
	 * similar to public void removeToolContent(Long toolContentId)
	 * gets called by Container+Flash
	 * 
	 */
	
    public void copyToolContent(Long fromContentId, Long toContentId) throws ToolException
    {
    	logger.debug("start of copyToolContent with ids: " + fromContentId + " and " + toContentId);

    	if (fromContentId == null)
        {
        	logger.debug("fromContentId is null.");
        	logger.debug("attempt retrieving tool's default content id with signatute : " + MY_SIGNATURE);
        	long defaultContentId=0;
			try
			{
				defaultContentId=getToolDefaultContentIdBySignature(MY_SIGNATURE);
				fromContentId= new Long(defaultContentId);
			}
			catch(Exception e)
			{
				logger.debug("default content id has not been setup for signature: " +  MY_SIGNATURE);
				throw new ToolException("WARNING! default content has not been setup for signature" + MY_SIGNATURE + " Can't continue!");
			}
        }
        
        if (toContentId == null)
        {
        	logger.debug("throwing ToolException: toContentId is null");
			throw new ToolException("toContentId is missing");
        }
        logger.debug("final - copyToolContent using ids: " + fromContentId + " and " + toContentId);
            
        try
        {
            McContent fromContent = mcContentDAO.findMcContentById(fromContentId);
        
            if (fromContent == null)
            {
            	logger.debug("fromContent is null.");
            	logger.debug("attempt retrieving tool's default content id with signatute : " + MY_SIGNATURE);
            	long defaultContentId=0;
    			try
    			{
    				defaultContentId=getToolDefaultContentIdBySignature(MY_SIGNATURE);
    				fromContentId= new Long(defaultContentId);
    			}
    			catch(Exception e)
    			{
    				logger.debug("default content id has not been setup for signature: " +  MY_SIGNATURE);
    				throw new ToolException("WARNING! default content has not been setup for signature" + MY_SIGNATURE + " Can't continue!");
    			}
    			
    			fromContent = mcContentDAO.findMcContentById(fromContentId);
    			logger.debug("using fromContent: " + fromContent);
            }
            
            logger.debug("final - retrieved fromContent: " + fromContent);
            logger.debug("final - before new instance using " + fromContent + " and " + toContentId);
            McContent toContent = McContent.newInstance(fromContent,toContentId);
            if (toContent == null)
            {
            	logger.debug("throwing ToolException: WARNING!, retrieved toContent is null.");
            	throw new ToolException("WARNING! Fail to create toContent. Can't continue!");
            }
            else
            {
            	logger.debug("retrieved toContent: " + toContent);
	            mcContentDAO.saveMcContent(toContent);
	            logger.debug("toContent has been saved successfully: " + toContent);
            }
            logger.debug("end of copyToolContent with ids: " + fromContentId + " and " + toContentId);
        }
        catch (DataAccessException e)
        {
        	logger.debug("throwing ToolException: Exception occured when lams is copying content between content ids.");
            throw new ToolException("Exception occured when lams is copying content between content ids."); 
        }
    }


    
    /**
     * TO BE DEFINED-FUTURE API
     * gets called from monitoring module
     * 
     * update the tool session status to COMPLETE for this tool session
     * 
     * @param Long toolSessionId
     */
    public void setAsForceCompleteSession(Long toolSessionId) throws McApplicationException
    {
    	McSession mcSession=retrieveMcSession(toolSessionId);	
    	mcSession.setSessionStatus(McSession.COMPLETED);
    	updateMcSession(mcSession);
   }

    
    /**
     * TO BE DEFINED
     * 
     * update the tool session status to COMPLETE for this user
     * IMPLEMENT THIS!!!! Is this from ToolContentManager???
     * 

     * @param userId
     */
    public void setAsForceComplete(Long userId) throws McApplicationException
    {
    	McQueUsr mcQueUsr=retrieveMcQueUsr(userId);
    	
    	if (mcQueUsr != null)
    	{
    		logger.debug("retrieved mcQueUsr : " + mcQueUsr);
        	logger.debug("retrieved mcQueUsr  has the tool session : " + mcQueUsr.getMcSession());
        	McSession mcSession=mcQueUsr.getMcSession();
        	if (mcSession != null)
        	{
        		Long usersToolSessionId=mcSession.getMcSessionId();
            	logger.debug("retrieved  tool session has tool session id : " + usersToolSessionId);
            	
            	mcSession=retrieveMcSession(usersToolSessionId);	
            	logger.debug("retrieved  mcSession is : " + mcSession);
            	mcSession.setSessionStatus(McSession.COMPLETED);
            	logger.debug("updated  mcSession to COMPLETED : ");
            	updateMcSession(mcSession);
            	logger.debug("updated  mcSession to COMPLETED in the db : ");
            	
            	McContent mcContent=mcSession.getMcContent();
            	logger.debug("mcSession uses qaContent : " + mcContent);
            	logger.debug("mcSession uses qaContentId : " + mcContent.getMcContentId());
            	
            	/**
            	 * if all the sessions of this content is COMPLETED, unlock the content
            	 * 
            	 */
            	int countIncompleteSession=countIncompleteSession(mcContent);
            	logger.debug("mcSession countIncompleteSession : " + countIncompleteSession);
            	
            	if (countIncompleteSession == 0)
            	{
            		mcContent.setContentInUse(false);
            		updateMc(mcContent);
                	logger.debug("qaContent has been updated for contentInUse" + mcContent);
            	}
        	}
        	else
        	{
        		logger.debug("WARNING!: retrieved mcSession is null.");
        		throw new McApplicationException("Fail to setAsForceComplete"
                        + " based on null mcSession.");
        	}
    	}
    	else
    	{
    		logger.debug("WARNING!: retrieved qaQueUsr is null.");
            throw new McApplicationException("Fail to setAsForceComplete"
                    + " based on null qaQueUsr.");
    	}
    }
    
    public void unsetAsDefineLater(Long toolContentId) throws McApplicationException
    {
    	logger.debug("request for unsetAsDefineLater with toolContentId: " + toolContentId);
    	if  (toolContentId == null)
    	{
    		logger.debug("WARNING!: retrieved toolContentId is null.");
            throw new McApplicationException("Fail to setAsDefineLater"
                    + " based on null toolContentId.");
    	}
    	McContent mcContent = mcContentDAO.findMcContentById(toolContentId);
    	if (mcContent == null)
    	{
    		logger.debug("WARNING!!!: retrieved mcContent is null.");
            throw new McApplicationException("Fail to unsetAsDefineLater"
                    + " based on null mcContent.");
    	}
    	mcContent.setDefineLater(false);
    	updateMc(mcContent);
    	logger.debug("qaContent has been updated for unsetAsDefineLater: " + mcContent);
    }
    
    /**
     * 
     * set the defineLater to true on this content
     * 
     * @param toolContentId
     * return void
     */
    public void setAsDefineLater(Long toolContentId) throws DataMissingException, ToolException
    {
    	logger.debug("request for setAsDefineLater with toolContentId: " + toolContentId);
    	if  (toolContentId == null)
    	{
    		logger.debug("throwing DataMissingException: WARNING!: retrieved toolContentId is null.");
            throw new DataMissingException("toolContentId is missing");
    	}
    	
    	McContent mcContent = mcContentDAO.findMcContentById(toolContentId);
    	if (mcContent == null)
    	{
    		logger.debug("throwing DataMissingException: WARNING!: retrieved mcContent is null.");
            throw new DataMissingException("mcContent is missing");
    	}
    	mcContent.setDefineLater(true);
    	updateMc(mcContent);
    	logger.debug("mcContent has been updated for defineLater: " + mcContent);
    }

    /**
     * 
     * set the runOffline to true on this content
     * 
     * @param toolContentId
     * return void
     */
    public void setAsRunOffline(Long toolContentId) throws DataMissingException, ToolException
    {
    	logger.debug("request for setAsRunOffline with toolContentId:" + toolContentId);
    	if  (toolContentId == null)
    	{
    		logger.debug("throwing DataMissingException: WARNING!: retrieved toolContentId is null.");
            throw new DataMissingException("toolContentId is missing");
    	}
    	McContent mcContent = mcContentDAO.findMcContentById(toolContentId);
    	if (mcContent == null)
    	{
    		logger.debug("throwing DataMissingException: WARNING!: retrieved mcContent is null.");
            throw new DataMissingException("mcContent is missing");
    	}
    	mcContent.setRunOffline(true);
    	updateMc(mcContent);
    	logger.debug("qaContent has been updated for runOffline: " + mcContent);
    }

        
    
    /** 
     * !!! UNUSED !!!
     * 
     * gets automatically called only in monitoring mode when the author chooses to delete a lesson.
     * 
     * The idea is to remove content + its relevant sessions + in q/a tools's case the question's content from the db. 
     * ToolContentManager CONTRACT
     *  this gets called automatically by Flash when a deletion is detected in the tool interface.
     */
    public void removeToolContent(Long toolContentId) 
    {
    	logger.debug("start of removeToolContent with toolContentId: " + toolContentId);
    	
    	McContent mcContent = mcContentDAO.findMcContentById(toolContentId);
    	logger.debug("retrieving mcContent: " + mcContent);
    	
    	if (mcContent != null)
    	{
    		Iterator sessionIterator=mcContent.getMcSessions().iterator();
            while (sessionIterator.hasNext())
            {
            	McSession mcSession=(McSession)sessionIterator.next(); 
            	logger.debug("iterated mcSession : " + mcSession);
            	
            	Iterator sessionUsersIterator=mcSession.getMcQueUsers().iterator();
            	while (sessionUsersIterator.hasNext())
            	{
            		McQueUsr mcQueUsr=(McQueUsr) sessionUsersIterator.next();
            		logger.debug("iterated mcQueUsr : " + mcQueUsr);
            		
            		Iterator sessionUsersAttemptsIterator=mcQueUsr.getMcUsrAttempts().iterator();
            		while (sessionUsersAttemptsIterator.hasNext())
                	{
            			McUsrAttempt mcUsrAttempt=(McUsrAttempt)sessionUsersAttemptsIterator.next();
            			logger.debug("iterated mcUsrAttempt : " + mcUsrAttempt);
            			removeAttempt(mcUsrAttempt);
            			logger.debug("removed qaUsrAttempt : " + mcUsrAttempt);
                	}
            	}
            }
            
            logger.debug("removed all existing responses of toolContent with toolContentId:" + 
            																toolContentId);                
            mcContentDAO.removeMcById(toolContentId);        
            logger.debug("removed qaContent:" + mcContent);
    	}
    }
    
    
    /*
     * 
     * Will need an update on the core tool signature: reason : when  mcContent is null throw an exception 
     *  (non-Javadoc)
     * @see org.lamsfoundation.lams.tool.ToolContentManager#removeToolContent(java.lang.Long, boolean)
     */
    public void removeToolContent(Long toolContentId, boolean removeSessionData) throws SessionDataExistsException, ToolException
	{
    	logger.debug("start of: removeToolContent(Long toolContentId, boolean removeSessionData");
    	logger.debug("start of removeToolContent with toolContentId: " + toolContentId + "removeSessionData: " + removeSessionData);
    	
    	if (toolContentId == null)
    	{
    		logger.debug("toolContentId is null");
    		throw new ToolException("toolContentId is missing");
    	}
    	
    	McContent mcContent = mcContentDAO.findMcContentById(toolContentId);
    	logger.debug("retrieving mcContent: " + mcContent);
    	
    	if (mcContent != null)
    	{
    		Iterator sessionIterator=mcContent.getMcSessions().iterator();
            while (sessionIterator.hasNext())
            {
            	if (removeSessionData == false)
            	{
            		logger.debug("removeSessionData is false, throwing SessionDataExistsException.");
            		throw new SessionDataExistsException();	
            	}
            	
            	McSession mcSession=(McSession)sessionIterator.next(); 
            	logger.debug("iterated mcSession : " + mcSession);
            	
            	Iterator sessionUsersIterator=mcSession.getMcQueUsers().iterator();
            	while (sessionUsersIterator.hasNext())
            	{
            		McQueUsr mcQueUsr=(McQueUsr) sessionUsersIterator.next();
            		logger.debug("iterated mcQueUsr : " + mcQueUsr);
            		
            		Iterator sessionUsersAttemptsIterator=mcQueUsr.getMcUsrAttempts().iterator();
            		while (sessionUsersAttemptsIterator.hasNext())
                	{
            			McUsrAttempt mcUsrAttempt=(McUsrAttempt)sessionUsersAttemptsIterator.next();
            			logger.debug("iterated mcUsrAttempt : " + mcUsrAttempt);
            			removeAttempt(mcUsrAttempt);
            			logger.debug("removed mcUsrAttempt : " + mcUsrAttempt);
                	}
            	}
            }
            
            logger.debug("removed all existing responses of toolContent with toolContentId:" + 
            																toolContentId);                
            mcContentDAO.removeMcById(toolContentId);        
            logger.debug("removed qaContent:" + mcContent);
    	}
    	else
    	{
        	logger.debug("Warning!!!, We should have not come here. mcContent is null.");
        	throw new ToolException("toolContentId is missing");
    	}
	}
    
    
    /**
	 * it is possible that the tool session id already exists in the tool sessions table
	 * as the users from the same session are involved.
	 * existsSession(long toolSessionId)
	 * @param toolSessionId
	 * @return boolean
	 */
	public boolean existsSession(Long toolSessionId) 
	{
		McSession mcSession= retrieveMcSession(toolSessionId);
    	
	    if (mcSession == null) 
	    {
	    	logger.debug("mcSession does not exist yet: " + toolSessionId);
	    	return false;
	    }
	    else
	    {
	    	logger.debug("retrieving an existing mcSession: " + mcSession + " " + toolSessionId);
	    }
		return true;	
	}
    
    /**
     * 
     * ToolSessionManager CONTRACT : creates a tool session with the incoming toolSessionId in the tool session table
     * 
     * gets called only in the Learner mode.
     * All the learners in the same group have the same toolSessionId.
     * 
     */
    public void createToolSession(Long toolSessionId, Long toolContentId) throws ToolException
    {
    	logger.debug("start of createToolSession with ids: " + toolSessionId + " and " + toolContentId);
    	if (toolSessionId == null)
    	{
    		logger.debug("toolSessionId is null");
    		throw new ToolException("toolSessionId is missing");
    	}
    	
    	long defaultContentId=0;
    	if (toolContentId == null)
        {
        	logger.debug("toolContentId is null.");
        	logger.debug("attempt retrieving tool's default content id with signatute : " + MY_SIGNATURE);
        
			try
			{
				defaultContentId=getToolDefaultContentIdBySignature(MY_SIGNATURE);
				toolContentId=new Long(defaultContentId);
				logger.debug("updated toolContentId to: " + toolContentId);
			}
			catch(Exception e)
			{
				logger.debug("default content id has not been setup for signature: " +  MY_SIGNATURE);
				throw new ToolException("WARNING! default content has not been setup for signature" + MY_SIGNATURE + " Can't continue!");
			}
        }
    	logger.debug("final toolSessionId and toolContentId: " +  toolSessionId + " " + toolContentId);
    	
        McContent mcContent = mcContentDAO.findMcContentById(toolContentId);
        logger.debug("retrieved mcContent: " + mcContent);
        
        if (mcContent == null)
        {
        	logger.debug("mcContent is null.");
        	logger.debug("attempt retrieving tool's default content id with signatute : " + MY_SIGNATURE);
        
			try
			{
				defaultContentId=getToolDefaultContentIdBySignature(MY_SIGNATURE);
				toolContentId=new Long(defaultContentId);
				logger.debug("updated toolContentId to: " + toolContentId);
			}
			catch(Exception e)
			{
				logger.debug("default content id has not been setup for signature: " +  MY_SIGNATURE);
				throw new ToolException("WARNING! default content has not been setup for signature" + MY_SIGNATURE + " Can't continue!");
			}

			mcContent = mcContentDAO.findMcContentById(toolContentId);
        }
        logger.debug("final - retrieved mcContent: " + mcContent);

            
        /**
         * create a new a new tool session if it does not already exist in the tool session table
         */
        if (!existsSession(toolSessionId))
        {
        	
        	try
			{
        		McSession mcSession = new McSession(toolSessionId,
                        new Date(System.currentTimeMillis()),
                        McSession.INCOMPLETE,
                        mcContent,
                        new TreeSet());

    		    logger.debug("created mcSession: " + mcSession);
    		    mcSessionDAO.saveMcSession(mcSession);
    		    logger.debug("created mcSession in the db: " + mcSession);	
	
        	}
        	catch(Exception e)
			{
        		logger.debug("Error creating new toolsession in the db");
				throw new ToolException("Error creating new toolsession in the db: " + e);
			}
        }
    }

    
    public void removeToolSession(Long toolSessionId) throws DataMissingException, ToolException
	{
    	logger.debug("start of removeToolSession with id: " + toolSessionId);
    	if (toolSessionId == null)
    	{
    		logger.debug("toolSessionId is null");
    		throw new DataMissingException("toolSessionId is missing");
    	}
    	
    	
    	McSession mcSession=null;
    	try
		{
    		mcSession=retrieveMcSession(toolSessionId);
    		logger.debug("retrieved qaSession: " + mcSession);
		}
    	catch(McApplicationException e)
		{
    		throw new DataMissingException("error retrieving mcSession: " + e);
		}
    	catch(Exception e)
		{
    		throw new ToolException("error retrieving qaSession: " + e);
		}
    	
    	if (mcSession == null)
    	{
    		logger.debug("mcSession is null");
    		throw new DataMissingException("mcSession is missing");
    	}
    	
    	try
		{
    		mcSessionDAO.removeMcSession(mcSession);
        	logger.debug("mcSession " + mcSession + " has been deleted successfully.");	
		}
    	catch(McApplicationException e)
		{
    		throw new ToolException("error deleting qaSession:" + e);
		}
	}
	
    
    /**FIX THIS ONE!!!!
     * TO BE TESTED
     * ToolSessionManager CONTRACT
     * gets called only in the Learner mode.
     * 
     * Call controller service to complete the qa session
     * @see org.lamsfoundation.lams.tool.ToolSessionManager#leaveToolSession(java.lang.Long)
     */
    public String leaveToolSession(Long toolSessionId,User learner) throws DataMissingException, ToolException 
    {
        logger.debug("start of leaveToolSession with toolSessionId:" + toolSessionId);
        logger.debug("start of leaveToolSession with learner:" + learner);
        
        if (toolSessionId == null)
    	{
    		logger.debug("toolSessionId is null");
    		throw new DataMissingException("toolSessionId is missing");
    	}
        
        if (learner == null)
    	{
    		logger.debug("learner is null");
    		throw new DataMissingException("learner is missing");
    	}
        
    	try
		{
    		/*
    		String nextUrl=learnerService.completeToolSession(toolSessionId,learner);
    		logger.debug(logger + " " + this.getClass().getName() +  " " + "nextUrl: " + nextUrl);
    		return nextUrl;
    		*/
    		return "nextUrl";
    	}
    	catch(DataAccessException e)
		{
    		throw new ToolException("Exception occured when user is leaving tool session: " + e);
		}
        
    }

    /**
     * ToolSessionManager CONTRACT
     * 
     */
    public ToolSessionExportOutputData exportToolSession(Long toolSessionId) throws DataMissingException, ToolException
    {
        throw new ToolException("not yet implemented");
    }

    /**
     * ToolSessionManager CONTRACT
     * 
     */
    public ToolSessionExportOutputData exportToolSession(List toolSessionIds) throws DataMissingException, ToolException
    {

        throw new ToolException("not yet implemented");
    }
    
    public BasicToolVO getToolBySignature(String toolSignature) throws McApplicationException
    {
    	logger.debug("attempt retrieving tool with signature : " + toolSignature);
    	BasicToolVO tool=toolService.getToolBySignature(toolSignature);
    	logger.debug("retrieved tool: " + tool);
	    return tool;
    }
    
    public long getToolDefaultContentIdBySignature(String toolSignature) throws McApplicationException
    {
    	long contentId=0;
    	contentId=toolService.getToolDefaultContentIdBySignature(toolSignature);
    	logger.debug("tool default contentId : " + contentId);
	    return contentId;
    }

    public McQueContent getToolDefaultQuestionContent(long contentId) throws McApplicationException
    {
    	McQueContent mcQueContent=mcQueContentDAO.getToolDefaultQuestionContent(contentId);
    	logger.debug("retrieved mcQueContent : " + mcQueContent);
    	return mcQueContent; 
    }

    
    public List getToolSessionsForContent(McContent mc)
    {
    	logger.debug("attempt retrieving listToolSessionIds for : " + mc);
    	List listToolSessionIds=mcSessionDAO.getSessionsFromContent(mc);
    	return listToolSessionIds;
    }
    
    
    
	/**
	 * This method verifies the credentials of the SubmitFiles Tool and gives it
	 * the <code>Ticket</code> to login and access the Content Repository.
	 * 
	 * A valid ticket is needed in order to access the content from the
	 * repository. This method would be called evertime the tool needs to
	 * upload/download files from the content repository.
	 * 
	 * @return ITicket The ticket for repostory access
	 * @throws SubmitFilesException
	 */
	public ITicket getRepositoryLoginTicket() throws McApplicationException {
		repositoryService = RepositoryProxy.getRepositoryService();
    	logger.debug("retrieved repositoryService : " + repositoryService);
		
		ICredentials credentials = new SimpleCredentials(
				repositoryUser,
				repositoryId);
		try {
			ITicket ticket = repositoryService.login(credentials,
					repositoryWorkspace);
			logger.debug("retrieved ticket: " + ticket);
			return ticket;
		} catch (AccessDeniedException e) {
			throw new McApplicationException("Access Denied to repository."
					+ e.getMessage());
		} catch (WorkspaceNotFoundException e) {
			throw new McApplicationException("Workspace not found."
					+ e.getMessage());
		} catch (LoginException e) {
			throw new McApplicationException("Login failed." + e.getMessage());
		}
	}
	
	
	/**
	 * This method deletes the content with the given <code>uuid</code> and
	 * <code>versionID</code> from the content repository
	 * 
	 * @param uuid
	 *            The <code>uuid</code> of the node to be deleted
	 * @param versionID
	 *            The <code>version_id</code> of the node to be deleted.
	 * @throws SubmitFilesException
	 */
	public void deleteFromRepository(Long uuid, Long versionID)
			throws McApplicationException {
		ITicket ticket = getRepositoryLoginTicket();
		logger.debug("retrieved ticket: " + ticket);
		try {
			String files[] = repositoryService.deleteVersion(ticket, uuid,versionID);
			logger.debug("retrieved files: " + files);
		} catch (Exception e) {
			throw new McApplicationException(
					"Exception occured while deleting files from"
							+ " the repository " + e.getMessage());
		}
	}
	
	
	/**
	 * This method is called everytime a new content has to be added to the
	 * repository. In order to do so first of all a valid ticket is obtained
	 * from the Repository hence authenticating the tool(SubmitFiles) and then
	 * the corresponding file is added to the repository.
	 * 
	 * @param stream
	 *            The <code>InputStream</code> representing the data to be
	 *            added
	 * @param fileName
	 *            The name of the file being added
	 * @param mimeType
	 *            The MIME type of the file (eg. TXT, DOC, GIF etc)
	 * @return NodeKey Represents the two part key - UUID and Version.
	 * @throws SubmitFilesException
	 */
	public NodeKey uploadFileToRepository(InputStream stream, String fileName) throws McApplicationException {
		logger.debug("attempt getting the ticket");
		ITicket ticket = getRepositoryLoginTicket();
		logger.debug("retrieved ticket: " + ticket);
		
		try {
			NodeKey nodeKey = repositoryService.addFileItem(ticket, stream,
					fileName, null, null);
			logger.debug("retrieved nodeKey from repository service: " + nodeKey);
			return nodeKey;
		} catch (Exception e) {
			throw new McApplicationException("Exception occured while trying to"
					+ " upload file into the repository" + e.getMessage());
		}
	}

	public InputStream downloadFile(Long uuid, Long versionID)throws McApplicationException{
		ITicket ticket = getRepositoryLoginTicket();		
		try{
			IVersionedNode node = repositoryService.getFileItem(ticket,uuid,null);
			logger.debug("retrieved node: " + node);
			return node.getFile();
		}catch(AccessDeniedException e){
			throw new McApplicationException("AccessDeniedException occured while trying to download file " + e.getMessage());
		}catch(FileException e){
			throw new McApplicationException("FileException occured while trying to download file " + e.getMessage());
		}catch(ItemNotFoundException e){
			throw new McApplicationException("ItemNotFoundException occured while trying to download file " + e.getMessage());			
		}
	}
	
	/**
	 *  !! COMPLETE THIS !!
	 * 
	 * adds a new entry to the uploaded files table
	 */
	public void persistFile(String uuid, boolean isOnlineFile, String fileName, McContent qaContent) throws McApplicationException {
		/*
		logger.debug("attempt persisting file to the db: " + uuid + " " + isOnlineFile + " " + fileName + " " + qaContent);
		QaUploadedFile qaUploadedFile= new QaUploadedFile(uuid, isOnlineFile, fileName, qaContent);
		logger.debug("created qaUploadedFile: " + qaUploadedFile);
		qaUploadedFileDAO.saveUploadFile(qaUploadedFile);
		logger.debug("persisted qaUploadedFile: " + qaUploadedFile);
		*/
	}

	/**
	 * 
	 *  !! COMPLETE THIS !!
	 * 
	 * removes all the entries in the uploaded files table
	 */
	public void cleanUploadedFilesMetaData() throws McApplicationException {
		/*
		logger.debug("attempt cleaning up uploaded file meta data table from the db");
		qaUploadedFileDAO.cleanUploadedFilesMetaData();
		logger.debug("files meta data has been cleaned up");
		*/
	}
    
	
	/**
	 * @return Returns the logger.
	 */
	public static Logger getLogger() {
		return logger;
	}
	/**
	 * @param logger The logger to set.
	 */
	public static void setLogger(Logger logger) {
		McServicePOJO.logger = logger;
	}
	/**
	 * @return Returns the cred.
	 */
	public ICredentials getCred() {
		return cred;
	}
	/**
	 * @param cred The cred to set.
	 */
	public void setCred(ICredentials cred) {
		this.cred = cred;
	}
	
	/*
	   !!! COMPLETE THIS !!!
	public IQaUploadedFileDAO getQaUploadedFileDAO() {
		return qaUploadedFileDAO;
	}
	
	public void setQaUploadedFileDAO(IQaUploadedFileDAO qaUploadedFileDAO) {
		this.qaUploadedFileDAO = qaUploadedFileDAO;
	}
	
	*/
	
	
	/**
	 * @return Returns the repositoryId.
	 */
	public char[] getRepositoryId() {
		return repositoryId;
	}
	/**
	 * @return Returns the repositoryUser.
	 */
	public String getRepositoryUser() {
		return repositoryUser;
	}
	/**
	 * @return Returns the repositoryWorkspace.
	 */
	public String getRepositoryWorkspace() {
		return repositoryWorkspace;
	}

	/**
	 * @return Returns the toolService.
	 */
	public ILamsToolService getToolService() {
		return toolService;
	}
	/**
	 * @return Returns the userManagementService.
	 */
	public IUserManagementService getUserManagementService() {
		return userManagementService;
	}
	/**
	 * @return Returns the mcContentDAO.
	 */
	public IMcContentDAO getMcContentDAO() {
		return mcContentDAO;
	}
	/**
	 * @param mcContentDAO The mcContentDAO to set.
	 */
	public void setMcContentDAO(IMcContentDAO mcContentDAO) {
		this.mcContentDAO = mcContentDAO;
	}
	/**
	 * @return Returns the mcOptionsContentDAO.
	 */
	public IMcOptionsContentDAO getMcOptionsContentDAO() {
		return mcOptionsContentDAO;
	}
	/**
	 * @param mcOptionsContentDAO The mcOptionsContentDAO to set.
	 */
	public void setMcOptionsContentDAO(IMcOptionsContentDAO mcOptionsContentDAO) {
		this.mcOptionsContentDAO = mcOptionsContentDAO;
	}
	/**
	 * @return Returns the mcQueContentDAO.
	 */
	public IMcQueContentDAO getMcQueContentDAO() {
		return mcQueContentDAO;
	}
	/**
	 * @param mcQueContentDAO The mcQueContentDAO to set.
	 */
	public void setMcQueContentDAO(IMcQueContentDAO mcQueContentDAO) {
		this.mcQueContentDAO = mcQueContentDAO;
	}
	/**
	 * @return Returns the mcSessionDAO.
	 */
	public IMcSessionDAO getMcSessionDAO() {
		return mcSessionDAO;
	}
	/**
	 * @param mcSessionDAO The mcSessionDAO to set.
	 */
	public void setMcSessionDAO(IMcSessionDAO mcSessionDAO) {
		this.mcSessionDAO = mcSessionDAO;
	}
	/**
	 * @return Returns the mcUserDAO.
	 */
	public IMcUserDAO getMcUserDAO() {
		return mcUserDAO;
	}
	/**
	 * @param mcUserDAO The mcUserDAO to set.
	 */
	public void setMcUserDAO(IMcUserDAO mcUserDAO) {
		this.mcUserDAO = mcUserDAO;
	}
	/**
	 * @return Returns the mcUsrAttemptDAO.
	 */
	public IMcUsrAttemptDAO getMcUsrAttemptDAO() {
		return mcUsrAttemptDAO;
	}
	/**
	 * @param mcUsrAttemptDAO The mcUsrAttemptDAO to set.
	 */
	public void setMcUsrAttemptDAO(IMcUsrAttemptDAO mcUsrAttemptDAO) {
		this.mcUsrAttemptDAO = mcUsrAttemptDAO;
	}
	
	/**
	 * @return Returns the repositoryService.
	 */
	public IRepositoryService getRepositoryService() {
		return repositoryService;
	}
	/**
	 * @param repositoryService The repositoryService to set.
	 */
	public void setRepositoryService(IRepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

    public void setUserManagementService(IUserManagementService userManagementService)
    {
        this.userManagementService = userManagementService;
    }
    
    public void setToolService(ILamsToolService toolService)
    {
        this.toolService = toolService;
    }

}
