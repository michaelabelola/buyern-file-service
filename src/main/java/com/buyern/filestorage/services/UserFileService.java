package com.buyern.filestorage.services;

import com.buyern.filestorage.dtos.FileUploadDto;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.File;

@Data
@Service
public class UserFileService {
    private final UserStorage userStorage;
    private static final String TEMP_DIR = "java.io.tmpdir";

    public String uploadUserProfilePicture(FileUploadDto fileUploadDto) {
        return userStorage.uploadToPublicStorage(fileUploadDto.getFile(), fileUploadDto.getName() + "/");
    }

    public String uploadUserProfilePicture(String userUid, File toFile, String contentType) {
        return userStorage.uploadToPublicStorage(toFile, userUid + "/image." + contentType);
    }
}