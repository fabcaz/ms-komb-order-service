package com.kombucha.model.brewery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class KombuchaPagedList extends PageImpl<KombuchaDto> {


    static final long serialVersionUID = 6526123459L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public KombuchaPagedList(@JsonProperty("content")List<KombuchaDto> content,
                             @JsonProperty("number") int number,
                             @JsonProperty("size") int size,
                             @JsonProperty("totalElements") Long totalElements,
                             @JsonProperty("pageable") JsonNode pageable,
                             @JsonProperty("last") boolean last,
                             @JsonProperty("totalPages") int totalPages,
                             @JsonProperty("sort") JsonNode sort,
                             @JsonProperty("first") boolean first,
                             @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    public KombuchaPagedList(List<KombuchaDto> content, Pageable pageable, long total){
        super(content, pageable, total);
    }

    public KombuchaPagedList(List<KombuchaDto> content) {
        super(content);
    }
}
