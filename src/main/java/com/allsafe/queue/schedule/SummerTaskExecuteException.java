package com.allsafe.queue.schedule;

public class SummerTaskExecuteException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6056711224130146342L;

	public SummerTaskExecuteException(Thread currentThread, Throwable t) {
		super(String.format("任务执行异常.Thread : %s", currentThread.getName()), t);
	}

}
