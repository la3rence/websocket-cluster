package me.lawrenceli.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;

/**
 * @author lawrence
 * @since 2021/3/21
 */
@ConfigurationProperties(prefix = "websocket")
@Configuration
public class WebSocketProperties extends Properties {

    private static final long serialVersionUID = -7568982050310381466L;

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

    public static class Service implements Serializable {

        private static final long serialVersionUID = 4470917617306041628L;

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    public static class Docker implements Serializable {

        private static final long serialVersionUID = -2233645916767230952L;

        private String host;

        private String network;

        private Image image;

        public static class Image implements Serializable {

            private static final long serialVersionUID = 648412373970515185L;

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


    @Override
    public synchronized boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WebSocketProperties that = (WebSocketProperties) o;
        return Objects.equals(nacosServerAddress, that.nacosServerAddress) && Objects.equals(nacosNamespace, that.nacosNamespace) && Objects.equals(service, that.service) && Objects.equals(docker, that.docker);
    }

    @Override
    public synchronized int hashCode() {
        return Objects.hash(super.hashCode(), nacosServerAddress, nacosNamespace, service, docker);
    }
}
