package com.mmall.util;


import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp =PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    //创建构造器
    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip=ip;
        this.port=port;
        this.user=user;
        this.pwd=pwd;
    }

    //书写开放出去的静态方法

    //判断批量上传是否成功
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始链接FTP服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("开始链接ftp服务器，完成上传，上传结果：{}");
        return result;
    }

    //remotePath  FTP服务器的文件夹目录
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException,IOException {
        boolean uploaded =true;
        FileInputStream fileInputStream = null;
        //链接ftp服务器
        if(connectServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);
                //设置缓存
                ftpClient.setBufferSize(1024);
                //设置编码格式为UTF-8
                ftpClient.setControlEncoding("UTF-8");
                //设置二进制文件类型，防止乱码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                //配置了FTP服务器的被动模式,打开本地的被动模式
                ftpClient.enterLocalPassiveMode();
                //开始传输
                for(File fileItem:fileList){
                    //通过这个文件创建一个流
                    fileInputStream = new FileInputStream(fileItem);
                    //使用storeFile保存文件,其中第一个参数是保持在远端的文件名 即The name to give the remote file，第二个是文件流InputStream
                    ftpClient.storeFile(fileItem.getName(),fileInputStream);
                }

            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
                e.printStackTrace();
            }finally {
                //释放链接, 关闭文件流
                fileInputStream.close();
                ftpClient.disconnect();
            }

        }
        return uploaded;
    }

    private boolean connectServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
        } catch (IOException e) {
            logger.error("链接FTP服务器异常",e);
            e.printStackTrace();
        }
        return isSuccess;
    }

}
