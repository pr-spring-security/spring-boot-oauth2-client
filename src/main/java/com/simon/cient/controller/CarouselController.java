package com.simon.cient.controller;

import com.simon.cient.domain.Carousel;
import com.simon.cient.domain.CarouselRepository;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/8/31.
 */
@Api(value = "轮播", description = "轮播")
@RestController
@RequestMapping("/api/carousels")
public class CarouselController {
    @Autowired
    private CarouselRepository carouselRepository;

    private final ResourceLoader resourceLoader;

    @Autowired
    public CarouselController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @ApiOperation(value = "插入一条轮播", notes = "不需要传id")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(@RequestBody Carousel carousel){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try{
            carouselRepository.insert(carousel);
            responseMap.put(ServerContext.STATUS_CODE, 201);
            responseMap.put(ServerContext.MSG, "插入成功");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "插入失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation(value = "获取轮播信息", notes = "")
    @RequestMapping(method = RequestMethod.GET)
    private Map<String, Object> get(){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA, carouselRepository.findAll());
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "获取轮播信息失败");
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
