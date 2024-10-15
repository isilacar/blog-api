package com.scalefocus.blog_api.mapper;

import com.scalefocus.blog_api.dto.TagDto;
import com.scalefocus.blog_api.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagMapper {

    public Tag mapToTag(TagDto tagDto) {
        return Tag.builder()
                .id(tagDto.id())
                .name(tagDto.name())
                .build();

    }

    public TagDto mapToTagDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();

    }

    public Set<TagDto> mapToTagDtoList(Set<Tag> tags) {
        return tags.stream().map(this::mapToTagDto).collect(Collectors.toSet());
    }

    public Set<Tag> mapToTagList(Set<TagDto> tagDtos) {
        return tagDtos.stream().map(this::mapToTag).collect(Collectors.toSet());
    }
}
