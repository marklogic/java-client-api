/*
 * Copyright (c) 2010-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.impl.okhttp.PartIterator;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.mail.util.ByteArrayDataSource;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Architecture tests to ensure streaming best practices are followed.
 * <p>
 * ByteArrayDataSource defeats streaming by loading entire InputStreams into memory.
 * We use InputStreamDataSource instead to enable true streaming for large documents.
 */
class VerifyByteArrayDataSourceIsNotUsedTest {

	private final JavaClasses classes = new ClassFileImporter()
		.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
		.importPackages("com.marklogic.client");

	@Test
	void shouldNotUseByteArrayDataSourceInProduction() {
		ArchRule rule = noClasses()
			// PartIterator can use ByteArrayDataSource because for an eval/invoke use case, it's very likely the user
			// wants the results in memory so they can perform some operation on them. If this proves false in the
			// future, PartIterator can be adjusted to use InputStreamDataSource instead.
			.that().doNotHaveSimpleName(PartIterator.class.getSimpleName())

			.should().dependOnClassesThat().haveSimpleName(ByteArrayDataSource.class.getSimpleName())
			.because("MLE-27509 identifies a problem where a multipart response was having each part " +
				"processed with the ByteArrayDataSource, which loads the entire stream into memory. This is " +
				"surprising to any user using InputStreamHandle to access the content of a document, as that user " +
				"is likely expecting to stream document from MarkLogic to some target. The InputStreamDataSource " +
				"class was created to avoiding reading the contents of the document into an in-memory byte " +
				"array, thus allowing for streaming reads to occur via a multipart response.");

		rule.check(classes);
	}
}
