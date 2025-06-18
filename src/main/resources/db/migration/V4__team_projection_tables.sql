CREATE TABLE project_demo_cqrs.teams (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE project_demo_cqrs.team_members (
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL REFERENCES project_demo_cqrs.teams(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    profession TEXT NOT NULL
);

CREATE TABLE project_demo_cqrs.team_tasks (
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL REFERENCES project_demo_cqrs.teams(id) ON DELETE CASCADE,
    project_task_id UUID NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    status TEXT NOT NULL,
    assignee_id UUID,
    actual_time_spent_hours INTEGER,
    actual_time_spent_minutes INTEGER
);

CREATE TABLE project_demo_cqrs.team_view_project_tasks (
    task_id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    estimation_hours INTEGER NOT NULL,
    estimation_minutes INTEGER NOT NULL
);