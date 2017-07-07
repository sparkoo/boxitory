package cz.sparko.boxitory.service;


public interface HashService {
    String getHashType();
    String getChecksum(String string);
}
