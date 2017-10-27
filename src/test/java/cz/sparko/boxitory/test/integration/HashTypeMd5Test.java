package cz.sparko.boxitory.test.integration;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"box.checksum=md5"})
public class HashTypeMd5Test extends HashTypeTest {
    @Override
    String expectedAlg() {
        return "md5";
    }
}
