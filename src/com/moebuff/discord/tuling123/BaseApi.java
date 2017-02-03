package com.moebuff.discord.tuling123;

import com.google.gson.Gson;
import com.moebuff.discord.utils.Log;
import com.moebuff.discord.utils.Operation;
import com.moebuff.discord.utils.UnhandledException;
import org.apache.commons.beanutils.BeanUtils;

/**
 * 不同版本实现的公共部分
 *
 * @author muto
 */
abstract class BaseApi implements ITuring {

    //混淆协议 - 加密
    protected class SecretRequest {
        private String key;
        private long timestamp;
        private String data;

        SecretRequest(String data) {
            key = apiKey;
            timestamp = System.currentTimeMillis();
            this.data = TuringUtils.aes(
                    Operation.md5Hex(secret + timestamp + apiKey),
                    data);

            //打印原数据
            Log.getLogger().debug(data);
        }

        String getJson() {
            return new Gson().toJson(this);
        }
    }

    //上下文语境 - 对话内容
    protected abstract class ContextAbstract extends BaseIssue {
        protected String answer;

        ContextAbstract(Issue issue) {
            super("");

            try {
                BeanUtils.copyProperties(this, issue);
            } catch (ReflectiveOperationException e) {
                throw new UnhandledException(e);
            }
        }

        @Override
        public String getAnswer() {
            return answer;
        }

        abstract String getRequest();

        abstract void setResponse(String json);

        /**
         * 是否出现服务端异常
         *
         * @return 当请求出现问题时，返回 false；其他为true
         */
        abstract boolean noProblem();

        /**
         * 用于解析服务端发来的 code
         *
         * @return 服务端信息
         */
        abstract String getMessage();
    }

    protected String apiKey;
    protected String secret;

    BaseApi(String apiKey, String secret) {
        this.apiKey = apiKey;
        this.secret = secret;
    }

    abstract ContextAbstract toContext(Issue issue);

    abstract String getUrl();

    @Override
    public Issue talk(Issue issue) throws TuringException {
        ContextAbstract context = toContext(issue);
        String param = new SecretRequest(context.getRequest()).getJson();
        Log.getLogger().debug(param);//打印请求参数

        String result = TuringUtils.post(getUrl(), param);
        Log.getLogger().debug(result);//打印响应结果

        context.setResponse(result);
        if (!context.noProblem()) {
            throw new TuringException(context.getMessage());
        }
        return context;
    }

    /**
     * 判断接口是否可用，默认是可用的。
     *
     * @return 可用返回 true；不可用返回 false。
     */
    boolean isAvailable() {
        return true;
    }
}
