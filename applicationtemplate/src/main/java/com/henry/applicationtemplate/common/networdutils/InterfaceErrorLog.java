package com.henry.applicationtemplate.common.networdutils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

/**
 * 输出网络接口的错误信息
 * @author henry
 */
public class InterfaceErrorLog{
	public static Boolean OPEN_SWITCH = true;//是否打印接口错误信息日志
	private static final String INTERFACE_LOG_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() 
			+ "/infoplatform/gdut/log/";//文件保存路径
	private static final int LOG_EXITES_DAY = 2;//文件保存天数
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat(  
            "yyyy-MM-dd HH:mm:ss");// 日志的输出格式  
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式	
	/**
	 * 保存错误信息
	 */
    public static void saveErrorLog(String url, String[] paramsNames, String[] paramsValues, String ErrorText){
    	if(OPEN_SWITCH){
    		String paramsName = "", paramsValue = "";
    		for(int i = 0; i < paramsNames.length; i++){
    			paramsName += paramsNames[i] + ",";
    		}
    		for(int i = 0; i < paramsValues.length; i++){
    			paramsValue += paramsValues[i] + ",";
    		}
    		String text = "interface url：" + url + "\n"
    				+ "interface paramsName:" + paramsName + "\n" 
    				+ "interface paramsValue:" + paramsValue + "\n" 
    				+ ErrorText + "\n\n";
    		writeLogtoFile(text);
    	}
    }
    
    /** 
     * 打开日志文件并写入日志   
     * @return 
     **/  
    private static void writeLogtoFile(String text) {// 新建或打开日志文件  
        Date nowtime = new Date();  
        String needWriteFiel = logfile.format(nowtime);  
        String needWriteMessage = myLogSdf.format(nowtime) + "\n" + text;  
        File path = new File(INTERFACE_LOG_FILE_PATH);  
        if(!path.exists()){
        	path.mkdirs();
        }
        File file = new File(INTERFACE_LOG_FILE_PATH,needWriteFiel + ".txt");
        try {  
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖  
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);  
            bufWriter.write(needWriteMessage);  
            bufWriter.newLine();  
            bufWriter.close();  
            filerWriter.close();  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
    } 
    
    /**
     * 删除文件，指定天数前的日志自动删除
     */
    private void deleteFile(){
    	 Date currentTime = new Date();  
    	 File logPath = new File(INTERFACE_LOG_FILE_PATH);
    	 String[] fileList = logPath.list();//获取所有日志
    	 //通过与当前时间比较，如果是在这之前N天的都自动删除
    	 for(int i = 0; i < fileList.length; i++){
    		 
    	 }
    }
    
}
