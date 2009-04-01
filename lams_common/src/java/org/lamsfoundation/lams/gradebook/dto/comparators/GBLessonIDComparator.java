/****************************************************************
 * Copyright (C) 2008 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */ 
 
/* $Id$ */ 
package org.lamsfoundation.lams.gradebook.dto.comparators; 

import java.util.Comparator;

import org.lamsfoundation.lams.gradebook.dto.GBLessonGridRowDTO;

@SuppressWarnings("unchecked")
public class GBLessonIDComparator implements Comparator{
    public int compare(Object gbLessonRowDTO, Object anotherGbLessonRowDTO) {

	if (gbLessonRowDTO instanceof GBLessonGridRowDTO && anotherGbLessonRowDTO instanceof GBLessonGridRowDTO) {
	   
	    Long lessonID1 = ((GBLessonGridRowDTO) gbLessonRowDTO).getLessonId();
	    Long lessonID2 = ((GBLessonGridRowDTO) anotherGbLessonRowDTO).getLessonId();

	    if (lessonID1 == null || lessonID2 == null) {
		return 0;
	    }
	    Long ret = lessonID1 - lessonID2;
	    return ret.intValue();    
	} else {
	    return 0;
	}
    }
}
 