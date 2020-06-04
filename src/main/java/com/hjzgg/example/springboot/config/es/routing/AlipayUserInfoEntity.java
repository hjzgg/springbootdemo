package com.hjzgg.example.springboot.config.es.routing;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 支付宝生活号 ES 用户信息
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(clientName = "XXXX"
        , indexName = "alipay_user_info"
        , type = "alipay_user"
        , mappingPath = "mapping/alipay_user_info.json"
        , shards = 10)
public class AlipayUserInfoEntity extends BasicElasticEntity {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 用户id
     */
    private String userId;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像地址
     */
    private String headPicUrl;
    /**
     * 支付宝生活号渠道应用对应的省编码
     */
    private Integer provinceId;
    /**
     * 支付宝生活号渠道对应的地市编码
     **/
    private Integer cityId;
    /**
     * 关注状态 0：未关注， 1：已关注
     **/
    private Integer subStatus;
    /**
     * 首次关注时间
     */
    @JsonFormat(pattern=DEFAULT_DATE_FORMAT)
    private Date subTime;
    /**
     * 最新关注时间
     */
    @JsonFormat(pattern=DEFAULT_DATE_FORMAT)
    private Date subTimeLoc;
    /**
     * 取消关注时间
     */
    @JsonFormat(pattern=DEFAULT_DATE_FORMAT)
    private Date unsubTime;
    /**
     * 绑定状态
     */
    private Integer bindStatus;
    /**
     * 首次绑定时间
     */
    @JsonFormat(pattern=DEFAULT_DATE_FORMAT)
    private Date bindTime;
    /**
     * 最新绑定时间
     */
    @JsonFormat(pattern=DEFAULT_DATE_FORMAT)
    private Date bindTimeLoc;
    /**
     * 取消绑定时间
     */
    @JsonFormat(pattern=DEFAULT_DATE_FORMAT)
    private Date unbindTime;
    /**
     * 渠道编码
     */
    private String channelType;
}