package com.github.jsonparser.util;


import com.github.jsonparser.exception.JsonParsingException;

import java.util.logging.Logger;

/**
 * @author Ram Alapure
 * @version 1.0
 * @since 17/02/2020
 */
public final class ErrorUtil {

    public static final Logger log = Logger.getLogger(ErrorUtil.class.getName());

    private ErrorUtil() {
    }

    public static void jsonParsingException(String message, Exception e) throws JsonParsingException {
        log.severe(String.format("%sCause: %s%s", message, AppConstants.STR_BRACES, e.getCause()));
        throw new JsonParsingException(message);
    }

    public static void jsonParsingException(String message) throws JsonParsingException {
        log.severe(message);
        throw new JsonParsingException(message);
    }

}
