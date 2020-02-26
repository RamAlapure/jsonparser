package com.github.jsonparser.util;

import com.github.jsonparser.exception.JsonParsingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author ralapure
 */
public class ValidationUtilTest {

    @Test
    public void exceptionTest() {
        assertThrows(JsonParsingException.class, () -> ValidationUtil.rejectNull(null, "field"));
        assertThrows(JsonParsingException.class, () -> ValidationUtil.rejectNull(" ", "field"));
    }

}
