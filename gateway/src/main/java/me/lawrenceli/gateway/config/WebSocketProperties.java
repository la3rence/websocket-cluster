package me.lawrenceli.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lawrence
 * @since 2021/3/21
 */
@ConfigurationProperties(prefix = "websocket")
@Configuration
public class WebSocketProperties {

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String nacosServerAddress;

    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String nacosNamespace;

    public String getNacosServerAddress() {
        return nacosServerAddress;
    }

    public void setNacosServerAddress(String nacosServerAddress) {
        this.nacosServerAddress = nacosServerAddress;
    }

    public String getNacosNamespace() {
        return nacosNamespace;
    }

    public void setNacosNamespace(String nacosNamespace) {
        this.nacosNamespace = nacosNamespace;
    }

    private Service service;

    private Docker docker;


    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Docker getDocker() {
        return docker;
    }

    public void setDocker(Docker docker) {
        this.docker = docker;
    }

    public static class Service {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public static class Docker {

        private String host;

        private String network;

        private Image image;

        public static class Image {
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }
    }


}
