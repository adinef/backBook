package net.fp.backBook.services;

import com.mongodb.client.gridfs.model.GridFSFile;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.FileNotFound;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.repositories.FileRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
public class StorageServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private GridFsOperations gridFsOperations;

    @InjectMocks
    private StorageServiceImpl storageService;

    @Test
    public void insertSuccess() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        String id = "1";
        String name = "name";

        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getName()).thenReturn(name);
        when(this.fileRepository.insert(inputStream, name)).thenReturn(id);

        Assert.assertEquals(id, this.storageService.store(multipartFile));

        verify(this.fileRepository, times(1)).insert(inputStream, name);
    }

    @Test(expected = AddException.class)
    public void insertFailure() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        String name = "name";

        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getName()).thenReturn(name);
        when(this.fileRepository.insert(inputStream, name)).thenThrow(RuntimeException.class);

        this.storageService.store(multipartFile);
    }

    @Test
    public void loadSuccess() {
        GridFSFile gridFSFile = mock(GridFSFile.class);
        GridFsResource resource = mock(GridFsResource.class);
        String id = "1";

        when(this.fileRepository.findById(id)).thenReturn(gridFSFile);
        when(gridFsOperations.getResource(gridFSFile)).thenReturn(resource);

        Assert.assertEquals(resource, this.storageService.load(id));

        verify(this.fileRepository, times(1)).findById(id);
    }

    @Test(expected = FileNotFound.class)
    public void loadNullFailure() {
        String id = "1";

        when(this.fileRepository.findById(id)).thenReturn(null);

        this.storageService.load(id);
    }

    @Test(expected = GetException.class)
    public void loadExceptionFailure() {
        String id = "1";

        when(this.fileRepository.findById(id)).thenThrow(RuntimeException.class);

        this.storageService.load(id);
    }

    @Test
    public void deleteSuccess() {
        String id = "1";

        doNothing().when(this.fileRepository).deleteById(id);

        this.storageService.delete(id);

        verify(this.fileRepository, times(1)).deleteById(id);
    }

    @Test(expected = DeleteException.class)
    public void deleteFailure() {
        String id = "1";

        doThrow(RuntimeException.class).when(this.fileRepository).deleteById(id);

        this.storageService.delete(id);
    }
}
