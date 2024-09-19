package vn.com.gsoft.consumer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ChiTietHangHoaLuanChuyen")
public class ChiTietHangHoaLuanChuyen extends BaseEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ThuocId")
    private Integer thuocId;
    @Column(name = "MaGiaoDich")
    private String maGiaoDich;
    @Column(name = "SoLuong")
    private BigDecimal soLuong;
    @Column(name = "TrangThai")
    private Integer trangThai;
    @Column(name = "MaCoSoGui")
    private String maCoSoGui;
    @Column(name = "MaCoSoNhan")
    private String maCoSoNhan;
    @Column(name = "IdLuanChuyen")
    private Integer idLuanChuyen;
    @Column(name = "ThoiHan")
    private Date thoiHan;
    @Transient
    private String tenCoSo;
    @Transient
    private String diaChi;
    @Transient
    private String soDienThoai;
    @Transient
    private String tenThuoc;
    @Transient
    private String soLo;
    @Transient
    private Date hanDung;
}

