package cn.cepower.spider.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class htmlUtil {
	public static String gettime(String str) {
		String reg = "20\\d{2}[./\\-年]\\d{1,2}[./\\-月]\\d{1,2}[日]?";
		String result = null;
		str = str.replaceAll(" ", "");
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(str);
		while (m.find()) {
			result = m.group();
			String time = result.replaceAll("[./年月]", "-");
			result = time.replaceAll("[日]", "");
			break;
		}
		return result;

	}
	
	/**
	 * @param str 需要搜索的字符
	 * @param list 关键词表   详情见demo
	 * @return
	 */
	public static String getKeyWord(String str,List<String> list) {
		Set<String> set = new HashSet<String>(); 		
		for (String s : list) {
			Pattern p = Pattern.compile(s);
			Matcher m = p.matcher(str);
			while(m.find()) set.add(m.group());
		}
		return set.toString();
	}
	
	
	@Test
	public void demo(){
		System.out.println(getKeyWord("湖南asdfas,湖北湖北北天津",
				new ArrayList<String>(Arrays.asList("湖南","湖北","湖北北"))));
	}
	
}
