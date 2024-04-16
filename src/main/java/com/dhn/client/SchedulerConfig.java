package com.dhn.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
	private final int POOL_SIZE = 10;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		// TODO Auto-generated method stub
		ThreadPoolTaskScheduler tpts = new ThreadPoolTaskScheduler();
		
		tpts.setPoolSize(POOL_SIZE);
		tpts.setThreadNamePrefix("DHN-sch-task-pool-");
		tpts.initialize();
		
		taskRegistrar.setTaskScheduler(tpts);
	}
	

}
