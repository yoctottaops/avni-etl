package org.avniproject.etl.controller;

import org.avniproject.etl.domain.OrgIdentityContextHolder;
import org.avniproject.etl.dto.AggregateReportResult;
import org.avniproject.etl.dto.UserActivityDTO;
import org.avniproject.etl.repository.ReportRepository;
import org.avniproject.etl.service.EtlService;
import org.avniproject.etl.service.ReportService;
import org.avniproject.etl.util.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    @RequestMapping(value = "reports/hr/userActivity", method = RequestMethod.GET)
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

    @RequestMapping(value = "/report/hr/syncFailures",method = RequestMethod.GET)
    public List<UserActivityDTO> getUserWiseSyncFailures(@RequestParam(value = "startDate", required = false) String startDate,
                                                  @RequestParam(value = "endDate", required = false) String endDate,
                                                  @RequestParam(value = "userIds", required = false, defaultValue = "") List<Long> userIds){
            return reportRepository.generateUserSyncFailures(
                    OrgIdentityContextHolder.getDbSchema(),
                    reportUtil.getDateDynamicWhere(startDate, endDate, "sync_start_time"),
                    reportUtil.getDynamicUserWhere(userIds, "u.id")
            );
    }

    @RequestMapping(value = "/report/hr/deviceModels", method = RequestMethod.GET)
    public List<AggregateReportResult> getUserWiseDeviceModels(@RequestParam(value = "userIds", required = false, defaultValue = "") List<Long> userIds) {

        return reportRepository.generateUserDeviceModels(
                OrgIdentityContextHolder.getDbSchema(),
                reportUtil.getDynamicUserWhere(userIds, "u.id"));
    }

    @RequestMapping(value = "/report/hr/appVersions", method = RequestMethod.GET)
    public List<AggregateReportResult> getUserWiseAppVersions(@RequestParam(value = "userIds", required = false, defaultValue = "") List<Long> userIds) {

        return reportRepository.generateUserAppVersions(
                OrgIdentityContextHolder.getDbSchema(),
                reportUtil.getDynamicUserWhere(userIds, "u.id"));
    }

    @RequestMapping(value = "/report/hr/userDetails", method = RequestMethod.GET)
    public List<UserActivityDTO> getUserDetails(@RequestParam(value = "userIds", required = false, defaultValue = "") List<Long> userIds) {

        return reportRepository.generateUserDetails(
                OrgIdentityContextHolder.getDbSchema(),
                reportUtil.getDynamicUserWhere(userIds, "u.id"));
    }



}

