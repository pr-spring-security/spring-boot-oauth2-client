package com.simon.cient.controller;

import com.simon.cient.domain.Organization;
import com.simon.cient.domain.OrganizationRepository;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/9/13.
 */
@Api(value = "组织", description = "组织")
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    @Autowired
    private OrganizationRepository organizationRepository;

    private final ResourceLoader resourceLoader;

    @Autowired
    public OrganizationController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @ApiOperation(value = "获取组织列表", notes = "")
    @RequestMapping(method = RequestMethod.GET)
    private Map<String, Object> get(@RequestParam Integer limit, @RequestParam Integer offset){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try {
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "获取组织列表成功");
            responseMap.put(ServerContext.DATA, organizationRepository.findAll(new PageRequest(offset/limit, limit, new Sort(Sort.Direction.ASC, "lastEditTime"))).getContent());
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "获取组织列表失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation(value = "根据id获取组织信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    private Map<String, Object> getById(@PathVariable String id){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try {
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "获取组织成功");
            responseMap.put(ServerContext.DATA, organizationRepository.findById(id));
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "获取组织失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation(value = "点击量+1")
    @RequestMapping(value = "/{id}/clickVolume", method = RequestMethod.PATCH)
    private Map<String, Object> clickVolumePlus(@PathVariable String id){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try{
            Organization organization = organizationRepository.findById(id);
            organization.setClickVolume(organization.getClickVolume()+1);
            organizationRepository.save(organization);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "点击量+1");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "点击量+1失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation(value = "获取组织简介和组织标识")
    @RequestMapping(value = "/{baseFolder}/{fileName:.+}", method = RequestMethod.GET)
    private ResponseEntity<?> getFile(@PathVariable("baseFolder")String root, @PathVariable("fileName")String fileName){
        try{
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(root, fileName).toString()));
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
