package vn.com.gsoft.consumer.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.com.gsoft.consumer.service.NotificationService;


@SpringBootTest
@Slf4j
class PushDataRedis {
    @Autowired
    private NotificationService notificationService;
    @Test
    void saveData() throws Exception {
        notificationService.sendNotificationToCS(11L);
    }
}