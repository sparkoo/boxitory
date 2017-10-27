package cz.sparko.boxitory.test.integration.latestversion;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"box.sort_desc=false"})
public class LatestVersionSortAscTest extends LatestVersionSortDescTest {
}
