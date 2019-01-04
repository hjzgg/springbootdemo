package com.hjzgg.example.springboot.utils.wechat.api;

import com.hjzgg.example.springboot.utils.JacksonHelper;
import com.hjzgg.example.springboot.utils.http.LocalHttpClient;
import com.hjzgg.example.springboot.utils.wechat.bean.BaseResult;
import com.hjzgg.example.springboot.utils.wechat.bean.user.*;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

import java.nio.charset.Charset;
import java.util.List;

public class UserAPI extends BaseAPI {
    public static User userInfo(String access_token, String openid) {
        HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("https://api.weixin.qq.com/cgi-bin/user/info").addParameter("access_token", access_token).addParameter("openid", openid).addParameter("lang", "zh_CN").build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, User.class);
    }

    public static FollowResult userGet(String access_token, String next_openid) {
        HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("https://api.weixin.qq.com/cgi-bin/user/get").addParameter("access_token", access_token).addParameter("next_openid", next_openid == null ? "" : next_openid).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, FollowResult.class);
    }

    public static UserInfoList userInfoBatchget(String access_token, String lang, List<String> openids) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"user_list\": [");
        for (int i = 0; i < openids.size(); i++) {
            sb.append("{").append("\"openid\": \"").append((String) openids.get(i)).append("\",").append("\"lang\": \"").append(lang).append("\"").append("}").append(i == openids.size() - 1 ? "" : ",");
        }
        sb.append("]}");

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/user/info/batchget").addParameter("access_token", access_token).setEntity(new StringEntity(sb.toString(), Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, UserInfoList.class);
    }

    public static BaseResult userInfoUpdateremark(String access_token, String openid, String remark) {
        String postJson = String.format("{\"openid\":\"%1$s\",\"remark\":\"%2$s\"}", new Object[]{openid, remark});

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/user/info/updateremark").addParameter("access_token", access_token).setEntity(new StringEntity(postJson, Charset.forName("utf-8"))).build();
        return (BaseResult) LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static Group groupsCreate(String access_token, String name) {
        String groupJson = String.format("{\"group\":{\"name\":\"%1$s\"}}", new Object[]{name});

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/groups/create").addParameter("access_token", access_token).setEntity(new StringEntity(groupJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Group.class);
    }

    public static Group groupsGet(String access_token) {
        HttpUriRequest httpUriRequest = RequestBuilder.get().setUri("https://api.weixin.qq.com/cgi-bin/groups/get").addParameter("access_token", access_token).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Group.class);
    }

    public static Group groupsGetid(String access_token, String openid) {
        String groupJson = String.format("{\"openid\":\"%1$s\"}", new Object[]{openid});

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/groups/getid").addParameter("access_token", access_token).setEntity(new StringEntity(groupJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, Group.class);
    }

    public static BaseResult groupsUpdate(String access_token, String id, String name) {
        String groupJson = "{\"group\":{\"id\":" + id + ",\"name\":\"" + name + "\"}}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/groups/update").addParameter("access_token", access_token).setEntity(new StringEntity(groupJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static BaseResult groupsMembersUpdate(String access_token, String openid, String to_groupid) {
        String groupJson = "{\"openid\":\"" + openid + "\",\"to_groupid\":" + to_groupid + "}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/groups/members/update").addParameter("access_token", access_token).setEntity(new StringEntity(groupJson, Charset.forName("utf-8"))).build();
        return (BaseResult) LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static BaseResult groupsMembersBatchUpdate(String access_token, List<String> openid_list, String to_groupid) {
        String openidListStr = JacksonHelper.toJson(openid_list);
        String groupJson = "{\"openid_list\":" + openidListStr + ",\"to_groupid\":" + to_groupid + "}";

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate").addParameter("access_token", access_token).setEntity(new StringEntity(groupJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static BaseResult groupsDelete(String access_token, String id) {
        String groupJson = String.format("{\"group\":{\"id\":%1$s}}", new Object[]{id});

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/cgi-bin/groups/delete").addParameter("access_token", access_token).setEntity(new StringEntity(groupJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, BaseResult.class);
    }

    public static ShakeUser userInfoShake(String access_token, String ticket, String need_poi) {
        String groupJson = String.format("{\"ticket\":\"%1$s\",\"need_poi\":%2$s}", new Object[]{ticket, need_poi});

        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader).setUri("https://api.weixin.qq.com/shakearound/user/getshakeinfo").addParameter("access_token", access_token).setEntity(new StringEntity(groupJson, Charset.forName("utf-8"))).build();
        return LocalHttpClient.executeJsonResult(httpUriRequest, ShakeUser.class);
    }
}
