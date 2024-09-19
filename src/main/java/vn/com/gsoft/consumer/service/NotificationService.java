package vn.com.gsoft.consumer.service;

import java.util.List;

public interface NotificationService {
    void getDataSendNotificationAllKafka(String payload);
    void sendNotificationToCS(List<Long> id);
}
