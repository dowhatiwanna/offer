package com.offer.query.bean;

import lombok.Data;

import java.util.List;

/**
 * @author : YCKJ3558
 * @since :2020/9/27 21:48
 */
@Data
public class finalResult {
    private List<interviewInfo> interviewResult;
    private List<rejectInfo> rejectResult;
    private Integer interviewSize;
    private Integer rejectSize;
}

