package com.buyern.filestorage.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserFileUploadDto {
    private MultipartFile file;
    private String userUid;

    @Data
    public static class UserFileUploadByByteArrayDto {
        private byte[] file;
        private String userUid;
        private String contentType;
    }
}
