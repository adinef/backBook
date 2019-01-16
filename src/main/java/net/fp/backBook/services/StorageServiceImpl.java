package net.fp.backBook.services;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.FileNotFound;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    private FileRepository fileRepository;

    private GridFsOperations gridFsOperations;

    @Autowired
    public StorageServiceImpl(FileRepository fileRepository, GridFsOperations gridFsOperations) {
        this.fileRepository = fileRepository;
        this.gridFsOperations = gridFsOperations;
    }

    @Override
    public String store(MultipartFile file) {
        try {
            return this.fileRepository.insert(file.getInputStream(), file.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AddException(e.getMessage(), e);
        }
    }

    @Override
    public Resource load(String id) {
        try {
            GridFSFile gridFSFile = this.fileRepository.findById(id);
            if (gridFSFile == null) {
                throw new FileNotFound("No file");
            }
            return gridFsOperations.getResource(gridFSFile);
        } catch (FileNotFound e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            this.fileRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AddException(e.getMessage(), e);
        }
    }
}
