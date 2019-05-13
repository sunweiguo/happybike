package cn.oursnail.happybike.service;

import cn.oursnail.happybike.entity.User;
import cn.oursnail.happybike.resp.ApiResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 9:43
 * @DESC
 * @CONTACT 317758022@qq.com
 */
public interface IUserService {
    String login(String data,String key);

    ApiResult modifyNickName(User user);

    ApiResult sendVercode(String mobile, String ipFromRequest);

    ApiResult<String> uploadHeadImg(MultipartFile file, Long userId) throws IOException;
}
