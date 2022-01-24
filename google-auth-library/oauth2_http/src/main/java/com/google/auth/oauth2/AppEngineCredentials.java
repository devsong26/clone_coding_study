package com.google.auth.oauth2;

public class AppEngineCredentials extends GoogleCredentials implements ServiceAccountSigner {

    private static final long serialVersionUID = -493219027336622194L;

    static final String APP_IDENTITY_SERVICE_FACTORY_CLASS =
            "com.google.appengine.api.appidentity.AppIdentityServiceFactory";
    static final String APP_IDENTITY_SERVICE_CLASS =
            "com.google.appengine.api.appidentity.AppIdentityService";
    static final String GET_ACCESS_TOKEN_RESULT_CLASS =
            "com.google.appengine.api.appidentity.AppIdentityService$GetAccessTokenResult";

}
