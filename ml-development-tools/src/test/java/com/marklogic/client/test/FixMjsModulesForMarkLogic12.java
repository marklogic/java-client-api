/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.QueryBatcher;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.TextDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.test.dbfunction.DBFunctionTestUtil;
import com.networknt.schema.utils.StringUtils;

/**
 * Program for "fixing" MJS modules when running against MarkLogic 12. A nightly version of 12 from early May
 * 2025 requires that MJS modules use "export default" in the last line of the module to return data.
 * <p>
 * This approach is being used as it's easier to maintain than trying to modify the Kotlin-based code generator for
 * MJS modules.
 */
public class FixMjsModulesForMarkLogic12 {

    private static final DocumentMetadataHandle MODULE_METADATA = new DocumentMetadataHandle()
            .withPermission("rest-reader", DocumentMetadataHandle.Capability.EXECUTE)
            .withPermission("rest-writer", DocumentMetadataHandle.Capability.UPDATE);

    public static void main(String[] args) {
        try (DatabaseClient client = DBFunctionTestUtil.makeAdminClient("java-unittest-modules")) {
            String version = client.newServerEval().javascript("xdmp.version()").evalAs(String.class);
            if (new ClonedMarkLogicVersion(version).getMajor() < 12) {
                return;
            }

            DataMovementManager dataMovementManager = client.newDataMovementManager();
            final TextDocumentManager documentManager = client.newTextDocumentManager();
            final DocumentWriteSet modulesToUpdate = documentManager.newWriteSet();

            QueryBatcher queryBatcher = dataMovementManager.newQueryBatcher(
                            client.newQueryManager().newStructuredQueryBuilder().trueQuery()
                    )
                    .withThreadCount(4)
                    .withBatchSize(100)
                    .onUrisReady(batch -> {
                        for (String uri : batch.getItems()) {
                            if (!uri.startsWith("/dbf/") || !uri.endsWith(".mjs")) {
                                continue;
                            }
                            String content = documentManager.read(uri, new StringHandle()).get();
                            String[] lines = getModuleContentAsLines(content);
                            if (!lines[lines.length - 1].startsWith("export default")) {
                                System.out.println("Fixing: " + uri);
                                String newContent = fixModuleContent(lines);
                                modulesToUpdate.add(uri, MODULE_METADATA, new StringHandle(newContent).withFormat(Format.TEXT));
                            } else {
                                System.out.println("Not fixing: " + uri);
                            }
                        }
                    });

            dataMovementManager.startJob(queryBatcher);
            queryBatcher.awaitCompletion();
            dataMovementManager.stopJob(queryBatcher);

            System.out.println("Count of modules to update: " + modulesToUpdate.size());
            if (modulesToUpdate.size() > 0) {
                documentManager.write(modulesToUpdate);
            }

            System.out.println("Done!");
        }
    }

    private static String[] getModuleContentAsLines(String content) {
        String[] lines = content.split("\n");
        // Drop the last line if it's blank, which oddly happens in a small handful of the generated modules.
        if (StringUtils.isBlank(lines[lines.length - 1])) {
            String[] newLines = new String[lines.length - 1];
            System.arraycopy(lines, 0, newLines, 0, lines.length - 1);
            return newLines;
        }
        return lines;
    }

    private static String fixModuleContent(String[] lines) {
        StringBuilder newContent = new StringBuilder();
        for (int i = 0; i < lines.length - 2; i++) {
            newContent.append(lines[i]).append("\n");
        }
        newContent.append("const theOutputToExport = ").append(lines[lines.length - 1]).append("\n");
        newContent.append("export default theOutputToExport");
        return newContent.toString();
    }
}
