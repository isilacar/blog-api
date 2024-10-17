package com.scalefocus.blogservice.repository;


import com.scalefocus.blogservice.entity.ElasticBlogDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ElasticBlogRepository extends ElasticsearchRepository<ElasticBlogDocument, Long> {

    @Query(""" 
            {
                "bool":
                    {
                        "should":
                                [
                            {"term":{"title":"#{#keyword}"}},
                            {"term":{"text":"#{#keyword}"}},
                            {"term":{"tags.name":"#{#keyword}"}}
                                 ]
                    }
            }
            """)
    List<ElasticBlogDocument> searchByKeyword(String keyword);

}