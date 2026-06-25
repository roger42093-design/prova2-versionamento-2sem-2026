package com.fiec.revisaop2.features.eventos;

import com.fiec.revisaop2.config.FirebaseConfig;
import com.fiec.revisaop2.features.aluno.repositories.AlunoRepository;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class KafkaConsumer {

    @Autowired
    private final FirebaseConfig firebaseConfig;
    private final AlunoRepository alunoRepository;

    @KafkaListener(topics = "mensagens", groupId = "my-consumer-group")
    public void consumeEvent(String message) throws FirebaseMessagingException, IOException {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        String fcmToken = "";
        String imageUrl = "";
        // Pegue a mensagem (que é uma string) e divida ela em id e imageurl.
        // Com o id você encontra o usuario no repositorio e depois
        // o fcmToken.



        FirebaseMessaging msg = FirebaseMessaging.getInstance(firebaseConfig.firebaseApp());
        msg.send(Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setBody(imageUrl)
                        .setTitle("Sua imagem")
                        .build())
                .build());
    }
}
