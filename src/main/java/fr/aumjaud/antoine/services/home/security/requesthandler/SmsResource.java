package fr.aumjaud.antoine.services.home.security.requesthandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.aumjaud.antoine.services.home.security.service.MessageService;

@RestController
public class SmsResource {
    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/secure/message/sms", method = RequestMethod.GET)
    public boolean sendSms(@RequestParam String user, @RequestParam String message) {
        return messageService.sendSMS(user, message); 
    }
}