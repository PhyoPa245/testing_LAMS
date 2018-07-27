/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.apache.poi.ss.usermodel;

import java.util.regex.Pattern;

/**
 * XSSF Only!
 * High level abstraction of table in a workbook.
 */
public interface Table {
    /**
     * Regular expression matching a Structured Reference (Table syntax) for XSSF table expressions.
     * Public for unit tests
     * @see <a href="https://support.office.com/en-us/article/Using-structured-references-with-Excel-tables-F5ED2452-2337-4F71-BED3-C8AE6D2B276E">
     *         Excel Structured Reference Syntax
     *      </a>
     */
    Pattern isStructuredReference = Pattern.compile("[a-zA-Z_\\\\][a-zA-Z0-9._]*\\[.*\\]");
    
    /**
     *  Get the top-left column index relative to the sheet
     * @return table start column index on sheet
     */
    int getStartColIndex();
    /**
     *  Get the top-left row index on the sheet
     * @return table start row index on sheet
     */
    int getStartRowIndex();
    /**
     *  Get the bottom-right column index on the sheet
     * @return table end column index on sheet
     */
    int getEndColIndex();
    /**
     *  Get the bottom-right row index
     * @return table end row index on sheet
     */
    int getEndRowIndex();
    /**
     * Get the name of the table.
     * @return table name
     */
    String getName();
    
    /**
     * Returns the index of a given named column in the table (names are case insensitive in XSSF).
     * Note this list is lazily loaded and cached for performance. 
     * Changes to the underlying table structure are not reflected in later calls
     * unless <code>XSSFTable.updateHeaders()</code> is called to reset the cache.
     * @param columnHeader the column header name to get the table column index of
     * @return column index corresponding to <code>columnHeader</code>
     */
    int findColumnIndex(String columnHeader);
    /**
     * Returns the sheet name that the table belongs to.
     */
    String getSheetName();
    /**
     * Returns true iff the table has a 'Totals' row
     */
    boolean isHasTotalsRow();
    
    
}
