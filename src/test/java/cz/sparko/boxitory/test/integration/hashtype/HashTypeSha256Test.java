package cz.sparko.boxitory.test.integration.hashtype;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"box.checksum=sha256"})
public class HashTypeSha256Test extends HashTypeTest {
    @Override
    String expectedAlg() {
        return "sha256";
    }
}
