package com.simon.cient.controller;

import com.simon.cient.domain.AppVersion;
import com.simon.cient.domain.AppVersionRepository;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/9/16.
 */
@Api(value = "版本更新", description = "版本更新")
@RestController
@RequestMapping("/api/appVersions")
public class AppVersionController {
    @Autowired
    private AppVersionRepository appVersionRepository;

    private final ResourceLoader resourceLoader;

    @Autowired
    public AppVersionController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(value = "/checkUpdate", method = RequestMethod.GET)
    private Map<String, Object> checkUpdate(@RequestParam Integer versionCode){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try{
            AppVersion lastVersion = appVersionRepository.findAll(new Sort(Sort.Direction.ASC, "versionCode")).get(0);
            if (lastVersion.getVersionCode()>versionCode){
                responseMap.put(ServerContext.STATUS_CODE, 200001);
                responseMap.put(ServerContext.MSG, "检测到新版本");
                responseMap.put(ServerContext.DATA, lastVersion);
            }else {
                responseMap.put(ServerContext.STATUS_CODE, 200002);
                responseMap.put(ServerContext.MSG, "已经是最新版本");
            }

        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "检测新版本失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @RequestMapping(value = "/{baseFolder}/{fileName:.+}", method = RequestMethod.GET)
    private ResponseEntity<?> getFile(@PathVariable("baseFolder")String root, @PathVariable("fileName")String fileName){
        try{
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(root, fileName).toString()));
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
