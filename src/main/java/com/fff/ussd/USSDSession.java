package com.fff.ussd;

import com.codesnippets4all.json.generators.JSONGenerator;
import com.codesnippets4all.json.generators.JsonGeneratorFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author majidkabir@gmail.com
 */
public class USSDSession {
    private String sessionID;
    private String phone;
    private String calledNumber;
    private Map<String, Object> parameters;
    private String currentStateName;

    public USSDSession(String sessionID, String phone) {
        this.sessionID = sessionID;
        this.phone = phone;
        //Default state for starting menu
        this.currentStateName = "Start";
        this.calledNumber = "";
        
        this.parameters = new HashMap<>();
        this.parameters.put("phone", phone);
        this.parameters.put("sessionid", sessionID);
    }

    public String getResponse() {
        StateManager manager = StateManager.getInstance();
        USSDState state = manager.getStateByName(getCurrentStateName());
        
        JsonGeneratorFactory factory=JsonGeneratorFactory.getInstance();
        JSONGenerator generator=factory.newJsonGenerator();

        HashMap<String, String> result = state.getOutput(getParameters());
        result.put("phone", phone);
        result.put("sessionid", sessionID);

        String json=generator.generateJson(result);

        return json;
    }
    
    public String getCurrentStateName() {
        return this.currentStateName;
    }
    
    public void setCurrentStateName(String stateName) {
        this.currentStateName = stateName;
    }
        
    public void setCalledNumber(String message) {
        this.calledNumber += message + "*";
    }
    
    public void putParameter(String key, Object object) {
        this.parameters.put(key, object);
    }
    
    public void putParameters(Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
    }
    
    public Object getParameter(String key) {
        return this.parameters.get(key);
    }
    
    public Map<String, Object> getParameters() {
        return this.parameters;
    }
}
