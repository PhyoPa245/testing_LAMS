-- CVS ID: $Id$
 
INSERT INTO lams_tool
(
tool_signature,
service_name,
tool_display_name,
description,
tool_identifier,
tool_version,
learning_library_id,
default_tool_content_id,
valid_flag,
grouping_support_type_id,
supports_run_offline_flag,
learner_url,
learner_preview_url,
learner_progress_url,
author_url,
monitor_url,
define_later_url,
export_pfolio_learner_url,
export_pfolio_class_url,
contribute_url,
moderation_url,
help_url,
language_file,
create_date_time
)
VALUES
(
'lantbk11',
'notebookService',
'Notebook',
'Notebook',
'notebook',
'1.1',
NULL,
NULL,
0,
2,
1,
'tool/lantbk11/learning.do?mode=learner',
'tool/lantbk11/learning.do?mode=author',
'tool/lantbk11/learning.do?mode=teacher',
'tool/lantbk11/authoring.do',
'tool/lantbk11/monitoring.do',
'tool/lantbk11/authoring.do?mode=teacher',
'tool/lantbk11/exportPortfolio?mode=learner',
'tool/lantbk11/exportPortfolio?mode=teacher',
'tool/lantbk11/contribute.do',
'tool/lantbk11/moderate.do',
'http://wiki.lamsfoundation.org/display/lamsdocs/lantbk11',
'org.lamsfoundation.lams.tool.notebook.ApplicationResources',
NOW()
)
