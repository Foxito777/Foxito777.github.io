package com.proyectoweb.Juledtoys.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "izipay")
public class IzipayConfig {
    
    private Shop shop = new Shop();
    private Test test = new Test();
    private Prod prod = new Prod();
    private Api api = new Api();
    private Js js = new Js();
    private String environment = "TEST"; // TEST o PRODUCTION
    private String currency = "PEN";
    
    public static class Shop {
        private String id;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
    }
    
    public static class Test {
        private String password;
        private String publicKey;
        private String hmacKey;
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getPublicKey() {
            return publicKey;
        }
        
        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
        
        public String getHmacKey() {
            return hmacKey;
        }
        
        public void setHmacKey(String hmacKey) {
            this.hmacKey = hmacKey;
        }
    }
    
    public static class Prod {
        private String password;
        private String publicKey;
        private String hmacKey;
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getPublicKey() {
            return publicKey;
        }
        
        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
        
        public String getHmacKey() {
            return hmacKey;
        }
        
        public void setHmacKey(String hmacKey) {
            this.hmacKey = hmacKey;
        }
    }
    
    public static class Api {
        private String url;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
    }
    
    public static class Js {
        private String url;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
    }
    
    // Getters y Setters
    public Shop getShop() {
        return shop;
    }
    
    public void setShop(Shop shop) {
        this.shop = shop;
    }
    
    public Test getTest() {
        return test;
    }
    
    public void setTest(Test test) {
        this.test = test;
    }
    
    public Prod getProd() {
        return prod;
    }
    
    public void setProd(Prod prod) {
        this.prod = prod;
    }
    
    public Api getApi() {
        return api;
    }
    
    public void setApi(Api api) {
        this.api = api;
    }
    
    public Js getJs() {
        return js;
    }
    
    public void setJs(Js js) {
        this.js = js;
    }
    
    public String getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getCurrentPassword() {
        return "TEST".equals(environment) ? test.getPassword() : prod.getPassword();
    }
    
    public String getCurrentPublicKey() {
        return "TEST".equals(environment) ? test.getPublicKey() : prod.getPublicKey();
    }
    
    public String getCurrentHmacKey() {
        return "TEST".equals(environment) ? test.getHmacKey() : prod.getHmacKey();
    }
}
