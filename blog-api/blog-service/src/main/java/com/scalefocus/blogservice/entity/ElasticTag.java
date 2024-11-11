package com.scalefocus.blogservice.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ElasticTag implements Serializable {

    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String name;
}