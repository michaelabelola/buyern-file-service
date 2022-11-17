package com.buyern.filestorage.services;

import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.PublicAccessType;
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
    private final Long entityId;

    public EntityStorage(Long entityId, BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
        this.entityId = entityId;
        this.blobPublicContainerClient = blobServiceClient.getBlobContainerClient(containerNameParser(entityId, false));
        this.blobPrivateContainerClient = blobServiceClient.getBlobContainerClient(containerNameParser(entityId, true));
    }

    public String uploadToPublicStorage(MultipartFile file, String destination) {

        String uploadedFileUrl = uploadToStorage(file, destination, blobPublicContainerClient);
        if (uploadedFileUrl == null) {
            blobPublicContainerClient = createPublicContainerClient();
            uploadedFileUrl = uploadToStorage(file, destination, blobPublicContainerClient);
        }
        log.info(uploadedFileUrl);
        return uploadedFileUrl;
    }

    public String uploadToPublicStorage(File file, String destination) {
        String uploadedFileUrl = uploadToStorage(file, destination, blobPublicContainerClient);
        if (uploadedFileUrl == null) {
            blobPublicContainerClient = createPublicContainerClient();
            uploadedFileUrl = uploadToStorage(file, destination, blobPublicContainerClient);
        }
        log.info(uploadedFileUrl);
        return uploadedFileUrl;
    }

    public String uploadToPrivateStorage(MultipartFile file, String destination) {
        String uploadedFileUrl = uploadToStorage(file, destination, blobPrivateContainerClient);
        if (uploadedFileUrl == null) {
            blobPrivateContainerClient = createPrivateContainerClient();
            uploadedFileUrl = uploadToStorage(file, destination, blobPrivateContainerClient);
        }
        log.info(uploadedFileUrl);
        return uploadedFileUrl;
    }

    public String uploadToPrivateStorage(File file, String destination) {
        String uploadedFileUrl = uploadToStorage(file, destination, blobPrivateContainerClient);
        if (uploadedFileUrl == null) {
            blobPrivateContainerClient = createPrivateContainerClient();
            uploadedFileUrl = uploadToStorage(file, destination, blobPrivateContainerClient);
        }
        log.info(uploadedFileUrl);
        return uploadedFileUrl;
    }

    public String uploadToStorage(MultipartFile file, String destination, BlobContainerClient blobContainerClient) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(destination);
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return blobClient.getBlobUrl();
        } catch (IOException ex) {
            return null;
//            throw new RuntimeException("can't convert file to stream");
        } catch (Exception ex) {
            return null;
//            throw new RuntimeException("Error uploading to storage server");
        }
    }

    public String uploadToStorage(File file, String destination, BlobContainerClient blobContainerClient) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(destination);
            blobClient.upload(BinaryData.fromFile(file.toPath()), true);
            return blobClient.getBlobUrl();
        } catch (Exception ex) {
            return null;
//            throw new RuntimeException("Error uploading to storage server");
        }
    }

    public static String containerNameParser(Long entityId, boolean isPrivate) {
        //container name must be at least 3 characters long
        if (entityId < 10) return "00" + entityId;
        else if (entityId < 100) return "0" + entityId;
        if (isPrivate)
            return entityId + "_private";
        else return String.valueOf(entityId);
    }

    private BlobContainerClient createPublicContainerClient() {
        try {
            this.blobPublicContainerClient = blobServiceClient.createBlobContainerWithResponse(containerNameParser(entityId, false), null, PublicAccessType.CONTAINER, Context.NONE).getValue();
            return blobPublicContainerClient;
        } catch (Exception ex) {
            throw new ServerErrorException("Can't create public storage account for entity", ex);
        }
    }

    private BlobContainerClient createPrivateContainerClient() {
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
