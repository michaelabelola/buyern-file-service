package com.buyern.filestorage.dtos;

import com.buyern.filestorage.enums.Container;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serializable;

@Data
public class EntityFileUploadDTO {
    private String entityUid;
    //    should be saved in the entities public container or private container
    private Container container;
    private MultipartFile file;
    private String name;

    @Data
    public static class EntityRegistrationFilesByteArrayDTO implements Serializable {
        private String entityUid;
        //    should be saved in the entities public container or private container
        private boolean isPublic;
        private byte[] file;
        private String name;
        private String contentType;

    }
}
