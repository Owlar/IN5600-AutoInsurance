package no.uio.ifi.oscarlr.in5600_autoinsurance.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HashTest {

    @Test
    public void givenString_whenHashString_thenReturnMD5Hash() {
        String expectedHash = "2e3817293fc275dbee74bd71ce6eb056";
        String actualHash = Hash.toMD5("lala");
        assertEquals(expectedHash, actualHash);
    }

}