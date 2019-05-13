package cn.oursnail.happybike.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 【swg】.
 * @Date 2018/3/14 14:00
 * @DESC
 * @CONTACT 317758022@qq.com
 */
@Data
public class UserElement {
    private Long userId;
    private String mobile;
    private String token;
    private String platform;
    private String pushUserId;
    private String pushChannelId;
    private String oldToken;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("platform", this.platform);
        map.put("userId", this.userId + "");
        map.put("token", token);
        map.put("mobile", mobile);
        if (this.pushUserId != null) {
            map.put("pushUserId", this.pushUserId);
        }
        if (this.pushChannelId != null) {
            map.put("pushChannelId", this.pushChannelId);
        }
        return map;
    }
    public static UserElement fromMap(Map<String, String> map) {
        UserElement ue = new UserElement();
        ue.setPlatform(map.get("platform"));
        ue.setToken(map.get("token"));
        ue.setMobile(map.get("mobile"));
        ue.setUserId(Long.parseLong(map.get("userId")));
        ue.setPushUserId(map.get("pushUserId"));
        ue.setPushChannelId(map.get("pushChannelId"));
        return ue;
    }

}
