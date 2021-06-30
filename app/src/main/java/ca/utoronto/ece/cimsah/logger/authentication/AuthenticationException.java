package ca.utoronto.ece.cimsah.logger.authentication;

import java.lang.Exception;

/**
 * Created by dandm on 2016-11-25.
 */

public class AuthenticationException extends Exception {
    public AuthenticationException() { super(); }
    public AuthenticationException(String message) { super(message); }
    public AuthenticationException(String message, Throwable cause) { super(message, cause); }
    public AuthenticationException(Throwable cause) { super(cause); }
}
