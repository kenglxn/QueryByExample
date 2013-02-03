package net.glxn.qbe.reflection;

public class ReflectionException extends RuntimeException {

    private static final long serialVersionUID = -4309252260252389963L;

    ReflectionException(String message, Exception underlyingException) {
        super(message, underlyingException);
    }

    ReflectionException(String message) {
        super(message);
    }
}
