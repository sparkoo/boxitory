package cz.sparko.boxitory.test.integration.hashtype;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"box.checksum=sha1"})
public class HashTypeSha1Test extends HashTypeTest {
    @Override
    String expectedAlg() {
        return "sha1";
    }
}
