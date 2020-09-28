package com.offer.query.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.offer.query.bean.finalResult;
import com.offer.query.bean.info;
import com.offer.query.bean.interviewInfo;
import com.offer.query.bean.rejectInfo;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : YCKJ3558
 * @since :2020/9/27 21:50
 */
@RestController
@RequestMapping
public class QueryController {
    @PostMapping("/query")
    public finalResult query(@RequestParam("page") String page, @RequestParam("cookie") String cookie) throws IOException {
        //创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建httpGet实例
        HttpGet httpGet = new HttpGet("https://app.mokahr.com/api/user/recommendation/applications?page=" + page);
        httpGet.setHeader("Cookie", cookie);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        finalResult finalResult = new finalResult();
        if (response != null) {
            HttpEntity entity = response.getEntity();  //获取网页内容

            String result = EntityUtils.toString(entity, "UTF-8");
            String rows = JSONObject.parseObject(result).getString("rows");
            List<info> infos = JSONObject.parseArray(rows, info.class);
            List<interviewInfo> interviewInfos = infos.stream()
                    .filter(a -> !"暂不匹配".equals(a.getStatus()) && !"初筛".equals(a.getStatus()))
                    .map(a -> new interviewInfo(a.getName(), a.getJobTitle(), a.getRecentActivityTime(), a.getStatus()))
                    .collect(Collectors.toList());

            List<rejectInfo> rejectInfos = infos.stream()
                    .filter(a -> "暂不匹配".equals(a.getStatus()))
                    .map(a -> new rejectInfo(a.getName(), a.getJobTitle(), a.getRecentActivityTime(), a.getStatus()))
                    .collect(Collectors.toList());
            finalResult.setInterviewResult(interviewInfos);
            finalResult.setInterviewSize(interviewInfos.size());
            finalResult.setRejectResult(rejectInfos);
            finalResult.setRejectSize(rejectInfos.size());

        }

        if (response != null) {
            response.close();
        }
        if (httpClient != null) {
            httpClient.close();
        }
        return finalResult;
    }


}

