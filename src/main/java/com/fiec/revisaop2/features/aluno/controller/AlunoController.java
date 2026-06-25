package com.fiec.revisaop2.features.aluno.controller;

import com.fiec.revisaop2.features.aluno.models.dto.LoginAlunoDto;
import com.fiec.revisaop2.features.aluno.models.dto.RegisterAlunoDto;
import com.fiec.revisaop2.features.aluno.models.dto.ResponseAlunoDto;
import com.fiec.revisaop2.features.aluno.services.AlunoService;
import com.fiec.revisaop2.features.usuario.models.dto.LoginUserDTO;
import com.fiec.revisaop2.features.usuario.models.dto.RegisterUserDTO;
import com.fiec.revisaop2.features.usuario.models.dto.UserResponseDTO;
import com.fiec.revisaop2.features.usuario.services.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/alunos")
@AllArgsConstructor
public class AlunoController {

    private AlunoService alunoService;

    @PostMapping("/register")
    ResponseEntity<Void> registraAluno(@RequestBody RegisterAlunoDto registerAlunoDto) {
        alunoService.registraAluno(registerAlunoDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    ResponseEntity<ResponseAlunoDto> logaAluno(@RequestBody LoginAlunoDto loginAlunoDto) {

        ResponseAlunoDto usuarioLogado = alunoService.loginAluno(loginAlunoDto);
        return ResponseEntity.ok().body(usuarioLogado);
    }

    @PutMapping(value = "/photo/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> atualizaImagem(MultipartFile multipartFile, @PathVariable("id") String userId) {
        alunoService.insereImagem(multipartFile, userId);
        return ResponseEntity.ok().build();
    }
}

