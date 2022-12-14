package com.buyern.filestorage.services;

import com.azure.storage.blob.BlobServiceClient;
import com.buyern.filestorage.dtos.EntityFileUploadDTO;
import com.buyern.filestorage.dtos.EntityRegistrationFilesDTO;
import com.buyern.filestorage.dtos.ResponseDTO;
import com.buyern.filestorage.enums.Container;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@Data
public class EntityFileService {
    private static final String TEMP_DIR = "java.io.tmpdir";
    private final BlobServiceClient blobServiceClient;
    private final FileService fileService;

    public String uploadEntityRegistrationFiles(EntityFileUploadDTO filesUploadDto) {
        return upload(filesUploadDto.getEntityUid(), filesUploadDto.getName(), filesUploadDto.getFile(), filesUploadDto.getContainer());

    }

    private String upload(String entityId, String destination, MultipartFile file, Container container) {
        if (file == null) {
            return null;
        }
        EntityStorage entityStorage = new EntityStorage(entityId, blobServiceClient);
        if (container == Container.PUBLIC)
            return entityStorage.uploadToPublicStorage(file, destination);
        else
            return entityStorage.uploadToPrivateStorage(file, destination);
    }

    private String uploadByteArray(String entityId, String destination, byte[] file, boolean isPublic) {
        if (file == null) {
            return null;
        }
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        try {
            Files.write(path, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EntityStorage entityStorage = new EntityStorage(entityId, blobServiceClient);
        if (isPublic)
            return entityStorage.uploadToPublicStorage(path.toFile(), destination);
        else
            return entityStorage.uploadToPrivateStorage(path.toFile(), destination);
    }

    public String uploadEntityRegistrationFiles(EntityFileUploadDTO.EntityRegistrationFilesByteArrayDTO fileUploadDto) {
        return uploadByteArray(fileUploadDto.getEntityUid(), fileUploadDto.getName(), fileUploadDto.getFile(), fileUploadDto.isPublic());
    }

    public Boolean registerEntity(String entityId) {
        EntityStorage entityStorage = new EntityStorage(entityId, blobServiceClient);
        entityStorage.createPublicContainerClient();
        entityStorage.createPrivateContainerClient();
        return true;
    }
}
