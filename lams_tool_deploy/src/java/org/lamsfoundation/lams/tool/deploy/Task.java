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
/* $$Id$$ */
package org.lamsfoundation.lams.tool.deploy;

import java.util.Map;

/**
 * Defines a Task in the tool deploy or undeploy process.
 * @author chris
 */
public interface Task
{
    /**
     * Executes the task.
     * @throws DeployException if Task cannot be executed.
     * @return Map of possibly useful values, such as the new ids 
     * created in the database. Values will vary from Task to Task.
     *
     */
    public Map<String,Object> execute() throws DeployException;
}
