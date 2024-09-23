package vn.com.gsoft.consumer.model.dto;

import lombok.Data;
import vn.com.gsoft.consumer.model.system.Profile;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class DataType {
    private Integer type;
    private Long[] ids;
}
