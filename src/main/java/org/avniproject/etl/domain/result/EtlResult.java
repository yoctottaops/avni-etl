package org.avniproject.etl.domain.result;

import java.io.Serializable;

public class EtlResult implements Serializable {
    private final Boolean isSuccess;

    public EtlResult(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
