package com.example.configuration;

import com.example.utils.DocCryptoTool;
import com.example.utils.PhotoCryptoTool;
import com.example.utils.UserCryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeConfiguration {
    @Value("${salt.doc}")
    private String docSalt;
    @Value("${salt.photo}")
    private String photoSalt;
    @Value("${salt.user}")
    private String userSalt;

    @Bean
    public DocCryptoTool getDocCryptoTool() {
        return new DocCryptoTool(docSalt);
    }

    @Bean
    public PhotoCryptoTool getPhotoCryptoTool() {
        return new PhotoCryptoTool(photoSalt);
    }

    @Bean
    public UserCryptoTool getUserCryptoTool() {
        return new UserCryptoTool(userSalt);
    }
}
