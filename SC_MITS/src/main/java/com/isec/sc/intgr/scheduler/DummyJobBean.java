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


public class DummyJobBean extends QuartzJobBean {

    private DummyTask dummyTask;

    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {

        dummyTask.print();

    }


    public void setDummyTask(DummyTask dummyTask) {
        this.dummyTask = dummyTask;
    }

}
