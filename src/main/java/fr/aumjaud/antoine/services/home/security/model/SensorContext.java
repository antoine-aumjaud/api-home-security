package fr.aumjaud.antoine.services.home.security.model;

import org.springframework.stereotype.Component;

@Component
public class SensorContext {
    private String context;

    public void set(String context) {
        this.context = context;
    }
    public String get() {
        return context;
    }
}