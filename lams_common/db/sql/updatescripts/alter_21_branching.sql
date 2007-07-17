-- Script to be run for LAMS 2.1 release, on LAMS 2.0.3 tables.
-- Adds all the data needed for branching, plus a small change to the tool table needed for tool admin screen.

ALTER TABLE lams_group
ADD COLUMN group_ui_id INT(11);

CREATE TABLE lams_group_branch_activity (
       entry_id BIGINT(20) NOT NULL AUTO_INCREMENT
     , entry_ui_id INT(11)
     , group_id BIGINT(20) NOT NULL
     , sequence_activity_id BIGINT(20) NOT NULL
     , branch_activity_id BIGINT(20) NOT NULL
     , UNIQUE UQ_lams_group_activity (group_id, branch_activity_id)
     , PRIMARY KEY (entry_id)
     , INDEX (group_id)
     , CONSTRAINT FK_lams_group_activity_1 FOREIGN KEY (group_id)
                  REFERENCES lams_group (group_id)
     , INDEX (sequence_activity_id)
     , CONSTRAINT FK_lams_branch_map_sequence FOREIGN KEY (sequence_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
     , INDEX (branch_activity_id)
     , CONSTRAINT FK_lams_branch_map_branch FOREIGN KEY (branch_activity_id)
                  REFERENCES lams_learning_activity (activity_id)
)TYPE=InnoDB;

ALTER TABLE lams_learning_activity 
ADD COLUMN first_activity_id BIGINT(20)
,ADD COLUMN start_xcoord INT(11)
,ADD COLUMN start_ycoord INT(11)
,ADD COLUMN end_xcoord INT(11)
,ADD COLUMN end_ycoord INT(11);
 

INSERT INTO lams_learning_activity_type VALUES (10, 'BRANCHING_CHOSEN');
INSERT INTO lams_learning_activity_type VALUES (11, 'BRANCHING_GROUP');
INSERT INTO lams_learning_activity_type VALUES (12, 'BRANCHING_TOOL');

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description, 
	learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url, 
	export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (6, 10, 'Monitor Chosen Branching', 'Select between multiple sequence activities, with the branch chosen in monitoring.', 
	'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching', 
	'learning/branching.do?method=viewBranching&mode=teacher', NULL,
	'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/branching.do?method=assignBranch', 
	'monitoring/branching.do?method=assignBranch', now());

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description,
        learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url,
        export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (7, 11, 'Group Based Branching', 'Select between multiple sequence activities, with the branch chosen by an existing group.',
        'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching',
        'learning/branching.do?method=viewBranching&mode=teacher', NULL,
        'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/branching.do?method=assignBranch',
        'monitoring/branching.do?method=assignBranch', now());

INSERT INTO lams_system_tool (system_tool_id, learning_activity_type_id, tool_display_name, description,
        learner_url, learner_preview_url, learner_progress_url, export_pfolio_learner_url,
        export_pfolio_class_url, monitor_url, contribute_url, create_date_time)
VALUES (8, 12, 'Tool Output Based Branching', 'Select between multiple sequence activities, with the branch chosen on results of another activity.',
        'learning/branching.do?method=performBranching', 'learning/branching.do?method=performBranching',
        'learning/branching.do?method=viewBranching&mode=teacher', NULL, 
        'monitoring/branchingExportPortfolio?mode=teacher', 'monitoring/branching.do?method=assignBranch',
        'monitoring/branching.do?method=assignBranch', now());

-- support tools having an admin screen

ALTER TABLE lams_tool ADD COLUMN admin_url TEXT;
ALTER TABLE lams_system_tool ADD COLUMN admin_url TEXT;
