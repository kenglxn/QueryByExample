package net.glxn.qbe.reflection;

public class ObjectInstantiationException extends RuntimeException {

    private static final long serialVersionUID = -4309252260252389963L;

    ObjectInstantiationException(String message, Exception underlyingException) {
        super(message, underlyingException);
    }

    ObjectInstantiationException(String message) {
        super(message);
    }
}
