package com.q276240802.infomask;

import lombok.Data;

import java.util.List;
import java.util.Map;


/**
 * 配置文件MaskInfo.yml实体类
 */
@Data
public class MaskInfoEntity {
    private Map<String, String> fields;
    private List<String> url;

    @Override
    public String toString() {
        return "{" +
                "fields=" + fields +
                ", url=" + url +
                '}';
    }
}
