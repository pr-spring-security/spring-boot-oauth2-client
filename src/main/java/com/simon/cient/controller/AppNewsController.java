package com.simon.cient.controller;

import com.simon.cient.domain.*;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by simon on 2016/8/17.
 */
@Api(value = "新闻接口")
@RestController
@RequestMapping("/api/appNews")
public class AppNewsController {

    @Autowired
    private AppNewsRepository appNewsRepository;
    @Autowired
    private NewsCommentRepository newsCommentRepository;

    private final ResourceLoader resourceLoader;

    private static final String ROOT = "news";

    @Autowired
    public AppNewsController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @ApiOperation(value="获取新闻列表", notes = "新闻列表按时间降序排列")
    @RequestMapping(method = RequestMethod.GET)
    private Map<String,Object> get(@RequestParam Integer limit,@RequestParam Integer offset){
        Map<String,Object> responseMap = new LinkedHashMap<>();

        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG,"");
            responseMap.put(ServerContext.DATA,appNewsRepository.findAll(new PageRequest(offset/limit, limit, new Sort(Sort.Direction.ASC, "lastEditTime"))).getContent());
        }catch (DataRetrievalFailureException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG,e.getMessage());
//            responseMap.put(ServerContext.DATA,"");
        }

        return responseMap;
    }

    @ApiOperation(value = "根据id获取新闻内容")
    @ApiResponses(value = { @ApiResponse(code=404, message = "invalid id")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    private Map<String,Object> getAppNewsById(@PathVariable("id")String id){
        Map<String,Object> responseMap = new LinkedHashMap<>();
        AppNews appNews = appNewsRepository.findOne(id);
        if(null != appNews){
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA, appNews);
        }else{
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG,"invalid id");
//            responseMap.put(ServerContext.DATA,"");
        }
        return responseMap;
    }

    @ApiOperation(value = "获取评论列表", notes = "评论列表按点赞数降序排列")
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
    private Map<String, Object> getCommentsByNewsId(@PathVariable("id") String newsId,
                                    @RequestParam Integer limit,@RequestParam Integer offset){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA,newsCommentRepository.findByNewsId(newsId, new PageRequest(offset/limit, limit, new Sort(Sort.Direction.ASC, "pointPraise"))).getContent());
        }catch (DataAccessException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "there is no records that news_id is "+newsId);
//            responseMap.put(ServerContext.DATA, "");
        }
        return responseMap;
    }

    @ApiOperation(value="插入一条新闻")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(@RequestBody AppNews appNews){
        Map<String,Object> responseMap = new LinkedHashMap<>();

        // 根据appNews的时间创建对应时间的文件夹，并创建以当前时间命名的html文件，
        // 把appNews的content内容写入html，设置appNews的content内容为html的url
        List<String> htmlContent = new ArrayList<>();
        htmlContent.add("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "\t<meta charset=\"UTF-8\">\n" +
                "\t<title>Document</title>\n" +
                "</head>\n" +
                "<body>");
        htmlContent.add(appNews.getContent());
        htmlContent.add("</body>\n" +
                "</html>");

        try{
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;//月份是从0开始的
            int date = calendar.get(Calendar.DATE);
            SimpleDateFormat format = new SimpleDateFormat("HHmmss");
            String htmlDir = ROOT + "/" + year + "/" + month + "/" + date + "/";
            String htmlUrl = htmlDir + format.format(System.currentTimeMillis())+".html";
            if (!Files.exists(Paths.get(htmlDir))){
                Files.createDirectories(Paths.get(htmlDir));
                Files.createFile(Paths.get(htmlUrl));
            }
            Files.write(Paths.get(htmlUrl), htmlContent, Charset.forName("UTF-8"));
            appNews.setContent(htmlUrl);
            appNewsRepository.insert(appNews);
            responseMap.put(ServerContext.STATUS_CODE,201);
            responseMap.put(ServerContext.MSG,"");
//            responseMap.put(ServerContext.DATA,"");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE,409);
            responseMap.put(ServerContext.MSG,e.getMessage());
//            responseMap.put(ServerContext.DATA,"");
        }

        return responseMap;
    }

    @ApiOperation(value="更新一条新闻")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    private Map<String, Object> update(@RequestBody AppNews appNews){
        Map<String,Object> responseMap = new LinkedHashMap<>();
        try{
            AppNews oldNews = appNewsRepository.findById(appNews.getId());
            String htmlUrl = oldNews.getContent();
            List<String> htmlContent = new ArrayList<>();
            htmlContent.add("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "\t<meta charset=\"UTF-8\">\n" +
                    "\t<title>Document</title>\n" +
                    "</head>\n" +
                    "<body>");
            htmlContent.add(appNews.getContent());//新的新闻内容
            htmlContent.add("</body>\n" +
                    "</html>");
            Files.write(Paths.get(htmlUrl), htmlContent, Charset.forName("UTF-8"));
            appNews.setContent(htmlUrl);
            appNewsRepository.save(appNews);
            responseMap.put(ServerContext.STATUS_CODE,200);
            responseMap.put(ServerContext.MSG,"");
//            responseMap.put(ServerContext.DATA,"");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE,409);
            responseMap.put(ServerContext.MSG,e.getMessage());
//            responseMap.put(ServerContext.DATA,"");
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
//            responseMap.put(ServerContext.DATA,"");
        }catch (DataAccessException e){
            responseMap.put(ServerContext.STATUS_CODE,404);
            responseMap.put(ServerContext.MSG,"there is no record that id is "+id);
//            responseMap.put(ServerContext.DATA,"");
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
//            responseMap.put(ServerContext.DATA,"");
        }catch (DataIntegrityViolationException e){
            responseMap.put(ServerContext.STATUS_CODE,409);
            responseMap.put(ServerContext.MSG, e.getMessage());
//            responseMap.put(ServerContext.DATA,"");
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
//            responseMap.put(ServerContext.DATA,"");
        }
        return responseMap;
    }

    @ApiOperation(value = "获取新闻网页内容")
    @RequestMapping(value = "/{baseFolder}/{year}/{month}/{date}/{htmlName:.+}", method = RequestMethod.GET)
    @ResponseBody
    private ResponseEntity<?> getHtmlContent(@PathVariable("baseFolder")String baseFolder,
                                             @PathVariable("year")Integer year,
                                             @PathVariable("month")Integer month,
                                             @PathVariable("date")Integer date,
                                             @PathVariable("htmlName")String htmlName){
        String root = baseFolder+"/"+year+"/"+month+"/"+date;
        try{
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(root, htmlName).toString()));
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
