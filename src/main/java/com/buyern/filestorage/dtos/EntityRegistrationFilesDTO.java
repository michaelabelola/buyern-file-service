package com.buyern.filestorage.dtos;

import lombok.Data;

import java.io.File;

@Data
public class EntityRegistrationFilesDTO {
    private long entityId;
    private File logo;
    private File logoDark;
    private File coverImage;
    private File coverImageDark;

    @Data
    public static class EntityRegistrationFilesByteArrayDTO {
        private long entityId;
        private byte[] logo;
        private byte[] logoDark;
        private byte[] coverImage;
        private byte[] coverImageDark;

    }
}
