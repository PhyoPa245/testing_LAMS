package org.lamsfoundation.lams.tool.forum.util;

/**
 * Created by IntelliJ IDEA.
 * User: conradb
 * Date: 14/06/2005
 * Time: 10:33:00
 * To change this template use File | Settings | File Templates.
 */
public interface ForumConstants {
    public final static int MAX_FILE_SIZE = 250 * 1000;
    public final static String FORUM_SERVICE = "forumService";
    
    public final static String CONTENT_HANDLER = "toolContentHandler";

	public static final String AUTHORING_DTO = "authoring";
	public static final String AUTHORING_TOPICS_LIST = "topicList";
	public static final String AUTHORING_TOPICS_INDEX = "topicIndex";
	public static final String AUTHORING_TOPIC_THREAD = "topicThread";
	public static final String AUTHORING_TOPIC = "topic";
	
	public static final String DEFAULT_TITLE = "Forum";
	//TODO:hard code!!! need to read from config
	public static final String TOOL_URL_BASE = "/lams/tool/lafrum11/";
	public static final String SUCCESS_FLAG = "SUCCESS_FLAG";
	public static final String FORUM_ID = "forum_id";
}
