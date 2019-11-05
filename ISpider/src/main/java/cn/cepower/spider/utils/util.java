package cn.cepower.spider.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class util {
	public static void sleep(long millis){
		try{
			Thread.sleep(millis);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static String now(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String temp = df.format(new Date());
		return temp;
	}
	
	public static byte[] serialize(Object obj)  {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o;
		try {
			o = new ObjectOutputStream(b);
			o.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b.toByteArray();
	}

	public static String rOUrl2String(Object o){
		return new String(serialize(o)).substring(27);
	}
}
