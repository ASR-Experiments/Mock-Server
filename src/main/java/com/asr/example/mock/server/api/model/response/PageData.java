package com.asr.example.mock.server.api.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageData<T> {

    int pageNumber;
    long pageSize;
    long totalCount;
    long currentPage;
    int totalPages;
    Set<T> elements;
    boolean hasNext;
}
