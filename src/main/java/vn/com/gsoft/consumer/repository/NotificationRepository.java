package vn.com.gsoft.consumer.repository;

import org.springframework.data.repository.CrudRepository;
import vn.com.gsoft.consumer.entity.Notification;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
}
