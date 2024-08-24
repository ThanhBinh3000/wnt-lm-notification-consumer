package vn.com.gsoft.consumer.repository.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import vn.com.gsoft.consumer.model.dto.NhaThuocReq;
import vn.com.gsoft.consumer.model.system.BaseResponse;

@FeignClient(name = "wnt-lm-system")
public interface NhaThuocFeign {
    @PostMapping("/search-list")
    BaseResponse searchList(@RequestBody NhaThuocReq objReq);
}
