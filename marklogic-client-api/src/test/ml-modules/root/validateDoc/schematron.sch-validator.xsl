<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet extension-element-prefixes="xdmp" xdmp:dialect="1.0-ml" version="2.0" xmlns:schold="http://www.ascc.net/xml/schematron" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xdmp="http://marklogic.com/xdmp" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--Implementers: please note that overriding process-prolog or process-root is
      the preferred method for meta-stylesheets to use where possible. -->
    <xsl:param name="archiveDirParameter"/>
    <xsl:param name="archiveNameParameter"/>
    <xsl:param name="fileNameParameter"/>
    <xsl:param name="fileDirParameter"/>
    <xsl:variable name="document-uri"><xsl:value-of select="document-uri(/)"/></xsl:variable>
    <!--PHASES-->
    <!--PROLOG-->
    <xsl:output method="xml" omit-xml-declaration="no" standalone="yes" indent="yes" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"/>
    <!--XSD TYPES FOR XSLT2-->
    <!--KEYS AND FUNCTIONS-->
    <!--DEFAULT RULES-->
    <!--MODE: SCHEMATRON-SELECT-FULL-PATH-->
    <!--This mode can be used to generate an ugly though full XPath for locators-->
    <xsl:template match="*|/|object-node()" mode="schematron-select-full-path"><xsl:apply-templates select="." mode="schematron-get-full-path"/></xsl:template>
    <!--MODE: SCHEMATRON-FULL-PATH-->
    <!--This mode can be used to generate an ugly though full XPath for locators-->
    <xsl:template match="*|/|object-node()" mode="schematron-get-full-path"><xsl:value-of select="xdmp:path(.)"/></xsl:template>
    <xsl:template match="@*" mode="schematron-get-full-path"><xsl:value-of select="xdmp:path(.)"/></xsl:template>
    <!--MODE: SCHEMATRON-FULL-PATH-2-->
    <!--This mode can be used to generate prefixed XPath for humans-->
    <xsl:template match="node() | @*" mode="schematron-get-full-path-2"><xsl:for-each select="ancestor-or-self::*"><xsl:text>/</xsl:text><xsl:value-of select="name(.)"/><xsl:if test="preceding-sibling::*[name(.)=name(current())]"><xsl:text>[</xsl:text><xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/><xsl:text>]</xsl:text></xsl:if></xsl:for-each><xsl:if test="not(self::*)"><xsl:text/>/@<xsl:value-of select="name(.)"/></xsl:if></xsl:template>
    <!--MODE: SCHEMATRON-FULL-PATH-3-->
    <!--This mode can be used to generate prefixed XPath for humans
      (Top-level element has index)-->
    <xsl:template match="node() | @*" mode="schematron-get-full-path-3"><xsl:for-each select="ancestor-or-self::*"><xsl:text>/</xsl:text><xsl:value-of select="name(.)"/><xsl:if test="parent::*"><xsl:text>[</xsl:text><xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/><xsl:text>]</xsl:text></xsl:if></xsl:for-each><xsl:if test="not(self::*)"><xsl:text/>/@<xsl:value-of select="name(.)"/></xsl:if></xsl:template>
    <!--MODE: GENERATE-ID-FROM-PATH -->
    <xsl:template match="/" mode="generate-id-from-path"/>
    <xsl:template match="text()" mode="generate-id-from-path"><xsl:apply-templates select="parent::*" mode="generate-id-from-path"/><xsl:value-of select="concat('.text-', 1+count(preceding-sibling::text()), '-')"/></xsl:template>
    <xsl:template match="comment()" mode="generate-id-from-path"><xsl:apply-templates select="parent::*" mode="generate-id-from-path"/><xsl:value-of select="concat('.comment-', 1+count(preceding-sibling::comment()), '-')"/></xsl:template>
    <xsl:template match="processing-instruction()" mode="generate-id-from-path"><xsl:apply-templates select="parent::*" mode="generate-id-from-path"/><xsl:value-of select="concat('.processing-instruction-', 1+count(preceding-sibling::processing-instruction()), '-')"/></xsl:template>
    <xsl:template match="@*" mode="generate-id-from-path"><xsl:apply-templates select="parent::*" mode="generate-id-from-path"/><xsl:value-of select="concat('.@', name())"/></xsl:template>
    <xsl:template match="*" mode="generate-id-from-path" priority="-0.5"><xsl:apply-templates select="parent::*" mode="generate-id-from-path"/><xsl:text>.</xsl:text><xsl:value-of select="concat('.',name(),'-',1+count(preceding-sibling::*[name()=name(current())]),'-')"/></xsl:template>
    <!--MODE: GENERATE-ID-2 -->
    <xsl:template match="/" mode="generate-id-2">U</xsl:template>
    <xsl:template match="*" mode="generate-id-2" priority="2"><xsl:text>U</xsl:text><xsl:number level="multiple" count="*"/></xsl:template>
    <xsl:template match="node()" mode="generate-id-2"><xsl:text>U.</xsl:text><xsl:number level="multiple" count="*"/><xsl:text>n</xsl:text><xsl:number count="node()"/></xsl:template>
    <xsl:template match="@*" mode="generate-id-2"><xsl:text>U.</xsl:text><xsl:number level="multiple" count="*"/><xsl:text>_</xsl:text><xsl:value-of select="string-length(local-name(.))"/><xsl:text>_</xsl:text><xsl:value-of select="translate(name(),':','.')"/></xsl:template>
    <!--Strip characters-->
    <xsl:template match="text()|number-node()|boolean-node()|null-node()" priority="-1"/>
    <!--SCHEMA SETUP-->
    <xsl:template match="/"><svrl:schematron-output title="user-validation" schemaVersion="1.0" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"><xsl:comment><xsl:value-of select="$archiveDirParameter"/>
        <xsl:value-of select="$archiveNameParameter"/>
        <xsl:value-of select="$fileNameParameter"/>
        <xsl:value-of select="$fileDirParameter"/></xsl:comment><svrl:active-pattern><xsl:attribute name="document"><xsl:value-of select="document-uri(/)"/></xsl:attribute><xsl:attribute name="id">structural</xsl:attribute><xsl:attribute name="name">structural</xsl:attribute><xsl:apply-templates/></svrl:active-pattern><xsl:apply-templates select="/" mode="M3"/><svrl:active-pattern><xsl:attribute name="document"><xsl:value-of select="document-uri(/)"/></xsl:attribute><xsl:attribute name="id">co-occurence</xsl:attribute><xsl:attribute name="name">co-occurence</xsl:attribute><xsl:apply-templates/></svrl:active-pattern><xsl:apply-templates select="/" mode="M4"/></svrl:schematron-output></xsl:template>
    <!--SCHEMATRON PATTERNS-->
    <svrl:text xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl">user-validation</svrl:text>
    <!--PATTERN structural-->
    <!--RULE -->
    <xsl:template match="user" priority="1000" mode="M3"><svrl:fired-rule context="user" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="@id"/>
            <xsl:otherwise><svrl:failed-assert test="@id" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"><xsl:attribute name="location"><xsl:apply-templates select="." mode="schematron-select-full-path"/></xsl:attribute><svrl:text>user element must have an id attribute</svrl:text></svrl:failed-assert></xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="count(*) = 5"/>
            <xsl:otherwise><svrl:failed-assert test="count(*) = 5" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"><xsl:attribute name="location"><xsl:apply-templates select="." mode="schematron-select-full-path"/></xsl:attribute><svrl:text>
                user element must have 5 child elements: name, gender,
                age, score and result
            </svrl:text></svrl:failed-assert></xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="score/@total"/>
            <xsl:otherwise><svrl:failed-assert test="score/@total" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"><xsl:attribute name="location"><xsl:apply-templates select="." mode="schematron-select-full-path"/></xsl:attribute><svrl:text>score element must have a total attribute</svrl:text></svrl:failed-assert></xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="score/count(*) = 2"/>
            <xsl:otherwise><svrl:failed-assert test="score/count(*) = 2" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"><xsl:attribute name="location"><xsl:apply-templates select="." mode="schematron-select-full-path"/></xsl:attribute><svrl:text>score element must have two child elements</svrl:text></svrl:failed-assert></xsl:otherwise>
        </xsl:choose><xsl:apply-templates select="*|comment()|processing-instruction()" mode="M3"/></xsl:template>
    <xsl:template match="text()" priority="-1" mode="M3"/>
    <xsl:template match="@*|node()" priority="-2" mode="M3"><xsl:apply-templates select="*|comment()|processing-instruction()" mode="M3"/></xsl:template>
    <!--PATTERN co-occurence-->
    <!--RULE -->
    <xsl:template match="score" priority="1000" mode="M4"><svrl:fired-rule context="score" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"/>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="@total = test-1 + test-2"/>
            <xsl:otherwise><svrl:failed-assert test="@total = test-1 + test-2" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"><xsl:attribute name="location"><xsl:apply-templates select="." mode="schematron-select-full-path"/></xsl:attribute><svrl:text>
                total score must be a sum of test-1 and test-2 scores
            </svrl:text></svrl:failed-assert></xsl:otherwise>
        </xsl:choose>

        <!--ASSERT -->
        <xsl:choose>
            <xsl:when test="(@total gt 30 and ../result = 'pass') or               (@total le 30 and ../result = 'fail')"/>
            <xsl:otherwise><svrl:failed-assert test="(@total gt 30 and ../result = 'pass') or (@total le 30 and ../result = 'fail')" xmlns:axsl="http://www.w3.org/1999/XSL/TransformAlias" xmlns:svrl="http://purl.oclc.org/dsdl/svrl"><xsl:attribute name="location"><xsl:apply-templates select="." mode="schematron-select-full-path"/></xsl:attribute><svrl:text>
                if the score is greater than 30 then the result will be
                'pass' else 'fail'
            </svrl:text> <svrl:diagnostic-reference diagnostic="d1">
                the score does not match with the result</svrl:diagnostic-reference></svrl:failed-assert></xsl:otherwise>
        </xsl:choose><xsl:apply-templates select="*|comment()|processing-instruction()" mode="M4"/></xsl:template>
    <xsl:template match="text()" priority="-1" mode="M4"/>
    <xsl:template match="@*|node()" priority="-2" mode="M4"><xsl:apply-templates select="*|comment()|processing-instruction()" mode="M4"/></xsl:template>
</xsl:stylesheet>
