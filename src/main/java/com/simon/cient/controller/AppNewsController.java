package com.simon.cient.controller;

import com.simon.cient.domain.*;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/8/17.
 */
@Api(value = "新闻接口")
@RestController
@RequestMapping("/appNews")
public class AppNewsController {

    @Autowired
    private AppNewsRepository appNewsRepository;
    @Autowired
    private NewsCommentRepository newsCommentRepository;
    @Autowired
    private SimpleNewsRepository simpleNewsRepository;

    @ApiOperation(value="获取新闻列表", notes = "新闻列表按时间降序排列")
    @RequestMapping(method = RequestMethod.GET)
    private Map<String,Object> get(Integer limit, Integer offset){
        Map<String,Object> responseMap = new LinkedHashMap<>();

        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG,"");
            responseMap.put(ServerContext.DATA,simpleNewsRepository.findAll(new PageRequest(offset/limit, limit, new Sort(Sort.Direction.ASC, "lastEditTime"))));
        }catch (DataRetrievalFailureException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG,e.getMessage());
            responseMap.put(ServerContext.DATA,"");
        }

        return responseMap;
    }

    @ApiOperation(value = "根据id获取新闻内容")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    private Map<String,Object> getAppNewsById(@PathVariable("id")String id){
        Map<String,Object> responseMap = new LinkedHashMap<>();
        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG,"");
            responseMap.put(ServerContext.DATA,appNewsRepository.findOne(id));
        }catch (DataRetrievalFailureException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG,e.getMessage());
            responseMap.put(ServerContext.DATA,"");
        }
        return responseMap;
    }

    @ApiOperation(value = "获取评论列表", notes = "评论列表按点赞数降序排列")
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
    private Map<String, Object> getCommentsByNewsId(@PathVariable("id") String newsId,
                                    Integer limit, Integer offset){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA,newsCommentRepository.findByNewsId(newsId, new PageRequest(offset/limit, limit, new Sort(Sort.Direction.ASC, "pointPraise"))));
        }catch (DataAccessException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "there is no records that news_id is "+newsId);
            responseMap.put(ServerContext.DATA, "");
        }
        return responseMap;
    }

    @ApiOperation(value="插入一条新闻")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(AppNews appNews){
        Map<String,Object> responseMap = new LinkedHashMap<>();

        try{
            appNewsRepository.insert(appNews);
            responseMap.put(ServerContext.STATUS_CODE,200);
            responseMap.put(ServerContext.MSG,"");
            responseMap.put(ServerContext.DATA,"");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE,409);
            responseMap.put(ServerContext.MSG,e.getMessage());
            responseMap.put(ServerContext.DATA,"");
        }

        return responseMap;
    }

    @ApiOperation(value="更新一条新闻")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    private Map<String, Object> update(AppNews appNews){
        Map<String,Object> responseMap = new LinkedHashMap<>();
        try{
            appNewsRepository.save(appNews);
            responseMap.put(ServerContext.STATUS_CODE,200);
            responseMap.put(ServerContext.MSG,"");
            responseMap.put(ServerContext.DATA,"");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE,409);
            responseMap.put(ServerContext.MSG,e.getMessage());
            responseMap.put(ServerContext.DATA,"");
        }
        return responseMap;
    }

    @ApiOperation(value="删除一条新闻")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    private Map<String, Object> delete(@PathVariable("id") String id){
        Map<String,Object> responseMap = new LinkedHashMap<>();
        try {
            appNewsRepository.delete(id);
            responseMap.put(ServerContext.STATUS_CODE,200);
            responseMap.put(ServerContext.MSG,"");
            responseMap.put(ServerContext.DATA,"");
        }catch (DataAccessException e){
            responseMap.put(ServerContext.STATUS_CODE,404);
            responseMap.put(ServerContext.MSG,"there is no record that id is "+id);
            responseMap.put(ServerContext.DATA,"");
        }
        return responseMap;
    }

    @ApiOperation(value = "插入一条评论")
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.POST)
    private Map<String, Object> insertComment(@PathVariable("id")String newsId, NewsComment newsComment){
        Map<String,Object> responseMap = new LinkedHashMap<>();
        newsComment.setNewsId(newsId);
        try{
            newsCommentRepository.save(newsComment);
            responseMap.put(ServerContext.STATUS_CODE,201);
            responseMap.put(ServerContext.MSG,"");
            responseMap.put(ServerContext.DATA,"");
        }catch (DataIntegrityViolationException e){
            responseMap.put(ServerContext.STATUS_CODE,409);
            responseMap.put(ServerContext.MSG, e.getMessage());
            responseMap.put(ServerContext.DATA,"");
        }
        return responseMap;
    }

    @ApiOperation(value = "对评论点赞", notes = "返回了点赞后的评论内容")
    @RequestMapping(value = "/{newsId}/comments/{commentId}", method = RequestMethod.PATCH)
    private Map<String, Object> pointPraiseComment(
            @PathVariable("newsId")String newsId,
            @PathVariable("commentId")String commentId){
        Map<String,Object> responseMap = new LinkedHashMap<>();
        try{
            NewsComment newsComment = newsCommentRepository.findOne(commentId);
            Integer pointPraise = newsComment.getPointPraise();
            pointPraise+=1;
            newsComment.setPointPraise(pointPraise);
            newsCommentRepository.save(newsComment);
            responseMap.put(ServerContext.STATUS_CODE,200);
            responseMap.put(ServerContext.MSG,"点赞成功");
            responseMap.put(ServerContext.DATA,newsCommentRepository.findOne(commentId));
        }catch (DataIntegrityViolationException e){
            responseMap.put(ServerContext.STATUS_CODE,404);
            responseMap.put(ServerContext.MSG, e.getMessage());
            responseMap.put(ServerContext.DATA,"");
        }
        return responseMap;
    }
}
