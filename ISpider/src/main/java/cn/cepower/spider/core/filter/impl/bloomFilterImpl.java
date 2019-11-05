package cn.cepower.spider.core.filter.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import cn.cepower.spider.constants.SpiderConstants;
import cn.cepower.spider.core.filter.Filter;

/**
 * @author yui
 * 用bloomfilter(布隆过滤器)过滤重复的url  <redis生成的> 
 */
@Component("bloomfilter")
public class bloomFilterImpl implements Filter {
	@Autowired
	@Qualifier("stringRedisTemplate")
	private StringRedisTemplate template;	 
	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	// 总的bitmap大小 64M
	private static final int cap = 1 << 31;
	//不同哈希函数的种子，一般取质数 seeds数组共有8个值，则代表采用8种不同的哈希函数
	private int[] seeds = new int[] { 3, 5, 7, 11, 13, 31, 37, 61 };
	//flag 判断是否有不重复的url 如果有就插入到数据库里
	private boolean flag = true;  

	/**
	 * 测试布隆过滤器整合在redis中的效果
	 */
	@Test
	public void demo1() {
		ApplicationContext app = new ClassPathXmlApplicationContext(
				"applicationcontext.xml");
		bloomFilterImpl rFilter = app.getBean(bloomFilterImpl.class);
		rFilter.filterUrl("测试");
	}

	private int hash(String value, int seed) {
		int result = 0;
		int length = value.length();
		for (int i = 0; i < length; i++) {
			result = seed * result + value.charAt(i);
		}
		return (cap - 1) & result;
	}

	public void multiSetBit(final String name, final Boolean value,
			final long... offsets) {
		template.executePipelined(new RedisCallback<Object>() {

			@Override
			public Object doInRedis(RedisConnection connection)
					throws DataAccessException {
				for (long offset : offsets) {
					connection.setBit(name.getBytes(), offset, value);
				}
				return null;
			}
		});
	}

	public List<Boolean> multiGetBit(final String name, final long... offsets) {
		List<Object> results = template
				.executePipelined(new RedisCallback<Object>() {

					@Override
					public Object doInRedis(RedisConnection connection)
							throws DataAccessException {
						for (long offset : offsets) {
							connection.getBit(name.getBytes(), offset);
						}
						return null;
					}
				});

		List<Boolean> list = new ArrayList<Boolean>();
		for (Object result : results) {
			list.add((Boolean) result);
		}

		return list;
	}


	@Override
	public String filterUrl(String url) {
		// 实际输入
		long[] offsets = new long[seeds.length];
		for (int i = 0; i < seeds.length; i++) {
			int hash = hash(url, seeds[i]);
			offsets[i] = hash;
		}
		List<Boolean> results = multiGetBit(
				SpiderConstants.BLOOM_FILTER_REDIS_KEY, offsets);
		if (results.contains(false)) {
			// System.out.println(key+"不存在");
			multiSetBit(SpiderConstants.BLOOM_FILTER_REDIS_KEY, true, offsets);
			return url;
		} else {
			// System.out.println(key+"已存在");
			return null;
		}

	}

	@Override
	public boolean filterUrlList(List<String> ulist) {
		final List<String> vlist = new ArrayList<String>();
		for (String url : ulist) {
			String vl = filterUrl(url);
			if (vl != null)
				vlist.add(vl);
		}
		bathUlist(vlist);
		return flag;
	}
	
	

	/**
	 * @param ulist 
	 * 插入到本地mysql下实现持久化
	 */
	public void bathUlist(final List<String> ulist) {
		if (ulist.size() > 0) {
			jdbcTemplate.batchUpdate(
					"insert ignore into temp_ulist(url) value (?)",
					new BatchPreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps, int i)
								throws SQLException {
							ps.setString(1, ulist.get(i));
						}
						@Override
						public int getBatchSize() {
							return ulist.size();
						}
					});
		} else {
			flag = false;
		}
	}

}
