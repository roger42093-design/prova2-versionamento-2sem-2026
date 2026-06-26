package com.fiec.revisaop2.features.aluno.models.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
public class ResponseAlunoDto {
    private UUID id;
    private String email;
    private String name;
    private String fcmToken;
    private String imageUrl;
}
