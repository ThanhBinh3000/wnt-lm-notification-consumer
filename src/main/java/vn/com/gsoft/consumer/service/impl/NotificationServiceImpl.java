package vn.com.gsoft.consumer.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.com.gsoft.consumer.constant.NotificationContains;
import vn.com.gsoft.consumer.constant.RecordStatusContains;
import vn.com.gsoft.consumer.constant.StatusLuanChuyenContains;
import vn.com.gsoft.consumer.entity.ChiTietHangHoaLuanChuyen;
import vn.com.gsoft.consumer.entity.Notification;
import vn.com.gsoft.consumer.model.dto.DataType;
import vn.com.gsoft.consumer.model.dto.NhaThuocReq;
import vn.com.gsoft.consumer.model.dto.NhaThuocRes;
import vn.com.gsoft.consumer.model.dto.WrapData;
import vn.com.gsoft.consumer.model.system.NhaThuocs;
import vn.com.gsoft.consumer.model.system.Profile;
import vn.com.gsoft.consumer.repository.*;
import vn.com.gsoft.consumer.repository.feign.NhaThuocFeign;
import vn.com.gsoft.consumer.repository.feign.UserProfileFeign;
import vn.com.gsoft.consumer.service.NotificationService;

import java.util.Arrays;
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
    @Autowired
    ChiTietHangHoaLuanChuyenRepository chiTietHangHoaLuanChuyenRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public List<NhaThuocs> searchListNhaThuocByArea(NhaThuocReq req) {
        ObjectMapper objectMapper = new ObjectMapper();
        var nhaThuocs = nhaThuocFeign.searchList(req).getData();
        return null;
    }

    public void getDataSendNotificationAllKafka(String payload) {
        Gson gson = new Gson();
        TypeToken<WrapData<DataType>> typeToken = new TypeToken<>() {};
        WrapData<DataType> data = gson.fromJson(payload, typeToken.getType());
        var ids = data.getData().getIds();
        switch (data.getData().getType()){
            case NotificationContains.THONG_BAO_LIEN_MINH ->  {
                sendNotificationToCS(Arrays.stream(ids).toList());
                break;
            }
            case NotificationContains.YEU_CAU_THONG_TIN ->  {
                sendNotificationYeuCau(ids[0]);
                break;
            }
            case NotificationContains.PHAN_HOI_THONG_TIN ->  {
                sendNotificationPhanHoi(ids[0]);
                break;
            }
        }
        messagingTemplate.convertAndSend("/topic/notifications", "call api");
    }

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
                                notification.setLink("/transfer/hang-luan-chuyen/list?id=" + hanghoa.getId());
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

    public void sendNotificationYeuCau(long id){
        var ctGD = chiTietHangHoaLuanChuyenRepository.findById(id);
        if(ctGD.isEmpty()) return;
        var nhaThuoc = nhaThuocsRepository.findByMaNhaThuoc(ctGD.get().getMaCoSoNhan());
        var thuoc = thuocsRepository.findById(Long.valueOf(ctGD.get().getThuocId()));
        var content = String.format("Cơ sở %s địa chỉ %s đang quan tâm mặt hàng %s muốn bạn cung cấp thông tin liên hệ, click vào để xem chi tiết",
                nhaThuoc.get().getTenNhaThuoc(), nhaThuoc.get().getDiaChi(), thuoc.get().getTenThuoc());
        Notification notification = new Notification();
        notification.setDrugStoreId(ctGD.get().getMaCoSoGui());
        notification.setContents(content);
        notification.setTitle(content);
        notification.setLink("/transfer/hang-luan-chuyen/list?tab=4");
        notification.setResourceID(0);
        notification.setStoreId(0);
        notification.setCreateDate(new Date());
        notification.setNotificationTypeID(NotificationContains.THONG_BAO_LIEN_MINH);
        notificationRepository.save(notification);
    }

    public void sendNotificationPhanHoi(long id){
        var ctGD = chiTietHangHoaLuanChuyenRepository.findById(id);
        if(ctGD.isEmpty()) return;
        var nhaThuoc = nhaThuocsRepository.findByMaNhaThuoc(ctGD.get().getMaCoSoGui());
        var thuoc = thuocsRepository.findById(Long.valueOf(ctGD.get().getThuocId()));
        var content = "";
        var tab = 0;
        if(ctGD.get().getTrangThai() == StatusLuanChuyenContains.DANG_XU_LY){
            content = String.format("Cơ sở %s địa chỉ %s có mặt hàng %s đã đồng ý cung cấp thông tin, click vào để xem chi tiết",
                    nhaThuoc.get().getTenNhaThuoc(), nhaThuoc.get().getDiaChi(), thuoc.get().getTenThuoc());
            tab = 3;
        }else {
            content = String.format("Cơ sở %s địa chỉ %s có mặt hàng %s đã từ chối cung cấp thông tin, click vào để xem chi tiết",
                    nhaThuoc.get().getTenNhaThuoc(), nhaThuoc.get().getDiaChi(), thuoc.get().getTenThuoc());
            tab = 2;
        }

        Notification notification = new Notification();
        notification.setDrugStoreId(ctGD.get().getMaCoSoNhan());
        notification.setContents(content);
        notification.setTitle(content);
        notification.setLink("/transfer/hang-luan-chuyen/list?tab=" +  tab);
        notification.setResourceID(0);
        notification.setStoreId(0);
        notification.setCreateDate(new Date());
        notification.setNotificationTypeID(NotificationContains.THONG_BAO_LIEN_MINH);
        notificationRepository.save(notification);
    }
}
