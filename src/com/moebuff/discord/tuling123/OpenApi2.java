package com.moebuff.discord.tuling123;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moebuff.discord.utils.JsonUtils;
import com.moebuff.discord.utils.Log;
import com.moebuff.discord.utils.Operation;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * WEB API 2.0
 *
 * @author muto
 */
public class OpenApi2 implements ITuring {

    private static final String ADDRESS = "http://openapi.tuling123.com/openapi/api/v2";

    //混淆协议 - 加密
    private class SecretRequest {
        private String key;
        private String timestamp;
        private String data;

        SecretRequest(String data) {
            key = apiKey;
            timestamp = DateFormatUtils.format(System.currentTimeMillis(),
                    "yyyySSSmmHHssMMdd");
            this.data = TuringUtils.aes(
                    Operation.md5Hex(secret + timestamp + apiKey),
                    data);

            //打印请求参数
            Log.getLogger().debug(data);
        }

        String getJson() {
            return new Gson().toJson(this);
        }
    }

    //上下文语境 - 对话内容
    private class Context extends BaseIssue {
        private Gson gson;
        private JsonObject req;
        private JsonObject res;

        private int code;
        private JsonArray results;
        private List<JsonObject> urls = new ArrayList<>();
        private List<JsonObject> news = new ArrayList<>();
        private JsonObject text;

        Context(Issue issue) {
            super(issue);
            gson = new GsonBuilder().serializeNulls().create();
            req = new JsonObject();

            JsonUtils.addProperty(req, "perception.inputText.text", info);
            JsonUtils.addProperty(req, "userInfo.apiKey", apiKey);
            JsonUtils.addProperty(req, "userInfo.userId", userId);
        }

        @Override
        public String getAnswer() {
            return JsonUtils.getElement(text, "values.text").getAsString();
        }

        String getRequest() {
            return gson.toJson(req);
        }

        Issue getResponse(String json) {
            Log.getLogger().debug(json);//打印响应结果
            res = gson.fromJson(json, JsonObject.class);

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
            } catch (ClassCastException ignored) {
                Log.getLogger().debug("", ignored);
            }
            return this;
        }

        boolean noProblem() {
            return code == 0;
        }

        String getCodeInfo() {
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

    private String apiKey;
    private String secret;

    OpenApi2(String apiKey, String secret) {
        this.apiKey = apiKey;
        this.secret = secret;
    }

    @Override
    public Issue talk(Issue issue) throws TuringException {
        Context context = new Context(issue);
        String param = new SecretRequest(context.getRequest()).getJson();
        String result = TuringUtils.post(ADDRESS, param);

        Issue response = context.getResponse(result);
        if (!context.noProblem()) {
            throw new TuringException(context.getCodeInfo());
        }
        return response;
    }
}
