package com.moebuff.discord.tuling123;

import com.google.gson.Gson;
import com.moebuff.discord.utils.Operation;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * WEB API 2.0
 *
 * @author muto
 */
public class OpenApi2 implements ITuring {

    private static final String ADDRESS = "http://openapi.tuling123.com/openapi/api/v2";

    //混淆协议 - 加密
    private class SecretRequest {
        String key;
        String timestamp;
        String data;

        SecretRequest(String data) {
            key = apiKey;
            timestamp = DateFormatUtils.format(System.currentTimeMillis(),
                    "yyyySSSmmHHssMMdd");
            this.data = TuringUtils.aes(
                    Operation.md5Hex(secret + timestamp + apiKey),
                    data);
        }

        String getJson() {
            return new Gson().toJson(this);
        }
    }

    private String apiKey;
    private String secret;

    OpenApi2(String apiKey, String secret) {
        this.apiKey = apiKey;
        this.secret = secret;
    }

    @Override
    public void talk(Issue issue) throws TuringException {

    }
}
