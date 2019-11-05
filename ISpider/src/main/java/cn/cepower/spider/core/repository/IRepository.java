package cn.cepower.spider.core.repository;

import java.util.List;
import java.util.Map;

public interface IRepository {
	public Map<String,List<Object>> poll();
	public void addSeedUrls();
}
