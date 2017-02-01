package com.moebuff.discord.tuling123.json.request;

import com.google.gson.annotations.SerializedName;

/**
 * 用于发送对话请求的内容
 *
 * @author muto
 */
public class ContextRequest {

    public Perception perception;//输入信息
    public UserInfo userInfo;//用户参数

    public static class Perception {
        public String text;//直接输入文本

        public String city;//所在城市
        public String latitude;//纬度，大于0为北纬，小于0为南纬
        public String longitude;//经度，大于0为东经，小于0为西经

        @SerializedName("nearest_poi_name")
        public String nearestPoiName;//最近街道名称
        public String province;//省份
        public String street;//街道
    }

    public static class UserInfo {
        public String apiKey;//场景标识
        public String userId;//用户唯一标识
    }
}
