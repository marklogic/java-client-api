# CHANGELOG

## 5.3.1
#### Improvements and Bug Fixes
- [#1206](https://github.com/marklogic/java-client-api/issues/1206) - README update to JCenter url
- [#1263](https://github.com/marklogic/java-client-api/issues/1263) - Switch jackson-dataformat-xml dependency to testCompile

## 5.3.0
#### New Functionality
- RowBatcher - bulk export of all rows or documents from a view (internal Jira issue)
- Refactored and Concurrent Bulk IO - concurrent calls to Data Services (internal Jira issue)
- #1197, #1223 - Added PathSplitter, JSONSplitter complementing existing splitters
- [#1239](https://github.com/marklogic/java-client-api/issues/1239) - Full outer join in Optic
- [#1248](https://github.com/marklogic/java-client-api/issues/1248) - Multi-valued equality expressions in Optic builder

#### Improvements and Bug Fixes
- [#981](https://github.com/marklogic/java-client-api/issues/981) - DOMWriter doesn't serialize namespaced XML properly
- [#1238](https://github.com/marklogic/java-client-api/issues/1238) - MetadataExtraction with DocumentWriteSet not extracting properties
- [#1242](https://github.com/marklogic/java-client-api/issues/1242) - RawCombinedQueryDefinition in QueryBatcher throws NPE
- [#1244](https://github.com/marklogic/java-client-api/issues/1244) - sevice property name typo in ServiceCompareTask

## 5.2.0
#### New Functionality
- [#1185](https://github.com/marklogic/java-client-api/issues/1185) - Splitter for large XML file
- [#1209](https://github.com/marklogic/java-client-api/issues/1209) - Cookbook example bridging from QueryBatcher to Data Services

#### Improvements and Bug Fixes
- [#1118](https://github.com/marklogic/java-client-api/issues/1118) - Upgrade to OkHttp 4.4.0 release and gradle wrapper upgrade
- [#1153](https://github.com/marklogic/java-client-api/issues/1153) - Able to write empty document properties
- [#1196](https://github.com/marklogic/java-client-api/issues/1196) - Optional forest name parameter on DocumentManager search

## 5.1.0
#### New Functionality
- New package to make it easy to implement connectors for dataflow frameworks. Refer to https://github.com/marklogic/java-client-api/wiki/Bulk-Data-Services page for details.
- Two new splitters complementing JacksonCSVSplitter. LineSplitter for line-delimited payloads (JSON) and ZipSplitter splitter for entries in a zipfile

#### Improvements and Bug Fixes
- [#1163](https://github.com/marklogic/java-client-api/issues/1163) - Eliminated potential for deadlocks between batches with URI sort and support for multibyte characters within uris
- [#1177](https://github.com/marklogic/java-client-api/issues/1177) - Addressed security vulnerability in Jackson by upgrading Jackson dependency

## 5.0.1
#### New Functionality
- [#636](https://github.com/marklogic/java-client-api/issues/636) - Support for debugging connection issues

#### Improvements and Bug Fixes
- [#1104](https://github.com/marklogic/java-client-api/issues/1104) - Fix for mimetype extensions in determining formats for Data Services
- [#1107](https://github.com/marklogic/java-client-api/issues/1107) - Expose server error information on the client
- [#1117](https://github.com/marklogic/java-client-api/issues/1117) - Few StructuredQueryBuilder methods enhanced to return StructuredQueryDefinition

## 5.0.0
#### New Functionality
- [#911](https://github.com/marklogic/java-client-api/issues/911) - Streaming Multipart for OkHttp
- [#1080](https://github.com/marklogic/java-client-api/issues/1080) - Returns version id for multiple documents in single request
- [#1101](https://github.com/marklogic/java-client-api/issues/1101) - Parameter names as strings in Optic bindings to literals

#### Improvements and Bug Fixes
- [#885](https://github.com/marklogic/java-client-api/issues/885) - SecurityContext details exposed via getters
- [#1090](https://github.com/marklogic/java-client-api/issues/1090), [#1098](https://github.com/marklogic/java-client-api/issues/1090) - Removed deprecated interfaces and XOM dependency
- [#1103](https://github.com/marklogic/java-client-api/issues/1103) - OWASP recommendations to default parsers

## 4.2.0
#### New Functionality
- [#862](https://github.com/marklogic/java-client-api/issues/862) - Support for Java 9, OpenJDK 10 and OpenJDK 11
- [#965](https://github.com/marklogic/java-client-api/issues/965) - Enable QueryBatcher to take in a RawCtsQueryDefinition

#### Improvements and Bug Fixes
- [#1038](https://github.com/marklogic/java-client-api/issues/1038) - Deadlock detection in multi-statement transactions
- [#1052](https://github.com/marklogic/java-client-api/issues/1052) - Splitter with CSV Implementation
- [#1057](https://github.com/marklogic/java-client-api/issues/1057) - Dependency on JAXB libraries conditional on Java version

## 4.1.2
#### New Functionality
- [#995](https://github.com/marklogic/java-client-api/issues/995) - Support for Kerberos keytab file to authenticate 

#### Improvements and Bug Fixes
- [#1020](https://github.com/marklogic/java-client-api/issues/1020) - Provide missing DatabaseClientFactory Bean configuration
- [#1046](https://github.com/marklogic/java-client-api/issues/1046) - Jackson library upgrade to version 2.9.8

## 4.1.1
#### New Functionality
- [#1006](https://github.com/marklogic/java-client-api/issues/1006) - Data Services First - MarkLogic's support for microservices

#### Improvements and Bug Fixes
- [#999](https://github.com/marklogic/java-client-api/issues/999) - Connect only to primary host for load balancer scenario
- [#991](https://github.com/marklogic/java-client-api/issues/991) - Correct cookie expiration for Application Load Balancer
- [#989](https://github.com/marklogic/java-client-api/issues/989) - Manage cookies for used for host affinity in Transaction
- [#968](https://github.com/marklogic/java-client-api/issues/968) - Deprecate XOMHandle

## 4.1.0

#### New Functionality
- [#943](https://github.com/marklogic/java-client-api/issues/943) - Add base class for Proxy Services and integrate with the DatabaseClient
- [#942](https://github.com/marklogic/java-client-api/issues/942) - Add value conversion utilities for Proxy Services
- [#951](https://github.com/marklogic/java-client-api/issues/951) - Add utility to produce an SJS/Xquery main module from Proxy services function declaration

#### Improvements and Bug Fixes
- [881](https://github.com/marklogic/java-client-api/issues/881) - Added a BlockingRunsPolicy for rejected tasks for better threadpool utilization
- [920](https://github.com/marklogic/java-client-api/issues/920) - Prevents QueryBatcher hang when you have a single thread
- [971](https://github.com/marklogic/java-client-api/issues/971) - Passed the TrustManager to SSLContext so that the trust manager is taken into account

## 4.0.4

#### New Functionality
- [#839](https://github.com/marklogic/java-client-api/issues/839) - Export Data from Documents as Rows using DMSDK's ExportRowsViaTemplateListener by supplying a query and a TDE template name
- [#853](https://github.com/marklogic/java-client-api/issues/853) - Exposed new Optic enhancements such as fromSql, fromSparql etc
- [#882](https://github.com/marklogic/java-client-api/issues/882) - QueryBatcher Enhancements - Listeners can be registered with onJobCompletion to run listeners when the QueryJob is completed. Also methods have been added to get the primary client of the Batcher.
- [#891](https://github.com/marklogic/java-client-api/issues/891) - The Batchers will automatically close the listeners registered with them if they have implemented AutoCloseable and have some resources to close
- [#647](https://github.com/marklogic/java-client-api/issues/647) - Added an overload in WriteBatcher to add DocumentWriteOperation
- [#835](https://github.com/marklogic/java-client-api/issues/835) - During Failover, if the hosts come back up from offline status, it would be automatically added back to the list of hosts
- [#877](https://github.com/marklogic/java-client-api/issues/877) - Changed the Java Client API maven project to a gradle project

#### Improvements and Bug Fixes
- [#874](https://github.com/marklogic/java-client-api/issues/874) - EvalResultIterator implements Closeable and it can be now used in the try with resources pattern
- [#892](https://github.com/marklogic/java-client-api/issues/892) - Removed NewCookie and replaced it with OkHttp Cookie thereby eliminating the mock implementation of RuntimDelegate class
- [#567](https://github.com/marklogic/java-client-api/issues/567) - Cookbook recipe for moving data out of one MarkLogic database to another (intra/inter cluster) and having server and JVM transforms mid way.
- [#909](https://github.com/marklogic/java-client-api/issues/909) - Added an overload for ModifyPlanBase.where to accept PlanCondition
- [#879](https://github.com/marklogic/java-client-api/issues/879) - Increase queue size (will use more memory) for QueryBatcher so iterator thread won't be distracted running batches as often and can be more focused on feeding the queue
- [#850](https://github.com/marklogic/java-client-api/issues/850) - Deprecated getQuerySuccessListeners and added getUrisReadyListeners
- [#836](https://github.com/marklogic/java-client-api/issues/836) - A Failover Bug where WriteBatcher hangs forever in awaitCompletion due to internal inconsistency
- [#474](https://github.com/marklogic/java-client-api/issues/474) - Performance Optimization when calling XMLOutputFactory::newInstance
- [#826](https://github.com/marklogic/java-client-api/issues/826) - Fixed inconsistency in to return the correct number of writes so far for WriteBatches
- [#860](https://github.com/marklogic/java-client-api/issues/860) - removed many License files and replaced it with LEGALNOTICES.txt

## 4.0.3

#### New Functionality
- [#639](https://github.com/marklogic/java-client-api/issues/639) - support DocumentManager.setReadTransform for DocumentManager.search calls
- [#690](https://github.com/marklogic/java-client-api/issues/690) - Get a JobTicket by JobId
- [#752](https://github.com/marklogic/java-client-api/issues/752) - Allow setting JobId for a JobTicket
- [#756](https://github.com/marklogic/java-client-api/issues/756) - Add RawCtsQueryDefinition support for values queries
- [#787](https://github.com/marklogic/java-client-api/issues/787) - Support advancing LSQT
- [#813](https://github.com/marklogic/java-client-api/issues/813) - Add NoResponseListener for failover scenarios which throw EOFException
- [#818](https://github.com/marklogic/java-client-api/issues/818) - Document failover support
- [#822](https://github.com/marklogic/java-client-api/issues/822) - Add initialization step for QueryBatchListener
- [#838](https://github.com/marklogic/java-client-api/issues/838) - Add HostAvailabilityListener.RetryListener and QueryBatcher.retryWithFailureListeners to facilitate listeners retrying during failover scenarios

#### Improvements and Bug Fixes

This release fixes many bugs related to failover scenarios

- [#473](https://github.com/marklogic/java-client-api/issues/473) - add "ML-Agent-ID: java" header to improve request origination metrics
- [#526](https://github.com/marklogic/java-client-api/issues/526) - Possible Race Condition during black-listing of hosts
- [#557](https://github.com/marklogic/java-client-api/issues/557), [#570](https://github.com/marklogic/java-client-api/issues/570) - Failures during failover require new HostAvailabilityListener.RetryListener
- [#562](https://github.com/marklogic/java-client-api/issues/562) - Cookbook recipe for bulk export to JDBC
- [#563](https://github.com/marklogic/java-client-api/issues/563) - Cookbook recipe for incremental load from JDBC
- [#565](https://github.com/marklogic/java-client-api/issues/565) - Cookbook recipe for bulk load from JDBC
- [#579](https://github.com/marklogic/java-client-api/issues/579) - Job not getting stopped when number of available hosts < 'minHosts' property
- [#641](https://github.com/marklogic/java-client-api/issues/641) - Upgrade all dependencies
- [#725](https://github.com/marklogic/java-client-api/issues/725) - Fix ServerEvaluationCall.evalAs(Class<T>) closing underlying streams prematurely
- [#744](https://github.com/marklogic/java-client-api/issues/744) - Fix a WriteBatcher hang in awaitCompletion after forest failover
- [#768](https://github.com/marklogic/java-client-api/issues/768) - Document supported Java runtime
- [#775](https://github.com/marklogic/java-client-api/issues/775) - Fix QueryManagerImpl which incorrectly hardcoded start page to 1
- [#785](https://github.com/marklogic/java-client-api/issues/785) - Support HTTPS with OKhttp
- [#798](https://github.com/marklogic/java-client-api/issues/798) - Fix WriteBatcher from retrying when job is stopped
- [#802](https://github.com/marklogic/java-client-api/issues/802) - Remove a spurious warning about StringQueryDefinitionImpl
- [#811](https://github.com/marklogic/java-client-api/issues/811) - Fix OkHttpServices which threw NPE when release() method called more than once

## 4.0.2

#### New Functionality
- [#720](https://github.com/marklogic/java-client-api/pull/720) - Add a RawCtsQueryDefinition to parallel RawCombinedQueryDefinition and RawStructuredQueryDefinition
- [#722](https://github.com/marklogic/java-client-api/pull/722) - add support for minimum distance on near-query

#### Improvements and Bug Fixes
- [#65](https://github.com/marklogic/java-client-api/issues/65) - Replace internal HTTP library Jersey with OkHttp to improve performance, future readiness [#391](https://github.com/marklogic/java-client-api/issues/391), and overcome version conflicts [#769](https://github.com/marklogic/java-client-api/issues/769), [#282](https://github.com/marklogic/java-client-api/issues/282)
- [#470](https://github.com/marklogic/java-client-api/issues/470) - Share http connection pool across DatabaseClient instances to allow scenarios where many DatabaseClient instances are desirable
- [#367](https://github.com/marklogic/java-client-api/issues/367) - Upgrade version of jdom2 dependency to 2.0.6 to overcome vulnerability in dependency xalan-2.7.1
- [#368](https://github.com/marklogic/java-client-api/issues/368) - Remove dependency on jersey-apache-client4-1.17 to overcome vulnerability in dependency HttpClient 4.1.1
- [#711](https://github.com/marklogic/java-client-api/pull/711) - Upgrade dependencies slf4j-api to 1.7.25
- [#715](https://github.com/marklogic/java-client-api/pull/715) - Upgrade dependencies jdom2 to 2.0.6, gson to 2.8.0, and htmlcleaner to 2.19

## 4.0.1

#### New Functionality
- [#400](https://github.com/marklogic/java-client-api/issues/400) - Optic API
- [#402](https://github.com/marklogic/java-client-api/issues/402) - geo double precision and geo polygon search
- [#404](https://github.com/marklogic/java-client-api/issues/404) - add Kerberos support
- [#413](https://github.com/marklogic/java-client-api/issues/413) - add support for document metadata values
- [#406](https://github.com/marklogic/java-client-api/issues/406) - certificate-based authentication
- [#414](https://github.com/marklogic/java-client-api/issues/414) - Bitemporal ML9 features - version URI, Wipe, Protect, document patch
- [#465](https://github.com/marklogic/java-client-api/issues/465) - Data Movement SDK
- [#466](https://github.com/marklogic/java-client-api/issues/466) - add StructuredQueryBuilder.coordSystem to set Geo coordinate system
- [#473](https://github.com/marklogic/java-client-api/issues/473) - send header "ML-Agent-ID: java" with every HTTP request so REST layer can track which calls come from Java Client API
- [#550](https://github.com/marklogic/java-client-api/issues/550) - add Capability NODE_UPDATE

#### Improvements and Bug Fixes
- many javadoc improvements
- [#210](https://github.com/marklogic/java-client-api/issues/210) - fail fast when library module path doesn't begin with /ext
- [#234](https://github.com/marklogic/java-client-api/issues/234) - offer a way to close the input source for XMLStreamReaderHandle
- [#241](https://github.com/marklogic/java-client-api/issues/241) - fix misleading WARN warning message from values call
- [#249](https://github.com/marklogic/java-client-api/issues/249) - fixes DocumentPatchBuilder.replaceValue with numbers or booleans
- [#256](https://github.com/marklogic/java-client-api/issues/256) - be explicit about desired format of QBE response
- [#262](https://github.com/marklogic/java-client-api/issues/262) - update SearchHandle to support search:extracted elements
- [#288](https://github.com/marklogic/java-client-api/issues/288) - support changes made in server-generated metadata for binary docs
- [#292](https://github.com/marklogic/java-client-api/issues/292) - support changes made in search:search response with extracted results
- [#294](https://github.com/marklogic/java-client-api/issues/294) - DocumentPage.size was including metadata in counts but should not have
- [#365](https://github.com/marklogic/java-client-api/issues/365) - reduce duplicate code in JerseyServices
- [#421](https://github.com/marklogic/java-client-api/issues/421) - Some of the read handlers do not populate properly formats and mime-types when used with ResourceServices
- [#424](https://github.com/marklogic/java-client-api/issues/424) - remove logback dependency so slf4j works as it should
- [#436](https://github.com/marklogic/java-client-api/issues/436) - rename java-client-api-M.m.p.jar JAR to marklogic-client-api-M.m.p.jar
- [#448](https://github.com/marklogic/java-client-api/issues/448) - Efficiencies: avoid Object construction, auto-boxing, etc.
- [#486](https://github.com/marklogic/java-client-api/issues/486) - PojoRepository.count(query) does not scope count with the query
- [#524](https://github.com/marklogic/java-client-api/issues/524) - add support for markdown javadocs
- [#560](https://github.com/marklogic/java-client-api/issues/560) - support new 428 status code for missing content version
- [#582](https://github.com/marklogic/java-client-api/issues/582) - NullPointerException thrown while doing eval() with client object created with incorrect credentials
- [#610](https://github.com/marklogic/java-client-api/issues/610) - remove support for deprecated keyvalue endpoint
- [#621](https://github.com/marklogic/java-client-api/issues/621) - remove deprecated APIs
- [#651](https://github.com/marklogic/java-client-api/issues/651) - don't set the handle format from the descriptor if it's null
- [#687](https://github.com/marklogic/java-client-api/issues/687) - clean up indenting across the project## 3.0.7

### 3.0.7

- some javadoc improvements and code/test cleanup
- [#423](https://github.com/marklogic/java-client-api/issues/423) - add support to SearchHandle for extract-resolution time
- #592, #594, #598 - chunked HTTP broke certain XML parsers unless we set XMLInputFactory.isCoalescing to true
- full list [here](https://github.com/marklogic/java-client-api/compare/3.0.6...3.0.7)

### 3.0.6

- some javadoc improvements and code/test cleanup
- [#380](https://github.com/marklogic/java-client-api/issues/380) - rename SPARQLQueryDefinition.setOptimzeLevel to setOptimizeLevel
- [#392](https://github.com/marklogic/java-client-api/issues/392) - add DocumentWriteSet.addAs methods
- [#432](https://github.com/marklogic/java-client-api/issues/432) - a more portable variant to initialize (and teardown) a test server
- [#433](https://github.com/marklogic/java-client-api/issues/433) - moving JAXBContext initialization into a static, lazily initialized field
- [#434](https://github.com/marklogic/java-client-api/issues/434) - pass thru full response body when an error message doesn't conform to expected formats
- [#437](https://github.com/marklogic/java-client-api/issues/437) - fix support for Date range queries; upgrade to latest Jackson
- [#467](https://github.com/marklogic/java-client-api/issues/467) - call shutdown on ThreadSafeClientConnManager (the HTTP connection pool) to avoid any lingering connections in CLOSE_WAIT
- [#472](https://github.com/marklogic/java-client-api/issues/472) - remove unnecessary INFO logging from RuleDefinition
- expose PojoRepository.getId
- full list [here](https://github.com/marklogic/java-client-api/compare/3.0.5...3.0.6)

### 3.0.5

- some javadoc improvements and code/test cleanup
- [#176](https://github.com/marklogic/java-client-api/issues/176) - deprecate QueryOptionsHandle since it depends on QueryOptions which is already deprecated
- #261, #266, #382, #388, and #390 - fix several errors when woodstox is used
- [#364](https://github.com/marklogic/java-client-api/issues/364) and #381 - respect all cookies (including load-balancer created cookies) when Transaction is used
- add method ServerTransform.addParameter
- [#378](https://github.com/marklogic/java-client-api/issues/378) - add DATE type support to RDFTypes
- [#396](https://github.com/marklogic/java-client-api/issues/396) - fix DocumentManager.write(DocumentWriteSet, ServerTransform, Transaction) to use ServerTransform
- [#395](https://github.com/marklogic/java-client-api/issues/395) - fix RuleManager.matchAs(content, ruleListHandle) which was throwing NPE
- full list [here](https://github.com/marklogic/java-client-api/compare/3.0.4...3.0.5)

### 3.0.4

* Semantics API - GraphManager for CRUD of semantic graphs; SPARQLQueryManager for executing SPARQL
  queries (select, describe, construct, and ask) and using SPARQL Update
* Enable [MarkLogic Jena API](https://github.com/marklogic/marklogic-jena) (separate project)
* Enable [MarkLogic Sesame API](https://github.com/marklogic/marklogic-sesame) (separate project)

### 3.0.3

* Search Extract - add support to SearchHandle (actually MatchDocumentSummary) for content extracted by
  the [extract-document-data option](http://docs.marklogic.com/search:search#opt-extract-document-data)
* Bi-Temporal enhancements - support bulk write of bitemporal documents; expose bitemporal system time
* Bulk delete

### 3.0.2

* Many bug fixes

## 3.0.1

* Pojo Façade - persist POJOs as JSON objects and use a simple API to query them with all the power
  of MarkLogic's search engine
* Eval (and Invoke) - directly access MarkLogic's powerful server-side XQuery or JavaScript
* Bulk Read & Write - send and retrieve documents and metadata in batches for significant performance
  improvements
* JSON - JacksonHandle, JacksonDatabindHandle, and JacksonParserHandle make wrangling JSON a pleasure
* JavaScript Extensions - develop production-ready server-side extensions using ready-to-go scaffolding

For more details, please read this [deeper dive](http://developer.marklogic.com/features/java-client-api-2)


