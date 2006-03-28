/* 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
USA

http://www.gnu.org/licenses/gpl.txt 
*/
/* $$Id$$ */
package org.lamsfoundation.lams.tool.deploy.libraryActivity;

/**
 * @author mtruong
 *
 */
public class ToolActivity {
    
    private String toolSignature;
    private String toolActivityInsertScriptPath;
   
    
    public ToolActivity()
    {}
    
    public ToolActivity(String toolSignature, String toolActivityInsertScriptPath)
    {
        this.toolSignature = toolSignature;
        this.toolActivityInsertScriptPath = toolActivityInsertScriptPath;
    }
    
    /**
     * @return Returns the toolActivityInsertScriptPath.
     */
    public String getToolActivityInsertScriptPath() {
        return toolActivityInsertScriptPath;
    }
    /**
     * @param toolActivityInsertScriptPath The toolActivityInsertScriptPath to set.
     */
    public void setToolActivityInsertScriptPath(
            String toolActivityInsertScriptPath) {
        this.toolActivityInsertScriptPath = toolActivityInsertScriptPath;
    }
   
    /**
     * @return Returns the toolSignature.
     */
    public String getToolSignature() {
        return toolSignature;
    }
    /**
     * @param toolSignature The toolSignature to set.
     */
    public void setToolSignature(String toolSignature) {
        this.toolSignature = toolSignature;
    }
}
