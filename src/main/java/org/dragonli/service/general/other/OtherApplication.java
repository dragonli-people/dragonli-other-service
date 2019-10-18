package org.dragonli.service.general.other;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.dragonli.service.dubbosupport.DubboApplicationBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
		exclude={DataSourceAutoConfiguration.class},
		scanBasePackages={"org.dragonli"})
@DubboComponentScan(basePackages = "org.dragonli.service.general.other")
public class OtherApplication extends DubboApplicationBase {

	public OtherApplication(@Value("${service.micro-service.simple-other-service.application-name}") String applicationName,
			@Value("${service.micro-service.common.registry-address}") String registryAddr,
			@Value("${service.micro-service.simple-other-service.protocol-name}") String protocolName,
			@Value("${service.micro-service.simple-other-service.protocol-port}") Integer protocolPort,
			@Value("${service.micro-service.simple-other-service.scan}") String registryId,
			@Value("${service.micro-service.simple-other-service.http-port}") int port) {
//
		super(applicationName, registryAddr, protocolName, protocolPort, registryId, port);
	}

//	@SuppressWarnings(value="unused")
//	final Logger logger = LoggerFactory.getLogger(getClass());


	public static void main(String[] args) throws Exception{
		SpringApplication.run(OtherApplication.class, args);
	}

//	@Bean
//	@ConditionalOnProperty(value = "service.general.open.redis")
//	public RedissonClient createRedisClient(@Autowired RedisConfigurationGeneral rc) {
//		return RedisClientBuilder.buildRedissionClient(rc);
//	}

}
