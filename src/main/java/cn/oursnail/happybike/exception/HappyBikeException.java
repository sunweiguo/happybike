package cn.oursnail.happybike.exception;

import cn.oursnail.happybike.constants.Constants;
import cn.oursnail.happybike.constants.ResponseEnum;
import lombok.Data;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 11:07
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Data
public class HappyBikeException extends RuntimeException{
    private Integer code = ResponseEnum.ERROR.getCode();

    public HappyBikeException(Integer code,String msg){
        super(msg);
        this.code = code;
    }

    public HappyBikeException(String msg){
        super(msg);
    }

}
