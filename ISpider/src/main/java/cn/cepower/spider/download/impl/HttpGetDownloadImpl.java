package cn.cepower.spider.download.impl;

import java.io.IOException;

import org.jsoup.nodes.Document;


import cn.cepower.spider.core.pojo.Page;
import cn.cepower.spider.download.IDownload;
import cn.cepower.spider.utils.httpUtil;

public class HttpGetDownloadImpl implements IDownload{

	@Override
	public Page download(String url, String urlLevel) {
		Page page = new Page();
        Document doc = null;
		try {
			//System.out.println("dsaf");
			doc = httpUtil.getPageDocument(url);
		} catch (IOException e) {
			//jdbcTemplate.execute("insert ignore into error (url) values (\'"+url+"\')");
			e.printStackTrace();
		}  // 获取网页数据
        page.setUrl(url);
        page.setDocument(doc);
        page.setUrlLevel(urlLevel);
        //System.out.println(doc.toString());
        return page;
	}

}
