package com.google.auth.oauth2;

import static com.google.common.base.Preconditions.checkNotNull;

final class ActingParty {
    private final String actorToken;
    private final String actorTokenType;

    ActingParty(String actorToken, String actorTokenType){
        this.actorToken = checkNotNull(actorToken);
        this.actorTokenType = checkNotNull(actorTokenType);
    }

    String getActorToken(){
        return actorToken;
    }

    String getActorTokenType(){
        return actorTokenType;
    }

}
