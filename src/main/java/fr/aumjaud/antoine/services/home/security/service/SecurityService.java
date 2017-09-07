package fr.aumjaud.antoine.services.home.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.aumjaud.antoine.services.common.server.springboot.ApplicationConfig;
import fr.aumjaud.antoine.services.home.security.model.SensorContext;

@Service
public class SecurityService {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private SensorContext sensorContext;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SecurityActivationManager securityActivationManager;

    public String activate(boolean isImmediate) {
        if (isImmediate) {
            securityActivationManager.activateNow();
            return "activated";
        } else {
            securityActivationManager.activate();
            return "activatation in progress";
        }
    }

    public String desactivate(String id) {
        String idLabel = applicationConfig.getProperty("desactivate.id." + id);
        if(idLabel != null) {
            messageService.identified(idLabel);
            securityActivationManager.desactivateNow();
            return "desactivated";
        }
        return "unauthorized id";
    }

    public String event(String sensorName) {
        if (!securityActivationManager.isActivated()) {
            return "security is desactivated";
        } else {
            sensorContext.set(sensorName);
            securityActivationManager.event();
            return "event sent";
        } 
    }
}