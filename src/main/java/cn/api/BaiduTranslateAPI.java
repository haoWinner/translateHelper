package cn.api;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 百度翻译通用API调用
 * @Author haodd
 * @Date 2024/8/10 14:02
 * @Version 1.0
 */
public class BaiduTranslateAPI {

    private static final String TRANSLATE_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    /**
     * 调用百度翻译API进行翻译
     *
     * @param appid 应用ID
     * @param key   应用密钥
     * @param query 要翻译的文本
     * @param from  源语言
     * @param to    目标语言
     * @return 翻译结果
     */
    public static String translate(String appid, String key, String query, String from, String to) {
        String salt = String.valueOf(System.currentTimeMillis());

        query = new String(query.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        String sign = generateSign(appid, query, salt, key);

        // 请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("q", HttpUtil.encodeParams(query, StandardCharsets.UTF_8));
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appid);
        params.put("salt", salt);
        params.put("sign", sign);

        HttpResponse response = HttpUtil.createPost(TRANSLATE_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .form(params)
                .execute();

        String responseBody = response.body();

        if (response.isOk()) {
            Map<String, Object> resultMap = JSONUtil.toBean(responseBody, Map.class);
            if (resultMap.containsKey("trans_result")) {
                List<Map<String,String>> transResultList = (List<Map<String, String>>)resultMap.get("trans_result");
                StringBuilder sb = new StringBuilder();
                transResultList.forEach(s->{
                    sb.append(s.get("dst")).append("\n");
                });
                return sb.toString();
            } else if (resultMap.containsKey("error_code")) {
                return JSONUtil.toJsonStr(resultMap);
            }
        }

        return "Error: Unable to translate.";
    }

    /**
     * 生成百度翻译API的签名
     * @param appid 百度翻译平台分配的appid
     * @param query 翻译的查询内容
     * @param salt 随机码
     * @param key 平台分配的密钥
     * @return 生成的签名sign
     */
    public static String generateSign(String appid, String query, String salt, String key) {
        String str = appid + query + salt + key;
        return SecureUtil.md5(str);
    }

}
