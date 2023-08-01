package org.avniproject.etl.controller;

import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.dto.UserActivityDTO;
import org.avniproject.etl.repository.ReportRepository;
import org.avniproject.etl.service.ReportService;
import org.avniproject.etl.util.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAnyAuthority('organisation_admin','admin')")
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportRepository reportRepository;
    private final ReportUtil reportUtil;

    @Autowired
    public ReportController(ReportService reportService, ReportRepository reportRepository, ReportUtil reportUtil) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
        this.reportUtil = reportUtil;
    }

    @GetMapping("/hr/userActivity")
    public ResponseEntity getUserActivity(@RequestParam(value = "start_date", required = false) String startDate,
                                          @RequestParam(value = "endDate", required = false) String endDate,
                                          @RequestParam(value = "userIds", required = false, defaultValue = "") List<Long> userIds)
    {
        try {
            List<UserActivityDTO> userActivity = reportRepository.getUserActivity(
                   OrgIdentityContextHolder.getDbSchema(),
                   reportUtil.getDateDynamicWhere(startDate, endDate, "registration_date"),
                   reportUtil.getDateDynamicWhere(startDate, endDate, "encounter_date_time"),
                   reportUtil.getDateDynamicWhere(startDate, endDate, "enrolment_date_time"),
                   reportUtil.getDynamicUserWhere(userIds, "u.id")
            );

            return ResponseEntity.ok(userActivity);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/hr/syncFailures")
    public ResponseEntity getUserWiseSyncFailures(@RequestParam(value = "start_date", required = false) String startDate,
                                                  @RequestParam(value = "endDate", required = false) String endDate,
                                                  @RequestParam(value = "userIds", required = false, defaultValue = "") List<Long> userIds){
        try {
            List<UserActivityDTO> userActivity = reportRepository.getUserSyncFailures(
                    OrgIdentityContextHolder.getDbSchema(),
                    reportUtil.getDateDynamicWhere(startDate, endDate, "sync_start_time"),
                    reportUtil.getDynamicUserWhere(userIds, "u.id")
            );
            return ResponseEntity.ok(userActivity);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}

