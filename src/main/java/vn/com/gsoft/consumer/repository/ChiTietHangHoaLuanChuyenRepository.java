package vn.com.gsoft.consumer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.com.gsoft.consumer.entity.ChiTietHangHoaLuanChuyen;
import vn.com.gsoft.consumer.entity.HangHoaLuanChuyen;

@Repository
public interface ChiTietHangHoaLuanChuyenRepository extends CrudRepository<ChiTietHangHoaLuanChuyen, Long> {

}
