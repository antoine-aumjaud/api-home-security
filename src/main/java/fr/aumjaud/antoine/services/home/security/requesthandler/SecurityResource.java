package fr.aumjaud.antoine.services.home.security.requesthandler;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.aumjaud.antoine.services.home.security.service.SecurityService;
import fr.aumjaud.antoine.services.common.security.WrongRequestException;

@RestController
public class SecurityResource {
    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/secure/activate")
    public String activate(@RequestParam boolean isImediate) {
        return securityService.activate(isImediate);
    }

    @RequestMapping(value = "/secure/desactivate")
    public String desactivate(@RequestParam String id) {
        return securityService.desactivate(id);
    }

    @RequestMapping(value = "/secure/event/{sensorName}")
    public String event(@PathVariable String sensorName) {
        return securityService.event(sensorName);
    }

    @RequestMapping(value = "/secure/event/camera")
    public String eventCamera(@RequestParam String message) {
        if(message.length() < 7) {
            throw new WrongRequestException("wrong message", "event/camera: " + message + " doesn't match pattern");
        }
        return securityService.event("camera " + message.substring(7, 12)); // bug in synology message (send invalid HTTP caracters)
    }
}