package com.grape.module.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatFileDto {

    private String fileKey;

    private String originalFileName;

    private String savedFileName;

    private String filePath;

    private String fileExtension;

    private Long fileSize;

    private String mimeType;

    private String createdUserKey;

    private LocalDateTime createdAt;
}