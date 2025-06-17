CREATE TABLE project_demo_cqrs.projects (
    id UUID PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    status TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    planned_end_date DATE,
    initial_estimated_time_hours INTEGER NOT NULL,
    initial_estimated_time_minutes INTEGER NOT NULL
);

CREATE TABLE project_demo_cqrs.project_tasks (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    estimated_time_hours INTEGER NOT NULL,
    estimated_time_minutes INTEGER NOT NULL,
    task_status TEXT NOT NULL,
    actual_time_spent_hours INTEGER,
    actual_time_spent_minutes INTEGER,
    project_id UUID REFERENCES project_demo_cqrs.projects(id) ON DELETE CASCADE
);