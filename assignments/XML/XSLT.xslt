<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:key name="teach" match="teacher" use="." />
    <xsl:key name="keyw" match="keyword" use="." />

    <xsl:template match="/courses">
        <html>
            <head>
                <title>Список курсов</title>
            </head>
            <body>
                <h1>Наши курсы</h1>


                <h3>Курсы, которые читает Борисов И.О.</h3>
                <ul>
                    <xsl:for-each select="course[key('teach', 'Борисов И.О.')]">
                        <li><xsl:value-of select="title"/></li>
                    </xsl:for-each>
                </ul>


                <h3>Курсы, в которых рассматривается тема 'XML'</h3>
                <ul>
                    <xsl:for-each select="course[key('keyw', 'XML')][title != 'веб-разработка']">
                        <li><xsl:value-of select="title"/></li>
                    </xsl:for-each>
                </ul>


                <h3>Курсы, которые читает 'Борисов И.О.' и в которых есть тема XSLT</h3>
                <ul>
                    <xsl:for-each select="course[key('teach', 'Борисов И.О.')][keywords/keyword='XSLT']">
                        <li><xsl:value-of select="title"/></li>
                    </xsl:for-each>
                </ul>

            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>