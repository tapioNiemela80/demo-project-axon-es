package tn.portfolio.axon.approval.domain;

public class ApprovalStateChangeNotAllowedException extends RuntimeException {
    public ApprovalStateChangeNotAllowedException(String message) {
        super(message);
    }
}
