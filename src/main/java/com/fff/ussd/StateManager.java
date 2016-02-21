/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fff.ussd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author majidkabir
 */
public class StateManager {

    public static String START_ACTION;
    public static String CONTINUE_ACTION;
    public static String END_ACTION;
    public static String ERROR_ACTION;
    public static String FORWARD_ACTION;

    private String hashmapType;

    // <StateName, State>
    private Map<String, USSDState> ussdStatesMap = new HashMap<>();
    // <SessionID, Session>
    private Map<String, USSDSession> sessionsMap = new HashMap<>();
    private static StateManager instance;
    Config config = Config.getInstance();

    public StateManager() {
        START_ACTION = config.getParameter("USSD.Parameter.action.START", "1");
        CONTINUE_ACTION = config.getParameter("USSD.Parameter.action.CONTINUE", "2");
        END_ACTION = config.getParameter("USSD.Parameter.action.END", "3");
        ERROR_ACTION = config.getParameter("USSD.Parameter.action.ERROR", "4");
        FORWARD_ACTION = config.getParameter("USSD.Parameter.action.FORWARD", "5");

        hashmapType = config.getParameter("USSD.Hashmap.type", "java");
        try {
            this.ussdStatesMap = loadStates();
        } catch (SAXException ex) {
        } catch (IOException ex) {
        } catch (ParserConfigurationException ex) {
        }
    }

    public static StateManager getInstance() {
        //double check locking thread safe singleton pattern
        if (instance == null) {
            synchronized (StateManager.class) {
                if (instance == null) {
                    instance = new StateManager();
                }
            }
        }

        return instance;
    }

    public String onReceivedMessage(String phone, String action, String message,
            String sessionID) {

        String response = null;

        if (START_ACTION.equalsIgnoreCase(action)
                || CONTINUE_ACTION.equalsIgnoreCase(action)) {
            USSDSession session = null;
            if (START_ACTION.equalsIgnoreCase(action)) {
                session = new USSDSession(sessionID, phone);
            } else if (CONTINUE_ACTION.equalsIgnoreCase(action)) {
                session = getSession(sessionID);
            }
            USSDSession resSession = processMessage(session, message);
            putSession(sessionID, resSession);
            response = resSession.getResponse();

        } else if (END_ACTION.equalsIgnoreCase(action)
                || ERROR_ACTION.equalsIgnoreCase(action)) {
            removeSession(sessionID);
        }

        return response;
    }

    private USSDSession processMessage(USSDSession session, String message) {
        if (message.endsWith("#")) {
            message = message.substring(0, message.length() - 1);
        }
        if (message.startsWith("*")) {
            message = message.substring(1, message.length());
        }
        String[] inputs = message.split("[*]");
        for (String input : inputs) {
            session = goNextInput(session, input);
            // State type
            String type = getStateByName(session.getCurrentStateName()).getType();
            if (type.equalsIgnoreCase("END") || type.equalsIgnoreCase("USSDFORWARD")) {
                //After these states the inputs is not processed 
                break;
            }
        }
        
        return session;
    }

    private USSDSession goNextInput(USSDSession session, String input) {
        USSDSession resSession = session;
        USSDState state = null;

        while (state == null || !state.isMenu()) {
            state = getStateByName(resSession.getCurrentStateName());
            state = state.processState(input, resSession.getParameters());
            resSession.setCurrentStateName(state.getName());
        }

        return resSession;
    }

    public USSDState getStateByName(String stateName) {
        return ussdStatesMap.get(stateName);
    }

    private USSDSession getSession(String sessionID) {
        USSDSession session = null;
        if (hashmapType.equalsIgnoreCase("java")) {
            session = sessionsMap.get(sessionID);
        } else if (hashmapType.equalsIgnoreCase("redis")) {

        };

        return session;
    }

    private void putSession(String sessionID, USSDSession session) {
        if (hashmapType.equalsIgnoreCase("java")) {
            session = sessionsMap.put(sessionID, session);
        } else if (hashmapType.equalsIgnoreCase("redis")) {

        };
    }

    private void removeSession(String sessionID) {
        if (hashmapType.equalsIgnoreCase("java")) {
            sessionsMap.remove(sessionID);
        } else if (hashmapType.equalsIgnoreCase("redis")) {

        };
    }

    private Map<String, USSDState> loadStates() throws SAXException, IOException,
            ParserConfigurationException {

        Map<String, USSDState> statesMap = new HashMap<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(getClass().getResourceAsStream("/states.xml"));

        NodeList stateNodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < stateNodes.getLength(); i++) {
            Node state = stateNodes.item(i);
            if (state.getNodeType() == Node.ELEMENT_NODE) {
                if (((Element) state).getTagName().equalsIgnoreCase("state")) {
                    USSDState ussdState = new USSDState((Element) state);
                    statesMap.put(ussdState.getName(), ussdState);
                }
            }
        }

        return statesMap;
    }
}
