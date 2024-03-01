package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final Semaphore semaphore;
    private String authToken;

    public CrptApi(TimeUnit timeUnit, int requestLimit, String username, String password) throws IOException {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.semaphore = new Semaphore(requestLimit);
        this.authToken = authenticate(username, password);
    }

    public void createDocument(Document doc) throws InterruptedException, IOException {

        semaphore.acquire();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            // Создание запроса
            HttpPost request = new HttpPost("https://ismp.crpt.ru/api/v3/lk/documents/create");

            // Установка JWT токена
            request.addHeader("Authorization", "Bearer " + authToken);

            // Добавление JSON в тело запроса
            StringEntity entity = new StringEntity(buildJson(doc));
            request.setEntity(entity);

            // Отправка запроса
            httpClient.execute(request);

        } finally {
            semaphore.release();
        }

    }

    private String buildJson(Document doc) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(doc);
    }

    private String authenticate(String username, String password) throws IOException {
        // Запрос аутентификации и получение JWT токена
        return authToken;
    }
    private class Document {
        private Description description;
        private String docId;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String owner_inn;

        //и т.д.

    }
    class Description {
        private String participantInn;

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }
    }
}
