package com.buyern.filestorage.services;

import com.azure.core.util.BinaryData;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.PublicAccessType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import java.io.File;
import java.io.IOException;

@Slf4j
@Data
public class Storage {
    private BlobServiceClient blobServiceClient;
    private BlobContainerClient blobContainerClient;

    public Storage(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
        this.blobContainerClient = blobServiceClient.getBlobContainerClient("");
    }

    public String uploadToStorage(MultipartFile file, String destination, BlobContainerClient blobContainerClient) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(destination);
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return blobClient.getBlobUrl();
        } catch (Exception ex) {
            return null;
//            throw new RuntimeException("can't convert file to stream");
        }//            throw new RuntimeException("Error uploading to storage server");

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
            return String.valueOf(entityId + "_private");
        else return String.valueOf(entityId);
    }

    private BlobContainerClient createContainerClient(String containerName, PublicAccessType accessType) {
        try {
            return blobServiceClient.createBlobContainerWithResponse(containerName, null, accessType, Context.NONE).getValue();
        } catch (Exception ex) {
            throw new ServerErrorException("Can't create private storage account for entity", ex);
        }
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
