package com.github.jsonparser.util;

import com.github.jsonparser.exception.JsonParsingException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Ram Alapure
 * @version 1.0
 * @since 17/02/2020
 */
public class ErrorUtilTest {

    @Test
    public void jsonParsingExceptionTest() {
        assertThrows(JsonParsingException.class, () -> ErrorUtil.jsonParsingException(ExceptionConstants.STR_IO_EXCEPTION,
                new IOException("IO exception occurred")));
    }

}
