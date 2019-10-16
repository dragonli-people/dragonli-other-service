package org.dragonli.service.general.other;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.dragonli.service.dubbosupport.DubboApplicationBase;
import org.dragonli.tools.redis.RedisConfigurationGeneral;
import org.dragonli.tools.redis.redisson.RedisClientBuilder;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

//@ComponentScan(basePackages = {"com.alpacaframework"})
@EnableScheduling
@SpringBootApplication(
		exclude={DataSourceAutoConfiguration.class},
		scanBasePackages={"org.dragonli","org.dragonli.tools.redis","org.dragonli.tools.redis.redisson"})
@DubboComponentScan(basePackages = "org.dragonli.service.general.other")
public class OtherApplication extends DubboApplicationBase {

	public OtherApplication(@Value("${spring.other-server.application.name}") String applicationName,
			@Value("${spring.common.registry.address}") String registryAddr,
			@Value("${spring.other-server.protocol.name}") String protocolName,
			@Value("${spring.other-server.protocol.port}") Integer protocolPort,
			@Value("${spring.other-server.scan}") String registryId,
			@Value("${micro-service-port.other-service-port}") int port) {
//
		super(applicationName, registryAddr, protocolName, protocolPort, registryId, port);
	}

//	@SuppressWarnings(value="unused")
//	final Logger logger = LoggerFactory.getLogger(getClass());


	public static void main(String[] args) {

		SpringApplication.run(OtherApplication.class, args);
	}

//	@Bean
//	@ConditionalOnProperty(value = "service.general.open.redis")
//	public RedissonClient createRedisClient(@Autowired RedisConfigurationGeneral rc) {
//		return RedisClientBuilder.buildRedissionClient(rc);
//	}

}
