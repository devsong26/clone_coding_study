package com.google.auth;

import org.junit.jupiter.api.Test;
import com.google.auth.ServiceAccountSigner.SigningException;

import static org.junit.jupiter.api.Assertions.*;

public class SigningExceptionTest {

    private static final String EXPECTED_MESSAGE = "message";
    private static final RuntimeException EXPECTED_CAUSE = new RuntimeException();

    @Test
    void constructor(){
        SigningException signingException = new SigningException(EXPECTED_MESSAGE, EXPECTED_CAUSE);
        assertEquals(EXPECTED_MESSAGE, signingException.getMessage());
        assertEquals(EXPECTED_CAUSE, signingException.getCause());
    }

    @Test
    void equals_true(){
        SigningException signingException = new SigningException(EXPECTED_MESSAGE, EXPECTED_CAUSE);
        SigningException otherSigningException = new SigningException(EXPECTED_MESSAGE, EXPECTED_CAUSE);
        assertTrue(signingException.equals(otherSigningException));
        assertTrue(otherSigningException.equals(signingException));
    }

    @Test
    void equals_false_cause(){
        SigningException signingException = new SigningException(EXPECTED_MESSAGE, EXPECTED_CAUSE);
        SigningException otherSigningException =
                new SigningException("otherMessage", new RuntimeException());
        assertFalse(signingException.equals(otherSigningException));
        assertFalse(otherSigningException.equals(signingException));
    }

    @Test
    void hashCode_equals(){
        SigningException signingException = new SigningException(EXPECTED_MESSAGE, EXPECTED_CAUSE);
        SigningException otherSigningException = new SigningException(EXPECTED_MESSAGE, EXPECTED_CAUSE);
        assertEquals(signingException.hashCode(), otherSigningException.hashCode());
    }

}
