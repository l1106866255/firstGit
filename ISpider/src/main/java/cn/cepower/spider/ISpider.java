package cn.cepower.spider;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;





import cn.cepower.spider.ISpider;
import cn.cepower.spider.core.parser.IParser;
import cn.cepower.spider.core.pojo.Page;
import cn.cepower.spider.core.repository.IRepository;
import cn.cepower.spider.download.IDownload;
import cn.cepower.spider.utils.util;


@Component
public class ISpider {
	private String key; //控制爬虫的选择器
	private List<Object> olist;
	private Logger logger = LoggerFactory.getLogger(ISpider.class);
	
	@Autowired
	@Qualifier("redisIRepositoryImpl")
	private IRepository repository;
	
	@Autowired
	@Qualifier("HttpGetDownloadImpl")
	private IDownload download;

	@Autowired
	@Qualifier("HtmlParserImpl")
	private IParser parser;
	/**
	 * 注册zk
	 */
	public void registerZK() {
		String zkStr = getZkStr();  //zookeeper服务的ip地址  如何配置请查看 帮助文档help.txt;
		int baseSleepTimeMs = 1000;
		int maxRetries = 3;
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs,
				maxRetries);
		CuratorFramework curator = CuratorFrameworkFactory.newClient(zkStr,
				retryPolicy);
		curator.start();
		String ip = null;
		try {
			// 向zk的具体目录注册 写节点 创建节点
			ip = InetAddress.getLocalHost().getHostAddress();
			curator.create().withMode(CreateMode.EPHEMERAL)
					.forPath("/server/" + ip, ip.getBytes());

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start_shave02() {

		Map<String, List<Object>> map = repository.poll();		//获取包含urls和选择器key的Map

		if (map!=null) {
			logger.info("初始化成功，顺利的获取url");
			for(Map.Entry<String, List<Object>> entry:map.entrySet()){
				key = entry.getKey();
				olist = entry.getValue();
			}
			
			
		} else {
			String now = util.now();
			try {
				System.out.println("现在的时间是：" + now);
				// MailUtil.sendMail("爬虫程序异常",
				// "节点"+InetAddress.getLocalHost().getHostAddress()+"无法获取到url,请及时添加seedurl");
			} catch (Exception e) {
				System.out.println("现在的时间是：" + now);
				// MailUtil.sendMail("爬虫程序异常", "有节点无法获取到url,请及时添加seedurl");
				e.printStackTrace();
			}
			System.out.println("正在等待。。。。。^^");
			
			repository.addSeedUrls();  //添加种子urls，不让程序停止
			util.sleep(1000 * 60 * 10); // 休息 。。 减小压力

		}

		ExecutorService executorService = Executors.newFixedThreadPool(5);
		for (Object o : olist) {
			final String url = util.rOUrl2String(o);  //从redis下获取的数据需要处理
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					// 下载网页
					Page page = download.download(url,
							key);
					// 解析并储存网页
					parser.parser(page);
				}
			});
		}

		executorService.shutdown();
		System.out.println("shutdown()：启动一次顺序关闭，执行以前提交的任务，但不接受新任务。");
		while (true) {
			if (executorService.isTerminated()) {
				System.out.println("子线程都已经结束了");
				util.sleep(2000);
				return;
			}
		}
	}

	
	
	private String getZkStr(){	
		//使用properties对象加载输入流   zookeeper是src/main/resources文件夹下(zookeeper.properties)的文件名        zookeeper.ip为文件的属性值
		ResourceBundle resource = ResourceBundle.getBundle("zookeeper");			
		String zkStr = resource.getString("zookeeper.ip"); 
	    return zkStr;
	} 
	
	@Test
	public void demo1(){
		System.out.println(getZkStr());
	}
}
