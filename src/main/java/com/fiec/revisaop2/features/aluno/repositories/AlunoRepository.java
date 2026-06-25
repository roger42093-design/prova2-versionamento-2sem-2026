package com.fiec.revisaop2.features.aluno.repositories;

import com.fiec.revisaop2.features.aluno.models.entities.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
    // faça o metodo para retornar um Optional<Aluno> dado email e password
}
