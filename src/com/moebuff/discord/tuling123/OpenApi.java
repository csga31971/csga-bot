package com.moebuff.discord.tuling123;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * WEB API
 *
 * @author muto
 */
public class OpenApi extends BaseApi {

    private static final String ADDRESS = "http://www.tuling123.com/openapi/api";

    private class Context extends ContextAbstract {
        private int code;

        Context(Issue issue) {
            super(issue);
        }

        @Override
        String getRequest() {
            JsonObject req = new JsonObject();
            req.addProperty("key", apiKey);
            req.addProperty("info", info);
            req.addProperty("userid", userId);
            return new Gson().toJson(req);
        }

        @Override
        void setResponse(String json) {
            JsonObject res = new GsonBuilder().serializeNulls().create()
                    .fromJson(json, JsonObject.class);
            code = res.get("code").getAsInt();
            answer = res.get("text").getAsString();
        }

        @Override
        boolean noProblem() {
            return code + 1 > 100000;
        }

        @Override
        String getMessage() {
            if (code == 100000) return "文本类";
            if (code == 200000) return "链接类";
            if (code == 302000) return "新闻类";
            if (code == 308000) return "菜谱类";
            if (code == 313000) return "儿歌类（儿童版）";
            if (code == 314000) return "诗词类（儿童版）";

            // 异常码
            if (code == 40001) return "参数key错误";
            if (code == 40002) return "请求内容info为空";
            if (code == 40004) return "当天请求次数已使用完";
            if (code == 40007) return "数据格式异常";
            return null;
        }
    }

    OpenApi(String apiKey, String secret) {
        super(apiKey, secret);
    }

    @Override
    ContextAbstract toContext(Issue issue) {
        return new Context(issue);
    }

    @Override
    String getUrl() {
        return ADDRESS;
    }
}
