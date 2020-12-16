package com.kombucha.model.order;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class KombuchaOrderPagedList extends PageImpl<KombuchaOrderDto> {
    public KombuchaOrderPagedList(List<KombuchaOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public KombuchaOrderPagedList(List<KombuchaOrderDto> content) {
        super(content);
    }
}
