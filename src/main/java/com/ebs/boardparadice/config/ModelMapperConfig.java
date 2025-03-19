package com.ebs.boardparadice.config;

import com.ebs.boardparadice.DTO.answers.RulebookAnswerDTO;
import com.ebs.boardparadice.model.answers.RulebookAnswer;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ModelMapperConfig {

	@Primary
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // 엄격한 매칭 방식 설정 (필수 아님)
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // RulebookAnswer의 createdate만 매핑되도록 설정
        modelMapper.typeMap(RulebookAnswer.class, RulebookAnswerDTO.class).addMappings(mapper -> {
            mapper.map(RulebookAnswer::getCreatedate, RulebookAnswerDTO::setCreateDate);
        });

        return modelMapper;
    }
}
