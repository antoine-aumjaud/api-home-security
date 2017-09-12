package fr.aumjaud.antoine.services.home.security.requesthandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.aumjaud.antoine.services.home.security.service.SecurityService;

@RestController
public class SecurityResource {
    @Autowired
    SecurityService securityService;

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
}