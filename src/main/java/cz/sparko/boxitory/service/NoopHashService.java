package cz.sparko.boxitory.service;

import java.io.File;

public class NoopHashService implements HashService {

    @Override
    public String getHashType() {
        return null;
    }

    @Override
    public String getChecksum(File file) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().getName().equals(NoopHashService.class.getName());
    }
}
