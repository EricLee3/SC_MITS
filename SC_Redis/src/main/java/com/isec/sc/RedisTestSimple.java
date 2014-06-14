package com.isec.sc;

import java.net.URL;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisTestSimple {

    // inject the actual template
    @Autowired
    private StringRedisTemplate redisTemplate;

    // inject the template as ListOperations
    // can also inject as Value, Set, ZSet, and HashOperations
    @Resource(name="stringRedisTemplate")
    private ListOperations<String, String> listOps;
    
    
    public RedisTestSimple(){}
    
    public void addLink(String userId, URL url) {
        listOps.leftPush(userId, url.toExternalForm());
        // or use template directly
        redisTemplate.boundListOps(userId).leftPush(url.toExternalForm());
    }
    
    
    public static void main(String[] args) throws Exception{
    	
    	RedisTestSimple redisTest = new RedisTestSimple();
    	
    	redisTest.addLink("user", new URL("http://localhost"));
    }
    
    
    
}