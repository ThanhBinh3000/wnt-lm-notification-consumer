package vn.com.gsoft.consumer.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.gsoft.consumer.constant.NotificationContains;
import vn.com.gsoft.consumer.constant.RecordStatusContains;
import vn.com.gsoft.consumer.entity.Notification;
import vn.com.gsoft.consumer.model.dto.NhaThuocReq;
import vn.com.gsoft.consumer.model.dto.WrapData;
import vn.com.gsoft.consumer.model.system.NhaThuocs;
import vn.com.gsoft.consumer.model.system.Profile;
import vn.com.gsoft.consumer.repository.*;
import vn.com.gsoft.consumer.repository.feign.NhaThuocFeign;
import vn.com.gsoft.consumer.repository.feign.UserProfileFeign;
import vn.com.gsoft.consumer.service.NotificationService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NhaThuocFeign nhaThuocFeign;
    @Autowired
    HangHoaLuanChuyenRepository luanChuyenRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    InventoryRepository inventoryRepository;
    @Autowired
    ThuocsRepository thuocsRepository;
    @Autowired
    NhaThuocsRepository nhaThuocsRepository;


    public List<NhaThuocs> searchListNhaThuocByArea(NhaThuocReq req) {
        ObjectMapper objectMapper = new ObjectMapper();
        var nhaThuocs = nhaThuocFeign.searchList(req).getData();
        return null;
    }

    @Override
    public void getDataKafka(String payload) {
        Gson gson = new Gson();
        TypeToken<WrapData<List<Long>>> typeToken = new TypeToken<>() {};
        WrapData<List<Long>> data = gson.fromJson(payload, typeToken.getType());
        var ids = data.getData();
        sendNotificationToCS(ids);

    }

    @Override
    public void sendNotificationToCS(List<Long> ids){
        ids.forEach(id->{
            var hanghoa = luanChuyenRepository.findById(id).get();
            if(hanghoa != null){
                //tim khu vu can luan chuyen va het ton
                var req = new NhaThuocReq();
                req.setCityId(hanghoa.getCitiId());
                req.setRegionId(hanghoa.getRegionId());
                req.setWardId(hanghoa.getWardId());
                var cs = nhaThuocsRepository.findByCityIdAndRegionIdAndWardIdAndHoatDong(
                        hanghoa.getCitiId(),
                        hanghoa.getRegionId(),
                        hanghoa.getWardId(),
                        true);
                if(cs != null){
                    //lay ra ma cs
                    var codes = cs.stream().filter(x->x.getMaNhaThuoc() != hanghoa.getMaCoSo())
                            .map(x->x.getMaNhaThuoc()).toArray();
                    //lay ra nhung thuoc thuoc co so do
                    var drugs = thuocsRepository.findByGroupIdMappingAndRecordStatusIDAndNhaThuocMaNhaThuocIn
                            (Long.valueOf(hanghoa.getThuocId()),
                                    (int) RecordStatusContains.ACTIVE,
                                    codes);
                    if(drugs != null){
                        var drugIds = drugs.stream().map(x->x.getId()).toArray();
                        //lấy ra thuốc id còn tồn
                        var inventories = inventoryRepository.findByDrugIDInAndRecordStatusIDAndLastValueLessThan(
                                drugIds, (int)RecordStatusContains.ACTIVE, 5
                        );
                        if(inventories != null){
                            var csNhanTB = inventories.stream().map(x->x.getDrugStoreID()).toList();
                            //tao thong bao cho tung cs lan can
                            var thuoc = thuocsRepository.findById(Long.valueOf(hanghoa.getThuocId()));
                            var content = String.format("Cơ sở %s địa chỉ %s có mặt hàng %s cần luân chuyển, nếu bạn quan tâm click vào để xem chi tiết",
                                    hanghoa.getTenCoSo(), hanghoa.getDiaChi(), thuoc.get().getTenThuoc());
                            csNhanTB.forEach(x->{
                                Notification notification = new Notification();
                                notification.setDrugStoreId(x);
                                notification.setContents(content);
                                notification.setTitle(content);
                                notification.setLink("http://10.0.2.140/transfer/hang-luan-chuyen/list?id=" + hanghoa.getId());
                                notification.setResourceID(0);
                                notification.setStoreId(0);
                                notification.setCreateDate(new Date());
                                notification.setNotificationTypeID(NotificationContains.THONG_BAO_LIEN_MINH);
                                notificationRepository.save(notification);
                            });
                        }
                    }
                }
            }
        });

    }
}
