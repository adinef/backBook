package net.fp.backBook.repositories;

import com.mongodb.client.gridfs.model.GridFSFile;

import java.io.InputStream;

public interface FileRepository {

    String insert(InputStream inputStream, String name);

    GridFSFile findById(String id);

    void deleteById(String id);
}
