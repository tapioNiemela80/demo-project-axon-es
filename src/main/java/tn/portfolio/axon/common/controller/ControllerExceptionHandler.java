package tn.portfolio.axon.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tn.portfolio.axon.approval.domain.ApprovalStateChangeNotAllowedException;
import tn.portfolio.axon.approval.service.ApprovalNotFoundException;
import tn.portfolio.axon.project.domain.ProjectAlreadyCompletedException;
import tn.portfolio.axon.project.domain.ProjectTimeEstimationWouldBeExceededException;
import tn.portfolio.axon.project.domain.UnknownProjectIdException;
import tn.portfolio.axon.project.domain.UnknownProjectTaskIdException;
import tn.portfolio.axon.team.domain.*;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ApprovalNotFoundException.class)
    public ResponseEntity<String> handleApprovalNotFoundException(ApprovalNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ApprovalStateChangeNotAllowedException.class)
    public ResponseEntity<String> handleApprovalStateChangeNotAllowedException(ApprovalStateChangeNotAllowedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownProjectIdException.class)
    public ResponseEntity<String> handleUnknownProjectIdException(UnknownProjectIdException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ProjectTimeEstimationWouldBeExceededException.class)
    public ResponseEntity<String> handleProjectTimeEstimationWouldBeExceededException(ProjectTimeEstimationWouldBeExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownProjectTaskIdException.class)
    public ResponseEntity<String> handleUnknownProjectTaskIdException(UnknownProjectTaskIdException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ProjectAlreadyCompletedException.class)
    public ResponseEntity<String> handleProjectAlreadyCompletedException(ProjectAlreadyCompletedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TaskTransitionNotAllowedException.class)
    public ResponseEntity<String> handleTaskNotInProgressException(TaskTransitionNotAllowedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TaskCannotBeDeletedException.class)
    public ResponseEntity<String> handleTaskCannotBeDeletedException(TaskCannotBeDeletedException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownTeamIdException.class)
    public ResponseEntity<String> handleUnknownTeamIdException(UnknownTeamIdException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownTeamMemberIdException.class)
    public ResponseEntity<String> handleUnknownTeamMemberIdException(UnknownTeamMemberIdException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnknownTeamTaskIdException.class)
    public ResponseEntity<String> handleUnknownTeamTaskIdException(UnknownTeamTaskIdException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TaskAlreadyAssignedException.class)
    public ResponseEntity<String> handleTaskAlreadyAssignedException(TaskAlreadyAssignedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TeamMemberHasAssignedTasksException.class)
    public ResponseEntity<String> handleTeamMemberHasAssignedTasksException(TeamMemberHasAssignedTasksException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

}