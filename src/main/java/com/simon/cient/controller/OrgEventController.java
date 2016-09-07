package com.simon.cient.controller;

import com.simon.cient.domain.OrgEvent;
import com.simon.cient.domain.OrgEventRepository;
import com.simon.cient.util.ImageUtil;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/8/30.
 */
@Api(value = "活动")
@RestController
@RequestMapping("/api/events")
public class OrgEventController {
    @Autowired
    private OrgEventRepository orgEventRepository;

    private final ResourceLoader resourceLoader;

    private static final String ROOT = "events/posters";

    @Autowired
    public OrgEventController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @ApiOperation(value = "发布活动", notes = "海报图片采用base64编码成字符串上传，服务端生成png，存储为url")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(@RequestBody OrgEvent orgEvent){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try{
            //将base64字符串转换成图片，poster存图片url
            BASE64Decoder decoder = new BASE64Decoder();
            //Base64解码
            byte[] imgBytes = decoder.decodeBuffer(orgEvent.getPoster());
            for (int i=0; i<imgBytes.length; i++){
                if (imgBytes[i]<0){
                    //调整异常数据
                    imgBytes[i]+=256;
                }
            }

            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
            String imgDir = ROOT + "/" + dayFormat.format(System.currentTimeMillis());
            String imgUrl = imgDir + "/" + dateFormat.format(System.currentTimeMillis()) + ".png";

            if (!Files.exists(Paths.get(imgDir))){
                Files.createDirectories(Paths.get(imgDir));
                Files.createFile(Paths.get(imgUrl));
            }

            Files.write(Paths.get(imgUrl), imgBytes);

            orgEvent.setPoster("http://" + ServerContext.IP + "/api/" + imgUrl);

            orgEventRepository.insert(orgEvent);
            responseMap.put(ServerContext.STATUS_CODE, 201);
            responseMap.put(ServerContext.MSG, "");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "发布活动失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation("获取活动详情")
    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> get(@RequestParam Integer limit, @RequestParam Integer offset){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA, orgEventRepository.findAll(new PageRequest(offset/limit, limit, new Sort(Sort.Direction.ASC, "publishTime"))).getContent());
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "获取活动失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation("修改活动内容")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public Map<String, Object> patch(@PathVariable("id")String id,@RequestBody OrgEvent orgEvent){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try{
            OrgEvent orgEventOld = orgEventRepository.findById(id);
            byte[] imgBytes = ImageUtil.convertToBytes(orgEvent.getPoster());
            Files.write(Paths.get(orgEventOld.getPoster()), imgBytes);

            orgEvent.setId(id);
            orgEventRepository.save(orgEvent);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
        }catch (DataRetrievalFailureException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "更新失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }catch (IOException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "更新失败，未上传海报");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation("根据id获取活动详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    private Map<String, Object> getById(@PathVariable String id){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            OrgEvent orgEvent = orgEventRepository.findById(id);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA, orgEvent);
        }catch (DataAccessException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "找不到id为"+id+"的活动");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation("根据id删除活动")
    @RequestMapping(value = "{/id}", method = RequestMethod.DELETE)
    private Map<String, Object> deleteById(@PathVariable String id){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try {
            orgEventRepository.delete(id);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "删除成功");
        }catch (DataAccessException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "找不到id为"+id+"的活动");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/posters/{parentDir}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable("parentDir") String parentDir, @PathVariable("filename") String filename){
        parentDir = ROOT+"/"+parentDir;
        try{
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(parentDir, filename).toString()));
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

}
