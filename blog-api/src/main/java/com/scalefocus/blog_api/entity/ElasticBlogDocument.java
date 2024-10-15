package com.scalefocus.blog_api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "blogs_document")
public class ElasticBlogDocument implements Serializable {

    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String text;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<ElasticTag> tags;
}
