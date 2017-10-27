package cz.sparko.boxitory.test.e2e.hashtype;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"box.checksum=md5"})
public class HashTypeMd5Test extends HashTypeTest {
    @Override
    String expectedAlg() {
        return "md5";
    }
}
