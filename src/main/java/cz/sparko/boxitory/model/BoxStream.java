package cz.sparko.boxitory.model;

import java.io.IOException;
import java.io.InputStream;

public interface BoxStream {
    String getFilename();

    InputStream getStream() throws IOException;

    long fileSize();
}
