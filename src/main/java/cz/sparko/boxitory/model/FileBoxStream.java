package cz.sparko.boxitory.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileBoxStream implements BoxStream {
    private final File file;

    public FileBoxStream(File file) {
        this.file = file;
    }

    @Override
    public String getFilename() {
        return file.getName();
    }

    @Override
    public InputStream getStream() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }
}
