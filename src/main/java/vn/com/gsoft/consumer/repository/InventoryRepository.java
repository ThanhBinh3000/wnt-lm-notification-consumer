package vn.com.gsoft.consumer.repository;

import org.springframework.data.repository.CrudRepository;
import vn.com.gsoft.consumer.entity.Inventory;

import java.util.List;

public interface InventoryRepository extends CrudRepository<Inventory, Long> {
    List<Inventory> findByDrugIDInAndRecordStatusIDAndLastValueLessThan(Object[] drugIds, int active, int i);
}