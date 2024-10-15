package com.scalefocus.blog_api.mapper;

import com.scalefocus.blog_api.dto.TagDto;
import com.scalefocus.blog_api.entity.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TagMapperTest {

    private Tag tag;
    private TagDto tagDto;
    private Set<Tag> tagSet;
    private Set<TagDto> tagDtoSet;

    @InjectMocks
    private TagMapper tagMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        tag = Tag.builder()
                .id(1L)
                .name("test tag")
                .build();

        tagDto = TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();

        tagSet = Set.of(tag);
        tagDtoSet = Set.of(tagDto);

    }

    @Test
    public void testMapToTag() {
        Tag returnedTag = tagMapper.mapToTag(tagDto);

        assertThat(returnedTag).isNotNull();
        assertThat(returnedTag.getId()).isEqualTo(tag.getId());
        assertThat(returnedTag.getName()).isEqualTo(tag.getName());

    }

    @Test
    public void testMapToTagDto() {
        TagDto returnedTagDto = tagMapper.mapToTagDto(tag);

        assertThat(returnedTagDto).isNotNull();
        assertThat(returnedTagDto.id()).isEqualTo(tagDto.id());
        assertThat(returnedTagDto.name()).isEqualTo(tagDto.name());

    }

    @Test
    public void testMapToTagDtoList() {
        Set<TagDto> returnedTagDtoSet = tagMapper.mapToTagDtoList(tagSet);

        assertThat(returnedTagDtoSet).isNotNull();
        assertThat(returnedTagDtoSet.size()).isNotEqualTo(0);
        assertThat(returnedTagDtoSet.size()).isEqualTo(tagDtoSet.size());
    }

    @Test
    public void testMapToTagList() {
        Set<Tag> returnedTags = tagMapper.mapToTagList(tagDtoSet);

        assertThat(returnedTags).isNotNull();
        assertThat(returnedTags.size()).isNotEqualTo(0);
        assertThat(returnedTags.size()).isEqualTo(tagSet.size());

    }
}