package me.lawrenceli.gateway.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.google.common.collect.Lists;
import me.lawrenceli.gateway.config.WebSocketProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Access Docker API by official Java SDK
 *
 * @author lawrence
 * @since 2021/3/19
 */
@Service
public class DockerService {

    private static final Logger logger = LoggerFactory.getLogger(DockerService.class);

    final WebSocketProperties webSocketProperties;
    final DockerClient dockerClient;

    public DockerService(WebSocketProperties webSocketProperties) {
        this.webSocketProperties = webSocketProperties;
        this.dockerClient = this.getDockerClient();
    }

    private DockerClient getDockerClient() {
        logger.debug("初始化 Docker 客户端");
        DockerClientBuilder dockerClientBuilder = DockerClientBuilder.getInstance();
        try {
            dockerClientBuilder.withDockerHttpClient(
                    new JerseyDockerHttpClient.Builder()
                            .dockerHost(new URI(webSocketProperties.getDocker().getHost()))
                            .build());
        } catch (URISyntaxException e) {
            logger.error("Docker Host 不是合法的 URI");
            e.printStackTrace();
        }
        return dockerClientBuilder.build();
    }

    public List<Container> ps(String containerNamePrefix) {
        logger.info("执行: docker ps|grep {}", containerNamePrefix);
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd().withNameFilter(Lists.newArrayList(containerNamePrefix));
        return listContainersCmd.exec();
    }

    public CreateContainerResponse createContainer(String imageName) {
        logger.info("执行: 创建 {} 的容器", imageName);
        return dockerClient.createContainerCmd(imageName)
                .withHostConfig(HostConfig.newHostConfig().withNetworkMode(webSocketProperties.getDocker().getNetwork()))
                .withName(webSocketProperties.getService().getName() + '-' + System.currentTimeMillis())
                .exec();
    }

    public void runContainer(String containerId) {
        logger.info("执行: 启动容器 {}", containerId);
        dockerClient.startContainerCmd(containerId).exec();
    }

    public void stopContainer(String containerId) {
        logger.info("执行: 关闭容器 {}", containerId);
        dockerClient.stopContainerCmd(containerId).exec();
    }

    public void removeContainer(String containerId) {
        logger.info("执行: 删除容器 {}", containerId);
        dockerClient.removeContainerCmd(containerId).exec();
    }

}
