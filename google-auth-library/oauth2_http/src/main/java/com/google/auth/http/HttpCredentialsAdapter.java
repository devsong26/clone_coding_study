package com.google.auth.http;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import com.google.api.client.util.Preconditions;
import com.google.auth.Credentials;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class HttpCredentialsAdapter
        implements HttpRequestInitializer, HttpUnsuccessfulResponseHandler {

    private static final Logger LOGGER = Logger.getLogger(HttpCredentialsAdapter.class.getName());

    private static final Pattern INVALID_TOKEN_ERROR =
            Pattern.compile("\\s*error\\s*=\\s*\"?invalid_token\"?");

    private final Credentials credentials;

    public HttpCredentialsAdapter(Credentials credentials){
        Preconditions.checkNotNull(credentials);
        this.credentials = credentials;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public void initialize(HttpRequest request) throws IOException {
        request.setUnsuccessfulResponseHandler(this);

        if( !credentials.hasRequestMetaData() ){
            return;
        }
        HttpHeaders requestHeaders = request.getHeaders();
        URI uri = null;
        if ( request.getUrl() != null ){
            uri = request.getUrl().toURI();
        }
        Map<String, List<String>> credentialsHeaders = credentials.getRequestMetadata(uri);
        if ( credentialsHeaders == null ) {
            return;
        }
        for ( Map.Entry<String, List<String>> entry : credentialsHeaders.entrySet() ){
            String headerName = entry.getKey();
            List<String> requestValues = new ArrayList<>();
            requestValues.addAll(entry.getValue());
            requestHeaders.put(headerName, requestValues);
        }
    }

    @Override
    public boolean handleResponse(HttpRequest request, HttpResponse response, boolean supportsRetry){
        boolean refreshToken = false;
        boolean bearer = false;

        List<String> authenticationList = response.getHeaders().getAuthenticateAsList();

        if (authenticationList != null) {
            for(String authenticate : authenticationList){
                if(authenticate.startsWith(InternalAuthHttpConstants.BEARER_PREFIX)){
                    bearer = true;
                    refreshToken = INVALID_TOKEN_ERROR.matcher(authenticate).find();
                    break;
                }
            }
        }

        if(!bearer){
            refreshToken = response.getStatusCode() == HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
        }

        if(refreshToken){
            try{
                credentials.refresh();
                initialize(request);
                return true;
            } catch (IOException exception) {
                LOGGER.log(Level.SEVERE, "unable to refresh token", exception);
            }
        }
        return false;
    }

}
