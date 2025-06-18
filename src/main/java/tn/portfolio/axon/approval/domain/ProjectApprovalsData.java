package tn.portfolio.axon.approval.domain;

import tn.portfolio.axon.project.domain.ApproverId;

import java.io.Serializable;
import java.util.Objects;

public class ProjectApprovalsData implements Serializable {

    private ApproverId approverId;
    private ApprovalStatus approvalStatus;

    public ProjectApprovalsData() {
    }

    public ProjectApprovalsData(ApproverId approverId, ApprovalStatus approvalStatus) {
        this.approverId = approverId;
        this.approvalStatus = approvalStatus;
    }

    public static ProjectApprovalsData newInstance(ApproverId approverId){
        return new ProjectApprovalsData(approverId, ApprovalStatus.PENDING);
    }

    public void approve(){
         approvalStatus = ApprovalStatus.APPROVED;
    }

    public void reject(){
        approvalStatus = ApprovalStatus.REJECTED;
    }

    public boolean hasApproverId(ApproverId id){
        return id.equals(approverId);
    }

    public boolean isApproved(){
        return approvalStatus == ApprovalStatus.APPROVED;
    }

    public boolean isRejected(){
        return approvalStatus == ApprovalStatus.REJECTED;
    }

    enum ApprovalStatus{PENDING, APPROVED, REJECTED}

    public ApproverId getApproverId() {
        return approverId;
    }

    public void setApproverId(ApproverId approverId) {
        this.approverId = approverId;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectApprovalsData that = (ProjectApprovalsData) o;
        return approverId.equals(that.approverId) && approvalStatus == that.approvalStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(approverId, approvalStatus);
    }
}
