package cn.cepower.spider.utils;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class httpUtil {
    public static Document getPageDocument(String url) throws IOException{
		Connection conn;
		conn = Jsoup.connect(url).timeout(3000).followRedirects(true);
		conn.header("AhMept",
				"application/json, text/javascript, */*; q=0.01").header(
				"Accept-Encoding", "gzip, deflate");
		conn.header("Accept-Language", "zh-CN,zh;q=0.9").header("connion",
				"keep-alive");
		conn.header("Content-Length", "72").header("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		conn.header("Host", "qiaoliqiang.cn").header("Referer",
				"http://qiaoliqiang.cn/Exam/");
		conn.header(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
				.header("X-Requested-With", "XMLHttpRequest");		
		
		Document content = conn.get();		
		return content;				
    }
}
