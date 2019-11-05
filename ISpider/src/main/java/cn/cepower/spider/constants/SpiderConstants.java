package cn.cepower.spider.constants;


public interface SpiderConstants {

	//种子url—Key
    public String SPIDER_SEED_URLS_KEY = "seedUrl";
    //来源
    String SPIDER_DOMAIN_HIGHER_SUFFIX = "higher";
    //结果
    String SPIDER_DOMAIN_LOWER_SUFFIX = "lower";
    
    Integer SPIDER_URLTRIM_LENGTH = 5000;
    
    String BLOOM_FILTER_REDIS_KEY = "bloomfilter";

}
