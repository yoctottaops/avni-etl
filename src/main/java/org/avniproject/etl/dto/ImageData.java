package org.avniproject.etl.dto;

public record ImageData(
        String uuid,
        String subjectFirstName,
        String subjectLastName,
        String url,
        String conceptName,
        String subjectTypeName,
        String programEnrolment,
        String encounterTypeName,
        String syncConcept1Name,
        String syncConcept2Name,
        String syncParameterValue1,
        String syncParameterValue2,
        String address) {
}
