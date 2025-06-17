CREATE TABLE project_demo_cqrs.approvals (
    id UUID PRIMARY KEY,
    approver_id UUID NOT NULL,
    project_id UUID NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    project_role TEXT NOT NULL,
    approval_status TEXT NOT NULL,
    decision_date TIMESTAMP WITHOUT TIME ZONE,
    decision_reason TEXT
);