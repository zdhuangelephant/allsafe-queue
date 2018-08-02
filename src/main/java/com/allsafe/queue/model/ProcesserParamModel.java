package com.allsafe.queue.model;

import java.util.List;

import com.allsafe.queue.enums.WeightEnum;
import com.allsafe.queue.processer.IProcesser;
import com.allsafe.queue.server.ContainerDefaultParam;
import com.google.common.collect.Lists;


/**
 * @name ProcesserParamModel 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 拉取消息处理者参数模型
 * @version 1.0
 */
public class ProcesserParamModel {

	/** processerList processer实例列表 */
	private List<Processer> processerList = Lists.newArrayList();

	/** totalWeight 总权重 */
	private Integer totalWeight = 0;

	/** processer执行的频率 */
	private Integer processerRateTime;

	/** processerPath 扫描消息处理者位置 */
	private String processerPath;

	public void addProcesser(IProcesser processer, WeightEnum weight) {
		if (null == processer)
			return;
		if (null == weight)
			weight = WeightEnum.ONE;
		totalWeight += weight.getCode();
		Processer _proProcesser = new Processer(processer, weight.getCode(),
				processerRateTime);
		processerList.add(_proProcesser);
	}

	public String getProcesserPath() {
		return processerPath;
	}

	public void setProcesserPath(String processerPath) {
		this.processerPath = processerPath;
	}

	public Integer getProcesserRateTime() {
		return processerRateTime == null || processerRateTime <= 0 ? ContainerDefaultParam.WORKER_RATE_TIME
				: processerRateTime;
	}

	public void setProcesserRateTime(Integer processerRateTime) {
		this.processerRateTime = processerRateTime;
	}

	public List<Processer> getProcesserList() {
		return processerList;
	}

	public Integer getTotalWeight() {
		return totalWeight;
	}

	public static class Processer {
		/** processer process实例 */
		private IProcesser processer;
		/** weight processer所占权重 */
		private Integer weight;

		private Processer(IProcesser processer, Integer weight,
				Integer processerRateTime) {
			this.processer = processer;
			this.weight = weight;
		}

		public IProcesser getProcesser() {
			return processer;
		}

		public void setProcesser(IProcesser processer) {
			this.processer = processer;
		}

		public Integer getWeight() {
			return weight;
		}

		public void setWeight(Integer weight) {
			this.weight = weight;
		}

	}

}
