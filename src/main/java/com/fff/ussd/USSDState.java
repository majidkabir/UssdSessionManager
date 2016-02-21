package com.fff.ussd;

import com.codesnippets4all.json.generators.JSONGenerator;
import com.codesnippets4all.json.generators.JsonGeneratorFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author majidkabir
 */
public class USSDState {

    /*
        type INPUT,SERVICE,USSDFORWARD,END
     */
    private Map<String, String> stateParams;

    public USSDState(Element state) {
        stateParams = new HashMap<>();
        setParam("name", state.getAttribute("name"));
        setParam("type", state.getAttribute("type"));
        NodeList stateParams = state.getChildNodes();
        for (int i = 0; i < stateParams.getLength(); i++) {
            Node node = stateParams.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element param = (Element)node;
                setParam(param.getTagName(), param.getTextContent());
            }
        }        
    }

    public boolean isMenu() {
        if (getParam("type").equalsIgnoreCase("END") || getParam("type").equalsIgnoreCase("INPUT")
                || getParam("type").equalsIgnoreCase("USSDFORWARD")) {
            return true;
        }
        return false;
    }

    public USSDState processState(String input, Map<String, Object> parameters) {
        USSDState resUSSDState = null;
        if (getParam("type").equalsIgnoreCase("INPUT")) {
            parameters.put("input", input);
            if (getParam("variable") != null) {
                parameters.put(getParam("variable"), input);
            }
            parameters.put("action", StateManager.CONTINUE_ACTION);
        } else if (getParam("type").equalsIgnoreCase("END")) {
            parameters.put("action", StateManager.END_ACTION);
        } else if (getParam("type").equalsIgnoreCase("USSDFORWARD")) {
            parameters.put("action", StateManager.FORWARD_ACTION);
        } else if (getParam("type").equalsIgnoreCase("SERVICE")) {
            String url = getParam("url");
            try {
                url = Util.replaceVariables(url, parameters, "UTF-8");
                int connection_timeout = 15000;
                int socket_timeout = 15000;
                if (getParam("connection_timeout") != null) {
                    connection_timeout = Integer.valueOf(getParam("connection_timeout"));
                }
                if (getParam("socket_timeout") != null) {
                    socket_timeout = Integer.valueOf(getParam("socket_timeout"));
                }
                Content content = Request.Get(new URI(url)).connectTimeout(connection_timeout).
                        socketTimeout(socket_timeout).execute().returnContent();
                parameters.putAll(Util.ParseResponse(content.asString()));
            } catch (UnsupportedEncodingException ex) {
                //TODO: Add logger
            } catch (IOException ex) {
                //TODO: Add logger
            } catch (URISyntaxException ex) {
                //TODO: Add logger
            }

        }

        resUSSDState = goNext(input, parameters);
        return resUSSDState;
    }

    public USSDState goNext(String input, Map<String, Object> parameters) {
        USSDState state = null;
        StateManager manager = StateManager.getInstance();
        if (getParam("next_state_variable") == null) {
            state = manager.getStateByName(getParam("next_state"));
        } else {
            String var_value = (String) parameters.get(getParam("next_state_variable"));
            state = manager.getStateByName(getParam("var_" + var_value));
            if (state == null) {
                state = manager.getStateByName(getParam("var_else"));
            }
        }
        return state;
    }

    private String getParam(String key) {
        return this.stateParams.get(key);
    }

    private void setParam(String key, String value) {
        this.stateParams.put(key, value);
    }

    public String getType() {
        return this.stateParams.get("type");
    }

    public String getName() {
        return this.stateParams.get("name");
    }

    public HashMap<String, String> getOutput(Map<String, Object> params) {
        HashMap<String, String> result = new HashMap<>(stateParams);
        result.put("menu", Util.replaceVariables(getParam("menu"), params));
        return result;
    }
}
