package com.github.jsonparser.util;

import com.github.jsonparser.exception.JsonParsingException;

import java.util.logging.Logger;

/**
 * Useful utilities to validate dependencies
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 17/02/2020
 */
public class ValidationUtil {

    public static final Logger log = Logger.getLogger(ValidationUtil.class.getName());

    private ValidationUtil() {
    }

    /**
     * <p>
     * Asserts that the specified parameter value is not <code>null</code> and if it
     * is, throws an <code>JsonParsingException</code> with the specified error
     * message.
     * </p>
     *
     * @param object    Object to assert on
     * @param fieldName Field name to display in exception message if null
     * @throws JsonParsingException
     */
    public static <T> void rejectNull(T object, String fieldName) throws JsonParsingException {
        if (object == null || (object instanceof String && object.toString().trim().length() == 0)) {
            String message = String.format("%s cannot be null or empty.", fieldName);
            log.severe(message);
            throw new JsonParsingException(message);
        }
    }

}
