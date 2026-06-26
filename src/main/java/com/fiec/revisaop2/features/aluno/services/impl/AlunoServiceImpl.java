package com.fiec.revisaop2.features.aluno.services.impl;

import com.fiec.revisaop2.features.aluno.models.dto.LoginAlunoDto;
import com.fiec.revisaop2.features.aluno.models.dto.RegisterAlunoDto;
import com.fiec.revisaop2.features.aluno.models.dto.ResponseAlunoDto;
import com.fiec.revisaop2.features.aluno.models.entities.Aluno;
import com.fiec.revisaop2.features.aluno.repositories.AlunoRepository;
import com.fiec.revisaop2.features.aluno.services.AlunoService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class AlunoServiceImpl implements AlunoService {

    private static final String TOPICO_MENSAGENS = "mensagens";

    private final AlunoRepository alunoRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // ===================== REGISTRO =====================

    @Override
    public void registraAluno(RegisterAlunoDto registerAlunoDto) {
        Aluno aluno = Aluno.builder()
                .name(registerAlunoDto.getName())
                .email(registerAlunoDto.getEmail())
                .password(registerAlunoDto.getPassword())
                .fcmToken(registerAlunoDto.getFcmToken())
                .build();

        aluno = alunoRepository.save(aluno);

        // Publica evento no Kafka em vez de notificar direto
        String mensagem = "REGISTRO," + aluno.getId();
        kafkaTemplate.send(TOPICO_MENSAGENS, mensagem);
        log.info("Evento de registro publicado no Kafka: {}", mensagem);
    }

    // ===================== LOGIN =====================

    @Override
    public ResponseAlunoDto loginAluno(LoginAlunoDto loginAlunoDto) {
        Optional<Aluno> alunoEncontrado = alunoRepository.findByEmailAndPassword(
                loginAlunoDto.getEmail(),
                loginAlunoDto.getPassword()
        );

        if (alunoEncontrado.isEmpty()) {
            throw new RuntimeException("Email ou senha inválidos.");
        }

        Aluno aluno = alunoEncontrado.get();

        return ResponseAlunoDto.builder()
                .name(aluno.getName())
                .email(aluno.getEmail())
                .fcmToken(aluno.getFcmToken())
                .imageUrl(aluno.getImageUrl())
                .build();
    }


    @Override
    public void insereImagem(MultipartFile multipartFile, String userId) {
        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        String bucket = "fiec-versionamento-46137";

        try {
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

            Optional<Aluno> alunoOptional = alunoRepository.findById(Integer.parseInt(userId));
            if (alunoOptional.isPresent()) {
                Aluno aluno = alunoOptional.get();
                aluno.setImageUrl(fileName);
                alunoRepository.save(aluno);

                // Publica evento no Kafka em vez de notificar direto
                String mensagem = "FOTO," + aluno.getId() + "," + fileName;
                kafkaTemplate.send(TOPICO_MENSAGENS, mensagem);
                log.info("Evento de foto publicado no Kafka: {}", mensagem);
            } else {
                log.warn("Aluno com id {} não encontrado ao atualizar imagem.", userId);
            }

        } catch (Exception e) {
            log.error("Erro ao enviar imagem para o S3: {}", e.getMessage());
        }
    }

    // ===================== CONSUMER KAFKA =====================

    /**
     * Escuta o tópico "mensagens" e dispara a notificação push correspondente.
     * Formato esperado:
     *   "REGISTRO,<alunoId>"
     *   "FOTO,<alunoId>,<nomeArquivo>"
     */
    @KafkaListener(topics = TOPICO_MENSAGENS, groupId = "${spring.kafka.consumer.group-id}")
    public void escutaMensagens(String mensagem) {
        log.info("Mensagem recebida do Kafka: {}", mensagem);

        String[] partes = mensagem.split(",", 3);
        String tipo = partes[0];
        Integer alunoId = Integer.parseInt(partes[1]);

        Optional<Aluno> alunoOptional = alunoRepository.findById(alunoId);
        if (alunoOptional.isEmpty()) {
            log.warn("Aluno com id {} não encontrado ao processar mensagem do Kafka.", alunoId);
            return;
        }

        Aluno aluno = alunoOptional.get();

        switch (tipo) {
            case "REGISTRO" -> enviarNotificacao(
                    aluno.getFcmToken(),
                    "Bem-vindo!",
                    "Seu cadastro foi realizado com sucesso."
            );
            case "FOTO" -> enviarNotificacao(
                    aluno.getFcmToken(),
                    "Foto enviada!",
                    "Sua foto foi enviada e está sendo processada."
            );
            default -> log.warn("Tipo de mensagem desconhecido: {}", tipo);
        }
    }

    // ===================== NOTIFICAÇÃO PUSH =====================

    private void enviarNotificacao(String fcmToken, String titulo, String corpo) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("Tentativa de enviar notificação sem fcmToken. Ignorando.");
            return;
        }

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                        Notification.builder()
                                .setTitle(titulo)
                                .setBody(corpo)
                                .build()
                )
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Notificação enviada com sucesso. ID: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Erro ao enviar notificação push: {}", e.getMessage());
        }
    }
}