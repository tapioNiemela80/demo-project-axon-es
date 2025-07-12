package tn.portfolio.axon.approval.service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.portfolio.axon.approval.command.ApproveProjectByApproverCommand;
import tn.portfolio.axon.approval.command.InitializeProjectApprovementCommand;
import tn.portfolio.axon.approval.command.RejectProjectByApproverCommand;
import tn.portfolio.axon.approval.domain.ApprovalId;
import tn.portfolio.axon.approval.projection.Approval;
import tn.portfolio.axon.approval.projection.ApprovalRepository;
import tn.portfolio.axon.project.domain.ProjectId;
import tn.portfolio.axon.common.service.EmailClientService;
import tn.portfolio.axon.common.service.EmailMessage;
import tn.portfolio.axon.common.service.IdService;
import tn.portfolio.axon.project.domain.ApproverId;
import tn.portfolio.axon.project.event.ProjectApproverPlacedEvent;
import tn.portfolio.axon.project.event.ProjectCompletedEvent;
import tn.portfolio.axon.project.event.TaskAddedToProjectEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final CommandGateway commandGateway;
    private final IdService idService;
    private final EmailClientService emailClientService;
    private final String sender;

    public ApprovalService(ApprovalRepository approvalRepository, CommandGateway commandGateway, IdService idService, EmailClientService emailClientService, @Value("${email.sender}") String sender) {
        this.approvalRepository = approvalRepository;
        this.commandGateway = commandGateway;
        this.idService = idService;
        this.emailClientService = emailClientService;
        this.sender = sender;
    }

    @EventHandler
    @DisallowReplay
    public void on(ProjectApproverPlacedEvent event) {
        //creates new Approval Aggregate per approver when project is created (and emits ProjectApproverPlacedEvent)
        commandGateway.send(initializeProjectApprovementCommand(event));
    }

    @ResetHandler
    public void reset(){
        //do nothing, required by axon..
    }

    private InitializeProjectApprovementCommand initializeProjectApprovementCommand(ProjectApproverPlacedEvent event) {
        return new InitializeProjectApprovementCommand(idService.newApprovalId(), event.approverId(), event.projectId(), event.name(), event.role(), event.email());
    }

    public CompletableFuture<ApprovalId> approve(ProjectId projectId, ApproverId approverId) {
        var approval = approvalRepository.findByProjectIdAndApproverId(projectId.value(), approverId.value())
                .orElseThrow(() -> new ApprovalNotFoundException(projectId, approverId));
        var id = new ApprovalId(approval.getId());
        return commandGateway.send(new ApproveProjectByApproverCommand(id, projectId, approverId));
    }

    public CompletableFuture<ApprovalId> reject(ProjectId projectId, ApproverId approverId, String reason) {
        var approval = approvalRepository.findByProjectIdAndApproverId(projectId.value(), approverId.value())
                .orElseThrow(() -> new ApprovalNotFoundException(projectId, approverId));
        var id = new ApprovalId(approval.getId());
        return commandGateway.send(new RejectProjectByApproverCommand(id, projectId, approverId, reason));
    }

    @EventHandler
    @DisallowReplay
    public void on(TaskAddedToProjectEvent event) {
        List<Approval> approvalList = approvalRepository.findByProjectId(event.projectId().value());
        approvalList.forEach(approval -> sendEmailAboutNewTask(approval, event));
    }

    @EventHandler
    @DisallowReplay
    public void on(ProjectCompletedEvent event) {
        List<Approval> approvalList = approvalRepository.findByProjectId(event.projectId().value());
        approvalList.forEach(approval -> sendEmailAboutProjectToApprove(approval, event));
    }

    private void sendEmailAboutProjectToApprove(Approval approval, ProjectCompletedEvent event) {
        emailClientService.send(new EmailMessage(sender, approval.getEmail(), "Project %s has been completed".formatted(event.name()),
                emailContent(approval, event), false));
    }

    private String emailContent(Approval approval, ProjectCompletedEvent event) {
        String url = "/projects/" + event.projectId().value() + "/approvals/" + approval.getApproverId();//in real app we would of course have authorization & authentication in place
        return "please visit %s to either accept or to reject project".formatted(url);
    }

    private void sendEmailAboutNewTask(Approval approval, TaskAddedToProjectEvent event) {
        emailClientService.send(new EmailMessage(sender, approval.getEmail(), "New task added to project", "Task %s".formatted(event), false));
    }

}
