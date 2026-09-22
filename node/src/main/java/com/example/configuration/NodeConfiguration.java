package com.example.configuration;

import com.example.utils.DocCryptoTool;
import com.example.utils.PhotoCryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfiguration {
    @Value("${salt.doc}")
    private String saltDoc;
    @Value("${salt.photo}")
    private String saltPhoto;

    @Bean
    public DocCryptoTool getDocCryptoTool() {
        return new DocCryptoTool(saltDoc);
    }

    @Bean
    public PhotoCryptoTool getPhotoCryptoTool() {
        return new PhotoCryptoTool(saltPhoto);
    }
}
