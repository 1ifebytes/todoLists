-- V4: Many-to-many join table between todos and tags.
-- Composite primary key (todo_id, tag_id) prevents duplicate associations.
-- Both FKs cascade on delete: removing a todo or a tag cleans up this table automatically.
-- No extra columns needed — the association itself is the data.
CREATE TABLE todo_tags (
    todo_id VARCHAR(36) NOT NULL COMMENT '待办事项ID，外键关联todos表 (Todo ID, foreign key to todos table)',
    tag_id  VARCHAR(36) NOT NULL COMMENT '标签ID，外键关联tags表 (Tag ID, foreign key to tags table)',
    PRIMARY KEY (todo_id, tag_id),
    CONSTRAINT fk_todo_tags_todo FOREIGN KEY (todo_id) REFERENCES todos(id) ON DELETE CASCADE,
    CONSTRAINT fk_todo_tags_tag  FOREIGN KEY (tag_id)  REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT '待办事项-标签多对多关联表 (Many-to-many join table between todos and tags)';
