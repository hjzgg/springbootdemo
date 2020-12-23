package com.hjzgg.example.springboot.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author hujunzheng
 * @create 2019-12-28 19:51
 **/
public class WechatTest {
    public static void main(String[] args) throws IOException {
        LocalHttpClient.init(20, 10);
        String accessToken = "40_yox74GM_ZvIyFehkjmqCvFIBLJZX0Vnxpgj_7VQD5iJlnpZUDQamE9wxZ69hEMzIjo-pgkE7U7LCVlc04GM02IWTmRv3ZMoJMx8InB88CgOVl-3bWRwYJpgAsZbmlsLXc7ZXPc_0YsC8jgl1PHOgAHAWOV";
        String url = "https://api.weixin.qq.com/cgi-bin/user/tag/get?access_token=" + accessToken;

        int counter = 0;
        String nextOpenid = "";
        for (; ; ) {
            String next_openid = nextOpenid;
            String response = LocalHttpClient.jsonPost(url, JSON.toJSONString(
                    new HashMap<String, Object>(3) {{
                        put("tagid", 2619);
                        put("next_openid", next_openid);
                    }}
            ));
            JSONObject responseJson = JSON.parseObject(response);
            if (responseJson.containsKey("errcode")) {
                continue;
            }
            int count = responseJson.getInteger("count");
            counter += count;
            System.out.println("阶段统计-用户总数：" + counter);

            nextOpenid = responseJson.getString("next_openid");

            if (StringUtils.isBlank(nextOpenid) || count < 10000) {
                break;
            }
        }
        System.out.println("最终统计-用户总数：" + counter);
    }
}