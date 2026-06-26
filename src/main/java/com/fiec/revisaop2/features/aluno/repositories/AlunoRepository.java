package com.fiec.revisaop2.features.aluno.repositories;

import com.fiec.revisaop2.features.aluno.models.entities.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
    Optional<Aluno> findByEmailAndPassword(String email, String password);
}
