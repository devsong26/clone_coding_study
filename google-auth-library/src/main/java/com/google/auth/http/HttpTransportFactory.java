package com.google.auth.http;

import com.google.api.client.http.HttpTransport;

public interface HttpTransportFactory {

    HttpTransport create();

}
