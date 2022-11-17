package com.buyern.filestorage.services;

import com.azure.storage.blob.BlobServiceClient;
import com.buyern.filestorage.dtos.EntityRegistrationFilesDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public JsonNode uploadEntityRegistrationFiles(EntityRegistrationFilesDTO.EntityRegistrationFilesByteArrayDTO registrationFilesDTO) {
//        Path logoPath;
//        Path logoDarkPath;
//        Path coverImagePath;
//        Path coverImageDarkPath;
        String logoSavedLocation = upload(registrationFilesDTO.getEntityId(), "logo", registrationFilesDTO.getLogo());
        String logoDarkSavedLocation = upload(registrationFilesDTO.getEntityId(), "logoDark", registrationFilesDTO.getLogoDark());
        String coverSavedLocation = upload(registrationFilesDTO.getEntityId(), "cover", registrationFilesDTO.getCoverImage());
        String coverDarkSavedLocation = upload(registrationFilesDTO.getEntityId(), "coverDark", registrationFilesDTO.getCoverImageDark());
        ObjectNode responseNode = new ObjectMapper().createObjectNode();
        responseNode.put("logo", logoSavedLocation);
        responseNode.put("logoDark", logoDarkSavedLocation);
        responseNode.put("cover", coverSavedLocation);
        responseNode.put("coverDark", coverDarkSavedLocation);
        return responseNode;

    }

    private String upload(Long entityId, String destination, byte[] image) {

        Path path;
        if (image == null) {
            return null;
        }
        //get path location file will be moved to locally
        path = Paths.get(System.getProperty(TEMP_DIR),
                UUID.randomUUID().toString());
        try {
//                move file
            Files.write(path, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EntityStorage entityStorage = new EntityStorage(entityId, blobServiceClient);
        return entityStorage.uploadToPublicStorage(path.toFile(), destination);

    }
}
