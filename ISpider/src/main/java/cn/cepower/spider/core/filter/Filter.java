package cn.cepower.spider.core.filter;

import java.util.List;
	public interface Filter{
		public String filterUrl(String url);
		public boolean filterUrlList(List<String> ulist);		
	}



