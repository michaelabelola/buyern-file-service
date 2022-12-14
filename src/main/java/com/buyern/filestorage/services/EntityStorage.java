package com.buyern.filestorage.services;

import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.PublicAccessType;
import com.buyern.filestorage.exceptions.BadRequestException;
import com.buyern.filestorage.exceptions.EntityAlreadyExistsException;
import com.buyern.filestorage.exceptions.RecordNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import java.io.File;
import java.io.IOException;

@Slf4j
@Data
public class EntityStorage {
    private BlobServiceClient blobServiceClient;
    private BlobContainerClient blobPublicContainerClient;
    private BlobContainerClient blobPrivateContainerClient;
    private final String entityId;

    public EntityStorage(String entityId, BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
        this.entityId = entityId;
        this.blobPublicContainerClient = blobServiceClient.getBlobContainerClient(containerNameParser(entityId, false));
        this.blobPrivateContainerClient = blobServiceClient.getBlobContainerClient(containerNameParser(entityId, true));
    }

    public String uploadToPublicStorage(MultipartFile file, String destination) {
        String uploadedFileUrl = uploadToStorage(file, destination, blobPublicContainerClient);
        if (uploadedFileUrl == null)
            throw new BadRequestException("file cant be uploaded to entity");
        log.info(uploadedFileUrl);
        return uploadedFileUrl;
    }

    public String uploadToPublicStorage(File file, String destination) {
//        log.warn("filename "+ file.getName());
//        log.warn("file space " + String.valueOf(file.getTotalSpace()));
        String uploadedFileUrl = uploadToStorage(file, destination, blobPublicContainerClient);
        log.warn("*******");
        log.info(uploadedFileUrl);
        return uploadedFileUrl;
    }

    public String uploadToPrivateStorage(MultipartFile file, String destination) {
        String uploadedFileUrl = uploadToStorage(file, destination, blobPrivateContainerClient);
        log.info(uploadedFileUrl);
        return uploadedFileUrl;
    }

    public String uploadToPrivateStorage(File file, String destination) {
        String uploadedFileUrl = uploadToStorage(file, destination, blobPrivateContainerClient);
        log.info(uploadedFileUrl);
        return uploadedFileUrl;
    }

    public String uploadToStorage(MultipartFile file, String destination, BlobContainerClient blobContainerClient) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(destination);
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return blobClient.getBlobUrl();
        } catch (BlobStorageException ex) {
            ex.printStackTrace();
            throw new RecordNotFoundException("Container not found");
        } catch (Exception ex) {
            return null;
        }
    }

    public String uploadToStorage(File file, String destination, BlobContainerClient blobContainerClient) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(destination);
            blobClient.upload(BinaryData.fromFile(file.toPath()), true);
            return blobClient.getBlobUrl();
        } catch (BlobStorageException ex) {
            ex.printStackTrace();
            throw new RecordNotFoundException("Container not found");
        } catch (Exception ex) {
            return null;
        }
    }

    public static String containerNameParser(String entityId, boolean isPrivate) {
        if (isPrivate)
            return entityId + "-private";
        else return String.valueOf(entityId);
    }

    BlobContainerClient createPublicContainerClient() {
        try {
            this.blobPublicContainerClient = blobServiceClient.createBlobContainerWithResponse(containerNameParser(entityId, false), null, PublicAccessType.CONTAINER, Context.NONE).getValue();
            return blobPublicContainerClient;
        } catch (BlobStorageException ex) {
            if (ex.getStatusCode() == 409) {
                throw new EntityAlreadyExistsException("entity storage account already exists");
            } else {
                throw new ServerErrorException("Can't create public storage account for entity", ex);
            }
        } catch (Exception ex) {
            throw new ServerErrorException("Can't create public storage account for entity", ex);
        }
    }

    BlobContainerClient createPrivateContainerClient() {
        try {
            this.blobPrivateContainerClient = blobServiceClient.createBlobContainerWithResponse(containerNameParser(entityId, true), null, null, Context.NONE).getValue();
            return blobPrivateContainerClient;
        } catch (Exception ex) {
            throw new ServerErrorException("Can't create private storage account for entity", ex);
        }
    }

    private void deletePrivateContainer() {
        deleteContainer(blobPrivateContainerClient);
    }

    private void deletePublicContainer() {
        deleteContainer(blobPublicContainerClient);
    }

    private void deleteContainer(BlobContainerClient blobContainerClient) {
        try {
            blobContainerClient.delete();
            log.info("Delete completed%n");
        } catch (BlobStorageException error) {
            if (error.getErrorCode().equals(BlobErrorCode.CONTAINER_NOT_FOUND)) {
                log.info("Delete failed. Container was not found %n");
            }
        }
    }
}
