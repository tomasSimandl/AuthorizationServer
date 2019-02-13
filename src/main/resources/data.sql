-- used in tests that use HSQL
drop table if exists oauth_client_details;
create table oauth_client_details (
  client_id VARCHAR(256) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(256)
);

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types,
web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, autoapprove)
VALUES (
        "my-client",
        "milky-way,authorization-manage",
        "{bcrypt}$2a$10$Hq/4xBuNd5uXwdzi2XPoruNgpStT.Arhc.r2KX6J2SR5Twfc/iBEu",
        "write,read",
        "refresh_token,password",
        NULL,
        "ROLE_CLIENT,ROLE_TRUSTED_CLIENT",
        10080,
        43200,
        "true");

drop table if exists oauth_client_token;
create table oauth_client_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256)
);

drop table if exists oauth_access_token;
create table oauth_access_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication BLOB,
  refresh_token VARCHAR(256)
);

drop table if exists oauth_refresh_token;
create table oauth_refresh_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication BLOB
);

drop table if exists oauth_code;
create table oauth_code (
  code VARCHAR(256), authentication BLOB
);

-- drop table if exists oauth_approvals;
-- create table oauth_approvals (
-- 	userId VARCHAR(256),
-- 	clientId VARCHAR(256),
-- 	scope VARCHAR(256),
-- 	status VARCHAR(10),
-- 	expiresAt TIMESTAMP,
-- 	lastModifiedAt TIMESTAMP
-- );
--
--
-- -- customized oauth_client_details table
-- drop table if exists ClientDetails;
-- create table ClientDetails (
--   appId VARCHAR(256) PRIMARY KEY,
--   resourceIds VARCHAR(256),
--   appSecret VARCHAR(256),
--   scope VARCHAR(256),
--   grantTypes VARCHAR(256),
--   redirectUrl VARCHAR(256),
--   authorities VARCHAR(256),
--   access_token_validity INTEGER,
--   refresh_token_validity INTEGER,
--   additionalInformation VARCHAR(4096),
--   autoApproveScopes VARCHAR(256)
-- );
--
-- drop table if exists  authorities;
--
-- create table authorities (
--   username varchar(50) Not null primary key,
--   authority varchar(50) not null
-- )