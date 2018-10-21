package fr.aumjaud.antoine.services.home.security.model;

import org.springframework.stereotype.Component;

@Component
public class SensorContext {
    private String source;
    private String image;

    public void set(String source, String image) {
        this.source = source;
        this.image = image;
    }
    public String getSource() {
        return source;
    }
    public String getImage() {
        return image;
    }
}