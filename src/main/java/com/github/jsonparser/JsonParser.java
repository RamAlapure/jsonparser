package com.github.jsonparser;

import com.github.jsonparser.exception.JsonParsingException;
import com.github.jsonparser.model.JsonOrder;
import com.github.jsonparser.util.AppConstants;
import com.github.jsonparser.util.ErrorUtil;
import com.github.jsonparser.util.ExceptionConstants;
import com.github.jsonparser.util.ValidationUtil;
import com.google.gson.JsonElement;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    /**
     * This method process the json input string and returns a csv string.
     *
     * @param json - Input json string
     * @return Returns a csv string.
     * @throws JsonParsingException
     */
    public static String parse2Csv(String json) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        List<Object[]> records = json2Sheet(json);
        headerSeparator(records);
        return write2String(records);
    }

    /**
     * This method process the json input string and writes to csv file path provided.
     *
     * @param json
     * @param csvFilePath
     * @throws JsonParsingException
     */
    public static void parse2Csv(String json, String csvFilePath) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        ValidationUtil.rejectNull(csvFilePath, "csv file path");
        List<Object[]> records = json2Sheet(json);
        headerSeparator(records);
        write2csv(records, csvFilePath);
    }

    /**
     * This method process the json input string and writes csv output to writer {@link Writer}.
     *
     * @param json
     * @param writer
     * @throws JsonParsingException
     */
    public static void parse2Csv(String json, Writer writer) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        ValidationUtil.rejectNull(writer, "writer");
        List<Object[]> records = json2Sheet(json);
        headerSeparator(records);
        write(records, writer);
    }

    /**
     * This method process the json input string and returns a csv string.
     *
     * @param json      - The input json
     * @param separator - The custom header/column separator key
     * @param delimiter - The custom delimiter key
     * @return
     * @throws JsonParsingException
     */
    public static String parse2Csv(String json, String separator, String delimiter) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        if (separator == null) separator = AppConstants.DEFAULT_SEPARATOR;
        if (delimiter == null) delimiter = AppConstants.DEFAULT_DELIMITER;
        List<Object[]> records = json2Sheet(json);
        headerSeparator(records, separator);
        return write2String(records, delimiter);
    }

    /**
     * @param json        - The input json
     * @param csvFilePath - The output csv file path with name
     * @param separator   - The custom header/column separator key
     * @param delimiter   - The custom delimiter key
     * @throws JsonParsingException
     */
    public void parse2Csv(String json, String csvFilePath, String separator, String delimiter) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        ValidationUtil.rejectNull(csvFilePath, "csv file path");
        if (separator == null) separator = AppConstants.DEFAULT_SEPARATOR;
        if (delimiter == null) delimiter = AppConstants.DEFAULT_DELIMITER;
        List<Object[]> records = json2Sheet(json);
        headerSeparator(records, separator);
        write2csv(records, csvFilePath, delimiter);
    }

    /**
     * This method process the json input string and writes csv output to writer {@link Writer}.
     *
     * @param json      - The input json
     * @param writer    - The writer object to write the csv e.g. StringWriter, FileWriter, etc.
     * @param separator - The custom header/column separator key
     * @param delimiter - The custom delimiter key
     * @throws JsonParsingException
     */
    public void parse2Csv(String json, Writer writer, String separator, String delimiter) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        ValidationUtil.rejectNull(writer, "writer");
        if (separator == null) separator = AppConstants.DEFAULT_SEPARATOR;
        if (delimiter == null) delimiter = AppConstants.DEFAULT_DELIMITER;
        List<Object[]> records = json2Sheet(json);
        headerSeparator(records, separator);
        write(records, writer, delimiter);
    }

    /**
     * This method process the input json and used the column/header from xsd schema json string.
     *
     * @param json      - The input json
     * @param xsdSchema - The xsd schema json string
     * @return Returns a string representation of csv.
     * @throws JsonParsingException
     */
    public String parse2CsvWithXsd(String json, String xsdSchema) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        ValidationUtil.rejectNull(xsdSchema, "xsd schema");
        List<Object[]> records = json2Sheet(json, xsdSchema);
        headerSeparator(records);
        return write2String(records);
    }

    /**
     * This method process the input json and used the column/header from xsd schema json string to create csv.
     *
     * @param json        - The input json
     * @param xsdSchema   - The xsd schema json string
     * @param csvFilePath - The output csv file path with name
     * @throws JsonParsingException
     */
    public void parse2CsvWithXsd(String json, String xsdSchema, String csvFilePath) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        ValidationUtil.rejectNull(xsdSchema, "xsd schema");
        List<Object[]> records = json2Sheet(json, xsdSchema);
        headerSeparator(records);
        write2csv(records, csvFilePath);
    }

    /**
     * This method process the input json and used the column/header from xsd schema json string to create csv.
     *
     * @param json      - The input json
     * @param xsdSchema - The xsd schema json string
     * @param writer    - The writer object to write the csv e.g. StringWriter, FileWriter, etc.
     * @throws JsonParsingException
     */
    public void parse2CsvWithXsd(String json, String xsdSchema, Writer writer) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        ValidationUtil.rejectNull(xsdSchema, "xsd schema");
        ValidationUtil.rejectNull(writer, "writer");
        List<Object[]> records = json2Sheet(json, xsdSchema);
        headerSeparator(records);
        write(records, writer);
    }

    /**
     * This method does some pre processing and then build csv.
     *
     * @param json - The input json
     * @return returns a JsonParser object
     */
    private static List<Object[]> json2Sheet(String json) {
        return processJson(json, null);
    }

    /**
     * This method does some pre processing and then build csv.
     *
     * @param json - The input json
     * @param xsd  - The xsd schema json string
     * @return
     */
    private List<Object[]> json2Sheet(String json, String xsd) {
        return processJson(json, xsd);
    }

    /**
     * This method does some pre processing and then calls buildCsv() to get the
     * 2D representation of Json document.
     *
     * @param json - The input json
     * @return returns a JsonParser object
     */
    private List<Object[]> json2Header(String json) {
        List<Object[]> records = new ArrayList<>();
        configureAndBuildHeader(json, records);
        return records;
    }

    /**
     * This method process the input json to csv with the xsd schema if given.
     *
     * @param json - The input json
     * @param xsd  - The xsd schema json string can be null
     * @return
     */
    private static List<Object[]> processJson(String json, String xsd) {
        List<Object[]> records = new ArrayList<>();
        List<String> headers;
        if (xsd == null) headers = configureAndBuildHeader(json, records);
        else headers = configureAndBuildHeader(xsd, records);
        //adding all the content of csv
        JsonElement ele = com.google.gson.JsonParser.parseString(json);
        records.add(buildCsv(new Object[headers.size()], ele, "$", headers, records));
        removeDuplicates(records);
        return records;
    }

    private static void removeDuplicates(List<Object[]> records) {
        Object[] last = records.get(records.size() - 1);
        Object[] secondLast = records.get(records.size() - 2);

        boolean delete = true;
        for (Object o : last) {
            if (o != null) {
                delete = false;
                break;
            }
        }

        if (!delete) {
            delete = true;
            for (int DEL = 0; DEL < last.length; DEL++) {
                if (last[DEL] != null && !last[DEL].equals(secondLast[DEL])) {
                    delete = false;
                    break;
                }
            }
        }

        if (delete) records.remove(records.size() - 1);
    }

    private static ArrayList<String> configureAndBuildHeader(String json, List<Object[]> records) {
        setDefaultConfiguration();

        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
                .addOptions(Option.SUPPRESS_EXCEPTIONS);

        Configuration pathConf = Configuration.defaultConfiguration().addOptions(Option.AS_PATH_LIST)
                .addOptions(Option.ALWAYS_RETURN_LIST);
        List<String> pathList = JsonPath.using(pathConf).parse(json).read("$..*");
        DocumentContext context = JsonPath.using(conf).parse(json);
        HashSet<String> primitivePaths = extractPrimitivePaths(context, pathList);
        HashSet<String> uniquePrimitivePaths = primitivePaths.stream().map(JsonParser::evaluatePath)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        ArrayList<String> uniqueHeaders = new ArrayList<>(uniquePrimitivePaths);
        addHeaders(uniqueHeaders, records);
        return uniqueHeaders;
    }

    private static void setDefaultConfiguration() {
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new JacksonJsonProvider();
            private final MappingProvider mappingProvider = new JacksonMappingProvider();

            // @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            // @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            // @Override
            public Set options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    private static HashSet<String> extractPrimitivePaths(DocumentContext parse, List<String> pathList) {
        HashSet<String> primitivePaths = new LinkedHashSet<>();
        pathList.forEach(path -> {
            Object tmp = parse.read(path);
            if (tmp == null) {
                primitivePaths.add(path);
            } else {
                String dataType = tmp.getClass().getSimpleName();
                if (dataType.equals("Boolean") || dataType.equals("Integer") || dataType.equals("String")
                        || dataType.equals("Double") || dataType.equals("Long")) {
                    primitivePaths.add(path);
                } else {
                    // its not a primitive data type
                }
            }
        });
        return primitivePaths;
    }

    private static void addHeaders(List<String> unique, List<Object[]> records) {
        Object[] headers = new Object[unique.size()];
        int i = 0;
        for (String header : unique) {
            headers[i] = header;
            i++;
        }
        //headers of the csv
        records.add(headers);
    }

    /**
     * This function transforms the JSON document to its equivalent 2D representation.
     *
     * @param old     it keeps the old row which is always assigned to the current
     *                row.
     * @param ele     this keeps the part of json being parsed to 2D.
     * @param path    this maintains the path of the Json element being processed.
     * @param headers
     * @param records
     * @return
     */
    private static Object[] buildCsv(Object[] old, JsonElement ele, String path, List<String> headers, List<Object[]> records) {
        Object[] cur = old.clone();
        boolean gotArray;
        String tmpPath;
        if (ele.isJsonObject()) {
            ele = JsonOrder.orderJson(ele);
            for (Map.Entry<String, JsonElement> entry : ele.getAsJsonObject().entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    tmpPath = path + "['" + entry.getKey() + "']";
                    evaluateTempPath(cur, entry, tmpPath, headers);
                } else if (entry.getValue().isJsonObject()) {
                    cur = buildCsv(cur, entry.getValue().getAsJsonObject(),
                            path + "['" + entry.getKey() + "']", headers, records);
                } else if (entry.getValue().isJsonArray()) {
                    cur = buildCsv(cur, entry.getValue().getAsJsonArray(),
                            path + "['" + entry.getKey() + "']", headers, records);
                }
            }

        } else if (ele.isJsonArray()) {
            int arrIndex = 0;
            for (JsonElement jsonElement : ele.getAsJsonArray()) {
                if (jsonElement.isJsonPrimitive()) {
                    tmpPath = path + "['" + arrIndex + "']";
                    evaluateTempPath(cur, jsonElement, tmpPath, headers);
                } else {
                    if (jsonElement.isJsonObject()) {
                        gotArray = isInnerArray(jsonElement);
                        records.add(buildCsv(cur, jsonElement.getAsJsonObject(), path + "[" + arrIndex + "]", headers, records));
                        if (gotArray) records.remove(records.size() - 1);
                    } else if (jsonElement.isJsonArray()) {
                        buildCsv(cur, jsonElement.getAsJsonArray(), path + "[" + arrIndex + "]", headers, records);
                    }
                }
                arrIndex++;
            }
        }
        return cur;
    }

    private static void evaluateTempPath(Object[] cur, Map.Entry<String, JsonElement> entry, String tmpPath, List<String> headers) {
        tmpPath = evaluatePath(tmpPath);
        if (headers.contains(tmpPath)) {
            int index = headers.indexOf(tmpPath);
            cur[index] = entry.getValue().getAsJsonPrimitive();
        }
    }

    private static void evaluateTempPath(Object[] cur, JsonElement tmp, String tmpPath, List<String> headers) {
        tmpPath = evaluatePath(tmpPath);
        if (headers.contains(tmpPath)) {
            int index = headers.indexOf(tmpPath);
            cur[index] = tmp.getAsJsonPrimitive();
        }
    }

    private static String evaluatePath(String path) {
        Pattern pattern = Pattern.compile(AppConstants.REGEX, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(path);
        String str;
        if (matcher.find()) {
            String[] tmp = path.replace("$", "").split(AppConstants.REGEX);
            tmp[0] = tmp[0].replaceAll("(\\[[0-9]*\\])", "");
            str = "/" + (tmp[0] + matcher.group()).replace("'][", "/");
        } else {
            str = "/" + path.replace("$", "").replaceAll("(\\[[0-9]*\\])", "");
        }
        return commonReplace(str);
    }

    private static String commonReplace(String str) {
        return str.replace("[", "")
                .replace("]", "")
                .replace("''", "/")
                .replace("'", "");
    }

    /**
     * This method checks whether object inside an array contains an array or
     * not.
     *
     * @param ele it a Json object inside an array
     * @return it returns true if Json object inside an array contains an array
     * or else false
     */
    private static boolean isInnerArray(JsonElement ele) {
        for (Map.Entry<String, JsonElement> entry : ele.getAsJsonObject().entrySet()) {
            JsonElement jsonElement = entry.getValue();
            if (jsonElement.isJsonArray() && jsonElement.getAsJsonArray().size() > 0) {
                for (JsonElement checkPrimitive : jsonElement.getAsJsonArray()) {
                    if (checkPrimitive.isJsonObject()) return true;
                }
            }
        }
        return false;
    }

    /**
     * This method replaces the default header separator i.e. "/" with a underscore.
     *
     * @return JsonParser
     */
    private static void headerSeparator(List<Object[]> records) {
        headerSeparator(records, AppConstants.DEFAULT_SEPARATOR);
    }

    /**
     * This method replaces the default header separator i.e. "/" with a custom separator provided by user.
     *
     * @param records
     * @param separator
     * @return JsonParser
     */
    private static void headerSeparator(List<Object[]> records, String separator) {
        int length = records.get(0).length;
        for (int I = 0; I < length; I++) {
            records.get(0)[I] = records.get(0)[I].toString()
                    .replaceFirst("^\\/", "")
                    .replaceAll("/", separator).trim();
        }
    }

    /**
     * This method writes the 2D representation in csv format with ',' as
     * default delimiter.
     *
     * @param records
     * @param destination it takes the destination path for the csv file.
     */
    private static void write2csv(List<Object[]> records, String destination) throws JsonParsingException {
        write2csv(records, destination, AppConstants.DEFAULT_DELIMITER);
    }

    /**
     * This method writes the 2D representation in csv format with ',' as
     * default delimiter.
     *
     * @param records
     * @return
     */
    private static String write2String(List<Object[]> records) throws JsonParsingException {
        return write2String(records, AppConstants.DEFAULT_DELIMITER);
    }

    /**
     * This method writes the 2D representation in csv format with custom
     * delimiter set by user.
     *
     * @param records
     * @param destination it takes the destination path for the csv file.
     * @param delimiter   it represents the delimiter set by user.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private static void write2csv(List<Object[]> records, String destination, String delimiter) throws JsonParsingException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(destination), Charset.defaultCharset().name());
            write(records, writer, delimiter);
            writer.close();
        } catch (FileNotFoundException e) {
            ErrorUtil.jsonParsingException(String.format(ExceptionConstants.STR_FILE_EXCEPTION, destination), e);
        } catch (UnsupportedEncodingException e) {
            ErrorUtil.jsonParsingException(ExceptionConstants.STR_PARSING_EXCEPTION, e);
        }
    }

    private static String write2String(List<Object[]> records, String delimiter) throws JsonParsingException {
        StringWriter writer = new StringWriter();
        try {
            write(records, writer, delimiter);
            writer.close();
        } catch (IOException e) {
            ErrorUtil.jsonParsingException(ExceptionConstants.STR_IO_EXCEPTION, e);
        }
        return writer.toString();
    }

    private static void write(List<Object[]> records, Writer writer) throws JsonParsingException {
        write(records, writer, AppConstants.DEFAULT_DELIMITER);
    }

    private static void write(List<Object[]> records, Writer writer, String delimiter) throws JsonParsingException {
        try {
            for (Object[] data : records) {
                boolean comma = false;
                for (Object text : data) {
                    String str;
                    if (text == null) {
                        str = comma ? delimiter : "";
                    } else {
                        str = comma ? delimiter + text.toString() : text.toString();
                    }
                    writer.write(str);
                    if (!comma) comma = true;
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            ErrorUtil.jsonParsingException(ExceptionConstants.STR_IO_EXCEPTION, e);
        }
    }
}