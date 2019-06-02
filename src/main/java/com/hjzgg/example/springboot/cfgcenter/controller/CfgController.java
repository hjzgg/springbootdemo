package com.hjzgg.example.springboot.cfgcenter.controller;

import com.hjzgg.example.springboot.beans.response.BaseResponse;
import com.hjzgg.example.springboot.beans.response.RestStatus;
import com.hjzgg.example.springboot.cfgcenter.utils.zk.ZKHelper;
import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.CfgMapper;
import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.CfgRecord;
import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.vo.DeleteVO;
import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.vo.PushVO;
import com.hjzgg.example.springboot.dao.mybatis.cfgcenter.vo.SearchVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 微门户配置中心接口
 */
@RestController
@RequestMapping("cfg")
public class CfgController {
    private static Logger LOGGER = LoggerFactory.getLogger(CfgController.class);

    private static final String ZK_PATH_PATTERN0 = "/wmhcfg/projects/%s/%s";
    private static final String ZK_PATH_PATTERN1 = ZK_PATH_PATTERN0 + "/%s";
    private static final String ZK_PATH_PATTERN = ZK_PATH_PATTERN1 + "/%s";

    @Autowired
    private CfgMapper mapper;

    @GetMapping(value = "/search", produces = MediaType.TEXT_PLAIN_VALUE)
    public String findCfgContents(@RequestBody @Validated SearchVO searchVO
            , @RequestParam(required = false) String cfgId) {
        List<CfgRecord> records = mapper.findRecords(searchVO);
        if (CollectionUtils.isEmpty(records)) {
            return StringUtils.EMPTY;
        }
        if (StringUtils.isNotBlank(cfgId)) {
            records = records.stream().filter(record -> cfgId.equals(record.getCfgId())).collect(Collectors.toList());
        }
        StringBuilder response = new StringBuilder();
        Properties properties = new Properties();
        records.forEach(record -> {
            try {
                properties.clear();
                properties.load(new StringReader(record.getCfgContent()));
                properties.forEach((key, value) -> response.append(key)
                        .append("=")
                        .append(value)
                        .append(System.lineSeparator())
                        .append(System.lineSeparator())
                );
            } catch (IOException e) {
                LOGGER.error("配置解析异常...", e);
            }
        });
        return response.toString();
    }

    @PostMapping(value = "/send/{systemId}/{appId}/{groupId}/{cfgId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse sendCfgContent(@RequestBody String cfgContent
            , @PathVariable String systemId
            , @PathVariable String appId
            , @PathVariable String groupId
            , @PathVariable String cfgId) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setRestStatus(RestStatus.SUCCESS);

        SearchVO searchVO = new SearchVO();
        searchVO.setSystemId(systemId);
        searchVO.setAppId(appId);
        searchVO.setGroupId(groupId);

        List<CfgRecord> records = mapper.findRecords(searchVO);
        CfgRecord record = null;

        if (!CollectionUtils.isEmpty(records)) {
            for (CfgRecord cfgRecord : records) {
                if (cfgId.equals(cfgRecord.getCfgId())) {
                    record = cfgRecord;
                    record.setCfgContent(cfgContent);
                    break;
                }
            }
        }

        if (null == record) {
            record = new CfgRecord();
            record.setSystemId(systemId);
            record.setAppId(appId);
            record.setGroupId(groupId);
            record.setCfgId(cfgId);
            record.setCfgContent(cfgContent);
        }

        StringBuilder cfgContentSB = new StringBuilder();
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(record.getCfgContent()));
        } catch (IOException e) {
            LOGGER.error("配置解析异常...", e);
            baseResponse.setErrors(e.getMessage());
            baseResponse.setRestStatus(RestStatus.FAIL_50001);
            return baseResponse;
        }
        properties.forEach((key, value) -> cfgContentSB.append(key)
                .append("=")
                .append(value)
                .append(System.lineSeparator())
        );

        record.setCfgContent(cfgContentSB.toString());

        if (null == record.getId()) {
            mapper.insertRecord(record);
        } else {
            mapper.updateRecord(record);
        }
        return baseResponse;
    }

    @PostMapping(value = "/push", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse pushCfgContent(@RequestBody @Validated PushVO pushVO) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setRestStatus(RestStatus.SUCCESS);
        String path = String.format(ZK_PATH_PATTERN
                , pushVO.getSystemId()
                , pushVO.getAppId()
                , pushVO.getGroupId()
                , pushVO.getCfgId()
        );

        try {
            SearchVO searchVO = new SearchVO();
            searchVO.setSystemId(pushVO.getSystemId());
            searchVO.setAppId(pushVO.getAppId());
            searchVO.setGroupId(pushVO.getGroupId());

            List<CfgRecord> records = mapper.findRecords(searchVO);
            StringBuilder cfgContent = new StringBuilder();
            records.forEach(record -> cfgContent.append(record.getCfgContent()).append(System.lineSeparator()));
            if (!ZKHelper.setData(path, cfgContent.toString().getBytes())) {
                baseResponse.setRestStatus(RestStatus.FAIL_50001);
            }
        } catch (Exception e) {
            LOGGER.error("配置推送异常...", e);
            baseResponse.setRestStatus(RestStatus.FAIL_50001);
            baseResponse.setErrors(e.getMessage());
        }
        return baseResponse;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse createCfg(@RequestBody @Validated PushVO pushVO) {
        BaseResponse baseResponse = new BaseResponse();
        String path = String.format(ZK_PATH_PATTERN
                , pushVO.getSystemId()
                , pushVO.getAppId()
                , pushVO.getGroupId()
                , pushVO.getCfgId()
        );
        if (ZKHelper.createPath(path)) {
            baseResponse.setRestStatus(RestStatus.SUCCESS);
        } else {
            baseResponse.setRestStatus(RestStatus.FAIL_50001);
        }
        return baseResponse;
    }

    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public BaseResponse deleteCfg(@RequestBody @Validated DeleteVO deleteVO) {
        BaseResponse baseResponse = new BaseResponse();
        String path;
        if (StringUtils.isBlank(deleteVO.getGroupId())) {
            path = String.format(ZK_PATH_PATTERN0
                    , deleteVO.getSystemId()
                    , deleteVO.getAppId()
            );
        } else if (StringUtils.isNotBlank(deleteVO.getGroupId()) && StringUtils.isBlank(deleteVO.getCfgId())) {
            path = String.format(ZK_PATH_PATTERN1
                    , deleteVO.getSystemId()
                    , deleteVO.getAppId()
                    , deleteVO.getGroupId()
            );
        } else {
            path = String.format(ZK_PATH_PATTERN
                    , deleteVO.getSystemId()
                    , deleteVO.getAppId()
                    , deleteVO.getGroupId()
                    , deleteVO.getCfgId()
            );
        }

        if (ZKHelper.deletePath(path)) {
            baseResponse.setRestStatus(RestStatus.SUCCESS);
        } else {
            baseResponse.setRestStatus(RestStatus.FAIL_50001);
        }
        return baseResponse;
    }

    @GetMapping(value = "/getdata", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getData(@RequestParam String path) {
        return ZKHelper.getData(path);
    }
}
