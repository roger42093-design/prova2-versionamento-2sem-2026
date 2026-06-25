package com.fiec.revisaop2.features.aluno.models.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ResponseAlunoDto {
    private UUID id;
    private String email;
    private String name;
    private String fcmToken;
    private String imageUrl;
}
