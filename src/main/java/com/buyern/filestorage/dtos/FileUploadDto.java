package com.buyern.filestorage.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class FileUploadDto {
    private String name;
    private MultipartFile file;
}
