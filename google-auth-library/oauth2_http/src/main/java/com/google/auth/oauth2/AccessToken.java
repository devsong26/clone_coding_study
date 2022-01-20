package com.google.auth.oauth2;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class AccessToken implements Serializable {

    private static final long serialVersionUID = -8514239465808977353L;

    private final String tokenValue;
    private final Long expirationTimeMillis;

    public AccessToken(String tokenValue, Date expirationTime){
        this.tokenValue = tokenValue;
        this.expirationTimeMillis = (expirationTime == null) ? null : expirationTime.getTime();
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public Date getExpirationTime() {
        if (expirationTimeMillis == null){
            return null;
        }
        return new Date(expirationTimeMillis);
    }

    Long getExpirationTimeMillis() {
        return expirationTimeMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenValue, expirationTimeMillis);
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("tokenValue", tokenValue)
                .add("expirationTimeMillis", expirationTimeMillis)
                .toString();
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof AccessToken)){
            return false;
        }
        AccessToken other = (AccessToken) obj;
        return Objects.equals(this.tokenValue, other.tokenValue)
            && Objects.equals(this.expirationTimeMillis, other.expirationTimeMillis);
    }

}
