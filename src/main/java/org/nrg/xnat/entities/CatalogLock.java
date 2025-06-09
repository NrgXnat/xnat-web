package org.nrg.xnat.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(indexes = {@Index(name = "xhbm_catalog_lock_locked_until_idx", columnList = "lockedUntil")})
@Access(AccessType.FIELD)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@With
@Slf4j
public class CatalogLock extends AbstractHibernateEntity {
    private static final long   serialVersionUID       = -2595929129619207093L;

    @Column(unique = true)
    private int resourceId;
    private String checksum;
    private LocalDateTime lockedUntil;

    public boolean isFree() {
        return lockedUntil == null;
    }

    public boolean isStale() {
        return lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil);
    }
}
