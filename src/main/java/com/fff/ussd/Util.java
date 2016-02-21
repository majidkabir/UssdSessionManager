package com.fff.ussd;

import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author majidkabir
 */
public class Util {

    /*
        Structure of result is an xml
    
        <result>
            <variable1>value1</variable1>
            <variable2>value2</variable2>
            <variable3>value3</variable3>
            <variable4>value4</variable4>
            <variable5>value5</variable5>
        </result>
    
        Name of result tag is not important
        This variables and values will add to sesstion parameters and can used in 
        next state.
     */
//    private Map<String, Object> ParseResponse(InputStream xml) throws 
//            ParserConfigurationException, SAXException, IOException {
//        Map<String, Object> map = new HashMap<>();
//        
//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//        Document doc = documentBuilder.parse(xml);
//        
//        doc.getDocumentElement().getChildNodes();
//
//        NodeList parameters = doc.getDocumentElement().getChildNodes();
//        for(int i = 0; i < parameters.getLength(); i++){
//            Node parameter = parameters.item(i);
//            map.put(parameter.getNodeName(), parameter.getTextContent());
//        }
//        
//        return map;
//    }

    //json parsing
    public static Map<String, Object> ParseResponse(String jsonString) {
        Map<String, Object> result = new HashMap<>();
        JsonParserFactory factory = JsonParserFactory.getInstance();
        JSONParser parser = factory.newJsonParser();
        Map<String, Object> jsonData = parser.parseJson(jsonString);

        getParamsRec("", jsonData, result);
        return result;
    }

    private static void getParamsRec(String prefix,
            Map<String, Object> jsonData, Map<String, Object> parameters) {
        for (String key : jsonData.keySet()) {
            parameters.put(prefix + key, jsonData.get(key).toString());
            if (jsonData.get(key) instanceof Map) {
                getParamsRec(prefix + key + ".", (Map) jsonData.get(key), parameters);
            } else if (jsonData.get(key) instanceof ArrayList) {
                ArrayList<Map<String, Object>> list = (ArrayList) jsonData.get(key);
                for (Map<String, Object> item : list) {
                    getParamsRec(prefix + key + ".", item, parameters);
                }
            }
        }

    }

    public static String replaceVariables(String str, Map<String, Object> map) {
        if (map != null && str != null) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                str = str.replaceAll("!" + key + "!", (String)map.get(key));
            }
        }
        return str;
    }

    public static String replaceVariables(String str, Map<String, Object> map, 
            String encoding) throws UnsupportedEncodingException {
        if (map != null && str != null) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                str = str.replaceAll("!" + key + "!", URLEncoder.encode((String)map.get(key),encoding));
            }
        }
        return str;
    }
}
