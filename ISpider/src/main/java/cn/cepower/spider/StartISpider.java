package cn.cepower.spider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.cepower.spider.ISpider;
import cn.cepower.spider.StartISpider;


public class StartISpider {
	@Autowired
	private ISpider iSpider;
	
    public static void main(String[] args) {
    	ApplicationContext app = new ClassPathXmlApplicationContext("applicationcontext.xml");
    	StartISpider startISpider = app.getBean(StartISpider.class);
		startISpider.start();
    }
    
    /**
     * 初始化
     */
    
    public void start(){
    	iSpider.registerZK();  //注册zookeeper 监视程序运行情况
    	//死循环 保证程序不被干扰
    	while(true){
        iSpider.start_shave02();
    	}
    }
}
