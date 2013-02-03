package net.glxn.qbe.reflection.exception;

public class ReflectionException extends RuntimeException {

    private static final long serialVersionUID = -4309252260252389963L;

    public ReflectionException(String message, Exception underlyingException) {
        super(message, underlyingException);
    }

    public ReflectionException(String message) {
        super(message);
    }
}
