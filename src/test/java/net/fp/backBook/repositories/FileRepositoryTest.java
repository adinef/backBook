package net.fp.backBook.repositories;

import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

@RunWith(SpringRunner.class)
@DataMongoTest
@ComponentScan(basePackages = {"net.fp.backBook.repositories"})
public class FileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Before
    public void setUp() {
        this.clean();
    }

    @After
    public void tearDown() {
        this.clean();
    }

    private void clean() {
        this.gridFsTemplate.delete(new Query(Criteria.where("_id").exists(true)));
    }

    @Test
    public void test() {
        InputStream inputStream = new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        };

        String id = this.fileRepository.insert(inputStream, "name");

        Assert.assertNotNull(this.fileRepository.findById(id));

        this.fileRepository.deleteById(id);

        Assert.assertNull(this.fileRepository.findById(id));
    }

}
