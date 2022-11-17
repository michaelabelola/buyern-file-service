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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
@Data
public class UserStorage {
    private BlobServiceClient blobServiceClient;
    private BlobContainerClient blobPublicContainerClient;
    private BlobContainerClient blobPrivateContainerClient;
    private static final String PUBLIC_CONTAINER_NAME = "users";
    private static final String PRIVATE_CONTAINER_NAME = "users_private";

    public UserStorage(BlobServiceClient blobServiceClient) {
        this.blobServiceClient = blobServiceClient;
        this.blobPublicContainerClient = blobServiceClient.getBlobContainerClient(PUBLIC_CONTAINER_NAME);
        this.blobPrivateContainerClient = blobServiceClient.getBlobContainerClient(PRIVATE_CONTAINER_NAME);
//        try {
////            main is glastid
//            //asset manager
//            blobServiceClient.createBlobContainerWithResponse("aseedle", null, PublicAccessType.CONTAINER, Context.NONE).getValue();
//            // finance manager
//            blobServiceClient.createBlobContainerWithResponse("financiend", null, PublicAccessType.CONTAINER, Context.NONE).getValue();
//            // ecommerce manager
//            blobServiceClient.createBlobContainerWithResponse("traddon", null, PublicAccessType.CONTAINER, Context.NONE).getValue();
//            //logistics
//            blobServiceClient.createBlobContainerWithResponse("wtics", null, PublicAccessType.CONTAINER, Context.NONE).getValue();
//        } catch (Exception ex) {
//            throw new ServerErrorException("Can't create public storage account for entity", ex);
//        }
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
//            throw new RuntimeException("can't convert file to stream");
            return null;
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

    private BlobContainerClient createPublicContainerClient() {
        try {
            return blobServiceClient.createBlobContainerWithResponse(PUBLIC_CONTAINER_NAME, null, PublicAccessType.CONTAINER, Context.NONE).getValue();
        } catch (Exception ex) {
            throw new ServerErrorException("Can't create public storage account for User", ex);
        }
    }

    private BlobContainerClient createPrivateContainerClient() {
        try {
            return blobServiceClient.createBlobContainerWithResponse(PRIVATE_CONTAINER_NAME, null, null, Context.NONE).getValue();
        } catch (Exception ex) {
            throw new ServerErrorException("Can't create private storage account for User", ex);
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
