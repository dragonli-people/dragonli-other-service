package org.dragonli.service.general.other;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.dragonli.tools.zookeeper.ZookeeperClientUtil;

public class ZookeeperLockElement {
	private String newNode;
	@SuppressWarnings(value="unused")
	public static Logger logger = Logger.getLogger(ZookeeperLockElement.class);
	public boolean lock(ZookeeperClientUtil zkp,String baseKey
			,String lockKey,int code
			,ExecutorService cachedThreadPool,int timeout)
	{
		String myKey = baseKey+"/"+lockKey+"@"+System.currentTimeMillis()+"@"+timeout+"@"+code+"@";
		newNode=null;
		try {
			newNode = zkp.getAliveZk().create(myKey , new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		if(newNode == null) return false;
		
		boolean success = false;
		try {
			List<String> list = zkp.getAliveZk().getChildren(baseKey, false);
			int minId=Integer.MAX_VALUE;
			for(String kk :list)
			{
//				logger.info("加锁成功==="+StringUtils.join(list, "|")+"     "+myKey+"     kk:"+kk);
				if(kk.startsWith(lockKey+"@"))
				{
					
//					success = kk.endsWith("@"+code);				
					minId = Math.min(minId, Integer.parseInt(kk.split("@")[4]));
//					logger.info("最小ID==="+minId);
				}
			}
//			logger.info(newNode);
//			logger.info(myKey);
//			logger.info(Integer.parseInt(newNode.replaceAll(myKey, "")));
			success = Integer.parseInt(newNode.replaceAll(myKey, "")) <= minId; 
//			logger.info("成功？"+success);
			
			if(success)
			{
				//若干秒后超时解锁
				cachedThreadPool.execute(new Runnable(){
					public void run()
					{
						String lastNode = newNode;
						try {
//							logger.info("时间后解锁？"+timeout);
							Thread.sleep(timeout);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try
						{
							if(zkp.getAliveZk().exists(lastNode, false)!=null)
								zkp.getAliveZk().delete(lastNode, -1);
						}catch(Exception ee){}
					}
				});
			}
			else{
//				logger.info("删除？"+success);
				zkp.getAliveZk().delete(newNode, -1);
			}
				
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return success;
	}
	
}