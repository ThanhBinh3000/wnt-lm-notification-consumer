package vn.com.gsoft.consumer.service;

public interface NotificationService {
    void getDataKafka(String payload);
    void sendNotificationToCS(Long id);
}
