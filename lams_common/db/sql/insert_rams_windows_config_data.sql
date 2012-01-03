insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('ServerURL','http://localhost:8080/rams/', 'config.server.url', 'config.header.system', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('ServerURLContextPath','rams/', 'config.server.url.context.path', 'config.header.system', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('Version','1.1', 'config.version', 'config.header.system', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('TempDir','C:/rams/temp', 'config.temp.dir', 'config.header.system', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('DumpDir','C:/rams/dump', 'config.dump.dir', 'config.header.system', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('EARDir','C:/jboss-5/server/default/deploy/rams.ear', 'config.ear.dir', 'config.header.system', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('InternalSMTPServer','true', 'config.use.internal.smtp.server', 'config.header.email', 'BOOLEAN', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('SMTPServer','', 'config.smtp.server', 'config.header.email', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LamsSupportEmail','', 'config.lams.support.email', 'config.header.email', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('ContentRepositoryPath','C:/rams/repository', 'config.content.repository.path', 'config.header.uploads', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('UploadFileMaxSize','1048576', 'config.upload.file.max.size', 'config.header.uploads', 'LONG', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('UploadLargeFileMaxSize','10485760', 'config.upload.large.file.max.size', 'config.header.uploads', 'LONG', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('UploadFileMaxMemorySize','4096', 'config.upload.file.max.memory.size', 'config.header.uploads', 'LONG', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('ExecutableExtensions','.bat,.bin,.com,.cmd,.exe,.msi,.msp,.ocx,.pif,.scr,.sct,.sh,.shs,.vbs', 'config.executable.extensions', 'config.header.uploads', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('UserInactiveTimeout','86400', 'config.user.inactive.timeout', 'config.header.system', 'LONG', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('UseCacheDebugListener','false', 'config.use.cache.debug.listener', 'config.header.system', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('CleanupPreviewOlderThanDays','7', 'config.cleanup.preview.older.than.days', 'config.header.system', 'LONG', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('AuthoringActivitiesColour', 'true', 'config.authoring.activities.colour', 'config.header.look.feel', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('AuthoringClientVersion','1.1.0.@datetimestamp@', 'config.authoring.client.version', 'config.header.versions', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('MonitorClientVersion','1.1.0.@datetimestamp@', 'config.monitor.client.version', 'config.header.versions', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LearnerClientVersion','1.1.0.@datetimestamp@', 'config.learner.client.version', 'config.header.versions', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('ServerVersionNumber','1.1.0.@datetimestamp@', 'config.server.version.number', 'config.header.versions', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('ServerLanguage','en_AU', 'config.server.language', 'config.header.look.feel', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('ServerPageDirection','LTR', 'config.server.page.direction', 'config.header.look.feel', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('DictionaryDateCreated','2007-05-24', 'config.dictionary.date.created', 'config.header.versions', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('HelpURL','http://wiki.lamsfoundation.org/display/lamsdocs/', 'config.help.url', 'config.header.system', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('XmppDomain','shaun.melcoe.mq.edu.au', 'config.xmpp.domain', 'config.header.chat', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('XmppConference','conference.shaun.melcoe.mq.edu.au', 'config.xmpp.conference', 'config.header.chat', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('XmppAdmin','admin', 'config.xmpp.admin', 'config.header.chat', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('XmppPassword','wildfire', 'config.xmpp.password', 'config.header.chat', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('DefaultFlashTheme','rams', 'config.default.flash.theme', 'config.header.look.feel', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('DefaultHTMLTheme','ramsthemeHTML', 'config.default.html.theme', 'config.header.look.feel', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('AllowDirectLessonLaunch','false', 'config.allow.direct.lesson.launch', 'config.header.features', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LAMS_Community_enable','false', 'config.community.enable', 'config.header.features', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('AllowLiveEdit','true', 'config.allow.live.edit', 'config.header.features', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPProvisioningEnabled','false', 'config.ldap.provisioning.enabled', 'config.header.ldap', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPProviderURL','ldap://192.168.111.15', 'config.ldap.provider.url', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPSecurityAuthentication','simple', 'config.ldap.security.authentication', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPSearchFilter','(cn={0})', 'config.ldap.search.filter', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPBaseDN',',ou=Users,dc=melcoe,dc=mq,dc=edu,dc=au', 'config.ldap.base.dn', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPBindUserDN','', 'config.ldap.bind.user.dn', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPBindUserPassword','', 'config.ldap.bind.user.password', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPSecurityProtocol','', 'config.ldap.security.protocol', 'config.header.ldap', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('TruststorePath','', 'config.ldap.truststore.path', 'config.header.system', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('TruststorePassword','', 'config.ldap.truststore.password', 'config.header.system', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPLoginAttr','uid', 'admin.user.login', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPFNameAttr','givenName', 'admin.user.first_name', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPLNameAttr','sn', 'admin.user.last_name', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPEmailAttr','mail', 'admin.user.email', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPAddr1Attr','postalAddress', 'admin.user.address_line_1', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPAddr2Attr','', 'admin.user.address_line_2', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPAddr3Attr','', 'admin.user.address_line_3', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPCityAttr','l', 'admin.user.city', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPStateAttr','st', 'admin.user.state', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPPostcodeAttr','postalCode', 'admin.user.postcode', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPCountryAttr','', 'admin.user.country', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPDayPhoneAttr','telephoneNumber', 'admin.user.day_phone', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPEveningPhoneAttr','homePhone', 'admin.user.evening_phone', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPFaxAttr','facsimileTelephoneNumber', 'admin.user.fax', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPMobileAttr','mobile', 'admin.user.mobile_phone', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPLocaleAttr','preferredLanguage', 'admin.organisation.locale', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPDisabledAttr','!accountStatus', 'sysadmin.disabled', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPOrgAttr','schoolCode', 'admin.course', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPRolesAttr','memberOf', 'admin.user.roles', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPLearnerMap','Student;SchoolSupportStaff;Teacher;SeniorStaff;Principal', 'config.ldap.learner.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPMonitorMap','SchoolSupportStaff;Teacher;SeniorStaff;Principal', 'config.ldap.monitor.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPAuthorMap','Teacher;SeniorStaff;Principal', 'config.ldap.author.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPGroupAdminMap','Teacher;SeniorStaff', 'config.ldap.group.admin.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPGroupManagerMap','Principal', 'config.ldap.group.manager.map', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPUpdateOnLogin', 'true', 'config.ldap.update.on.login', 'config.header.ldap', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPOrgField', 'code', 'config.ldap.org.field', 'config.header.ldap.attributes', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPOnlyOneOrg', 'true', 'config.ldap.only.one.org', 'config.header.ldap', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPEncryptPasswordFromBrowser', 'true', 'config.ldap.encrypt.password.from.browser', 'config.header.ldap', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LDAPSearchResultsPageSize', '100', 'config.ldap.search.results.page.size', 'config.header.ldap', 'LONG', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LearnerProgressBatchSize', '10', 'config.learner.progress.batch.size', 'config.header.look.feel', 'LONG', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('CustomTabLink','', 'config.custom.tab.link', 'config.header.look.feel', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('CustomTabTitle','', 'config.custom.tab.title', 'config.header.look.feel', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('EnableFlash','true', 'config.flash.enable', 'config.header.features', 'BOOLEAN', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('AuthoringScreenSize','1024x768', 'config.authoring.screen.size', 'config.header.look.feel', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('MonitorScreenSize','1024x768', 'config.monitor.screen.size', 'config.header.look.feel', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('LearnerScreenSize','1024x768', 'config.learner.screen.size', 'config.header.look.feel', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('AdminScreenSize','1024x768', 'config.admin.screen.size', 'config.header.look.feel', 'STRING', 1);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('GmapKey','', 'config.gmap.gmapkey', 'config.gmap.section.title', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('KalturaServer','', 'config.kaltura.server', 'config.header.kaltura', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('KalturaPartnerId','', 'config.kaltura.partner.id', 'config.header.kaltura', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('KalturaSubPartnerId','', 'config.kaltura.sub.partner.id', 'config.header.kaltura', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('KalturaUserSecret','', 'config.kaltura.user.secret', 'config.header.kaltura', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('KalturaKCWUiConfId','', 'config.kaltura.kcw.uiconfid', 'config.header.kaltura', 'STRING', 0);

insert into lams_configuration (config_key, config_value, description_key, header_name, format, required) 
values ('KalturaKDPUiConfId','', 'config.kaltura.kdp.uiconfid', 'config.header.kaltura', 'STRING', 0);
