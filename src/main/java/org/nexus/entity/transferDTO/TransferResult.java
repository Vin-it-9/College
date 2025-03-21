package org.nexus.entity.transferDTO;

import java.util.List;


public class TransferResult {

    private int transferredCount;
    private String fromLabName;
    private String toLabName;
    private List<String> warnings;


    public TransferResult(int transferredCount, String fromLabName, String toLabName, List<String> warnings) {
        this.transferredCount = transferredCount;
        this.fromLabName = fromLabName;
        this.toLabName = toLabName;
        this.warnings = warnings;
    }

    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }


    public int getTransferredCount() {
        return transferredCount;
    }

    public void setTransferredCount(int transferredCount) {
        this.transferredCount = transferredCount;
    }

    public String getFromLabName() {
        return fromLabName;
    }

    public void setFromLabName(String fromLabName) {
        this.fromLabName = fromLabName;
    }

    public String getToLabName() {
        return toLabName;
    }

    public void setToLabName(String toLabName) {
        this.toLabName = toLabName;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
