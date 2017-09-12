# CHANGELOG

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


