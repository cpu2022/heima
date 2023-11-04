package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.bridge.Message;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Resource
    AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestPart("file") MultipartFile file) {
        log.info("文件准备上传");
        try {
            String originalFilename = file.getOriginalFilename();
            String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            //文件全名
            String fileRealName = uuid + substring;
            String filePath = aliOssUtil.upload(file.getBytes(), fileRealName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败",e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
