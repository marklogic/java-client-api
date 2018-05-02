<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:map="http://marklogic.com/xdmp/map"
    xmlns:search="http://marklogic.com/appservices/search"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="map">

<xsl:param name="context" as="map:map"/>
<xsl:param name="params"  as="map:map"/>

<xsl:output method="html"
    doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
    doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

<xsl:template match="/">
  <xsl:sequence select="map:put($context,'output-type','text/html')"/>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="search:response">
  <html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Search Results</title>
  </head>
  <body>
    <table>
    <tbody>
      <tr>
      <th align="left">
          URI
      </th>
      <th align="left">
          Score
      </th>
      <th align="left">
          Match
      </th>
      </tr>
      <xsl:apply-templates mode="row"/>
    </tbody>
    </table>
  </body>
  </html>
</xsl:template>

<xsl:template match="search:result" mode="row">
  <tr>
  <td align="left">
      <xsl:value-of select="./@uri"/>
  </td>
  <td align="left">
      <xsl:value-of select="./@score"/>
  </td>
  <td align="left">
      <xsl:value-of select="./search:snippet/search:match"/>
  </td>
  </tr>
</xsl:template>

</xsl:stylesheet>
