<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:param name="myParam" />
    <xsl:template match="/">
        <result>
            <xsl:copy-of select="/"/>
            <hello>world</hello>
            <yourParam><xsl:value-of select="$myParam"/></yourParam>
        </result>
    </xsl:template>
</xsl:stylesheet>
