package com.ebs.boardparadice.config;

import org.hibernate.collection.spi.PersistentBag;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Hibernate PersistentBag -> List 변환 컨버터 추가
        Converter<PersistentBag, List> persistentBagConverter = new Converter<PersistentBag, List>() {
            @Override
            public List convert(MappingContext<PersistentBag, List> context) {
                return new ArrayList<>(context.getSource());
            }
        };

        modelMapper.addConverter(persistentBagConverter);

        return modelMapper;
    }
}
