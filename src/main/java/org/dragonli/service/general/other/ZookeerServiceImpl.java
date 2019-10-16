/**
 * 
 */
package org.dragonli.service.general.other;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import org.dragonli.service.general.interfaces.general.ZookeeperService;

/**
 * @author kangzhijie
 *
 */
@Service(interfaceClass=ZookeeperService.class, register=true, timeout = 6000,retries=0,delay=-1)
public class ZookeerServiceImpl implements ZookeeperService {
//	@Autowired
//	@Qualifier(OtherApplication.MANAGE_JDBC_TEMPLATE)
//	protected JdbcTemplate jdbcTemplate;
	@Autowired
	RedissonClient redission;
	private static Logger logger = Logger.getLogger(ZookeerServiceImpl.class);
	private final AtomicInteger sendWarningMessageTimeListCount = new AtomicInteger(0);
	private final Queue<Long> sendWarningMessageTimeList = new ConcurrentLinkedQueue<>(); 
//	public ConfigsService getConfigsService() {
//		return configsService;
//	}
//
//	public void setConfigsService(ConfigsService configsService) {
//		this.configsService = configsService;
//	}

	private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	@Autowired
	private ZookeeperLock lockHandler;
	

//	@Override
//	public void start() throws Exception {
//		// TODO Auto-generated method stub
//	}
	
	public void sendWarningMessage(String content){		
		//TODO 
	}
	
	public void asynExecute(String url,Map<String,Object> para) throws Exception
	{
		// TODO 
	}
	
	@Override
	public boolean lock(String key,int code)
	{
		return this.lock(key, code, null);
	}
	
	@Override
	public boolean lock(String key,int code,Integer timeout)
	{
//		return true;//调通之前，保持如此。调试时启用下面的语句
		return lockHandler.lock(key, code,timeout);
	}
	
	@Override
	public void releaseLock(String key,int code)
	{
		//调通之前，保持如此。调试时启用下面的语句
		lockHandler.release(key, code);
	}
	
	public int stringHashCode(String s)
	{
		return s == null ? 0 : s.hashCode();
	}
	
//	public String mapRedisHost(String key)
//	{
//		return configsService.achieveConfig("redis.host");//临时
//	}
	
	private String serviceVersion;

	public String getServiceVersion() {
		return serviceVersion;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	@Override
	public String mapRedisHost(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void sendMessage(String mobile, String content) {

	}
}
