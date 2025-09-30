<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


    <xsl:template match="/">
        <items>
            <xsl:apply-templates select="ul/li"/>
        </items>
    </xsl:template>


    <xsl:template match="li">
        <xsl:param name="parent-id" select="0"/>

        <item>
            <xsl:attribute name="id">
                <xsl:value-of select="@data-id"/>
            </xsl:attribute>
            <xsl:attribute name="parentid">
                <xsl:value-of select="$parent-id"/>
            </xsl:attribute>
            <xsl:attribute name="author">
                <xsl:value-of select="b"/>
            </xsl:attribute>

            <xsl:value-of select="span"/>
        </item>


        <xsl:apply-templates select="ul/li">
            <xsl:with-param name="parent-id" select="@data-id"/>
        </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>