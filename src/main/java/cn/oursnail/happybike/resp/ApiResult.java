package cn.oursnail.happybike.resp;

import cn.oursnail.happybike.constants.ResponseEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 10:58
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Data
public class ApiResult<T> {
    private int code;
    private String msg;
    private T data;

    private ApiResult(){}

    private ApiResult(int code){
        this.code = code;
    }


    private ApiResult(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    private ApiResult(int code,T data){
        this.code = code;
        this.data = data;
    }

    private ApiResult(int code,String msg,T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    //返回成功的几个方法
    public static <T>ApiResult<T> createBySuccess(){
        return new ApiResult<>(ResponseEnum.SUCCESS.getCode());
    }

    public static <T>ApiResult<T> createBySuccessMessage(String msg){
        return new ApiResult<>(ResponseEnum.SUCCESS.getCode(),msg);
    }

    public static <T>ApiResult<T> createBySuccessData(T data){
        return new ApiResult<>(ResponseEnum.SUCCESS.getCode(),data);
    }

    public static <T>ApiResult<T> createBySuccessMessageAndData(String msg,T data){
        return new ApiResult<>(ResponseEnum.SUCCESS.getCode(),msg,data);
    }

    //返回失败的几个方法
    public static <T>ApiResult<T> createByErrorCode(){
        return new ApiResult<>(ResponseEnum.ERROR.getCode());
    }

    public static <T>ApiResult<T> createByErrorMessage(String msg){
        return new ApiResult<>(ResponseEnum.ERROR.getCode(),msg);
    }

    public static <T>ApiResult<T> createByErrorCodeAndMessage(int code,String msg){
        return new ApiResult<>(code,msg);
    }
}
