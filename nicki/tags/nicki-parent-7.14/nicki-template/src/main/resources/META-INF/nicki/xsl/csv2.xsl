<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml">
 
 <xsl:output method="text" />
 
 <xsl:variable name="newline" select="string('&#xa;')" />
 <xsl:variable name="highcomma" >"</xsl:variable>
 <xsl:variable name="separator" select="string(';')" />
 
 <xsl:template match="/" >
  <xsl:apply-templates select="//table" />
 </xsl:template>
 
    <xsl:template match="table">
        <xsl:apply-templates select=".//comment()" mode="comment"/>
  <xsl:apply-templates select="node()|@*" />
    </xsl:template>
 
 <xsl:template match="row[column]">
  <xsl:apply-templates select="node()|@*" />
  <xsl:value-of select="$newline" />
 </xsl:template>
 
 <xsl:template match="header[column]">
  <xsl:apply-templates select="node()|@*" />
  <xsl:value-of select="$newline" />
 </xsl:template>
 
 <xsl:template match="column">
  <xsl:variable name="table" select="ancestor::table[1]" />
  <xsl:value-of select="$highcomma" />
  <xsl:copy-of select=".//text()" />
  <xsl:value-of select="$highcomma" />
  <xsl:value-of select="$separator" />
 </xsl:template>
 
 <xsl:template match="column[last()]">
  <xsl:variable name="table" select="ancestor::table[1]" />
  <xsl:value-of select="$highcomma" />
  <xsl:copy-of select=".//text()" />
  <xsl:value-of select="$highcomma" />
 </xsl:template>
 
 <xsl:template match="comment()" mode="comment">
  <xsl:text>#</xsl:text>
  <xsl:value-of select="normalize-space(.)" />
  <xsl:value-of select="$newline" />
 </xsl:template>
    
 <xsl:template match="node()|@*">
        <xsl:apply-templates select="node()|@*"/>
    </xsl:template>
</xsl:stylesheet>
