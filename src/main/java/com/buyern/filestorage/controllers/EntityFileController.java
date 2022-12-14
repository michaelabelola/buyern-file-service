package com.buyern.filestorage.controllers;

import com.buyern.filestorage.dtos.EntityFileUploadDTO;
import com.buyern.filestorage.dtos.ResponseDTO;
import com.buyern.filestorage.services.EntityFileService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
@Slf4j
@Data
@RestController
@RequestMapping("api/v1/")
public class EntityFileController {
    private final EntityFileService entityFileService;

    @PostMapping("entity/uploadFile")
    public ResponseEntity<ResponseDTO<String>> entityUploadFile(@ModelAttribute @Validated EntityFileUploadDTO fileUploadDto) {
        log.warn(fileUploadDto.toString());
        fileUploadDto.setEntityUid(fileUploadDto.getEntityUid().toLowerCase(Locale.ROOT));
        return ResponseEntity.ok(new ResponseDTO<>("00", "SUCCESSFUL", entityFileService.uploadEntityRegistrationFiles(fileUploadDto)));
    }
    @PostMapping("entity/upload")
    public ResponseEntity<ResponseDTO<String>> entityUploadFile2(@RequestBody @Validated EntityFileUploadDTO.EntityRegistrationFilesByteArrayDTO fileUploadDto) {
        fileUploadDto.setEntityUid(fileUploadDto.getEntityUid().toLowerCase(Locale.ROOT));
        return ResponseEntity.ok(new ResponseDTO<>("00", "SUCCESSFUL", entityFileService.uploadEntityRegistrationFiles(fileUploadDto)));
    }

    @PostMapping("entity/register/{entityId}")
    public ResponseEntity<ResponseDTO<Boolean>> registerEntityStorage(@PathVariable(name = "entityId") String entityId) {
        return ResponseEntity.ok(new ResponseDTO<>("00", "REGISTERED", entityFileService.registerEntity(entityId.toLowerCase(Locale.ROOT))));
    }
}
