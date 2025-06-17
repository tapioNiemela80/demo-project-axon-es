package tn.portfolio.axon.approval.service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tn.portfolio.axon.approval.command.InitializeProjectApprovementCommand;
import tn.portfolio.axon.approval.projection.Approval;
import tn.portfolio.axon.approval.projection.ApprovalRepository;
import tn.portfolio.axon.common.service.IdService;
import tn.portfolio.axon.project.event.ProjectApproverPlacedEvent;
import tn.portfolio.axon.project.event.TaskAddedToProjectEvent;
import tn.portfolio.axon.project.projection.ProjectRepository;

import java.util.List;

@Service
public class ApprovalService {
    private final ProjectRepository projects;

    private final ApprovalRepository approvalRepository;
    private final CommandGateway commandGateway;
    private final IdService idService;

    public ApprovalService(ProjectRepository projects, ApprovalRepository approvalRepository, CommandGateway commandGateway, IdService idService) {
        this.projects = projects;
        this.approvalRepository = approvalRepository;
        this.commandGateway = commandGateway;
        this.idService = idService;
    }

    @EventHandler
    public void on(ProjectApproverPlacedEvent event){
        commandGateway.send(initializeProjectApprovementCommand(event));
    }

    private InitializeProjectApprovementCommand initializeProjectApprovementCommand(ProjectApproverPlacedEvent event) {
        return new InitializeProjectApprovementCommand(idService.newApprovalId(), event.approverId(), event.projectId(), event.name(), event.role(), event.email());
    }

    @EventHandler
    public void on(TaskAddedToProjectEvent event){
        System.out.println("ApprovalService "+event);
        System.out.println("--------------------------->");
        List<Approval> approvalList = approvalRepository.findByProjectId(event.projectId().value());
        approvalList.forEach(System.out::println);
        System.out.println("<--------------------------- make email sending");


    }

}
