package com.buyern.filestorage.controllers;

import com.buyern.filestorage.dtos.EntityRegistrationFilesDTO;
import com.buyern.filestorage.dtos.ResponseDTO;
import com.buyern.filestorage.services.EntityFileService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data
@RestController
@RequestMapping("api/v1/")
public class EntityFileController {
    private final EntityFileService entityFileService;

    @PostMapping("entity/signUpFiles")
    public ResponseEntity<ResponseDTO<JsonNode>> userUploadProfileImage(@RequestBody EntityRegistrationFilesDTO.EntityRegistrationFilesByteArrayDTO registrationFilesDTO) {
        return ResponseEntity.ok(new ResponseDTO<>("00", "SUCCESSFUL", entityFileService.uploadEntityRegistrationFiles(registrationFilesDTO)));
    }
}
