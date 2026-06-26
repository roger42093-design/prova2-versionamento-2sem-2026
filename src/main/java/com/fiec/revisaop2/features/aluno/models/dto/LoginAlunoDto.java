package com.fiec.revisaop2.features.aluno.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginAlunoDto {
    private String email;
    private String password;
}
