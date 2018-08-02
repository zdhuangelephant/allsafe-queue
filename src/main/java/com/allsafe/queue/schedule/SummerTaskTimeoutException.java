package com.allsafe.queue.schedule;

import com.alibaba.fastjson.JSON;


/**
 * @name SummerTaskTimeoutException 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 任务超时异常
 * @version 1.0
 */
public class SummerTaskTimeoutException extends RuntimeException {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8665534678998540108L;

	private static class TaskInfo {
		@SuppressWarnings("unused")
		String threadName;
		@SuppressWarnings("unused")
		long startTime;
		@SuppressWarnings("unused")
		long validTime;
		@SuppressWarnings("unused")
		long queuingTime;

		@Override
		public String toString() {
			return JSON.toJSONString(this);
		}

		TaskInfo(Thread t, long startTime, long validTime, long queuingTime) {
			this.queuingTime = queuingTime;
			this.startTime = startTime;
			this.validTime = validTime;
			this.threadName = t.getName();
		}
	}

	public SummerTaskTimeoutException(Thread t, long startTime, long validTime,
			long queuingTime) {
		super(String.format("SummerTaskExecutor 执行任务超时.TaskInfo:%s",
				new TaskInfo(t, startTime, validTime, queuingTime).toString()));
	}

}
