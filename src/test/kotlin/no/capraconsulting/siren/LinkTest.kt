package no.capraconsulting.siren

import no.capraconsulting.siren.internal.getResource
import no.capraconsulting.siren.internal.parseAndVerifyRootRelaxed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URI

class LinkTest {

    @Test
    fun testParse() {
        val json = getResource("LinkTest.siren.json")
        val root = parseAndVerifyRootRelaxed(json)

        assertEquals(1, root.links.size.toLong())
        assertEquals("city", root.links[0].firstClass)
    }

    @Test
    fun testLinkWithoutType() {
        val link = Link.newBuilder("containsChild", URI.create("http://localhost:8080")).build()
        assertTrue(link.clazz.isEmpty())
        assertNull(link.firstClass)
        assertFalse(link.toRaw().containsKey("class"))
    }

    @Test
    fun testLinkWithType() {
        val link = Link
            .newBuilder("containsChild", URI.create("http://localhost:8080"))
            .clazz("dummyclass")
            .build()

        assertEquals(1, link.clazz.size.toLong())
        assertEquals("dummyclass", link.firstClass)
        assertTrue(link.toRaw().containsKey("class"))
    }
}
