<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- Define layout -->
    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="invoice-page" page-height="11in" page-width="8.5in"
                                       margin-top="0.5in" margin-bottom="0.5in" margin-left="0.5in" margin-right="0.5in">
                    <fo:region-body margin-top="2cm" margin-bottom="2cm"/>
                    <fo:region-after extent="1cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="invoice-page">
                <!-- Define the footer content -->
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block text-align="center" font-weight="bold">Powered by <fo:inline color="blue">IncreffPOS</fo:inline></fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-size="18pt" font-weight="bold" text-align="center">INVOICE</fo:block>
                    <fo:block space-after="1cm"/>
                    <fo:block>
                        <xsl:text>Order Code: </xsl:text>
                        <xsl:value-of select="invoice/orderCode"/>
                    </fo:block>
                    <fo:block>
                        <xsl:text>Invoiced Time: </xsl:text>
                        <xsl:value-of select="invoice/orderInvoicedTime"/>
                    </fo:block>
                    <fo:block space-after="1cm"/>
                    <fo:table>
                        <fo:table-header>
                            <fo:table-row font-weight="bold">
                                <fo:table-cell border="solid" text-align="center" padding="3pt">
                                    <fo:block>Serial Number</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" text-align="center" padding="3pt">
                                    <fo:block>Product Name</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" text-align="center" padding="3pt">
                                    <fo:block>Barcode</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" text-align="center" padding="3pt">
                                    <fo:block>Quantity</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" text-align="center" padding="3pt">
                                    <fo:block>Selling Price</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border="solid" text-align="center" padding="3pt">
                                    <fo:block>Amount</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>
                        <fo:table-body>
                            <xsl:for-each select="invoice/orderItems/orderItem">
                                <fo:table-row>
                                    <fo:table-cell border="solid" text-align="center" padding="3pt">
                                        <fo:block><xsl:value-of select="serialNumber"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="solid" text-align="start" padding="3pt">
                                        <fo:block wrap-option="wrap"><xsl:value-of select="productName"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="solid" text-align="start" padding="3pt">
                                        <fo:block ><xsl:value-of select="barcode"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="solid" text-align="center" padding="3pt">
                                        <fo:block><xsl:value-of select="quantity"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="solid" text-align="center" padding="3pt">
                                        <fo:block>
                                            <xsl:attribute name="font-size">
                                                <xsl:choose>
                                                    <xsl:when test="string-length(sellingPrice) &gt; 8">9pt</xsl:when>
                                                    <xsl:otherwise>10pt</xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:attribute>
                                            <xsl:value-of select="sellingPrice"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="solid" text-align="end" padding="3pt">
                                        <fo:block>
                                            <xsl:attribute name="font-size">
                                                <xsl:choose>
                                                    <xsl:when test="string-length(amount) &gt; 8">9pt</xsl:when>
                                                    <xsl:otherwise>10pt</xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:attribute>
                                            <xsl:value-of select="amount"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                    <fo:block space-after="1cm"/>
                    <!-- Display total quantity and total amount in bold -->
                    <fo:block font-weight="bold">
                        <xsl:text>Total Quantity: </xsl:text>
                        <xsl:value-of select="invoice/totalQuantity"/>
                    </fo:block>
                    <fo:block font-weight="bold">
                        <xsl:text>Total Amount: </xsl:text>
                        <xsl:value-of select="invoice/totalAmount"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
