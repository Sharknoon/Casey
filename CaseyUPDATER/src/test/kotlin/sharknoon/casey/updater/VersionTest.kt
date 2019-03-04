package sharknoon.casey.updater

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class VersionTest {

    private val test = ""
    private val test2 = "1.2.3"
    private val test3 = ".6"
    private val test5 = "oasdgn"
    private val test6 = "sadg.sadg"

    @Test
    private fun testParsing() {
        test.toVersion().also { assertEquals("0", it.toString()) }
        test2.toVersion().also { assertEquals("1.2.3", it.toString()) }
        test3.toVersion().also { assertEquals("0.6", it.toString()) }
        test5.toVersion().also { assertEquals("0", it.toString()) }
        test6.toVersion().also { assertEquals("0.0", it.toString()) }
    }

    @Test
    private fun testComparing() {
        //Comparing empty Strings
        assertFalse { test.toVersion() > test5.toVersion() }
        assertFalse { test.toVersion() < test5.toVersion() }
        assertTrue { test.toVersion() == test5.toVersion() }
        assertTrue { test.toVersion() == test6.toVersion() }

        //Regualar comparing
        assertFalse { test3.toVersion() > test2.toVersion() }
        assertFalse { test3.toVersion() == test2.toVersion() }
        assertTrue { test3.toVersion() < test2.toVersion() }
    }
}