package com.simon.cient.controller;

import com.simon.cient.domain.Carousel;
import com.simon.cient.domain.CarouselRepository;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/8/31.
 */
@Api("轮播")
@RestController
@RequestMapping("/api/carousels")
public class CarouselController {
    @Autowired
    private CarouselRepository carouselRepository;

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
}
