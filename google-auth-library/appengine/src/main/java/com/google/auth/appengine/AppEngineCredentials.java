package com.google.auth.appengine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

public class AppEngineCredentials extends GoogleCredentials implements ServiceAccountSigner {

    private static final long serialVersionUID = -2627708355455064660L;

    private final String appIdentityServiceClassName;
    private final Collection<String> scopes;
    private final boolean scopesRequired;

    private transient AppIdentityService appIdentityService;

    private AppEngineCredentials(
            Collection<String> scopes,
            AppIdentityService appIdentityService){
        this.scopes = scopes == null ? ImmutableSet.<String>of() : ImmutableList.copyOf(scopes);
        this.appIdentityService =
                appIdentityService != null
                    ? appIdentityService
                    : AppIdentityServiceFactory.getAppIdentityService();
        this.appIdentityServiceClassName = this.appIdentityService.getClass().getName();
        scopesRequired = this.scopes.isEmpty();
    }

    @Override
    public AccessToken refreshAccessToken() throws IOException {
        if(createScopedRequired()){
            throw new IOException("AppEngineCredentials requires createScoped call before use.");
        }

        GetAccessTokenResult accessTokenResponse = appIdentityService.getAccessToken(scopes);
        String accessToken = accessTokenResponse.getAccessToken();
        Date expirationTime = accessTokenResponse.getExpirationTime();
        return new AccessToken(accessToken, expirationTime);
    }

    @Override
    public boolean createScopedRequired() {
        return scopesRequired;
    }

    @Override
    public GoogleCredentials createScoped(Collection<String> scopes) {
        return new AppEngineCredentials(scopes, appIdentityService);
    }

    @Override
    public String getAccount(){
        return appIdentityService.getServiceAccountName();
    }

    @Override
    public byte[] sign(byte[] toSign) {
        return appIdentityService.signForApp(toSign).getSignature();
    }

    @Override
    public int hashCode() {
        return Objects.hash(scopes, scopesRequired, appIdentityServiceClassName);
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("scopes", scopes)
                .add("scopeRequired", scopesRequired)
                .add("appIdentityServiceClassName", appIdentityServiceClassName)
                .toString();
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof AppEngineCredentials)){
            return false;
        }
        AppEngineCredentials other = (AppEngineCredentials) obj;
        return this.scopesRequired == other.scopesRequired
                && Objects.equals(this.scopes, other.scopes)
                && Objects.equals(this.appIdentityServiceClassName, other.appIdentityServiceClassName);
    }

    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        appIdentityService = newInstance(appIdentityServiceClassName);
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public Builder toBuilder(){
        return new Builder(this);
    }

    public static class Builder extends GoogleCredentials.Builder{

        private Collection<String> scopes;
        private AppIdentityService appIdentityService;

        protected Builder() {}

        protected Builder(AppEngineCredentials credentials) {
            this.scopes = credentials.scopes;
            this.appIdentityService = credentials.appIdentityService;
        }

        public Builder setScopes(Collection<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder setAppIdentityService(AppIdentityService appIdentityService) {
            this.appIdentityService = appIdentityService;
            return this;
        }

        public Collection<String> getScopes() {
            return scopes;
        }

        public AppIdentityService getAppIdentityService() {
            return appIdentityService;
        }

        public AppEngineCredentials build() {
            return new AppEngineCredentials(scopes, appIdentityService);
        }

    }

}
