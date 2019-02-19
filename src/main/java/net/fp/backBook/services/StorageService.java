package net.fp.backBook.services;

import org.springframework.core.io.Resource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    @Secured({"ROLE_USER"})
    String store(MultipartFile file);

    @Secured({"ROLE_USER"})
    Resource load(String id);

    @Secured({"ROLE_USER"})
    void delete(String id);

}
