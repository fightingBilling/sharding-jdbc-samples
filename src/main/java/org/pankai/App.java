package org.pankai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by pktczwd on 2016/9/22.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@PropertySource({"classpath:/jdbc.properties"})
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
