package com.buyern.filestorage;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileServerConfig {
    @Bean
    public BlobServiceClient blobServiceClient(@Value("${spring.cloud.azure.storage.blob.connection-string}") String azureBlobConnString) {
        return new BlobServiceClientBuilder()
                .connectionString(azureBlobConnString)
                .buildClient();
    }
}
