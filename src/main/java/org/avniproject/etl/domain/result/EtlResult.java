package org.avniproject.etl.domain.result;

public class EtlResult {
    private Boolean isSuccess;

    public EtlResult(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
