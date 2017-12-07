package com.ackerman.reddit.service;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuException.class);
    private String ACCESS_KEY = "y7rQlLBmNp9UBIwVT3j8x2i4UhXNuuCZuplVRB0M";
    private String SECRET_KEY = "rU2TPU50tvAdFqeQa_imw8F_3_JMmhxbqKYLpJZh";
    private String QINIU_IMAGE_DOMAIN = "http://oz15aje2y.bkt.clouddn.com/";
    private String bucket = "reddit";

    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    UploadManager uploadManager = new UploadManager();

    public String getUpToken(){
        return auth.uploadToken(bucket);
    }

    public String savaImage(MultipartFile file) throws IOException{
        try{
            if(file == null){
                return null;
            }
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if(dotPos < 0){
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos+1);
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;

            //七牛Response
            Response response = uploadManager.put(file.getBytes(), fileName, getUpToken());
            if(response.isOK() && response.isJson()){
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(response.bodyString()).get("key");
            }
            else{
                logger.error("七牛云储存异常" + response.bodyString());
                return null;
            }
        }catch (Exception e){
            logger.error("七牛云储存异常" + e.getMessage());
            return null;
        }
    }
}
