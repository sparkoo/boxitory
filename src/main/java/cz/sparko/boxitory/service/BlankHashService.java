package cz.sparko.boxitory.service;

import java.io.File;

public class BlankHashService implements HashService{

    @Override
    public String getHashType() {
        return "";
    }

    @Override
    public String getChecksum(File file) {
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        return true;
    }
}
