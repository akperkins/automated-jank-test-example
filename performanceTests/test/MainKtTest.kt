package com.example

import org.junit.Test
import com.example.MainKtTest.*
import org.junit.Assert

/**
 * Created by Andre Perkins (akperkins1@gmail.com) on 6/6/17.
 */
class MainKtTest {
    @Test
    fun toJson_emptyMap_emptyObjectIsReturned() {
        val map = emptyMap<String, String>()
        val expected = "{}"
        val actual = map.toJson()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun toJson_regularMap_validJsonIsConstructed() {
        val map = mapOf(Pair("id", "5"), Pair("name", "Andre"))
        val expected = "{\n  \"id\": \"5\",\n  \"name\": \"Andre\"\n}"
        val actual = map.toJson()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun toXml_emptyMap_emptyObjectIsReturned() {
        val map = emptyMap<String, String>()
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gfxinfo />"
        val actual = map.toXml()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun toXml_regularMap_validXmlIsConstructed() {
        val map = mapOf(Pair("id", "5"), Pair("name", "Andre"))
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gfxinfo>\n    <id>\"5\"</id>\n    <name>\"Andre\"</name>\n</gfxinfo>"
        val actual = map.toXml()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun toXml_regularMap_andSomeKeyValuesHaveSpaceDelimiter_validXmlIsConstructed_andSpacesAreReplaced() {
        val map = mapOf(Pair("id", "5"), Pair("zip code", "99999"))
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gfxinfo>\n    <id>\"5\"</id>\n    <zip_code>\"99999\"</zip_code>\n</gfxinfo>"
        val actual = map.toXml()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun toXml_regularMap_andSomeKeyValuesStartWithDigit_validXmlIsConstructed_andKeysThatStartWithDigitPrependWithUnderscore() {
        val map = mapOf(Pair("id", "5"), Pair("1stzip", "2"))
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gfxinfo>\n    <id>\"5\"</id>\n    <_1stzip>\"2\"</_1stzip>\n</gfxinfo>"
        val actual = map.toXml()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun toXml_regularMap_andSomeKeyValuesStartWithDigitAndHaveSpaceDelimiter_validXmlIsConstructed_andKeysThatStartWithDigitPrependWithUnderscore() {
        val map = mapOf(Pair("id", "5"), Pair("90th percentile", "2"))
        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<gfxinfo>\n    <id>\"5\"</id>\n    <_90th_percentile>\"2\"</_90th_percentile>\n</gfxinfo>"
        val actual = map.toXml()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun extractValues_stringIsBlank_emptyMapIsReturned() {
        val stats = ""
        val expected = emptyMap<String, String>()
        val actual = stats.extractValues()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun extractValues_stringContainsEasilyMappableFields_theFieldsArePlacedInTheMap() {
        val stats = "janky frames: 49"
        val expected = mapOf("janky frames" to "49")
        val actual = stats.extractValues()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun extractValues_stringContainsExtraData_theMapIsEmpty() {
        val stats = "TextureCache 0 / 75497472"
        val expected = emptyMap<String, String>()
        val actual = stats.extractValues()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun extractValues_stringContainsEasilyMappableFields_andExtraData_theFieldsArePlacedInTheMap_andTheRandomDataIsIgnored() {
        val stats = "janky frames: 49\n TextureCache 0 / 75497472"
        val expected = mapOf("janky frames" to "49")
        val actual = stats.extractValues()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun extractValues_stringContainsEasilyMappableFields_andExtraDataInTheMiddleOfMappableFields_theFieldsArePlacedInTheMap_andTheRandomDataIsIgnored() {
        val stats = "janky frames: 49\n TextureCache 0 / 75497472 \nNumber Missed Vsync: 4"
        val expected = mapOf("janky frames" to "49", "Number Missed Vsync" to "4")
        val actual = stats.extractValues()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun extractValues_stringContainsJankyFramesAndPercentageOnTheSameLine_splitIntoTwoSeparateFieldsInMap() {
        val stats = "Janky frames: 4 (80.00%)"
        val expected = mapOf("Janky frames" to "4", "Janky frames Percentage" to "80.00%")
        val actual = stats.extractValues()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun isDigit_firstCharInStringIsDigit_returnsTrue() {
        val str = "89thPercentile"
        Assert.assertTrue(str.startsWithDigit())
    }

    @Test
    fun isDigit_firstCharInStringIsNotADigit_returnsFalse() {
        val str = "Jank"
        Assert.assertFalse(str.startsWithDigit())
    }

}