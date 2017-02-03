package com.moebuff.discord.tuling123;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moebuff.discord.utils.JsonUtils;
import com.moebuff.discord.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * WEB API 2.0
 *
 * @author muto
 */
@Deprecated
public class OpenApi2 extends BaseApi {

    private static final String ADDRESS = "http://openapi.tuling123.com/openapi/api/v2";

    private class Context2 extends ContextAbstract {
        private Gson gson;
        private JsonUtils utils;

        private int code;
        private JsonArray results;
        private List<JsonObject> urls = new ArrayList<>();
        private List<JsonObject> news = new ArrayList<>();
        private JsonObject text;

        Context2(Issue issue) {
            super(issue);

            gson = new GsonBuilder().serializeNulls().create();
            utils = new JsonUtils(gson);
        }

        @Override
        String getRequest() {
            JsonObject req = new JsonObject();
            utils.setElement(req, "perception.inputText.text", info);
            utils.setElement(req, "perception.selfInfo", null);
            utils.setElement(req, "userInfo.apiKey", apiKey);
            utils.setElement(req, "userInfo.userId", userId);
            return gson.toJson(req);
        }

        @Override
        void setResponse(String json) {
            JsonObject res = gson.fromJson(json, JsonObject.class);
            code = JsonUtils.getElement(res, "intent.code").getAsInt();
            try {
                results = JsonUtils.getElement(res, "results");
                results.forEach(t -> {
                    JsonObject object = t.getAsJsonObject();
                    String type = JsonUtils.getElement(t, "resultType").getAsString();
                    if ("url".equals(type)) {
                        urls.add(object);//连接
                    } else if ("news".equals(type)) {
                        news.add(object);//图文
                    } else if ("text".equals(type)) {
                        text = object;//文本
                    }
                });
                answer = JsonUtils.getElement(text, "values.text").getAsString();
            } catch (ClassCastException ignored) {
                Log.getLogger().debug("", ignored);
            }
        }

        @Override
        boolean noProblem() {
            return code == 0;
        }

        @Override
        String getMessage() {
            if (code == 5000) return "暂不支持该功能";
            if (code == 6000) return "暂不支持该功能";
            if (code == 4000) return "请求参数格式错误";
            if (code == 4001) return "加密方式错误";
            if (code == 4002) return "无功能权限";
            if (code == 4003) return "该apikey没有可用请求次数";
            if (code == 4005) return "无功能权限";
            if (code == 4007) return "apikey不合法";
            if (code == 4100) return "userid获取失败";
            if (code == 4200) return "上传格式错误";
            if (code == 4300) return "批量操作超过限制";
            if (code == 4400) return "没有上传合法userid";
            if (code == 4500) return "userid申请个数超过限制";
            if (code == 7002) return "上传信息失败";
            if (code == 8008) return "服务器错误";
            return "上传成功";//code == 0
        }
    }

    OpenApi2(String apiKey, String secret) {
        super(apiKey, secret);
    }

    @Override
    ContextAbstract toContext(Issue issue) {
        return new Context2(issue);
    }

    @Override
    String getUrl() {
        return ADDRESS;
    }

    @Override
    boolean isAvailable() {
//        return true;
        return false;//废弃，文档有误
    }
}
