package com.proj.msorder.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@Entity
public class KombuchaOrderLine extends BaseEntity {

    @Builder
    public KombuchaOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                             KombuchaOrder kombuchaOrder, UUID kombId, String upc, Integer orderQuantity,
                             Integer quantityAllocated){
        super(id,version,createdDate,lastModifiedDate);
        this.kombuchaOrder = kombuchaOrder;
        this.kombId = kombId;
        this.upc = upc;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;

    }

    @ManyToOne
    private KombuchaOrder kombuchaOrder;

    @Type(type="org.hibernate.type.UUIDCharType")
    @Column(length = 36, columnDefinition = "varchar(36)")
    private UUID kombId;
    private String upc;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;

    public KombuchaOrderLine() {
    }
}
