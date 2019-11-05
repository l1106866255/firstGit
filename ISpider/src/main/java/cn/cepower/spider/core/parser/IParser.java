package cn.cepower.spider.core.parser;

import cn.cepower.spider.core.pojo.Page;

public interface IParser {
	public void parser(Page page);
	
	public void parserLPage(Page page);
	
	public void parserHPage(Page page);
}
