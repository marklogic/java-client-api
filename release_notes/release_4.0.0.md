NOTES FOR RELEASE 4.0.0

Adds Optic API, Data Movement SDK, Kerberos, Client Certificate Authentication, Geospatial enhancements,
Temporal documen enhancements, support for metadata values.

New Functionality
392 - add shortcut addAs methods to DocumentWriteSet
400 - Optic API
402 - geo double precision and geo polygon search
404 - add Kerberos support
413 - add support for document metadata values
406 - certificate-based authentication
414 - Bitemporal ML9 features - version URI, Wipe, Protect, document patch
465 - Data Movement SDK
466 - add StructuredQueryBuilder.coordSystem to set Geo coordinate system
473 - send header "ML-Agent-ID: java" with every HTTP request so REST layer can track which calls come
from Java Client API
550 - add Capability NODE_UPDATE

Improvements and Bug Fixes
- many javadoc improvements
210 - fail fast when library module path doesn't begin with /ext
234 - offer a way to close the input source for XMLStreamReaderHandle
241 - fix misleading WARN warning message from values call
249 - fixes DocumentPatchBuilder.replaceValue with numbers or booleans
256 - be explicit about desired format of QBE response
262 - update SearchHandle to support search:extracted elements
288 - support changes made in server-generated metadata for binary docs
292 - support changes made in search:search response with extracted results
294 - DocumentPage.size was including metadata in counts but should not have
365 - reduce duplicate code in JerseyServices
371 - JAXBContext should be cached for improved performance
380 - setOptimzeLevel should have been setOptimizeLevel
385 - improve javadocs for RuleManager.readAs
421 - Some of the read handlers do not populate properly formats and mime-types when used with
ResourceServices
423 - add support for extract-resolution time
424 - remove logback dependency so slf4j works as it should
434 - imporove error response processing
436 - rename java-client-api-M.m.p.jar JAR to marklogic-client-api-M.m.p.jar
437 - fix support for date range queries
448 - Efficiencies: avoid Object construction, auto-boxing, etc.
Commit ca96e23- expose PojoRepository.getId
467 - connections are left in CLOSE_WAIT after DatabaseClient.release is called
472 - remove unnecessary INFO logging from RuleDefinition
486 - PojoRepository.count(query) does not scope count with the query
524 - add support for markdown javadocs
560 - support new 428 status code for missing content version
582 - NullPointerException thrown while doing eval() with client object created with incorrect credentials
587 - rename GeoSpatialOperator to GeospatialOperator
Commit 484d15 - avoid errors by only parsing non-blank entityResolutionTime
592, 594, 598 - chunked HTTP broke certain XML parsers unless we set XMLInputFactory.isCoalescing to true
610 - remove support for deprecated keyvalue endpoint
621 - remove deprecated APIs
651 - don't set the handle format from the descriptor if it's null
687 - clean up indenting across the project
