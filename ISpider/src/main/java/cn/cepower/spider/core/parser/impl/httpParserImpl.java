package cn.cepower.spider.core.parser.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


import cn.cepower.spider.core.filter.Filter;
import cn.cepower.spider.core.parser.IParser;
import cn.cepower.spider.core.pojo.Page;
import cn.cepower.spider.utils.htmlUtil;
@Component
public class httpParserImpl implements IParser {
	private Logger logger = LoggerFactory.getLogger(httpParserImpl.class);
	@Autowired
	@Qualifier("bloomfilter")
	private Filter filter;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public void parser(Page page) {
		Document doc = page.getDocument();
		if (doc == null) {
			return;
		}
		long start = System.currentTimeMillis(); // 解析开始时间
		// 进行判断 根据url的类型进行列表解析还是商品解析
		
		//logger.info("运行到这里了......" + page.getUrlLevel());
		if (page.getUrlLevel().equalsIgnoreCase("higher")) { // 解析结果页面
			parserHPage(page);
			logger.info("解析来源页面:{}, 消耗时长:{}ms", page.getUrl(),
					System.currentTimeMillis() - start);
		} else if (page.getUrlLevel().equalsIgnoreCase("lower")) {

			// 解析来源页面
			parserLPage(page);
			logger.info("解析结果页面:{}, 消耗时长:{}ms", page.getUrl(),
					System.currentTimeMillis() - start);

		} else {
			logger.info("解析页面:{}失败无相关的解析器, 消耗时长:{}ms", page.getUrl(),
					System.currentTimeMillis() - start);
		}
	}
	
	/*
	 * 自定义爬取规则
	 *	
	 */
	public void parserLPage(Page page) {
		Document doc = page.getDocument();
		String time = htmlUtil.gettime(doc.text());
		String keyword = htmlUtil.getKeyWord(doc.text(), new ArrayList<String>(Arrays.asList("电话","联系人")));
		//通过jdbc模板插入数据到数据库下实现持久化
		jdbcTemplate.execute("insert ignore into test(time,keyword) value " +
				"(\'"+time+"\',\'"+keyword+"\')");
	}

	public void parserHPage(Page page) {
		Document doc = page.getDocument();
		Elements href = doc.select("a");
		List<String> ulist = new ArrayList<String>();
		for (Element e : href) {
			String url = e.absUrl("abs:href");
			if(url!=null) ulist.add(url);
		}
		filter.filterUrlList(ulist);
	}

}
