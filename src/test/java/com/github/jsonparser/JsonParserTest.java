package com.github.jsonparser;

import com.github.jsonparser.exception.JsonParsingException;
import com.github.jsonparser.util.TestConstants;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author Ram Alapure
 * @version 1.0
 * @since 17/02/2020
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonParserTest {

    String json;

    @BeforeAll
    public void setup() throws IOException {
        File file = new File(new File(TestConstants.LOCATION).getAbsolutePath() + TestConstants.CHAR_FORWARD_SLASH
                + TestConstants.JSON_SAMPLE);
        json = FileUtils.readFileToString(file);
    }

    @Test
    void parse2Csv() throws JsonParsingException {
        String csv = JsonParser.parse2Csv(json);
        System.out.println(csv);
    }

    @Test
    void parse2CsvWithWriter() throws JsonParsingException {
        StringWriter writer = new StringWriter();
        JsonParser.parse2Csv(json, writer);
        System.out.println(writer.toString());
    }

    @Test
    void parse2CsvFile() throws JsonParsingException {
        String csvFilePath = new File(TestConstants.LOCATION).getAbsolutePath() + TestConstants.CHAR_FORWARD_SLASH
                + "test.csv";
        JsonParser.parse2Csv(json, csvFilePath);
    }

    @Test
    void parse2CsvWithSeparatorAndDelimiter() throws JsonParsingException {
        System.out.println(JsonParser.parse2Csv(json, "/", ","));
    }

    @Test
    void parseWithXsd() {
    }

    @Test
    void parseWithXsd1() {
    }
}