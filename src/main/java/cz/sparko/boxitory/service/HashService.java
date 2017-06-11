package cz.sparko.boxitory.service;

import java.io.File;

public interface HashService {
    String getHashType();
    String encode(File file);
}
