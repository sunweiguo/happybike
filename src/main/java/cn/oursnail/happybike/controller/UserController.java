package cn.oursnail.happybike.controller;

import cn.oursnail.happybike.entity.User;
import cn.oursnail.happybike.exception.HappyBikeException;
import cn.oursnail.happybike.resp.ApiResult;
import cn.oursnail.happybike.service.IUserService;
import cn.oursnail.happybike.vo.LoginInfo;
import cn.oursnail.happybike.vo.UserElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 9:45
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@RestController
@RequestMapping("/user/")
public class UserController extends BaseController{
    @Autowired
    private IUserService userService;

    @RequestMapping(value="/login",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<String> login(@RequestBody LoginInfo loginInfo){
        String data = loginInfo.getData();
        String key = loginInfo.getKey();
        if(StringUtils.isBlank(data) || StringUtils.isBlank(key)){
            throw new HappyBikeException("参数校验出错");
        }
        String token = userService.login(data,key);
        if(StringUtils.isNotBlank(token)){
            return ApiResult.createBySuccessMessageAndData("返回token成功",token);
        }else {
            throw new HappyBikeException("token生成错误");
        }
    }

    @RequestMapping("modifyNickName")
    public ApiResult modifyNickName(@RequestBody User user){
        UserElement ue = getCurrentUser();
        if(ue == null){
            return ApiResult.createByErrorMessage("用户未登陆");
        }
        user.setId(ue.getUserId());
        return userService.modifyNickName(user);
    }

    @RequestMapping("sendVercode")
    public ApiResult sendVercode(@RequestBody User user, HttpServletRequest request){
        return userService.sendVercode(user.getMobile(),getIpFromRequest(request));
    }

    @RequestMapping(value = "/uploadHeadImg", method = RequestMethod.POST)
    public ApiResult<String> uploadHeadImg(HttpServletRequest req, @RequestParam(required=false ) MultipartFile file) throws IOException {
        UserElement ue = getCurrentUser();
        if(ue == null){
            return ApiResult.createByErrorMessage("用户未登陆");
        }
        return userService.uploadHeadImg(file,ue.getUserId());
    }
}
