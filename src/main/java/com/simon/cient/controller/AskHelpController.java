package com.simon.cient.controller;

import com.simon.cient.domain.AppUser;
import com.simon.cient.domain.AppUserRepository;
import com.simon.cient.domain.AskHelp;
import com.simon.cient.domain.AskHelpRepository;
import com.simon.cient.util.ImageUtil;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by simon on 2016/9/11.
 */
@Api(value = "请求帮助", description = "请求帮助")
@RestController
@RequestMapping("/api/askHelps")
public class AskHelpController {
    @Autowired
    private AskHelpRepository askHelpRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String ROOT = "askHelp";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ResourceLoader resourceLoader;

    {
        try{
            if(!Files.exists(Paths.get(ROOT))){
                Files.createDirectories(Paths.get(ROOT));
            }
        }catch (IOException e){
            logger.error("create askHelp folder failed", e);
        }
    }

    @Autowired
    public AskHelpController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @ApiOperation(value = "发布求助信息", notes = "")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(@RequestParam String access_token, @RequestParam String content, @RequestParam(required = false) String contentImg){
        logger.warn(access_token);
        logger.warn(content);
        logger.warn(contentImg);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser publisher = appUserRepository.findByPhone(phone);

        AskHelp askHelp = new AskHelp();
        askHelp.setPublisherId(publisher.getId());
        askHelp.setPublisher(publisher);
        askHelp.setPublishTime(System.currentTimeMillis());
        askHelp.setContent(content);
        askHelp.setAuditResult(0);//0，待审核；1，审核成功；2，审核失败；3，重新提交。

        if (null!=contentImg&&!"".equals(contentImg)){
            //存储图片
            String imgDir = ROOT + "/" + publisher.getPhone();
            String imgUrl = imgDir + "/" + System.currentTimeMillis() + ".png";
            try{
                if (!Files.exists(Paths.get(imgDir))){
                    Files.createDirectories(Paths.get(imgDir));
                    if (!Files.exists(Paths.get(imgUrl))){
                        Files.createFile(Paths.get(imgUrl));
                    }
                }
                Files.write(Paths.get(imgUrl), ImageUtil.convertToBytes(contentImg));
                askHelp.setContentImg(imgUrl);
            }catch (IOException e){
                logger.error("存储图片出错", e);
                logger.error(e.getMessage());
            }
        }

        try{
            responseMap.put(ServerContext.STATUS_CODE, 201);
            responseMap.put(ServerContext.MSG, "发布成功");
            responseMap.put(ServerContext.DATA, askHelpRepository.insert(askHelp));
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "发布失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value = "修改求助信息", notes = "id是AskHelp的id")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    private Map<String, Object> patch(@RequestParam String access_token, @PathVariable String id, @RequestParam String content, @RequestParam(required = false) String contentImg){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser publisher = appUserRepository.findByPhone(phone);

        AskHelp askHelp = new AskHelp();
        askHelp.setId(id);
        askHelp.setPublisherId(publisher.getId());
        askHelp.setPublisher(publisher);
        askHelp.setPublishTime(System.currentTimeMillis());
        askHelp.setContent(content);
        askHelp.setAuditResult(3);//0，待审核；1，审核成功；2，审核失败；3，重新提交。

        String contentImgOld = askHelp.getContentImg();
        try{
            if (null!=contentImgOld&&!"".equals(contentImgOld)){
                Files.write(Paths.get(contentImgOld), ImageUtil.convertToBytes(contentImg));
            }else{
                //存储图片
                String imgDir = ROOT + "/" + publisher.getPhone();
                String imgUrl = imgDir + "/" + System.currentTimeMillis() + ".png";
                if (!Files.exists(Paths.get(imgDir))){
                    Files.createDirectories(Paths.get(imgDir));
                    if (!Files.exists(Paths.get(imgUrl))){
                        Files.createFile(Paths.get(imgUrl));
                    }
                }
                Files.write(Paths.get(imgUrl), ImageUtil.convertToBytes(contentImg));
                askHelp.setContentImg(imgUrl);
            }
        }catch (IOException e){
            logger.error("存储图片出错", e);
            logger.error(e.getMessage());
        }

        try{
            responseMap.put(ServerContext.STATUS_CODE, 201);
            responseMap.put(ServerContext.MSG, "重新发布成功");
            responseMap.put(ServerContext.DATA, askHelpRepository.save(askHelp));
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "重新发布失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value = "获取求助列表")
    @RequestMapping(method = RequestMethod.GET)
    private Map<String, Object> get(@RequestParam Integer limit, @RequestParam Integer offset){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try{
            //AuditResult:0，待审核；1，审核成功；2，审核失败；3，重新提交。
            List<AskHelp> askHelpList = askHelpRepository.findByAuditResult(
                    1, new PageRequest(offset/limit, limit,
                    new Sort(Sort.Direction.DESC, "publishTime")));
            for (AskHelp askHelp : askHelpList){
                askHelp.setPublisher(appUserRepository.findById(askHelp.getPublisherId()));
                askHelp.setAuditor(appUserRepository.findById(askHelp.getAuditorId()));
            }

            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "获取求助列表成功");
            responseMap.put(ServerContext.DATA, askHelpList);
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "获取求助列表失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value = "查找用户发布的不同状态下的求助信息", notes = "")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "auditResult", value = "审核状态", required = true, dataType = "int"),
            @ApiImplicitParam(name = "access_token", value = "access_token", required = true, dataType = "string"),
            @ApiImplicitParam(name = "limit", value = "返回记录行的最大数目", required = true, dataType = "int"),
            @ApiImplicitParam(name = "offset", value = "偏移量", required = true, dataType = "int")
    })*/
    @RequestMapping(value = "/auditResult/{auditResult}", method = RequestMethod.GET)
    private Map<String, Object> getByAuditResult(@PathVariable Integer auditResult, @RequestParam String access_token, @RequestParam Integer limit, @RequestParam Integer offset){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser currentUser = appUserRepository.findByPhone(phone);

        try{
            List<AskHelp> askHelpList = askHelpRepository.findByPublisherIdAndAuditResult(
                    currentUser.getId(), auditResult,
                    new PageRequest(offset/limit, limit, new Sort(Sort.Direction.DESC, "publishTime")));

            for (AskHelp askHelp : askHelpList){
                askHelp.setPublisher(currentUser);
                askHelp.setAuditor(appUserRepository.findById(askHelp.getAuditorId()));
            }

            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.DATA, askHelpList);
            responseMap.put(ServerContext.MSG, "获取未通过审核信息成功");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.DEV_MSG, "获取未通过审核信息失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value = "获取内容图片")
    @RequestMapping(value = "/{baseFolder}/{phoneFolder}/{fileName:.+}", method = RequestMethod.GET)
    private ResponseEntity<?> getFile(@PathVariable("baseFolder")String root, @PathVariable("phoneFolder")String phoneFolder, @PathVariable("fileName")String fileName){
        try{
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(root+"/"+phoneFolder, fileName).toString()));
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    private String getPhoneByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE CONCAT('%', ?)", new Object[]{access_token}, String.class);
    }
}
