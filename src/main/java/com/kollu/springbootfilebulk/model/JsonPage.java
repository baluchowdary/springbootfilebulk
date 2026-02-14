package com.kollu.springbootfilebulk.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Custom wrapper to handle Jackson deserialization for Spring Data Pages
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonPage extends PageImpl<Product> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public JsonPage(@JsonProperty("content") List<Product> content,
                    @JsonProperty("number") int number,
                    @JsonProperty("size") int size,
                    @JsonProperty("totalElements") Long totalElements,
                    @JsonProperty("pageable") JsonNode pageable,
                    @JsonProperty("last") boolean last,
                    @JsonProperty("totalPages") int totalPages,
                    @JsonProperty("sort") JsonNode sort,
                    @JsonProperty("first") boolean first,
                    @JsonProperty("numberOfElements") int numberOfElements) {

        super(content, PageRequest.of(number, size > 0 ? size : 1), totalElements);
    }

    public JsonPage(Page<Product> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    public JsonPage() {
        super(new ArrayList<>());
    }
}
