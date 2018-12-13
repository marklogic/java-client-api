package com.marklogic.client.dhs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class CSVConverter {
	private CsvMapper csvMapper;
	private CountInputStream countInputStream;

	public long getByteCount() {
		return countInputStream.getByteCount();
	}

	public CSVConverter() {
		csvMapper = new CsvMapper().configure(CsvParser.Feature.ALLOW_TRAILING_COMMA, true)
				.configure(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS, false)
				.configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, false)
				.configure(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS, false)
				.configure(CsvParser.Feature.SKIP_EMPTY_LINES, true).configure(CsvParser.Feature.TRIM_SPACES, true)
				.configure(CsvParser.Feature.WRAP_AS_ARRAY, false);

	}

// TODO: in job control file, expose schema control over whether the value is a JSON string, number, or boolean
	public Iterator<ObjectNode> convertObject(InputStream csvSource) throws IOException {
		this.countInputStream =  new CountInputStream(csvSource);
		CsvSchema firstLineSchema = CsvSchema.emptySchema().withHeader();

		Iterator<ObjectNode> objectItr = csvMapper.readerFor(ObjectNode.class).with(firstLineSchema)
				.readValues(this.countInputStream);
		return objectItr;
	}

}