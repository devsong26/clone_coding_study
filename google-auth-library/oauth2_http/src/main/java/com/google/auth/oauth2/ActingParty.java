package com.google.auth.oauth2;

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
