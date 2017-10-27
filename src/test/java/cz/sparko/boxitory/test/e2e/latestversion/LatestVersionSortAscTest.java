package cz.sparko.boxitory.test.e2e.latestversion;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"box.sort_desc=false"})
public class LatestVersionSortAscTest extends LatestVersionSortDescTest {
}
