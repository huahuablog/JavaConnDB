package com.conn.db.redis.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.conn.db.base.entity.Constants;
import com.java.util.IOUtil;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public class RedisDao {

	private static JedisCluster redis=null;
	private static JedisCluster getRedisConnection(){
		if(redis==null){
			createPool();
		}
		return redis;
	}
	private static void createPool(){
		String maxToal=IOUtil.getPropValue("redis.server.pool.size",Constants.CONFIG);
		String addressList=IOUtil.getPropValue("redis.cluster.address",Constants.CONFIG);
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		for(String address:addressList.split(";")){
			jedisClusterNodes.add(
					new HostAndPort( address.split(":")[0] , Integer.parseInt(address.split(":")[1]) ) );
		}
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxWaitMillis(20*1000);
		config.setMaxTotal(Integer.parseInt(maxToal));
		redis = new JedisCluster(jedisClusterNodes,config);
		
	}
	
//getvalue
	public static String getValue(String key,String subKey){
		JedisCluster jd=RedisDao.getRedisConnection();
		String reStr= jd.hget(key, subKey);
		return reStr;
	}
	public static String getValue(String key){
		JedisCluster jd=RedisDao.getRedisConnection();
		String reStr=jd.get(key);
		return reStr;
	}
	//��ȡkey���õĴ���
	public static Long getIncr(String key){
		JedisCluster jd=RedisDao.getRedisConnection();
		return jd.incr(key);
	}
	public static Long getIncr(String key,String subKey){
		JedisCluster jd=RedisDao.getRedisConnection();
		return jd.hincrBy(key, subKey,1);
	}
	//��ȡkey��Ӧvalueֵ���õĴ���
	public static Long getIncr(String key,String subKey,long value){
		JedisCluster jd=RedisDao.getRedisConnection();
		return jd.hincrBy(key, subKey,value);
	}
	//setvalueֵ
	public static void setValue(String key,String value){
		JedisCluster jd=RedisDao.getRedisConnection();
		jd.set(key, value);
	}
	
	public static void setValue(String key,String subKey,String value){
		JedisCluster jd=RedisDao.getRedisConnection();
		jd.hset(key,subKey, value);
	}
	//����key�����ʱ�䣬��ʱ���Զ�ɾ��
	public static void expire(String key, int seconds){
		JedisCluster jd=RedisDao.getRedisConnection();
		jd.expire(key, seconds);
	}
	//ֱ��ɾ��key
	public static void delKey(String key){
		JedisCluster jd=RedisDao.getRedisConnection();
		jd.del(key);
	}
	
	public static void delKey(String key,String subKey){
		JedisCluster jd=RedisDao.getRedisConnection();
		jd.hdel(key, subKey);
	}
	//��ȡkey���Ӧ��key-value��ֵ�Դ�map
	public static Map<String,String> getAllValue(String key){
		JedisCluster jd=RedisDao.getRedisConnection();
		Map<String,String> reStr=jd.hgetAll(key);
		return reStr;
	}
	//�ж�key�Ƿ����
	public static boolean keyIsExists(String key){
		JedisCluster jd=RedisDao.getRedisConnection();
		return jd.exists(key);
	}
	
	public static boolean keyIsExists(String key,String subKey){
		JedisCluster jd=RedisDao.getRedisConnection();
		return jd.hexists(key, subKey);
	}
		
}

