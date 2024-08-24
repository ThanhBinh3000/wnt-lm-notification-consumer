package vn.com.gsoft.consumer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.consumer.entity.HangHoaLuanChuyen;

import java.util.List;

@Repository
public interface HangHoaLuanChuyenRepository extends CrudRepository<HangHoaLuanChuyen, Long> {

}
