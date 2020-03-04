package com.github.jsonparser;

import com.github.jsonparser.exception.JsonParsingException;
import com.github.jsonparser.reader.JsonReader;
import com.github.jsonparser.util.AppConstants;
import com.github.jsonparser.util.ValidationUtil;
import com.github.jsonparser.writer.CsvWriter;

import java.io.Writer;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class parse a Json document to csv format. The default separator is "_" and delimiter is ",".
 * The separator will be used to divide the json path.
 * e.g. if json path user/name then csv column will be user_name.
 * The JsonParser supports output in 3 different formats:
 * 1. String
 * 2. File
 * 3. Writer
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 17/02/2020
 */
public class JsonParser {

    public static final Logger log = Logger.getLogger(JsonParser.class.getName());

    private JsonParser() {
    }

    /**
     * This method process the json input string and returns a csv string.
     *
     * @param json - The input json string
     * @return Returns a csv string.
     * @throws JsonParsingException
     */
    public static String parse2Csv(String json) throws JsonParsingException {
        log.info("Received request to parse json to csv.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Sheet(json));
        return CsvWriter.write2String(records);
    }


    /**
     * This method process the json input string and writes to csv file path provided.
     *
     * @param json        - The input json string
     * @param csvFilePath - The output csv file path with name
     * @throws JsonParsingException
     */
    public static void parse2Csv(String json, String csvFilePath) throws JsonParsingException {
        ValidationUtil.rejectNull(csvFilePath, "csv file path");
        log.info("Received request to parse json to csv.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Sheet(json));
        CsvWriter.write2csv(records, csvFilePath);
    }

    /**
     * This method process the json input string and writes csv output to writer {@link Writer}.
     *
     * @param json   - The input json string
     * @param writer - The writer object to write the csv e.g. StringWriter, FileWriter, etc.
     * @throws JsonParsingException
     */
    public static void parse2Csv(String json, Writer writer) throws JsonParsingException {
        ValidationUtil.rejectNull(writer, "writer");
        log.info("Received request to parse json to csv.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Sheet(json));
        CsvWriter.write(records, writer);
    }

    /**
     * This method process the json input string and returns a csv string.
     *
     * @param json      - The input json string
     * @param separator - The custom header/column separator key
     * @param delimiter - The custom delimiter key
     * @return Returns string representation of csv.
     * @throws JsonParsingException
     */
    public static String parse2Csv(String json, String separator, String delimiter) throws JsonParsingException {
        if (separator == null) separator = AppConstants.DEFAULT_SEPARATOR;
        if (delimiter == null) delimiter = AppConstants.DEFAULT_DELIMITER;
        log.info("Received request to parse json to csv.");
        List<Object[]> records = JsonReader.parse(json, separator);
        return CsvWriter.write2String(records, delimiter);
    }

    /**
     * @param json        - The input json string
     * @param csvFilePath - The output csv file path with name
     * @param separator   - The custom header/column separator key
     * @param delimiter   - The custom delimiter key
     * @throws JsonParsingException
     */
    public static void parse2Csv(String json, String csvFilePath, String separator, String delimiter) throws JsonParsingException {
        ValidationUtil.rejectNull(csvFilePath, "csv file path");
        if (separator == null) separator = AppConstants.DEFAULT_SEPARATOR;
        if (delimiter == null) delimiter = AppConstants.DEFAULT_DELIMITER;
        log.info("Received request to parse json to csv.");
        List<Object[]> records = JsonReader.parse(json, separator);
        CsvWriter.write2csv(records, csvFilePath, delimiter);
    }

    /**
     * This method process the json input string and writes csv output to writer {@link Writer}.
     *
     * @param json      - The input json string
     * @param writer    - The writer object to write the csv e.g. StringWriter, FileWriter, etc.
     * @param separator - The custom header/column separator key
     * @param delimiter - The custom delimiter key
     * @throws JsonParsingException
     */
    public static void parse2Csv(String json, Writer writer, String separator, String delimiter) throws JsonParsingException {
        ValidationUtil.rejectNull(writer, "writer");
        if (separator == null) separator = AppConstants.DEFAULT_SEPARATOR;
        if (delimiter == null) delimiter = AppConstants.DEFAULT_DELIMITER;
        log.info("Received request to parse json to csv.");
        List<Object[]> records = JsonReader.parse(json, separator);
        CsvWriter.write(records, writer, delimiter);
    }

    /**
     * This method process the xsd json input string and returns a csv string with header/column row.
     *
     * @param json - The input xsd json string
     * @return Returns a csv string.
     * @throws JsonParsingException
     */
    public static String parseXsd2Csv(String json) throws JsonParsingException {
        log.info("Received request to parse xsd json string to csv.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Header(json));
        return CsvWriter.write2String(records);
    }

    /**
     * This method process the xsd json input string and writes to csv file path provided.
     *
     * @param json        - The input xsd json string
     * @param csvFilePath - The output csv file path with name
     * @throws JsonParsingException
     */
    public static void parseXsd2Csv(String json, String csvFilePath) throws JsonParsingException {
        ValidationUtil.rejectNull(csvFilePath, "csv file path");
        log.info("Received request to parse xsd json string to csv.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Header(json));
        CsvWriter.write2csv(records, csvFilePath);
    }

    /**
     * This method process the xsd json input string and writes csv output to writer {@link Writer}.
     *
     * @param json   - The input xsd json string
     * @param writer - The writer object to write the csv e.g. StringWriter, FileWriter, etc.
     * @throws JsonParsingException
     */
    public static void parseXsd2Csv(String json, Writer writer) throws JsonParsingException {
        ValidationUtil.rejectNull(writer, "writer");
        log.info("Received request to parse xsd json string to csv.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Header(json));
        CsvWriter.write(records, writer);
    }

    /**
     * This method process the input json and used the column/header from xsd schema json string.
     *
     * @param json      - The input json string
     * @param xsdSchema - The xsd schema json string
     * @return Returns string representation of csv.
     * @throws JsonParsingException
     */
    public static String parse2CsvWithXsd(String json, String xsdSchema) throws JsonParsingException {
        ValidationUtil.rejectNull(xsdSchema, "xsd schema");
        log.info("Received request to parse json string to csv with xsd.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Sheet(json, xsdSchema));
        return CsvWriter.write2String(records);
    }

    /**
     * This method process the input json and used the column/header from xsd schema json string to create csv.
     *
     * @param json        - The input json string
     * @param xsdSchema   - The xsd schema json string
     * @param csvFilePath - The output csv file path with name
     * @throws JsonParsingException
     */
    public static void parse2CsvWithXsd(String json, String xsdSchema, String csvFilePath) throws JsonParsingException {
        ValidationUtil.rejectNull(xsdSchema, "xsd schema");
        log.info("Received request to parse json string to csv with xsd.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Sheet(json, xsdSchema));
        CsvWriter.write2csv(records, csvFilePath);
    }

    /**
     * This method process the input json and used the column/header from xsd schema json string to create csv.
     *
     * @param json      - The input json string
     * @param xsdSchema - The xsd schema json string
     * @param writer    - The writer object to write the csv e.g. StringWriter, FileWriter, etc.
     * @throws JsonParsingException
     */
    public static void parse2CsvWithXsd(String json, String xsdSchema, Writer writer) throws JsonParsingException {
        ValidationUtil.rejectNull(xsdSchema, "xsd schema");
        ValidationUtil.rejectNull(writer, "writer");
        log.info("Received request to parse json string to csv with xsd.");
        List<Object[]> records = JsonReader.parse(json, JsonReader.json2Sheet(json, xsdSchema));
        CsvWriter.write(records, writer);
    }

}