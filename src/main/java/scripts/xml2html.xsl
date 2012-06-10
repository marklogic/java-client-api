<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:map="http://marklogic.com/xdmp/map"
    xmlns="http://www.w3.org/1999/xhtml">

<xsl:param name="context" as="map:map"/>
<xsl:param name="params"  as="map:map"/>

<xsl:output method="html"
    doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
    doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

<xsl:template match="/">
  <xsl:sequence select="map:put($context,'output-type','text/html')"/>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="product">
  <html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <xsl:apply-templates select="name" mode="title"/>
  </head>
  <body>
    <table>
    <tbody>
      <xsl:apply-templates mode="row"/>
    </tbody>
    </table>
  </body>
  </html>
</xsl:template>

<xsl:template match="name" mode="title">
  <title>
    <xsl:apply-templates/>
  </title>
</xsl:template>

<xsl:template match="name" mode="row">
  <tr>
  <td align="right"><i>Product</i></td>
  <th align="left">
    <xsl:apply-templates/>
  </th>
  </tr>
</xsl:template>

<xsl:template match="industry|description" mode="row">
  <xsl:variable name="local-name" select="local-name(.)"/>
  <tr>
  <td align="right">
    <i>
      <xsl:sequence select="concat(upper-case(substring($local-name,1,1)),substring($local-name,2))"/>
    </i>
  </td>
  <td>
    <xsl:apply-templates/>
  </td>
  </tr>
</xsl:template>

</xsl:stylesheet>