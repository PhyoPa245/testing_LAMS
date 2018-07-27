INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
valid_flag,
grouping_support_type_id,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
help_url,
language_file,
create_date_time,
modified_date_time
)
VALUES
(
'lasprd10',
'spreadsheetService',
'Spreadsheet Tool',
'Spreadsheet Tool',
'spreadsheet',
'@tool_version@',
0,
2,
'tool/lasprd10/learning/start.do?mode=learner',
'tool/lasprd10/learning/start.do?mode=author',
'tool/lasprd10/learning/start.do?mode=teacher',
'tool/lasprd10/authoring/start.do',
'tool/lasprd10/monitoring/summary.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/lasprd10',
'org.lamsfoundation.lams.tool.spreadsheet.ApplicationResources',
NOW(),
NOW()
)