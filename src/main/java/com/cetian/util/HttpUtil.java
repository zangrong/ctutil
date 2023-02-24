/**
 * @Copyright: 2017 cetian.com Inc. All rights reserved.
 * @Title: HttpUtil.java 
 * @date 2017年4月11日 下午3:07:29 
 * @version V1.0
 * @author zangrong
 */
package com.cetian.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.util.StreamUtils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @ClassName: HttpUtil
 * @Description: TODO 需要判断结果是否成功
 * @date: 2017年4月11日 下午3:07:29
 * @author: zangrong
 * 
 */
@Slf4j
public class HttpUtil {

	private static PoolingHttpClientConnectionManager connMgr;
	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 7000;
	// 连接超时时间，默认10秒
	private static int connectTimeout = 10000;
	// 传输超时时间，默认30秒
	private static int socketTimeout = 30000;

	private static int validateTimeout = 5000;

	private static String UTF_8 = "UTF-8";
	private static Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

	static {
		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connMgr.setMaxTotal(200);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
		connMgr.setValidateAfterInactivity(validateTimeout);

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(connectTimeout);
		// 设置读取超时
		configBuilder.setSocketTimeout(socketTimeout);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		// configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();
	}

	/**
	 * 发送 GET 请求（HTTP），不带输入数据
	 * 
	 * @param url
	 * @return
	 */
	public static String doGet(String url) {
		return doGet(url, null, null);
	}

	/**
	 * 发送 GET 请求（HTTP），K-V形式
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doGet(String url, Map<String, Object> params, Map<String, Object> headers) {
		String apiUrl = url;
		String result = null;
		try {
			// 设置参数
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairList = new ArrayList<>(params.size());
				for (Entry<String, Object> entry : params.entrySet()) {
					NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
					pairList.add(pair);
				}
				String paramString = EntityUtils.toString(new UrlEncodedFormEntity(pairList), CHARSET_UTF_8);
				apiUrl += "?" + paramString;
			}
//			log.info(apiUrl);
			// 实例化get请求
			HttpGet httpGet = new HttpGet(apiUrl);
			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, Object> header : headers.entrySet()) {
					httpGet.setHeader(header.getKey(), ObjectUtil.trimToEmpty(header.getValue()));
				}
			}
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpResponse response = httpclient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			log.debug("执行状态码 : " + statusCode);

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = IOUtils.toString(instream, UTF_8);
			}
		} catch (IOException e) {
			log.error("", e);
		}
		return result;
	}

	/**
	 * 发送 POST 请求（HTTP），不带输入数据
	 *
	 * @param apiUrl
	 * @return
	 */
	public static String doPost(String apiUrl) {
		Map<String, Object> params = null;
		return doPost(apiUrl, params, null);
	}

	/**
	 * 发送 POST 请求（HTTP），K-V形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param params
	 *            参数map
	 * @return
	 */
	public static String doPost(String apiUrl, Map<String, Object> params, Map<String, Object> headers) {
		String httpStr = null;
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			httpPost.setConfig(requestConfig);
			// 设置参数
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairList = new ArrayList<>(params.size());
				for (Entry<String, Object> entry : params.entrySet()) {
					NameValuePair pair = new BasicNameValuePair(entry.getKey(),
							ObjectUtil.trimToEmpty(entry.getValue()));
					pairList.add(pair);
				}
				httpPost.setEntity(new UrlEncodedFormEntity(pairList, CHARSET_UTF_8));
			}
			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, Object> header : headers.entrySet()) {
					httpPost.setHeader(header.getKey(), ObjectUtil.trimToEmpty(header.getValue()));
				}
			}
			CloseableHttpClient httpClient = HttpClients.createDefault();
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			httpStr = EntityUtils.toString(entity, CHARSET_UTF_8);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
				}
			}
		}
		return httpStr;
	}

	/**
	 * 发送 POST 请求（HTTP），body是文件
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param headers
	 *            参数map
	 * @return
	 */
	public static String doPost(String apiUrl, File file, Map<String, Object> headers) {
		String httpStr = null;
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(apiUrl);
			httpPost.setConfig(requestConfig);
			// 设置上传文件参数参数
			if (file != null && file.exists()) {
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.addBinaryBody("zipFile", file, ContentType.DEFAULT_BINARY, file.getName());
				HttpEntity postEntity = builder.build();
				httpPost.setEntity(postEntity);
			}

			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, Object> header : headers.entrySet()) {
					httpPost.setHeader(header.getKey(), ObjectUtil.trimToEmpty(header.getValue()));
				}
			}
			CloseableHttpClient httpClient = HttpClients.createDefault();
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			httpStr = EntityUtils.toString(entity, CHARSET_UTF_8);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
				}
			}
		}
		return httpStr;
	}

	/**
	 * @Title: doDelete
	 * @Description: 发送delete请求
	 * @param params
	 * @param headers
	 * @return: String
	 * @throws:
	 */
	public static String doDelete(String apiUrl, Map<String, Object> params, Map<String, Object> headers) {
		String httpStr = null;
		CloseableHttpResponse response = null;
		try {
			// 设置参数
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairList = new ArrayList<>(params.size());
				for (Entry<String, Object> entry : params.entrySet()) {
					NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
					pairList.add(pair);
				}
				String paramString = EntityUtils.toString(new UrlEncodedFormEntity(pairList), CHARSET_UTF_8);
				apiUrl += paramString;
			}
			HttpDelete httpDelete = new HttpDelete(apiUrl);
			httpDelete.setConfig(requestConfig);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, Object> header : headers.entrySet()) {
					httpDelete.setHeader(header.getKey(), ObjectUtil.trimToEmpty(header.getValue()));
				}
			}
			response = httpClient.execute(httpDelete);
			HttpEntity entity = response.getEntity();
			httpStr = EntityUtils.toString(entity, CHARSET_UTF_8);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
				}
			}
		}
		return httpStr;
	}

	/**
	 * 发送 POST 请求（HTTP），JSON形式
	 *
	 * @param apiUrl
	 * @param json
	 *            json对象
	 * @return
	 */
	public static String doPostJson(String apiUrl, String json) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String httpStr = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;

		try {
			httpPost.setConfig(requestConfig);
			StringEntity stringEntity = new StringEntity(json, UTF_8);// 解决中文乱码问题
			stringEntity.setContentEncoding(UTF_8);
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			System.out.println(response.getStatusLine().getStatusCode());
			httpStr = EntityUtils.toString(entity, UTF_8);
		} catch (IOException e) {
			log.warn("", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					log.warn("", e);
				}
			}
		}
		return httpStr;
	}

	/**
	 * 发送 POST 请求（HTTP），JSON形式
	 *
	 * @param apiUrl
	 * @param json
	 *            json对象
	 * @return
	 */
	public static String doPostJson(String apiUrl, Map<String, Object> headers, String json, RequestConfig config) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String httpStr = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;
		try {
			if (config == null){
				config = requestConfig;
			}
			httpPost.setConfig(config);
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, Object> header : headers.entrySet()) {
					httpPost.setHeader(header.getKey(), ObjectUtil.trimToEmpty(header.getValue()));
				}
			}
			StringEntity stringEntity = new StringEntity(json, UTF_8);// 解决中文乱码问题
			stringEntity.setContentEncoding(UTF_8);
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			System.out.println(response.getStatusLine().getStatusCode());
			httpStr = EntityUtils.toString(entity, UTF_8);
		} catch (IOException e) {
			log.warn("", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					log.warn("", e);
				}
			}
		}
		return httpStr;
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），K-V形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param params
	 *            参数map
	 * @return
	 */
	public static String doPostSsl(String apiUrl, Map<String, Object> params) {
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
				.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;
		String httpStr = null;

		try {
			httpPost.setConfig(requestConfig);
			List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
			for (Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
				pairList.add(pair);
			}
			httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName(UTF_8)));
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			httpStr = EntityUtils.toString(entity, UTF_8);
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					log.warn("", e);
				}
			}
		}
		return httpStr;
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），JSON形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param json
	 *            JSON对象
	 * @return
	 */
	public static String doPostSslJson(String apiUrl, Object json) {
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
				.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;
		String httpStr = null;

		try {
			httpPost.setConfig(requestConfig);
			StringEntity stringEntity = new StringEntity(json.toString(), UTF_8);// 解决中文乱码问题
			stringEntity.setContentEncoding(UTF_8);
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			httpStr = EntityUtils.toString(entity, UTF_8);
		} catch (Exception e) {
			log.warn("", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					log.warn("", e);
				}
			}
		}
		return httpStr;
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），JSON形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param json
	 *            JSON对象
	 * @return
	 */
	public static String doPostSslJsonGzip(String apiUrl, Object json) {
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
				.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse httpResponse = null;
		String response = null;
		try {
			httpPost.setConfig(requestConfig);
			StringEntity stringEntity = new StringEntity(json.toString(), UTF_8);// 解决中文乱码问题
			stringEntity.setContentEncoding(UTF_8);
			stringEntity.setContentType("application/json;charset=UTF-8");
			httpPost.setEntity(new GzipCompressingEntity(stringEntity));
			httpPost.setEntity(stringEntity);
			httpResponse = httpClient.execute(httpPost);
			HttpEntity entity = httpResponse.getEntity();
			if (entity == null) {
				return null;
			}
			response = EntityUtils.toString(entity, UTF_8);
		} catch (Exception e) {
			log.warn("请求异常 request[{}] response[{}]", json, response);
			log.warn("", e);
		} finally {
			if (httpResponse != null) {
				try {
					EntityUtils.consume(httpResponse.getEntity());
				} catch (IOException e) {
				}
			}
		}
		return response;
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），xml形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param xml
	 *            xml对象
	 * @return
	 */
	public static String doPostSslXml(String apiUrl, String xml) {
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory())
				.setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
		HttpPost httpPost = new HttpPost(apiUrl);
		CloseableHttpResponse response = null;
		String responseStr = null;
		try {
			httpPost.setConfig(requestConfig);
			httpPost.addHeader("Content-Type", "text/xml");
			StringEntity stringEntity = new StringEntity(xml, UTF_8);// 解决中文乱码问题
			stringEntity.setContentEncoding(UTF_8);
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			}
			responseStr = EntityUtils.toString(entity, UTF_8);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
		return responseStr;
	}

	/**
	 * 创建SSL安全连接
	 *
	 * @return
	 */
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
		SSLConnectionSocketFactory sslsf = null;
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					return true;
				}
			}).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		} catch (GeneralSecurityException e) {
			log.warn("", e);
		}
		return sslsf;
	}

	public static String doHttp(String url, String method, String xml) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		// 加入数据
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());
		buffOutStr.write(xml.getBytes());
		buffOutStr.flush();
		buffOutStr.close();

		// 获取输入流
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param requestUrl
	 * @param requestMethod
	 * @param outputString
	 * @return
	 */
	public static JSONObject httpsRequestJson(String requestUrl, String requestMethod, String outputString) {
		JSONObject jsonObject = null;
		TrustManager[] tm = { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };
		StringBuffer sb = new StringBuffer();
		HttpsURLConnection con = null;
		InputStream inputStream = null;
		InputStreamReader ir = null;
		BufferedReader br = null;
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			con = (HttpsURLConnection) url.openConnection();
			con.setSSLSocketFactory(ssf);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);

			con.setRequestMethod(requestMethod);
			if (outputString != null) {
				OutputStream outputStream = con.getOutputStream();
				outputStream.write(outputString.getBytes(UTF_8));
				outputStream.close();
			}
			inputStream = con.getInputStream();
			ir = new InputStreamReader(inputStream, UTF_8);
			br = new BufferedReader(ir);

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			// 转换为json对象
			jsonObject = new JSONObject(sb.toString());
		} catch (Exception e) {
			log.error("", e);
		} finally {
			IoUtil.close(br, ir, inputStream);
			if (con != null) {
				con.disconnect();
			}
		}
		return jsonObject;
	}

	private static final int downloadTimeout = 10000;

	public static boolean download(String url, String filePath, Map<String, String> headers) {
		boolean result = false;
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(url);
			// 设置header
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> header : headers.entrySet()) {
					httpGet.setHeader(header.getKey(), ObjectUtil.trimToEmpty(header.getValue()));
				}
			}
			httpGet.setConfig(RequestConfig.custom() //
					.setConnectionRequestTimeout(downloadTimeout) //
					.setConnectTimeout(downloadTimeout) //
					.setSocketTimeout(downloadTimeout) //
					.build());
			try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
				HttpEntity entity = response.getEntity();
				File desc = new File(filePath);
				try (InputStream is = entity.getContent(); //
						OutputStream os = new FileOutputStream(desc)) {
					StreamUtils.copy(is, os);
					result = true;
				}
			}
		} catch (Throwable e) {
			log.error("文件下载失败 url[{}]", url);
			log.error("文件下载失败", e);
		}
		return result;
	}

}
