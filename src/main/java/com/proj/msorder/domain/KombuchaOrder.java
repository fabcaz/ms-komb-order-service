package com.proj.msorder.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class KombuchaOrder extends BaseEntity{

    @Builder
    public KombuchaOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef, Customer customer,
                         Set<KombuchaOrderLine> kombuchaOrderLines, KombuchaOrderStatusEnum orderStatus,
                         String orderStatusCallbackUrl){
        super(id, version, createdDate, lastModifiedDate);
        this.customer = customer;
        this.customerRef = customerRef;
        this.kombuchaOrderLines = kombuchaOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;

    }


    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "kombuchaOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<KombuchaOrderLine> kombuchaOrderLines;

    @Enumerated(EnumType.STRING)
    private KombuchaOrderStatusEnum orderStatus = KombuchaOrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;



}
