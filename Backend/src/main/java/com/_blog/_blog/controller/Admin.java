package com._blog._blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog._blog.service.ReportService;


@RestController
@RequestMapping("/api/admin")
public class Admin {
    
    @Autowired
    ReportService reportService;

    @DeleteMapping("/report/{reportID}")
    public ResponseEntity<?> deleteReport(
        @PathVariable long reportID
    ) {
        return reportService.deleteReport(reportID);
    }

}
