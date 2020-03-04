The library perform operation on json and returns csv data in multiple output formats:
1. String
2. File
3. Writer

How you can use it:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.ramalapure/json-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.ramalapure/json-parser)

**Maven**
```
<dependency>
  <groupId>com.github.ramalapure</groupId>
  <artifactId>json-parser</artifactId>
  <version>1.1</version>
</dependency>
```

**Gradle**
```
implementation 'com.github.ramalapure:json-parser:1.1'
```

To parse json input string to csv, you can do it by following ways:

**Note:** The following default separator **"_"** and delimiter **","** are used while creating CSV.  
Also there is an alternative to pass custom separator (e.g. "/") and delimiter(e.g. "|").

1. Output as String
   ```
   JsonParser.parse2Csv(INPUT_JSON_STRING); 
   // or
   JsonParser.parse2Csv(INPUT_JSON_STRING, CUSTOM_SEPARATOR, CUSTOM_DELIMITER);
   ```

2. Output as Writer
   ```
   JsonParser.parse2Csv(INPUT_JSON_STRING, WRITER_OBJECT); //e.g. StringWriter, FileWriter, etc.
   // or
   JsonParser.parse2Csv(INPUT_JSON_STRING, WRITER_OBJECT, CUSTOM_SEPARATOR, CUSTOM_DELIMITER);
   ```

3. Output as File
   ```
   JsonParser.parse2Csv(INPUT_JSON_STRING, CSV_FILE_PATH); // CSV_FILE_PATH e.g. test.csv or D://somefolder/test.csv or /home/user/downloads/test.csv
   // or
   JsonParser.parse2Csv(INPUT_JSON_STRING, CSV_FILE_PATH, CUSTOM_SEPARATOR, CUSTOM_DELIMITER);
   ```