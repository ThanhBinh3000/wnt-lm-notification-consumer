package vn.com.gsoft.consumer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.consumer.entity.Thuocs;

import java.util.List;

@Repository
public interface ThuocsRepository extends CrudRepository<Thuocs, Long> {
    List<Thuocs> findByGroupIdMappingAndRecordStatusIDAndNhaThuocMaNhaThuocIn(Long groupIdMapping,
                                                                              Integer recordStatusId,
                                                                              Object[] maNhaThuocs);
}
