package com.fiec.revisaop2.features.aluno.models.dto;

import lombok.Data;

@Data
public class RegisterAlunoDto {
    private String email;
    private String password;
    private String name;
}
