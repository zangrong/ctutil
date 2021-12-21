/**
 * @Copyright: 2018 720yun.com Inc. All rights reserved. 
 * @Title: PageUtil.java 
 * @date 2018年3月8日 上午10:02:17 
 * @version V1.0
 * @author zangrong
 */
package com.cetian.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:  PageUtil   
 * @Description: 自定义分页工具类
 * @date:  2018年3月8日 上午10:02:17
 * @author: zangrong
 * 
 */
public class PageUtil {
	
	/**
	 * @Title: page   
	 * @Description: 对一个列表的数据进行分页
	 * @param collection
	 * @param pageSize
	 * @return: List<List<T>>      
	 * @throws:
	 */
	public static <T> List<List<T>> page(List<T> collection, int pageSize){
		// 如果小于等于0，就默认按10个一页分
		if (pageSize <= 0) {
			pageSize = 10;
		}
		List<List<T>> all = new ArrayList<>();
		int totalCount = collection.size();
		int totalPage = getTotalPage(totalCount, pageSize);
		for (int i = 0; i < totalPage; i ++) {
			
			int start = i * pageSize;
			int end = (i + 1) * pageSize;
			if (end >= totalCount) {
				end = totalCount;
			}
			List<T> list = collection.subList(start, end);
			all.add(list);
		}
		return all;
	}
	
	public static int getTotalPage(int totalCount, int pageSize) {
		int totalPage = (totalCount % pageSize == 0) ? (totalCount / pageSize) : (totalCount / pageSize + 1);
		return totalPage;
	}

	
}
