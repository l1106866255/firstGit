package cn.cepower.spider.download;

import cn.cepower.spider.core.pojo.Page;


public interface IDownload {
	/**
     * 下载给定url的网页数据
     * @param url
     * @return
     */
    public Page download(String url,String urlLevel);
}
