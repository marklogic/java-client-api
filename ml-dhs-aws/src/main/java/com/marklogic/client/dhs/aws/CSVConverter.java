package com.marklogic.client.dhs.aws;

import com.fasterxml.jackson.databind.node.ObjectNode;
// import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class CSVConverter {
   private CsvMapper csvMapper;
//   private CsvFactory factory;

   public CSVConverter() {
      csvMapper = new CsvMapper()
            .configure(CsvParser.Feature.ALLOW_TRAILING_COMMA, true)
            .configure(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS, false)
            .configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, false)
            .configure(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS, false)
            .configure(CsvParser.Feature.SKIP_EMPTY_LINES, true)
            .configure(CsvParser.Feature.TRIM_SPACES, true)
            .configure(CsvParser.Feature.WRAP_AS_ARRAY, false);
/*
      factory = new CsvFactory()
            .configure(CsvParser.Feature.ALLOW_TRAILING_COMMA, true)
            .configure(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS, false)
            .configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, false)
            .configure(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS, false)
            .configure(CsvParser.Feature.SKIP_EMPTY_LINES, true)
            .configure(CsvParser.Feature.TRIM_SPACES, true)
            .configure(CsvParser.Feature.WRAP_AS_ARRAY, false);
  */
   }

// TODO: in job control file, expose schema control over whether the value is a JSON string, number, or boolean
   public Iterator<ObjectNode> convertObject(InputStream csvSource) throws IOException {
      CsvSchema firstLineSchema = CsvSchema.emptySchema().withHeader();

      Iterator<ObjectNode> objectItr = csvMapper
            .readerFor(ObjectNode.class)
            .with(firstLineSchema)
            .readValues(csvSource);

      return objectItr;
   }
/* NOTE: Jackson CsvParser doesn't support parsing a CSV row as a JSON string
   to optimize, may need to implement Iterator<String> or a stream on top of CsvParser

   public Iterator<String> convertString(InputStream csvSource) throws IOException {
      CsvSchema firstLineSchema = CsvSchema.emptySchema().withHeader();

      CsvParser parser = factory.createParser(csvSource);
      parser.setSchema(firstLineSchema);
      Iterator<String> stringItr = parser.readValuesAs(String.class);

      return stringItr;
   }
 */
}
