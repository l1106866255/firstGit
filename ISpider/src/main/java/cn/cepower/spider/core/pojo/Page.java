package cn.cepower.spider.core.pojo;

import org.jsoup.nodes.Document;

public class Page {
    private String url;                 
    private String urlLevel;
    private Document document;
    
    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getUrlLevel() {
		return urlLevel;
	}

	public void setUrlLevel(String urlLevel) {
		this.urlLevel = urlLevel;
}
	}
