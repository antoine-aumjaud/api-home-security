package fr.aumjaud.antoine.services.home.security.requesthandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.aumjaud.antoine.services.common.security.WrongRequestException;
import fr.aumjaud.antoine.services.common.server.springboot.ApplicationConfig;

@RestController
public class CameraResource {
    
    @Autowired
    private ApplicationConfig applicationConfig;

    @RequestMapping(value = "/secure/camera/{sensorName}.jpg", method = RequestMethod.GET) 
    public void getCameraImage(HttpServletResponse response, @PathVariable String sensorName)  throws IOException {
        String imgUrl = applicationConfig.getProperty("camera." + sensorName + ".image.url");
        String imgAuth = applicationConfig.getProperty("camera." + sensorName + ".image.auth");
        
        if(imgUrl == null)  
            throw new WrongRequestException("wrong sensor name", "get camera image: " + sensorName + " doesn't exit");

        response.setContentType("image/jpeg");
        response.setHeader("Pragma", CacheControl.noCache().getHeaderValue());
        
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", imgAuth); 

        InputStream in = conn.getInputStream();
        OutputStream out = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        in.close();
        response.flushBuffer();
    }
}