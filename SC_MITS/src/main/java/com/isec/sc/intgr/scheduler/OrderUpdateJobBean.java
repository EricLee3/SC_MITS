/*
 *  
 *  * Revision History
 *  * Author              Date                  Description
 *  * ------------------   --------------       ------------------
 *  *  beyondj2ee          2014.01.02              
 *  
 */

package com.isec.sc.intgr.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class OrderUpdateJobBean extends QuartzJobBean {

    private OrderCreateTask orderCreateTask;
    private String redisKey;
    private String redisPushKey;
    private String redisErrKey;

    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {

    	
    	orderCreateTask.createOrder(redisKey, redisPushKey, redisErrKey);

    }


    public void setOrderCreateTask(OrderCreateTask orderCreateTask) {
        this.orderCreateTask = orderCreateTask;
    }
    
    public void setRedisKey(String redisKey){
    	this.redisKey = redisKey;
    }
    
    public void setRedisPushKey(String redisPushKey){
    	this.redisPushKey = redisPushKey;
    }
    
    public void setRedisErrKey(String redisErrKey){
    	this.redisErrKey = redisErrKey;
    }
}
