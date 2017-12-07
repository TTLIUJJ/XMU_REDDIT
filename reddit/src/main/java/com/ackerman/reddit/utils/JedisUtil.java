package com.ackerman.reddit.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JedisUtil {
    private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);
    private static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.get(key);
        }catch (Exception e){
            logger.error("get异常 " + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long del(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.del(key);
        }catch (Exception e){
            logger.info("del异常: " + e.getMessage());
            return -1;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }


    public void set(String key, String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        }catch (Exception e){
            logger.error("set异常 " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }


    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("sadd异常" + e.getMessage());
            return -1;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("srem异常" + e.getMessage());
            return -1;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("scard异常: " + e.getMessage());
            return -1;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("sismember异常: "+e.getMessage());
            return false;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public Set<String> smembers(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.smembers(key);
        }catch (Exception e){
            logger.info("smembers异常: " + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long lpush(String key, String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.lpush(key, value);
        }catch (Exception e){
            logger.error("lpush异常"+e.getMessage());
            return -1;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    //timeout == 0 时候阻塞线程
    public List<String> brpop(String key, int timeout){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.brpop(timeout, key);
        }catch (Exception e){
            logger.error("lpush异常"+e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public String lindex(String key, int inedex){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.lindex(key, inedex);
        }catch (Exception e){
            logger.error("lindex异常 " + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }


    public String hget(String key, String field){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.hget(key, field);
        }catch (Exception e){
            logger.error("hget异常" + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void hset(String key, String field, String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.hset(key, field, value);
        }catch (Exception e){
            logger.error("hset异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void hdel(String key, String field){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.hdel(key, field);
        }catch (Exception e){
            logger.error("hdel异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public Map<String, String> hgetAll(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.hgetAll(key);
        }catch (Exception e){
            logger.error("hgetall异常" + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public boolean hexists(String key, String field){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.hexists(key, field);
        }catch (Exception e){
            logger.error("hexists异常" + e.getMessage());
            return false;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long zcard(String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.zcard(key);
        }catch (Exception e){
            logger.info("zcard异常: " + e.getMessage());
            return -1;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void zadd(String key, double score, String value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.zadd(key, score, value);
        }catch (Exception e){
            logger.info("zadd异常: " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long zrem(String key, String member){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.zrem(key, member);
        }catch (Exception e){
            logger.info("zadd异常: " + e.getMessage());
            return -1;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public Set<String> zrevrangeByScore(String key, double max, double min){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.zrevrangeByScore("REPORT_NEWS", "+inf", "0");
//            return jedis.zrangeByScore(key, max, min);
        }catch (Exception e){
            logger.info("zrangeByScore异常: " + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public double zscore(String key, String member){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return zscore(key, member);
        }catch (Exception e){
            logger.info("zscore异常: " + e.getMessage());
            return -1.0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }

    }
}
