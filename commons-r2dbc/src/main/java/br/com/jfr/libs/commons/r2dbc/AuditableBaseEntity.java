package br.com.jfr.libs.commons.r2dbc;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

public abstract class AuditableBaseEntity<T> extends BaseEntity<T> {

  @CreatedDate
  @Column("create_date")
  private LocalDateTime created;

  @Column("created_by")
  private UUID createdBy;

  @LastModifiedDate
  @Column("change_date")
  private LocalDateTime changed;

  @Column("changed_by")
  private UUID changedBy;

  protected AuditableBaseEntity() {}

  protected AuditableBaseEntity(
      LocalDateTime created, UUID createdBy, LocalDateTime changed, UUID changedBy) {
    this.created = created;
    this.createdBy = createdBy;
    this.changed = changed;
    this.changedBy = changedBy;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public UUID getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(UUID createdBy) {
    this.createdBy = createdBy;
  }

  public UUID getChangedBy() {
    return changedBy;
  }

  public void setChangedBy(UUID changedBy) {
    this.changedBy = changedBy;
  }

  public LocalDateTime getChanged() {
    return changed;
  }

  public void setChanged(final LocalDateTime changed) {
    this.changed = changed;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
