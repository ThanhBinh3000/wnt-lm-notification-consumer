package vn.com.gsoft.consumer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.com.gsoft.consumer.entity.NhaThuocs;

import java.util.List;

@Repository
public interface NhaThuocsRepository extends CrudRepository<NhaThuocs, Long> {
    List<NhaThuocs> findByCityIdAndRegionIdAndWardIdAndHoatDong(Long citiId, Long regionId, Long wardId, boolean hoatDong);
}
