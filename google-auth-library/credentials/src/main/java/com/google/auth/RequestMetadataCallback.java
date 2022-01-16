package com.google.auth;

import java.util.List;
import java.util.Map;

public interface RequestMetadataCallback {

    void onSuccess(Map<String, List<String>> metadata);

    void onFailure(Throwable exception);

}
