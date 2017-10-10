package cz.sparko.boxitory.service.noop;


import cz.sparko.boxitory.service.HashService;

public class NoopHashService implements HashService {

    @Override
    public String getHashType() {
        return null;
    }

    @Override
    public String getChecksum(String string) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().getName().equals(NoopHashService.class.getName());
    }
}
