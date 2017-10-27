package cz.sparko.boxitory.test.integration;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"box.checksum=sha1"})
public class HashTypeSha1Test extends HashTypeTest {
    @Override
    String expectedAlg() {
        return "sha1";
    }
}
