-- 기존 자료와 Flyway checksum을 보존하는 전진 migration입니다.
ALTER TABLE gw.conversation DROP CONSTRAINT conversation_owner_system_check;
ALTER TABLE gw.conversation ADD CONSTRAINT conversation_owner_system_check CHECK (owner_system ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$');
ALTER TABLE gw.audit_event DROP CONSTRAINT audit_event_owner_system_check;
ALTER TABLE gw.audit_event ADD CONSTRAINT audit_event_owner_system_check CHECK (owner_system ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$');
DROP INDEX gw.conversation_expires_at_idx;
ALTER TABLE gw.conversation DROP COLUMN expires_at;
CREATE INDEX conversation_owner_updated_idx ON gw.conversation(owner_system,owner_actor_ref,updated_at DESC,id DESC);
CREATE INDEX cache_entry_expires_idx ON gw.cache_entry(expires_at);

CREATE TABLE kn.index_set (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL,
    embedding_model text NOT NULL CHECK (length(embedding_model)>0), embedding_dimension integer NOT NULL CHECK (embedding_dimension>0),
    config_sha256 text NOT NULL CHECK (config_sha256 ~ '^[0-9a-f]{64}$'),
    state text NOT NULL CHECK (state IN ('BUILDING','ACTIVE','INACTIVE')), created_at timestamptz NOT NULL,
    PRIMARY KEY(system_id,id)
);
CREATE UNIQUE INDEX index_set_one_active_idx ON kn.index_set(system_id) WHERE state='ACTIVE';
CREATE TABLE kn.document (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL, index_set_id uuid NOT NULL,
    bucket text NOT NULL CHECK (length(bucket)>0), object_key text NOT NULL,
    revision text NOT NULL CHECK (revision ~ '^[0-9a-f]{64}$'), title text NOT NULL,
    permissions jsonb NOT NULL CHECK (jsonb_typeof(permissions)='array' AND jsonb_array_length(permissions)>0),
    extraction_issues jsonb NOT NULL CHECK (jsonb_typeof(extraction_issues)='array'),
    PRIMARY KEY(system_id,id), UNIQUE(system_id,index_set_id,bucket,object_key),
    FOREIGN KEY(system_id,index_set_id) REFERENCES kn.index_set(system_id,id) ON DELETE CASCADE
);
CREATE INDEX document_index_set_idx ON kn.document(system_id,index_set_id);
CREATE TABLE kn.parent_chunk (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL, document_id uuid NOT NULL,
    location jsonb NOT NULL CHECK (jsonb_typeof(location)='object'), header text NOT NULL, body_masked text NOT NULL CHECK (length(body_masked)<=4000),
    body_sha256 text NOT NULL CHECK (body_sha256 ~ '^[0-9a-f]{64}$'), PRIMARY KEY(system_id,id),
    FOREIGN KEY(system_id,document_id) REFERENCES kn.document(system_id,id) ON DELETE CASCADE
);
CREATE INDEX parent_chunk_document_idx ON kn.parent_chunk(system_id,document_id);
CREATE TABLE kn.child_chunk (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL, parent_id uuid NOT NULL,
    ordinal integer NOT NULL CHECK (ordinal>=0), location jsonb NOT NULL CHECK (jsonb_typeof(location)='object'),
    header text NOT NULL, body_masked text NOT NULL CHECK (length(body_masked)<=1000), identifiers jsonb NOT NULL CHECK (jsonb_typeof(identifiers)='array'), embedding vector NOT NULL,
    PRIMARY KEY(system_id,id), UNIQUE(system_id,parent_id,ordinal),
    FOREIGN KEY(system_id,parent_id) REFERENCES kn.parent_chunk(system_id,id) ON DELETE CASCADE
);
CREATE INDEX child_chunk_parent_idx ON kn.child_chunk(system_id,parent_id);
-- 스냅샷은 Job 삭제와 독립된 영구 자료입니다.
CREATE TABLE kn.answer_snapshot (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL, job_id text NOT NULL,
    actor_ref text NOT NULL CHECK (actor_ref ~ '^[A-Za-z0-9_-]{1,64}$'), question_masked text NOT NULL,
    answer jsonb NOT NULL CHECK (jsonb_typeof(answer)='object'), evidence jsonb NOT NULL CHECK (jsonb_typeof(evidence)='array'),
    config_sha256 text NOT NULL CHECK (config_sha256 ~ '^[0-9a-f]{64}$'), created_at timestamptz NOT NULL,
    PRIMARY KEY(system_id,id), UNIQUE(system_id,job_id)
);
CREATE TABLE kn.feedback (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL, answer_id uuid NOT NULL,
    actor_ref text NOT NULL CHECK (actor_ref ~ '^[A-Za-z0-9_-]{1,64}$'), rating text NOT NULL CHECK (rating IN ('UP','DOWN')),
    comment_masked text NOT NULL CHECK (length(comment_masked)<=2000), state text NOT NULL CHECK (state IN ('OPEN','RESOLVED','REJECTED')),
    version integer NOT NULL CHECK (version>0), created_at timestamptz NOT NULL, updated_at timestamptz NOT NULL,
    PRIMARY KEY(system_id,id), UNIQUE(system_id,answer_id,actor_ref),
    FOREIGN KEY(system_id,answer_id) REFERENCES kn.answer_snapshot(system_id,id) ON DELETE RESTRICT
);
CREATE INDEX feedback_state_created_idx ON kn.feedback(system_id,state,created_at,id);
CREATE TABLE kn.review (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL, feedback_id uuid NOT NULL,
    feedback_version integer NOT NULL CHECK (feedback_version>0), reviewer_ref text NOT NULL CHECK (reviewer_ref ~ '^[A-Za-z0-9_-]{1,64}$'),
    cause text NOT NULL CHECK (cause IN ('DOCUMENT','RETRIEVAL','ANSWER','REJECT')), action jsonb NOT NULL CHECK (jsonb_typeof(action)='object'),
    created_at timestamptz NOT NULL, PRIMARY KEY(system_id,id), UNIQUE(system_id,feedback_id,feedback_version),
    FOREIGN KEY(system_id,feedback_id) REFERENCES kn.feedback(system_id,id) ON DELETE RESTRICT
);
CREATE TABLE kn.verified_answer (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL, review_id uuid NOT NULL,
    question_masked text NOT NULL, answer_masked jsonb NOT NULL CHECK (jsonb_typeof(answer_masked)='object' AND answer_masked ? 'sentences'
        AND jsonb_typeof(answer_masked->'sentences')='array' AND jsonb_array_length(answer_masked->'sentences') BETWEEN 1 AND 20),
    embedding_model text NOT NULL CHECK (length(embedding_model)>0), embedding_dimension integer NOT NULL CHECK (embedding_dimension>0),
    config_sha256 text NOT NULL CHECK (config_sha256 ~ '^[0-9a-f]{64}$'),
    locations jsonb NOT NULL CHECK (jsonb_typeof(locations)='array' AND jsonb_array_length(locations) BETWEEN 1 AND 6),
    permissions jsonb NOT NULL CHECK (jsonb_typeof(permissions)='array' AND jsonb_array_length(permissions)>0),
    embedding vector NOT NULL CHECK (vector_dims(embedding)=embedding_dimension), active boolean NOT NULL,
    PRIMARY KEY(system_id,id), UNIQUE(system_id,review_id),
    FOREIGN KEY(system_id,review_id) REFERENCES kn.review(system_id,id) ON DELETE RESTRICT
);
CREATE INDEX verified_answer_active_idx ON kn.verified_answer(system_id,active);
CREATE TABLE kn.glossary (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), term text NOT NULL CHECK (length(term) BETWEEN 1 AND 64),
    expansion text NOT NULL CHECK (length(expansion) BETWEEN 1 AND 256), review_id uuid,
    version integer NOT NULL CHECK (version>0), PRIMARY KEY(system_id,term),
    FOREIGN KEY(system_id,review_id) REFERENCES kn.review(system_id,id) ON DELETE RESTRICT
);
CREATE TABLE kn.question_case (
    system_id text NOT NULL CHECK (system_id ~ '^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$'), id uuid NOT NULL, feedback_id uuid, review_version integer,
    payload jsonb NOT NULL CHECK (jsonb_typeof(payload)='object'), active boolean NOT NULL, created_at timestamptz NOT NULL,
    CHECK ((feedback_id IS NULL AND review_version IS NULL) OR (feedback_id IS NOT NULL AND review_version IS NOT NULL AND review_version>0)),
    PRIMARY KEY(system_id,id), UNIQUE(system_id,feedback_id,review_version),
    FOREIGN KEY(system_id,feedback_id) REFERENCES kn.feedback(system_id,id) ON DELETE RESTRICT
);
