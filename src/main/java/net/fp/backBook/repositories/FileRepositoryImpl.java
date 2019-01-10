package net.fp.backBook.repositories;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository
public class FileRepositoryImpl implements FileRepository {

    private GridFsTemplate gridFsTemplate;

    @Autowired
    FileRepositoryImpl(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public String insert(InputStream inputStream, String name) {
        return gridFsTemplate.store(inputStream, name).toString();
    }

    @Override
    public GridFSFile findById(String id) {
        return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
    }

    @Override
    public void deleteById(String id) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(id)));
    }
}
