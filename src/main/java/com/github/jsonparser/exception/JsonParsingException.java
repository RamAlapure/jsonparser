package com.github.jsonparser.exception;

/**
 * Signals that a json parsing exception of some sort has occurred.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 17/02/2020
 */
public class JsonParsingException extends Exception {

    private static final long serialVersionUID = 3009370493293321348L;

    /**
     * Constructs an {@code JsonParsingException} with the specified detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method)
     */
    public JsonParsingException(String message) {
        super(message);
    }

}
