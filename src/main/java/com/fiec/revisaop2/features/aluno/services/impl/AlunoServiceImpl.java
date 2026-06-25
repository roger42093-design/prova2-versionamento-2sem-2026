package com.fiec.revisaop2.features.aluno.services.impl;

import com.fiec.revisaop2.features.aluno.models.dto.LoginAlunoDto;
import com.fiec.revisaop2.features.aluno.models.dto.RegisterAlunoDto;
import com.fiec.revisaop2.features.aluno.models.dto.ResponseAlunoDto;
import com.fiec.revisaop2.features.aluno.services.AlunoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AlunoServiceImpl implements AlunoService {

    // Aqui seria interessante voce importar tanto o repositorio quanto
    // o kafka producer

    @Override
    public void registraAluno(RegisterAlunoDto registerAlunoDto) {
        // Salve isso no banco usando o repositorio
    }

    @Override
    public ResponseAlunoDto loginAluno(LoginAlunoDto loginAlunoDto) {
        // Use aquela funcao de encontrar por email e password.
        // Retorne o usuario encontrado
        return null;
    }

    @Override
    public void insereImagem(MultipartFile multipartFile, String userId) {
        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        String bucket = null; // de o nome de prova2

        try{
            S3Client s3Client = S3Client.builder()
                    .region(Region.US_EAST_2)
                    .credentialsProvider(DefaultCredentialsProvider.builder().build())
                    .build();
            PutObjectRequest request = PutObjectRequest.builder()
                    .contentType(multipartFile.getContentType())
                    .key(fileName)
                    .bucket(bucket)
                    .build();
            s3Client.putObject(request,
                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            s3Client.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        // Descomente a linha abaixo uma vez que você importar o servico kafka
        // kafkaProducer.sendMessage("mensagens", id+","+fileName);



    }


}
