package com.henry.applicationtemplate.common.storageutils;
import java.io.File;
import android.os.Environment;
import android.os.StatFs;
/**
 * Created by holiy on 2015/1/10.
 *  SD卡相关的辅助类,判断sd卡是否存在，sd卡容量，获取系统存储路径
 */
public class SDCardUtils {
    /**
     * 自己app创建文件的地址，如需更改名称，直接在这里改，一般一个app创建1个总的folder
     */
    private static final String MYAPPFOLDERNAME = "/holiyutil/";

    private SDCardUtils()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    /**
     * 判断SDCard是否可用
     * @return
     */
    public static boolean isSDCardEnable()
    {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径
     * @return
     */
    public static String getSDCardPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     * @return
     */
    public static long getSDCardAllSize()
    {
        if (isSDCardEnable())
        {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath)
    {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath()))
        {
            filePath = getSDCardPath();
        } else
        {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取系统存储路径
     * @return
     */
    public static String getDataDirectoryPath()
    {
        String packageName = "com.chuangxue.knowledgerabbit";//manifest上的package
        return "/data"+ Environment.getDataDirectory().getAbsolutePath() + "/"+ packageName+"/files/";
    }

    /**
     * 智能判断手机是否存在sd卡，如果sd卡不存在，则将创建的文件路径指定为手机本地存储
     * @param childFolderName
     * @return
     */
    public static String getFileFolder(String childFolderName){
        String childFoler;
        if(childFolderName == null || "".equals(childFolderName)){
            childFoler = "";
        }else{
            childFoler = childFolderName + "/";
        }
        String sdStatus = Environment.getExternalStorageState();
        if(sdStatus.equals(Environment.MEDIA_MOUNTED)){//sd卡存在
            String path =  Environment.getExternalStorageDirectory().getAbsolutePath()
                    + MYAPPFOLDERNAME + childFoler;
            File filePath = new File(path);
            if(!filePath.exists()){//如果不存在该路径则自动创建
                filePath.mkdirs();
            }
            return path;
        }else{//sd卡不存在
            String localPath = "";
            localPath = getLocalPath(childFoler);
            File localFilePath = new File(localPath);
            if(!localFilePath.exists()){
                localFilePath.mkdirs();
            }
            return localPath;
        }
    }

    /**
     * 获取手机内置SD卡地址
     * @return
     */
    private static String getLocalPath(String childFoler){
        String localPath = "";
        File mntFolder = new File("/mnt");
        String folders[] = mntFolder.list();//获取子目录
        for(int i = 0; i < folders.length; i++){
            if(("/mnt/" + folders[i]).equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
                continue;//外置SD卡
            }
            //存在 DICM 或者 Pictures文件夹的为内置存储卡路径
            File dcimFile = new File("/mnt/" + folders[i] + "/DCIM");
            if(dcimFile.exists()){
                localPath = "/mnt/" + folders[i] +  MYAPPFOLDERNAME + childFoler;
                break;
            }
            File dcimFile1 = new File("/mnt/" + folders[i] + "/dcim");
            if(dcimFile1.exists()){
                localPath = "/mnt/" + folders[i] +  MYAPPFOLDERNAME + childFoler;
                break;
            }
            File picturesFile = new File("/mnt/" +folders[i] + "/Pictures");
            if(picturesFile.exists()){
                localPath = "/mnt/" + folders[i] +  MYAPPFOLDERNAME + picturesFile;
                break;
            }
        }
        //异常情况，什么都没找到
        if("".equals(localPath)){
            localPath = getDataDirectoryPath();
        }
        return localPath;
    }

    /**
     * 判断文件是否存在，如果存在则返回文件路径
     * @param childFolderName 所在文件夹
     * @param picName 文件名
     * @return
     */
    public static String getfilePath(String childFolderName, String picName){
        String childFoler;
        if(childFolderName == null || "".equals(childFolderName)){
            childFoler = "";
        }else{
            childFoler = childFolderName + "/";
        }

        String sdStatus = Environment.getExternalStorageState();
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + MYAPPFOLDERNAME + childFoler;
        String localPath = getLocalPath(childFoler);//当查找本地地址失败时，localPath为默认sd卡地址
        if(sdStatus.equals(Environment.MEDIA_MOUNTED)){//sd卡存在
            File filePath = new File(sdPath,picName);
            if(filePath.exists()){
                return sdPath + picName;//在SD卡里
            }else{
                if(sdPath.equals(localPath)){
                    return "NOTEXIST";//后面不用执行了
                }
                File localFilePath = new File(localPath, picName);
                if(localFilePath.exists()){//在本地存储里
                    return localPath + picName;
                }else{
                    return "NOTEXIST";
                }
            }
        }else{
            File localFilePath = new File(localPath, picName);
            if(localFilePath.exists()){//在本地存储里
                return localPath + picName;
            }else{
                return "NOTEXIST";
            }
        }
    }



}
