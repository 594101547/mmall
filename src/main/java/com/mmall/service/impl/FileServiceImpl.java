package com.mmall.service.impl;
/*
created by dingtao
 */
import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class  FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //拓展名 abc.jpg ->.jpg  +1后移一位  ->jpg
        String fileExtendName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = "";
        //A:abc.jpg
        //B:abc.jpg
        //两者就会冲突，把前一个覆盖掉，所以加上UUID，以保证唯一性
        uploadFileName = UUID.randomUUID().toString()+"."+fileExtendName;

        logger.info("开始上传文件，上传文件的文件名:{}，上传的路径:{}，新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            //赋予权限，可写。用户不一定有在webapp下创建文件夹的权限，这一步必不可少
            fileDir.setWritable(true);
            //mkdir 是当前级别的  mkdirs可以直接递归创建所有的文件夹和文件  例如 /ex/cc/dd/e.txt 则如果ex cc dd都不存在的也会被直接创建
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);

        try {
            //上传文件成功
            file.transferTo(targetFile);
            //todo: 将targetFile的文件上传至FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //todo：将上传完的targetFile删除，顺带将对应的文件夹也删除掉
            targetFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return targetFile.getName();
    }
}
