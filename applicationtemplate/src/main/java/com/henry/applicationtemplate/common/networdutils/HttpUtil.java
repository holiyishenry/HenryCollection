package com.henry.applicationtemplate.common.networdutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import cxhttp.HttpResponse;
import cxhttp.NameValuePair;
import cxhttp.client.HttpClient;
import cxhttp.client.entity.UrlEncodedFormEntity;
import cxhttp.client.methods.HttpPost;
import cxhttp.entity.ContentType;
import cxhttp.entity.StringEntity;
import cxhttp.entity.mime.MultipartEntityBuilder;
import cxhttp.entity.mime.content.FileBody;
import cxhttp.entity.mime.content.StringBody;
import cxhttp.impl.client.DefaultHttpClient;
import cxhttp.message.BasicNameValuePair;
import cxhttp.params.CoreConnectionPNames;
import cxhttp.protocol.HTTP;
import cxhttp.util.EntityUtils;

public class HttpUtil {
	private String key = "sfaf1520144201551864rf042013111820";
//	private String key = MyApplication.getAppInstance().getKey();

	/*
	 * public HttpUtil(String key) { this.key = key; }
	 */

	/**
	 * 发送post请求,无参数,不加密
	 * 
	 * @param httpUrl
	 *            请求地址
	 * @return 状态码为非200或接收数据失败时,返回null,反则返回网络返回值
	 * @author dongxr
	 */
	public String requestByPost(String httpUrl) {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(httpUrl);

		// 发出HTTP request
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		// 设置请求超时
		defaultHttpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
		// 设置读取超时
		defaultHttpClient.getParams().setParameter(
				CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);
		try {
			HttpResponse httpResponse = defaultHttpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出响应字符串
				strResult = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
				// 取出utf-8中BOM
				if (strResult.startsWith("\ufeff")) {
					strResult = strResult.substring(1);
				}
				return strResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}

	public String requestByPostParam(String[] paramNames, String[] paramValus,
			String httpUrl) {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(httpUrl);

		try {
			// 如果有参数,先对参数加密
			if (paramNames != null && paramValus != null) {
				JSONObject jsonObjSend = new JSONObject();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (int i = 0; i < paramNames.length; i++) {
					jsonObjSend.put(paramNames[i],
							URLEncoder.encode(paramValus[i], HTTP.UTF_8));
				}
				// 设置参数
				String strencrept = jsonObjSend.toString();
				// String str = new
				// EncodeAndDecodeUtil().encryptToHex(key,strencrept);
				params.add(new BasicNameValuePair("encrept", strencrept));
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
			}
			// 发出HTTP request
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			// 设置请求超时
			defaultHttpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
			// 设置读取超时
			defaultHttpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);

			HttpResponse httpResponse = defaultHttpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出响应字符串
				strResult = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
				// 取出utf-8中BOM
				if (strResult.startsWith("\ufeff")) {
					strResult = strResult.substring(1);
				}

			} /*
			 * else { return httpResponse.getStatusLine().getStatusCode() + "";
			 * }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}

	/**
	 * 发送post请求,带参数,加密
	 * 
	 * @param paramName
	 *            参数名数组
	 * @param paramValue
	 *            参数值数组
	 * @param httpUrl
	 *            请求地址
	 * @return 状态码为非200或接收数据失败时,返回null,反则返回网络返回值
	 * @author dongxr
	 */
	public String requestByPostEncode(String[] paramName, String[] paramValue,
			String httpUrl) {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(httpUrl);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			// 如果有参数,先对参数加密
			if (paramName != null && paramValue != null) {
				JSONObject jsonObjSend = new JSONObject();
				for (int i = 0; i < paramName.length; i++) {
					jsonObjSend.put(paramName[i],
							URLEncoder.encode(paramValue[i], HTTP.UTF_8));
				}
				// 设置参数
				String strencrept = jsonObjSend.toString();
				System.out.println(strencrept);
				String str = new EncodeAndDecodeUtil().encryptToHex(key,
						strencrept);
				params.add(new BasicNameValuePair("encrept", str));
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
			}
			// 发出HTTP requestdf
			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			// 设置请求超时
			defaultHttpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
			// 设置读取超时
			defaultHttpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);
		
			HttpResponse httpResponse = defaultHttpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出响应字符串
				strResult = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
				// 取出utf-8中BOM
				if (strResult.startsWith("\ufeff")) {
					strResult = strResult.substring(1);
				}
				
				// 11月 12日，添加接口错误日志输出
			    if(strResult.length() == 0){	    	
					InterfaceErrorLog.saveErrorLog(httpUrl, paramName, paramValue, "输出为空");
				}else if(strResult.indexOf("<div") >= 0){
					InterfaceErrorLog.saveErrorLog(httpUrl, paramName, paramValue, strResult);
				}
			}
			// 11月 12日，添加接口错误日志输出
			else{
				InterfaceErrorLog.saveErrorLog(httpUrl, paramName, paramValue, httpResponse.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}

	private String sendJsonArrByPost(String[] paramName, String[] paramValue,
			String httpUrl) {
		String resultStr = "";

		return resultStr;
	}

	public String sendRequestByGet(String[] paramNames, String[] paramValues,
			String url) {
		StringBuilder strBuilder = new StringBuilder(url);
		if (paramNames != null && paramValues != null) {
			strBuilder.append("?");
			for (int i = 0; i < paramNames.length; i++) {
				strBuilder.append(paramNames[i] + "=" + paramValues[i] + "&");
			}
			strBuilder.deleteCharAt(strBuilder.length() - 1);
		}
		String strResult = "";
		/*		HttpGet httpRequest = new HttpGet(strBuilder.toString().trim());
		// 发出HTTP request
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		defaultHttpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
		defaultHttpClient.getParams().setParameter(
				CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);
		try {
			HttpResponse httpResponse = defaultHttpClient.execute(httpRequest);
			Log.i("test", httpResponse.getStatusLine().getStatusCode() + "");
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 取出响应字符串
				strResult = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
				// 取出utf-8中BOM
				if (strResult.startsWith("\ufeff")) {
					strResult = strResult.substring(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		BufferedReader in = null;
		try {
			URL realUrl = new URL(strBuilder.toString().trim());
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 建立实际的连接
			conn.connect();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line = "";
			while ((line = in.readLine()) != null) {
				strResult += line;
			}
			if (strResult.startsWith("\ufeff")) {
				strResult = strResult.substring(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return strResult;
	}

	public  String sendJsonData(String url, String strJson) {
		try {
			HttpPost httpPost = new HttpPost(url);

			StringEntity entity = new StringEntity(strJson, HTTP.UTF_8);
			entity.setContentType("application/json");
			httpPost.setEntity(entity);

			// 这里开始

			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
			// 请求超时
			defaultHttpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5 * 1000);
			// 读取超时
			defaultHttpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 5 * 1000);

			// 这里结束

			HttpResponse httpResponse = defaultHttpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String firstResponseData = EntityUtils.toString(
						httpResponse.getEntity(), HTTP.UTF_8);
				if (firstResponseData.startsWith("\ufeff")) {
					firstResponseData = firstResponseData.substring(1);
				}
				if (firstResponseData != null
						&& firstResponseData.equalsIgnoreCase("ok")) {
					return "ok";
				} else if (firstResponseData != null
						&& firstResponseData.equalsIgnoreCase("RIGHT")) {
					return "RIGHT";
				} else if (firstResponseData != null
						&& firstResponseData.equalsIgnoreCase("CERTIFICATED")) {
					return "CERTIFICATED";
				}
			} else {
				return EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
			}
		} catch (Exception e) {
			return "";
		}

		return "";
	}

	/**
	 * 发送文件
	 * 
	 * @param paramDescriptNames
	 *            描述文件参数名,如果没有时应该设为null
	 * @param paramDescriptValues
	 *            描述文件参数值,如果没有时应该设为null
	 * @param filePath
	 *            本地文件路径
	 * @param requestUrl
	 *            上传文件url
	 * @param fileTypeCode
	 *            需要上传文件类型, UtilConstant.UPLOAD_IMAGE_TARGET:图片类型 201
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String uploadFileByForm(String[] paramDescriptNames,
			String[] paramDescriptValues, String filePath, String requestUrl,
			int fileTypeCode) {
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(requestUrl);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			//builder.setCharset(Charset.forName("UTF-8"));
			/*
			 * 文件描述
			 */
			if (paramDescriptNames != null && paramDescriptValues != null) {
				for (int i = 0; i < paramDescriptValues.length; i++) {
					//
					builder.addPart(paramDescriptNames[i], new StringBody(
							URLEncoder.encode(paramDescriptValues[i], HTTP.UTF_8), ContentType.DEFAULT_TEXT));
							//URLEncoder.encode(paramDescriptValues[i], HTTP.UTF_8)));
				}
			}
			/*
			 * 构建文件
			 */
			String fileFieldName = "";
			String contentType = "";
			switch (fileTypeCode) {
			case UtilConstant.UPLOAD_IMAGE_TARGET:
				fileFieldName = "image";
				contentType = "image/jpeg";
				break;
			case UtilConstant.UPLOAD_VOICE_TARGET:
				fileFieldName = "voice";
				contentType = "audio/mp3";
				break;
			default:
				break;
			}
			if (filePath != null) {
				String fileName = filePath.substring(filePath.lastIndexOf("/") + 1,
						filePath.length());
				builder.addPart(fileFieldName, new FileBody(new File(filePath),
				 		ContentType.create(contentType), fileName));
				//builder.addPart(fileFieldName, new FileBody(new File(filePath,fileName)));
			}
			
				httppost.setEntity(builder.build());
				HttpResponse response = httpclient.execute(httppost);
				if (response.getStatusLine().getStatusCode() == 200) {
					// 取出响应字符串
					result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
					// 取出utf-8中BOM
					if (result.startsWith("\ufeff")) {
						result = result.substring(1);
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			return "exception";
		}
		return result;
	}
}
