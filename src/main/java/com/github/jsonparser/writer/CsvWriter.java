package com.github.jsonparser.writer;

import com.github.jsonparser.exception.JsonParsingException;
import com.github.jsonparser.util.AppConstants;
import com.github.jsonparser.util.ErrorUtil;
import com.github.jsonparser.util.ExceptionConstants;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class parse a Json document.
 * The JsonParser supports output in 3 different formats:
 * 1. String
 * 2. File
 * 3. Writer
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 04/03/2020
 */
public class CsvWriter {

    public static final Logger log = Logger.getLogger(CsvWriter.class.getName());

    private CsvWriter() {
    }

    /**
     * This method writes the 2D representation in csv format with ',' as
     * default delimiter.
     *
     * @param records     - The list of processed csv records
     * @param destination - It takes the destination path for the csv file.
     */
    public static void write2csv(List<Object[]> records, String destination) throws JsonParsingException {
        write2csv(records, destination, AppConstants.DEFAULT_DELIMITER);
    }

    /**
     * This method writes the 2D representation in csv format with ',' as
     * default delimiter.
     *
     * @param records - The list of processed csv records
     * @return Returns string representation of csv.
     */
    public static String write2String(List<Object[]> records) throws JsonParsingException {
        return write2String(records, AppConstants.DEFAULT_DELIMITER);
    }

    /**
     * This method writes the 2D representation in csv format with custom
     * delimiter set by user.
     *
     * @param records     - The list of processed csv records
     * @param destination - It takes the destination path for the csv file.
     * @param delimiter   - It represents the delimiter set by user.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static void write2csv(List<Object[]> records, String destination, String delimiter) throws JsonParsingException {
        log.info(String.format("Writing csv records to file : %s, with delimiter \"%s\"", destination, delimiter));
        try {
            PrintWriter writer = new PrintWriter(new File(destination), Charset.defaultCharset().name());
            write(records, writer, delimiter);
            writer.close();
        } catch (FileNotFoundException e) {
            ErrorUtil.jsonParsingException(String.format(ExceptionConstants.STR_FILE_EXCEPTION, destination), e);
        } catch (UnsupportedEncodingException e) {
            ErrorUtil.jsonParsingException(ExceptionConstants.STR_PARSING_EXCEPTION, e);
        }
        log.info(String.format("The csv records written in file : %s successfully.", destination));
    }

    /**
     * @param records   - The list of processed csv records
     * @param delimiter - It represents the delimiter set by user.
     * @return Returns string representation of csv.
     * @throws JsonParsingException
     */
    public static String write2String(List<Object[]> records, String delimiter) throws JsonParsingException {
        StringWriter writer = new StringWriter();
        try {
            write(records, writer, delimiter);
            writer.close();
        } catch (IOException e) {
            ErrorUtil.jsonParsingException(ExceptionConstants.STR_IO_EXCEPTION, e);
        }
        return writer.toString();
    }

    /**
     * @param records - The list of processed csv records
     * @param writer  - The writer object to write the csv e.g. StringWriter, FileWriter, etc.
     * @throws JsonParsingException
     */
    public static void write(List<Object[]> records, Writer writer) throws JsonParsingException {
        write(records, writer, AppConstants.DEFAULT_DELIMITER);
    }

    /**
     * @param records   - The list of processed csv records
     * @param writer    - The writer object to write the csv e.g. StringWriter, FileWriter, etc.
     * @param delimiter - It represents the delimiter set by user.
     * @throws JsonParsingException
     */
    public static void write(List<Object[]> records, Writer writer, String delimiter) throws JsonParsingException {
        log.info(String.format("Writing records as csv representation with delimiter \"%s\"", delimiter));
        try {
            for (Object[] data : records) {
                boolean comma = false;
                for (Object text : data) {
                    String str;
                    if (text == null) str = comma ? delimiter : "";
                    else str = comma ? delimiter + text.toString() : text.toString();
                    writer.write(str);
                    if (!comma) comma = true;
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            ErrorUtil.jsonParsingException(ExceptionConstants.STR_IO_EXCEPTION, e);
        }
        log.info("The records written as csv successfully.");
    }
}
