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

package org.lamsfoundation.lams.lesson.dto;


/**
 * The data transfer object for remoting data communication.
 * @author Jacky Fang
 * @since  2005-3-11
 * @version 1.1
 * 
 */
public class LearnerProgressDTO
{

    private Long lessonId;
    private String lessonName;
    private String userName;
    private Integer learnerId;
    private Long currentActivityId;
    private Long [] attemptedActivities;
    private Long [] completedActivities;
    
    /**
     * Full constructor 
     */
    public LearnerProgressDTO(Long lessonId,
                              String lessonName,
                              String userName,
                              Integer learnerId,
                              Long currentActivityId,
                              Long[] attemptedActivities,
                              Long[] completedActivities)
    {
        this.lessonId = lessonId;
        this.lessonName = lessonName;
        this.userName = userName;
        this.learnerId = learnerId;
        this.currentActivityId = currentActivityId;
        this.attemptedActivities = attemptedActivities;
        this.completedActivities = completedActivities;
    }

    /**
     * @return Returns the currentActivityID.
     */
    public Long getCurrentActivityID()
    {
        return currentActivityId;
    }
    /**
     * @return Returns the learnerId.
     */
    public Integer getLearnerId()
    {
        return learnerId;
    }
    /**
     * @return Returns the lessonId.
     */
    public Long getLessonId()
    {
        return lessonId;
    }
    /**
     * @return Returns the lessonName.
     */
    public String getLessonName()
    {
        return lessonName;
    }
    /**
     * @return Returns the userName.
     */
    public String getUserName()
    {
        return userName;
    }
    
    /**
     * @return Returns the completedActivities.
     */
    public Long[] getCompletedActivities()
    {
        return completedActivities;
    }
    /**
     * @return Returns the currentActivityId.
     */
    public Long getCurrentActivityId()
    {
        return currentActivityId;
    }    
}
