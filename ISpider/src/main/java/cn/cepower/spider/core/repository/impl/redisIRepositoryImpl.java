package cn.cepower.spider.core.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


import cn.cepower.spider.constants.SpiderConstants;
import cn.cepower.spider.core.repository.IRepository;

@Component("redisIRepositoryImpl")
public class redisIRepositoryImpl implements IRepository {
	@Autowired
	@Qualifier("stringRedisTemplate")
	private StringRedisTemplate template;  //主服务器上的redis
	@Autowired
	@Qualifier("stringRedisTemplate2")
	private StringRedisTemplate template2;  //本地的redis
	@Override
	public Map<String, List<Object>> poll() {
		Map<String,List<Object>> result = new HashMap<String, List<Object>>();
    	List<Object> list = new ArrayList<Object>();
    	if((list = pollL()).size()>0) {
    		 result.put(SpiderConstants.SPIDER_DOMAIN_LOWER_SUFFIX, list);
    		 return result;
    	}else if((list = pollH()).size()>0) {
    		result.put(SpiderConstants.SPIDER_DOMAIN_HIGHER_SUFFIX, list);
    		return result; 	
    	}
    	return null; 
	}
	
	 /**
     * 截取主机上传的任务
     * @return
     */
    public List<Object> pollL() {
    	int flag = SpiderConstants.SPIDER_URLTRIM_LENGTH;
    	String key = SpiderConstants.SPIDER_DOMAIN_LOWER_SUFFIX;
    	int size =  template.opsForList().size(key).intValue();
    	List<Object> ulist = new  ArrayList<Object>();	
    	if(size>flag) ulist = leftPopAll(flag,key);
		else {
			ulist = leftPopAll(size,SpiderConstants.SPIDER_DOMAIN_LOWER_SUFFIX);
		} 
        System.out.println(ulist.size());
    	return ulist;
    }
    
    
    /**
     * 截取主机上传的任务
     * @return
     */
    public List<Object> pollH() {
    	int flag = SpiderConstants.SPIDER_URLTRIM_LENGTH;
    	String key = SpiderConstants.SPIDER_DOMAIN_HIGHER_SUFFIX;
    	int size =  template.opsForList().size(key).intValue();
    	List<Object> ulist = new  ArrayList<Object>();	
    	if(size>flag) ulist = leftPopAll(flag,key);
    		else {
    			ulist = leftPopAll(size,key);
    		} 
        System.out.println(ulist.size());
    	return ulist;
    }
    
    
    public List<Object> leftPopAll(final Integer len,final String key){
    	
    	return template.execute(new RedisCallback<List<Object>>() {
			@Override
			public List<Object> doInRedis(RedisConnection connection)
					throws DataAccessException {
				connection.openPipeline();
				for(int i=0; i<len;i++){		
					connection.lPop(key.getBytes());		
				}				
				return connection.closePipeline();
			}
		});
    	
    	}

	@Override
	public void addSeedUrls() {
		final List<String> seedUrls = template2.opsForList().range(SpiderConstants.SPIDER_SEED_URLS_KEY, 0, -1);
    	System.out.println(seedUrls.size());
		template2.executePipelined(new RedisCallback<Object>() {
    	//int i = 1/0;  //测试	
 		@Override
 		public Object doInRedis(RedisConnection connection)
 				throws DataAccessException {
 			int flag = 50000;
 			int size = seedUrls.size();
 			int temp = size / flag + 1;
 			for (int i = 0; i < temp; i++) {
 				List<String> slist = null;
 				if (i == temp - 1) {
 					if (size % flag == 0) {
 						break;
 					} else {
 						slist = seedUrls.subList(flag * i, size);
 						for(String url:slist){
 			 				connection.lPush(SpiderConstants.SPIDER_DOMAIN_HIGHER_SUFFIX.getBytes(), url.getBytes());
 			 			}	
 					}
 				} else {
 					slist = seedUrls.subList(flag * i, flag * (i + 1));
 					for(String url:slist){
 		 				connection.lPush(SpiderConstants.SPIDER_DOMAIN_HIGHER_SUFFIX.getBytes(), url.getBytes());
 		 			}
 				}
 			}
 			
 		
 			return null;
 		}
 	}) ;
	}

}
