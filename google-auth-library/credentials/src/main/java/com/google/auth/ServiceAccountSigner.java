package com.google.auth;

import java.util.Objects;

public interface ServiceAccountSigner {

    class SigningException extends RuntimeException {

        private static final long serialVersionUID = -6503954300538947223L;

        public SigningException(String message, Exception cause) {
            super(message, cause);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if(!(obj instanceof SigningException)){
                return false;
            }

            SigningException other = (SigningException) obj;
            return Objects.equals(getCause(), other.getCause())
                && Objects.equals(getMessage(), other.getMessage());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getMessage(), getCause());
        }

    }

    String getAccount();

    byte[] sign(byte[] toSign);

}
