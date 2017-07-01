package cz.sparko.boxitory.service;

import java.io.File;

public class BlankHashService implements HashService{

    @Override
    public String getHashType() {
        return "";
    }

    @Override
    public String encode(File file) {
        return "";
    }
}
