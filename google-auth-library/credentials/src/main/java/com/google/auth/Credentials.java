package com.google.auth;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public abstract class Credentials implements Serializable {

    private static final long serialVersionUID = 808575179767517313L;

    public abstract String getAuthenticationType();

    public Map<String, List<String>> getRequestMetadata() throws IOException {
        return getRequestMetadata(null);
    }

    public void getRequestMetadata(
            final URI uri,
            Executor executor,
            final RequestMetadataCallback callback){
        executor.execute(
            new Runnable() {
                @Override
                public void run(){
                    blockingGetToCallback(uri, callback);
                }
            });
    }

    protected final void blockingGetToCallback(URI uri, RequestMetadataCallback callback) {
        Map<String, List<String>> result;

        try{
            result = getRequestMetadata(uri);
        }catch(Throwable e){
            callback.onFailure(e);
            return;
        }
        callback.onSuccess(result);
    }

    public abstract Map<String, List<String>> getRequestMetadata(URI uri) throws IOException;

    public abstract boolean hasRequestMetadata();

    public abstract boolean hasRequestMetadataOnly();

    public abstract void refresh() throws IOException;

}
