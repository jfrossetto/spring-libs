package br.com.jfr.libs.commons.r2dbc;

import static br.com.jfr.libs.commons.security.ContextHolder.CONTEXT_HOLDER_KEY;

import br.com.jfr.libs.commons.exception.BusinessException;
import br.com.jfr.libs.commons.security.ContextHolder;
import java.util.Optional;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuditableEntityCallback
    implements BeforeConvertCallback<AuditableBaseEntity>, BeforeSaveCallback<AuditableBaseEntity> {

  @Override
  public Publisher<AuditableBaseEntity> onBeforeConvert(
      AuditableBaseEntity entity, SqlIdentifier table) {
    return Mono.deferContextual(
        ctx -> {
          if (ctx.getOrEmpty(CONTEXT_HOLDER_KEY).isEmpty()) {
            return Mono.error(new BusinessException("Reactor Context is Empty!"));
          }

          final ContextHolder contextHolder = ctx.get(CONTEXT_HOLDER_KEY);

          if (Optional.of(contextHolder.getCredential()).isEmpty()) {
            return Mono.error(new BusinessException("Credential is Empty!"));
          }

          if (entity.isNew()) {
            entity.setCreatedBy(contextHolder.getCredential().getUserId());
          }
          entity.setChangedBy(contextHolder.getCredential().getUserId());

          return Mono.just(entity);
        });
  }

  @Override
  public Publisher<AuditableBaseEntity> onBeforeSave(
      AuditableBaseEntity entity, OutboundRow row, SqlIdentifier table) {
    if (!entity.isNew()) {
      // r2dbc has no support for updatable=false, than remove columns that shouldn't be updated
      row.remove(SqlIdentifier.unquoted("create_date"));
      row.remove(SqlIdentifier.unquoted("created_by"));
    }
    return Mono.just(entity);
  }
}
