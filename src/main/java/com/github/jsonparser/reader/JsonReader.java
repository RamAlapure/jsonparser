package com.github.jsonparser.reader;

import com.github.jsonparser.exception.JsonParsingException;
import com.github.jsonparser.model.JsonOrder;
import com.github.jsonparser.util.AppConstants;
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

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class parse a json string to 2D representation.
 *
 * @author Ram Alapure
 * @version 1.0
 * @since 04/03/2020
 */
public class JsonReader {

    public static final Logger log = Logger.getLogger(JsonReader.class.getName());

    private JsonReader() {
    }

    // Set of data types to check the primitive types.
    private static final Set<String> DATA_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList("Boolean", "Integer", "String", "Double", "Long")));

    /**
     * The method process the input json string and returns the 2D representation of data.
     *
     * @param json    - The input json string
     * @param records - The list of processed csv records
     * @return Returns list of processed csv records.
     * @throws JsonParsingException
     */
    public static List<Object[]> parse(String json, List<Object[]> records) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        headerSeparator(records);
        return records;
    }

    /**
     * The method process the input json string and returns the 2D representation of data.
     *
     * @param json      - The input json string
     * @param separator - The custom header/column separator key
     * @return Returns list of processed csv records.
     * @throws JsonParsingException
     */
    public static List<Object[]> parse(String json, String separator) throws JsonParsingException {
        ValidationUtil.rejectNull(json, "json");
        List<Object[]> records = json2Sheet(json);
        headerSeparator(records, separator);
        return records;
    }

    /**
     * This method does some pre processing and then build csv.
     *
     * @param json - The input json string
     * @return Returns list of records.
     */
    public static List<Object[]> json2Sheet(String json) {
        return processJson(json, null);
    }

    /**
     * This method does some pre processing and then build csv.
     *
     * @param json - The input json string
     * @param xsd  - The xsd schema json string
     * @return Returns list of records.
     */
    public static List<Object[]> json2Sheet(String json, String xsd) {
        return processJson(json, xsd);
    }

    /**
     * This method does some pre processing on xsd and returns the header/column list.
     *
     * @param json - The input xsd json string
     * @return Returns list of records.
     */
    public static List<Object[]> json2Header(String json) {
        log.info("Processing input xsd json to 2D representation.");
        List<Object[]> records = new ArrayList<>();
        configureAndBuildHeader(json, records);
        log.info("Returning processed headers/columns records.");
        return records;
    }

    /**
     * This method process the input json to csv with the xsd schema if given.
     *
     * @param json - The input json string
     * @param xsd  - The xsd schema json string can be null
     * @return Returns list of records.
     */
    private static List<Object[]> processJson(String json, String xsd) {
        log.info("Processing input json to 2D representation.");
        List<Object[]> records = new ArrayList<>();
        List<String> headers;
        if (xsd == null) headers = configureAndBuildHeader(json, records);
        else headers = configureAndBuildHeader(xsd, records);
        //adding all the content of csv
        JsonElement ele = com.google.gson.JsonParser.parseString(json);
        records.add(buildCsv(new Object[headers.size()], ele, "$", headers, records));
        removeDuplicates(records);
        log.info("Returning processed list of records.");
        return records;
    }

    /**
     * This method removes the duplicate records from list.
     *
     * @param records - List of records processed from json.
     */
    private static void removeDuplicates(List<Object[]> records) {
        if (records.size() > 2) {
            Object[] last = records.get(records.size() - 1);
            Object[] secondLast = records.get(records.size() - 2);

            boolean delete = Arrays.stream(last).noneMatch(Objects::nonNull);

            if (!delete) {
                delete = true;
                if (IntStream.range(0, last.length).anyMatch(DEL -> last[DEL] != null && !last[DEL].equals(secondLast[DEL]))) {
                    delete = false;
                }
            }

            if (delete) records.remove(records.size() - 1);
        }
    }

    /**
     * This method build configuration for json processing and extracting the headers/columns.
     *
     * @param json    - The input json string
     * @param records - The input record list to add headers/columns.
     * @return Returns he headers/columns.
     */
    private static ArrayList<String> configureAndBuildHeader(String json, List<Object[]> records) {
        log.info("Setting the default configuration for json processing.");
        setDefaultConfiguration();

        Configuration conf = Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
                .addOptions(Option.SUPPRESS_EXCEPTIONS);

        Configuration pathConf = Configuration.defaultConfiguration().addOptions(Option.AS_PATH_LIST)
                .addOptions(Option.ALWAYS_RETURN_LIST);
        List<String> pathList = JsonPath.using(pathConf).parse(json).read("$..*");
        DocumentContext context = JsonPath.using(conf).parse(json);
        HashSet<String> primitivePaths = extractPrimitivePaths(context, pathList);
        HashSet<String> uniquePrimitivePaths = primitivePaths.stream().map(JsonReader::evaluatePath)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        ArrayList<String> uniqueHeaders = new ArrayList<>(uniquePrimitivePaths);
        addHeaders(uniqueHeaders, records);
        log.info("Returning the header/column list.");
        return uniqueHeaders;
    }

    /**
     * The default configuration to process json.
     */
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

    /**
     * This method extracts the primitive paths headers/columns from the json.
     *
     * @param parse    - The json parse document context.
     * @param pathList - The input path list.
     * @return Returns primitive paths headers/columns.
     */
    private static HashSet<String> extractPrimitivePaths(DocumentContext parse, List<String> pathList) {
        log.info("Extracting primitive path list from json.");
        HashSet<String> primitivePaths = new LinkedHashSet<>();
        pathList.forEach(path -> {
            Object tmp = parse.read(path);
            if (tmp == null) primitivePaths.add(path);
            else {
                String dataType = tmp.getClass().getSimpleName();
                if (DATA_TYPES.contains(dataType)) primitivePaths.add(path);
            }
        });
        log.info("Returning extracted primitive path list from json.");
        return primitivePaths;
    }

    /**
     * @param unique
     * @param records - The list of processed csv records
     */
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
     * This function transforms the JSON document to its equivalent csv representation.
     *
     * @param old     - It keeps the old row which is always assigned to the current row
     * @param ele     - This keeps the part of json being parsed to csv
     * @param path    - This maintains the path of the Json element being processed
     * @param headers - The headers/columns list for csv
     * @param records - The list of processed csv records
     * @return Returns list of processed csv records for each data iterations.
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
                        removeDuplicates(records);
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

    /**
     * @param cur
     * @param entry
     * @param tmpPath
     * @param headers
     */
    private static void evaluateTempPath(Object[] cur, Map.Entry<String, JsonElement> entry, String tmpPath, List<String> headers) {
        tmpPath = evaluatePath(tmpPath);
        if (headers.contains(tmpPath)) {
            int index = headers.indexOf(tmpPath);
            cur[index] = entry.getValue().getAsJsonPrimitive();
        }
    }

    /**
     * @param cur
     * @param tmp
     * @param tmpPath
     * @param headers
     */
    private static void evaluateTempPath(Object[] cur, JsonElement tmp, String tmpPath, List<String> headers) {
        tmpPath = evaluatePath(tmpPath);
        if (headers.contains(tmpPath)) {
            int index = headers.indexOf(tmpPath);
            cur[index] = tmp.getAsJsonPrimitive();
        }
    }

    /**
     * @param path
     * @return
     */
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

    /**
     * @param str - The input string to perform replace operation.
     * @return Returns processed string.
     */
    private static String commonReplace(String str) {
        return str.replace("[", "")
                .replace("]", "")
                .replace("''", "/")
                .replace("'", "");
    }

    /**
     * This method checks whether object inside an array contains an array or not.
     *
     * @param ele - It's a Json object inside an array
     * @return Returns true if Json object inside an array contains an array or else false.
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
     * @param records - The list of processed csv records
     */
    private static void headerSeparator(List<Object[]> records) {
        headerSeparator(records, AppConstants.DEFAULT_SEPARATOR);
    }

    /**
     * This method replaces the default header separator i.e. "/" with a custom separator provided by user.
     *
     * @param records   - The list of processed csv records
     * @param separator - The custom header/column separator key
     */
    private static void headerSeparator(List<Object[]> records, String separator) {
        log.info(String.format("Updating header/column values with separator \"%s\"", separator));
        IntStream.range(0, records.get(0).length)
                .forEach(I -> records.get(0)[I] = records.get(0)[I].toString()
                        .replaceFirst("^\\/", "")
                        .replaceAll("/", separator).trim());
        log.info(String.format("Successfully updated header/column values with separator \"%s\"", separator));
    }

}
