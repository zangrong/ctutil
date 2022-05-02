/**
 * @Copyright: 2017 cetian.com Inc. All rights reserved.
 * @Title: IoUtil.java 
 * @date 2017年3月9日 上午9:10:34 
 * @version V1.0
 * @author zangrong
 */
package com.cetian.util;

import java.io.Closeable;

/**
 * @ClassName: IoUtil
 * @Description:
 * @date: 2017年3月9日 上午9:10:34
 * @author: zangrong
 * 
 */
public class IoUtil {

	/**
	 * 
	 * @Title: close
	 * @Description: 关闭能关闭的
	 * @param closeables
	 * @return: void
	 * @throws:
	 */
	public static void close(Closeable... closeables) {
		for (Closeable c : closeables) {
			try {
				if (c != null) {
					c.close();
				}
			} catch (Exception e) {
			}
		}
	}

}
