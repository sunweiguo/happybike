package cn.oursnail.happybike.exception;

import cn.oursnail.happybike.resp.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 11:20
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandle {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResult handle(Exception e){
        if(e instanceof HappyBikeException){
            HappyBikeException happyBikeException = (HappyBikeException)e;
            return ApiResult.createByErrorCodeAndMessage(happyBikeException.getCode(),happyBikeException.getMessage());
        }
        log.error("【系统异常】",e);
        return ApiResult.createByErrorCodeAndMessage(-1,"系统错误");
    }
}
