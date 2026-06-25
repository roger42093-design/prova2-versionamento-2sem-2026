package com.fiec.revisaop2.features.aluno.services;

import com.fiec.revisaop2.features.aluno.models.dto.LoginAlunoDto;
import com.fiec.revisaop2.features.aluno.models.dto.RegisterAlunoDto;
import com.fiec.revisaop2.features.aluno.models.dto.ResponseAlunoDto;
import org.springframework.web.multipart.MultipartFile;

public interface AlunoService {
    void registraAluno(RegisterAlunoDto registerAlunoDto);

    void insereImagem(MultipartFile multipartFile, String userId);

    ResponseAlunoDto loginAluno(LoginAlunoDto loginAlunoDto);
}
