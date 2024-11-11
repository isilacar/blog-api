package com.scalefocus.blogservice.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
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
