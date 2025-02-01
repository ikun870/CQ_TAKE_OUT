package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 公共控制器
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    private final AliOssUtil aliOssUtil;

    public CommonController(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public Result<String> upload(MultipartFile file)  {
        log.info("文件上传：{}", file.getOriginalFilename());
        String originalFilename = file.getOriginalFilename();
        //截取后缀
        String suffix = null;
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String s = UUID.randomUUID().toString() + suffix;
        String uploadpath = null;
        try {
            uploadpath = aliOssUtil.upload(file.getBytes(), s);
            return Result.success(uploadpath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
        }
       return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
