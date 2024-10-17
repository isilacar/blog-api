package com.scalefocus.blogservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ElasticTag implements Serializable {

    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String name;
}