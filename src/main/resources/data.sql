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


INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types,
                                  web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, autoapprove)
VALUES (
         "rest-client",
         "rest-client",
         "{bcrypt}$2a$10$Hq/4xBuNd5uXwdzi2XPoruNgpStT.Arhc.r2KX6J2SR5Twfc/iBEu",
         "write,read",
         "refresh_token,client_credentials",
         NULL,
         "ROLE_CLIENT,ROLE_TRUSTED_CLIENT",
         3600,
         43200,
         "true");

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types,
                                  web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, autoapprove)
VALUES (
         "mobile-app-client",
         "mobile-app-client",
         "{bcrypt}$2a$10$Hq/4xBuNd5uXwdzi2XPoruNgpStT.Arhc.r2KX6J2SR5Twfc/iBEu",
         "write,read",
         "refresh_token,password",
         NULL,
         "ROLE_CLIENT,ROLE_TRUSTED_CLIENT,ROLE_USER_REGISTRATION_CLIENT",
         3600,
         15734800,
         "true");

INSERT INTO oauth_client_details (client_id, resource_ids, client_secret, scope, authorized_grant_types,
                                  web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, autoapprove)
VALUES (
         "create-client",
         "authorization-manage",
         "{bcrypt}$2a$10$Hq/4xBuNd5uXwdzi2XPoruNgpStT.Arhc.r2KX6J2SR5Twfc/iBEu",
         "write,read",
         "refresh_token,password,client_credentials",
         NULL,
         "ROLE_USER_REGISTRATION_CLIENT",
         10080,
         43200,
         "true");