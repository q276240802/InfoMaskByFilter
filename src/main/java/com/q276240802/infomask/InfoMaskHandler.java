package com.q276240802.infomask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class InfoMaskHandler {

    private static MaskInfoEntity maskInfoEntity;
    private static final String YMLPATH = "MaskInfo.yml";

    static {
        InfoMaskHandler.maskInfoEntity = new MaskInfoEntity();
        try {
            updateMaskInfoEntity();
        } catch (FileNotFoundException e) {
            log.error("InfoMask config file \"MaskInfo.yml\" does not exist");
            log.error(e.getMessage());
        }
    }

    /**
     * update configuration
     */
    public static MaskInfoEntity updateMaskInfoEntity() throws FileNotFoundException {
        File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + YMLPATH);
        InfoMaskHandler.maskInfoEntity = new Yaml().loadAs(new FileInputStream(file), MaskInfoEntity.class);
        return InfoMaskHandler.maskInfoEntity;
    }


    /**
     * handle json
     */
    public static byte[] handleResult(byte[] content) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(content);

        Map<String, String> fields = maskInfoEntity.getFields();
        if (fields == null) {
            return content;
        }
        for (Map.Entry<String, String> entry:fields.entrySet()){
            JsonNode jsonField = jsonNode.findValue(entry.getKey());
            if (jsonField==null){
                continue;
            }
            if (jsonField.isArray()) {
                Iterator<JsonNode> elements = jsonField.elements();
                ArrayNode arrayNode = ((ObjectNode) jsonNode).putArray(entry.getKey());
                while (elements.hasNext()){
                    JsonNode next = elements.next();
                    String value = next.asText();
                    String maskValue = generateMask(value, entry.getValue());
                    arrayNode.add(maskValue);
                }
            }else {
                String value = jsonField.asText();
                String maskValue = generateMask(value, entry.getValue());
                ((ObjectNode)jsonNode).put(entry.getKey(),maskValue);
            }
        }
        byte[] bytes = mapper.writeValueAsBytes(jsonNode);
        return bytes;
    }

    /**
     * mask json by the rules
     */
    private static String generateMask(String value, String regular){
        if (!regular.matches("(\\([0-9]{0,2},[0-9]{0,2}\\))+")) {
            log.error("error regular:"+regular);
            return value;
        }
        String[] regulars = regular.split("\\)");
        for (String eachRegular : regulars) {
            String[] split = eachRegular.substring(1).split(",");
            value = replace(value, Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        }
        return value;
    }


    /**
     * replace by *
     */
    private static String replace(String value, int start, int maskLength){
        int valueLength = value.length();
        if (start>valueLength){
            return value;
        }
        char[] chars = value.toCharArray();
        for (int i = start-1;i<valueLength&&i<start+maskLength-1;i++){
            chars[i] = '*';
        }
        return new String(chars);
    }

    public static MaskInfoEntity getMaskInfoEntity() {
        return maskInfoEntity;
    }
}
