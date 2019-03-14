package com.q276240802.infomask;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping(path = {"/infomask","infoMask"})
@Slf4j
public class InfoMaskApi {

    @RequestMapping({"/updateconfig","/updateConfig"})
    public String updateConfig(){
        MaskInfoEntity maskInfoEntity;
        try {
            maskInfoEntity = InfoMaskHandler.updateMaskInfoEntity();
        } catch (FileNotFoundException e) {
            log.error("InfoMask config file \"MaskInfo.yml\" does not exist");
            return "Update Failed : InfoMask config file \"MaskInfo.yml\" does not exist \t"+e.getMessage();
        }
        return "Update Success : "+maskInfoEntity.toString();
    }
}
