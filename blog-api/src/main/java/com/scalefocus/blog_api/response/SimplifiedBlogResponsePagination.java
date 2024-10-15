package com.scalefocus.blog_api.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedBlogResponsePagination {
    private List<SimplifiedBlogResponse> simplifiedBlogResponseList;
    private long totalValue;
    private long totalPages;
    private long currentPage;
    private long viewedValueCount;

}
