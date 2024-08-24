package vn.com.gsoft.consumer.repository.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import vn.com.gsoft.consumer.model.system.BaseResponse;

@FeignClient(name = "wnt-lm-security")
public interface UserProfileFeign {
    @GetMapping("/profile")
    BaseResponse getProfile();
}
