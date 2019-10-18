package org.dragonli.service.general.other;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.dragonli.tools.zookeeper.ZookeeperClientUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ZookeeperLock {
	private final static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	private ZookeeperClientUtil zkp;
	private String baseKey;
	private int timeout;
	
	public ZookeeperLock(
			@Value("${spring.lock-cinfig.zookeeperAddr}") String zkpAdd
			,@Value("${spring.lock-cinfig.zkpTimeout}") int zkpTimeout
			,@Value("${spring.lock-cinfig.baseKey}") String baseKey
			,@Value("${spring.lock-cinfig.timeout}") int timeout)
	{
		this.zkp = new ZookeeperClientUtil();
		zkp.setServers(zkpAdd);
		zkp.setSessionTimeout(zkpTimeout);
		zkp.zkReconnect();//??
		this.baseKey = baseKey;
		this.timeout = timeout;
		
		cachedThreadPool.execute(new Runnable(){

			@Override
			public void run() {
				//定时清除过期的、不知因何故未被清除的锁
				// TODO Auto-generated method stub
				while(true)
				{
					long now = System.currentTimeMillis();
					List<String> list = null;
					try
					{
						list = zkp.getAliveZk().getChildren(baseKey, false);
					}catch(Exception e){}
					if(list!=null)
					{
						for(String k:list)
						{
							if( Math.abs(now-Long.parseLong(k.split("@")[1])) > 20000 && Math.abs(now-Long.parseLong(k.split("@")[1])) > Long.parseLong(k.split("@")[2]) )
							{
								try
								{
									if(zkp.getAliveZk().exists(baseKey+"/"+k, false)!=null)
										zkp.getAliveZk().delete(baseKey+"/"+k, -1);
								}catch(Exception e){}
							}
						}
					}
					
					try {
						Thread.sleep(10000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		});
	}

	public boolean lock(String lockKey,int code,Integer time)
	{
		
		ZookeeperLockElement e = new ZookeeperLockElement();
		return e.lock(zkp, baseKey, lockKey, code, cachedThreadPool, time == null ? timeout : time);
	}
	
	public void release(String lockKey,int code)
	{
		try {
			List<String> list = zkp.getAliveZk().getChildren(baseKey, false);
			for(String k:list)
			{
				String[] split = k.split("@");
				if(k.startsWith(lockKey+"@")&&split[3].equals(String.valueOf(code)))
				{
					if(zkp.getAliveZk().exists(baseKey+"/"+k, false)!=null)
						zkp.getAliveZk().delete(baseKey+"/"+k, -1);
					return;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}
	
}


