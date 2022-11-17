package com.buyern.filestorage.services;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.Context;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.PublicAccessType;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;

import java.io.File;
import java.io.IOException;

@Data
@Service
@ConstructorBinding
public class FileService {
    private final BlobServiceClient blobServiceClient;
    private final Logger logger = LoggerFactory.getLogger(FileService.class);
    private ContainerHandler containerHandler;
    private BlobHandler blobHandler;

    public String upload(BlobContainerClient containerClient, MultipartFile file, String destination) {
        try {
            BlobClient blobClient = blobHandler.blobClient(containerClient, destination);
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return blobClient.getBlobUrl();
        } catch (IOException ex) {
            throw new RuntimeException("can't convert file to stream");
        } catch (Exception ex) {
            throw new RuntimeException("Error uploading to storage server");
        }
    }

    public String upload(BlobContainerClient containerClient, File file, String destination) {
        try {
            BlobClient blobClient = blobHandler.blobClient(containerClient, destination);
            blobClient.uploadFromFile(file.getPath(), true);
            return blobClient.getBlobUrl();
        } catch (Exception ex) {
            throw new RuntimeException("Error uploading to storage server");
        }
    }

    public boolean uploadFile(BlobClient blobClient, String pathToFile) throws IOException {
        System.out.println("\nUploading to Blob storage as blob:\n\t" + blobClient.getBlobUrl());
// Upload the blob
        blobClient.uploadFromFile(pathToFile);
        return true;
    }

    /**
     * <h3>Upload file to Container</h3>
     *
     * @param containerName the name of the container the file will be uploaded to.
     * @param destination   path the file will be located at
     * @param file          the file to be uploaded
     */
    public String uploadToContainer(String containerName, MultipartFile file, String destination) {
        String uploadedFileUrl = upload(containerHandler.getContainerClient(containerName), file, destination);
        if (uploadedFileUrl == null)
            return upload(containerHandler.createContainerClient(containerName), file, destination);
        return uploadedFileUrl;
    }

    public String uploadToContainer(Long containerName, MultipartFile file, String destination) {
        String uploadedFileUrl = upload(containerHandler.getContainerClient(containerName.toString()), file, destination);
        if (uploadedFileUrl == null)
            return upload(containerHandler.createContainerClient(containerName, null), file, destination);
        return uploadedFileUrl;
    }

    /**
     * <h3>Upload file to Container</h3>
     *
     * @param containerName the name of the container the file will be uploaded to.
     * @param destination   path the file will be located at
     * @param file          the file to be uploaded
     */
    public String uploadToContainer(String containerName, File file, String destination) {
        String uploadedFileUrl = upload(containerHandler.getContainerClient(containerName), file, destination);
        if (uploadedFileUrl == null)
            return upload(containerHandler.createContainerClient(containerName), file, destination);
        System.out.println(uploadedFileUrl);
        return uploadedFileUrl;
    }
    //    public String uploadToEntityContainer(long entityId, MultipartFile file, String destination) {
//        String uploadedFileUrl = upload(getContainerClient(String.valueOf(entityId)), file, destination);
//        if (uploadedFileUrl == null)
//            return upload(createContainerClient(String.valueOf(entityId)), file, destination);
//        return uploadedFileUrl;
//    }
//
//    public String uploadToEntityContainer(long entityId, File file, String destination) {
//        String uploadedFileUrl = upload(getContainerClient(String.valueOf(entityId)), file, destination);
//        if (uploadedFileUrl == null)
//            return upload(createContainerClient(usersContainerName), file, destination);
//        return uploadedFileUrl;
//    }

    private BlobContainerClient containerClient(String containerName) {
        try {
            return blobServiceClient.createBlobContainer(containerName);
        } catch (Exception ex) {
            return blobServiceClient.getBlobContainerClient(containerName);
        }
    }

    public class ContainerHandler {

        public BlobContainerClient getContainerClient(String containerName) {
            return blobServiceClient.getBlobContainerClient(containerName);
        }

        public BlobContainerClient createContainerClient(String containerName) {
            try {
                return blobServiceClient.createBlobContainerWithResponse(containerName, null, null, Context.NONE).getValue();
            } catch (Exception ex) {
                throw new ServerErrorException("Can't create storage account for entity", ex);
            }
        }

        public BlobContainerClient createContainerClient(Long containerName, @Nullable PublicAccessType accessType) {
            String containerNameString = "";
            //container name must be at least 3 characters long
            if (containerName < 10) containerNameString = "00" + containerName;
            else if (containerName < 100) containerNameString = "0" + containerName;
            try {
                return blobServiceClient.createBlobContainerWithResponse(containerNameString, null, accessType, Context.NONE).getValue();
            } catch (Exception ex) {
                throw new ServerErrorException("Can't create storage account for entity", ex);
            }
        }

        private void deleteContainer(BlobContainerClient blobContainerClient) {
            try {
                blobContainerClient.delete();
                logger.info("Delete completed%n");
            } catch (BlobStorageException error) {
                if (error.getErrorCode().equals(BlobErrorCode.CONTAINER_NOT_FOUND)) {
                    logger.info("Delete failed. Container was not found %n");
                }
            }
        }
    }

    public static class BlobHandler {
        // Get a reference to a blob
        public BlobClient blobClient(BlobContainerClient containerClient, String fileName) {
            return containerClient.getBlobClient(fileName);
        }

        public PagedIterable<BlobItem> listBlob(BlobContainerClient containerClient) {
            return containerClient.listBlobs();
        }
    }

}