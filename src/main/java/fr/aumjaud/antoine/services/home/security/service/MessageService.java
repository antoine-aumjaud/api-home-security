package fr.aumjaud.antoine.services.home.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.aumjaud.antoine.services.common.http.HttpHelper;
import fr.aumjaud.antoine.services.common.http.HttpMessage;
import fr.aumjaud.antoine.services.common.http.HttpMessageBuilder;
import fr.aumjaud.antoine.services.common.http.HttpResponse;
import fr.aumjaud.antoine.services.common.server.springboot.ApplicationConfig;
import fr.aumjaud.antoine.services.home.security.model.SensorContext;

@Service
public class MessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private HttpHelper httpHelper;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private SensorContext sensorContext;

    public void notif(String message) {
        LOGGER.info("Send a notification: {}", message);

        //Nabaztag message
        sendToNabaztag(message);
    }

    public void activation() {
        notif("Alarme active");
        changeNabaztagColor("red");
    }

    public void desactivation() {
        notif("Alarme désactivée");
        changeNabaztagColor("green");

        sendToChat("Alarme désactivée", false);
    }

    public void identified(String idLabel) {
        notif("Bonjour " + idLabel);
    }

    public void alerte(int nb) {
        LOGGER.info("Send an alerte ({})", nb);

        //Nabaztag message 
        if(nb == 1) wakeUpNabaztag();
        StringBuilder nabaztagMessage = new StringBuilder("Merci de vous identifier");
        //  add spaces to avoid openjabnab filter
        for (int i = 1; i < nb; i++) nabaztagMessage.append(" ");
        sendToNabaztag(nabaztagMessage.toString()); 

        //Chat message
        if(nb == 1) sendToChat("Merci de valider la désactivation (" + sensorContext.get() + ")", false);
    }

    public void intrusion() {
        LOGGER.info("Send an intrusion");

        //Nabaztag message
        sendToNabaztag("Intrusion non autorisée, alerte lancée");

        //SMS message
        sendToSMS("Intrusion détectée : " + sensorContext.get());

        //Chat message
        sendToChat("Intrusion détectée : " + sensorContext.get(), true);

        //Email message
        sendToMail("Intrusion détectée : " + sensorContext.get());
    }

    private void wakeUpNabaztag() {
        LOGGER.debug("Wake Up Nabaztag");
    
        String urlIsAwake = applicationConfig.getProperty("nabaztag.url") + applicationConfig.getProperty("nabaztag.url.path.isAwake");
        String secureKey = applicationConfig.getProperty("nabaztag.secure-key");
        HttpResponse httpResponse = httpHelper.getData(urlIsAwake, secureKey);
        String content = httpResponse.getContent();
        boolean isAwake = content.contains("true");
        LOGGER.debug("Nabaztag awake status is: {}", isAwake);
        if(!isAwake) {
            String urlWakeup = applicationConfig.getProperty("nabaztag.url") + applicationConfig.getProperty("nabaztag.url.path.wakeup");
            httpHelper.postData(urlWakeup, secureKey);
        }
    }

    private void sendToNabaztag(String message) {
        LOGGER.debug("Send to Nabaztag: {}", message);

        String url = applicationConfig.getProperty("nabaztag.url") + applicationConfig.getProperty("nabaztag.url.path.message");
        String secureKey = applicationConfig.getProperty("nabaztag.secure-key");
        HttpMessage httpMessage = new HttpMessageBuilder(url).setSecureKey(secureKey)
                .setJsonMessage("{ \"message\": \"" + message + "\"}").build();
        httpHelper.postData(httpMessage);
    }

    private void changeNabaztagColor(String color) {
        LOGGER.debug("Change Nabaztag to color: {}", color);
    
        String url = applicationConfig.getProperty("nabaztag.url") + applicationConfig.getProperty("nabaztag.url.path.changeColor");
        String secureKey = applicationConfig.getProperty("nabaztag.secure-key");
        HttpMessage httpMessage = new HttpMessageBuilder(url).setSecureKey(secureKey)
                .setJsonMessage("{ \"color\": \"" + color + "\"}").build();
        httpHelper.postData(httpMessage);
    }

    private void sendToSMS(String message) {
        LOGGER.debug("Send to SMS: {}", message);

        for (String user : applicationConfig.getProperty("sms.users").split(";")) {
            String url = String.format(applicationConfig.getProperty("sms.url"),
                    applicationConfig.getProperty("sms.user." + user + ".id"),
                    applicationConfig.getProperty("sms.user." + user + ".token"), //
                    message);
            httpHelper.getData(url);
        }
    }

    private void sendToChat(String message, boolean isAlerte) {
        LOGGER.debug("Send to Chat: {}", message);

        String url = applicationConfig.getProperty("synology-chatbot.url")
            + applicationConfig.getProperty(isAlerte ? "synology-chatbot.path.alerte" : "synology-chatbot.path.info");
        String secureKey = applicationConfig.getProperty("synology-chatbot.secure-key");
        HttpMessage httpMessage = new HttpMessageBuilder(url).setSecureKey(secureKey)
                .setJsonMessage("{ \"message\": \"" + message + "\"}").build();
        httpHelper.postData(httpMessage);
    }

    private void sendToMail(String message) {
        //TODO ??
        //  Add sensor info (image, video link) ?
    }
}