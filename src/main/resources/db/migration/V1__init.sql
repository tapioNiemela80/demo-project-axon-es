CREATE SCHEMA IF NOT EXISTS project_demo_cqrs;

CREATE TABLE project_demo_cqrs.association_value_entry (
    id bigint NOT NULL,
    association_key character varying(255) NOT NULL,
    association_value character varying(255),
    saga_id character varying(255) NOT NULL,
    saga_type character varying(255)
);

CREATE SEQUENCE project_demo_cqrs.association_value_entry_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE project_demo_cqrs.dead_letter_entry (
    dead_letter_id character varying(255) NOT NULL,
    cause_message character varying(1023),
    cause_type character varying(255),
    diagnostics oid,
    enqueued_at timestamp(6) with time zone NOT NULL,
    last_touched timestamp(6) with time zone,
    aggregate_identifier character varying(255),
    event_identifier character varying(255) NOT NULL,
    message_type character varying(255) NOT NULL,
    meta_data oid,
    payload oid NOT NULL,
    payload_revision character varying(255),
    payload_type character varying(255) NOT NULL,
    sequence_number bigint,
    time_stamp character varying(255) NOT NULL,
    token oid,
    token_type character varying(255),
    type character varying(255),
    processing_group character varying(255) NOT NULL,
    processing_started timestamp(6) with time zone,
    sequence_identifier character varying(255) NOT NULL,
    sequence_index bigint NOT NULL
);

CREATE TABLE project_demo_cqrs.domain_event_entry (
    global_index bigint NOT NULL,
    event_identifier character varying(255) NOT NULL,
    meta_data oid,
    payload oid NOT NULL,
    payload_revision character varying(255),
    payload_type character varying(255) NOT NULL,
    time_stamp character varying(255) NOT NULL,
    aggregate_identifier character varying(255) NOT NULL,
    sequence_number bigint NOT NULL,
    type character varying(255)
);

CREATE SEQUENCE project_demo_cqrs.domain_event_entry_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE project_demo_cqrs.saga_entry (
    saga_id character varying(255) NOT NULL,
    revision character varying(255),
    saga_type character varying(255),
    serialized_saga oid
);

CREATE TABLE project_demo_cqrs.snapshot_event_entry (
    aggregate_identifier character varying(255) NOT NULL,
    sequence_number bigint NOT NULL,
    type character varying(255) NOT NULL,
    event_identifier character varying(255) NOT NULL,
    meta_data oid,
    payload oid NOT NULL,
    payload_revision character varying(255),
    payload_type character varying(255) NOT NULL,
    time_stamp character varying(255) NOT NULL
);

CREATE TABLE project_demo_cqrs.token_entry (
    processor_name character varying(255) NOT NULL,
    segment integer NOT NULL,
    owner character varying(255),
    "timestamp" character varying(255) NOT NULL,
    token oid,
    token_type character varying(255)
);

ALTER TABLE ONLY project_demo_cqrs.association_value_entry
    ADD CONSTRAINT pk_association_value_entry PRIMARY KEY (id);

ALTER TABLE ONLY project_demo_cqrs.dead_letter_entry
    ADD CONSTRAINT pk_dead_letter_entry PRIMARY KEY (dead_letter_id);

ALTER TABLE ONLY project_demo_cqrs.domain_event_entry
    ADD CONSTRAINT pk_domain_event_entry PRIMARY KEY (global_index);

ALTER TABLE ONLY project_demo_cqrs.saga_entry
    ADD CONSTRAINT pk_saga_entry PRIMARY KEY (saga_id);

ALTER TABLE ONLY project_demo_cqrs.snapshot_event_entry
    ADD CONSTRAINT pk_snapshot_event_entry PRIMARY KEY (aggregate_identifier, sequence_number, type);

ALTER TABLE ONLY project_demo_cqrs.token_entry
    ADD CONSTRAINT pk_token_entry PRIMARY KEY (processor_name, segment);

ALTER TABLE ONLY project_demo_cqrs.domain_event_entry
    ADD CONSTRAINT uq_domain_event_agg_seq UNIQUE (aggregate_identifier, sequence_number);

ALTER TABLE ONLY project_demo_cqrs.snapshot_event_entry
    ADD CONSTRAINT uq_snapshot_event_entry_event_id UNIQUE (event_identifier);

ALTER TABLE ONLY project_demo_cqrs.domain_event_entry
    ADD CONSTRAINT uq_domain_event_entry_event_id UNIQUE (event_identifier);

ALTER TABLE ONLY project_demo_cqrs.dead_letter_entry
    ADD CONSTRAINT uq_dead_letter_entry_group_seq UNIQUE (processing_group, sequence_identifier, sequence_index);

CREATE INDEX idx_association_by_saga ON project_demo_cqrs.association_value_entry USING btree (saga_id, saga_type);

CREATE INDEX idx_association_by_type_key_val ON project_demo_cqrs.association_value_entry USING btree (saga_type, association_key, association_value);

CREATE INDEX idx_dead_letter_by_group ON project_demo_cqrs.dead_letter_entry USING btree (processing_group);

CREATE INDEX idx_dead_letter_by_group_seq_id ON project_demo_cqrs.dead_letter_entry USING btree (processing_group, sequence_identifier);
