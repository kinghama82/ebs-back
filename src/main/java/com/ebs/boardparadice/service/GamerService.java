package com.ebs.boardparadice.service;

import com.ebs.boardparadice.model.Gamer;
import com.ebs.boardparadice.repository.GamerRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamerService {

    private final GamerRepository gamerRepository;

    public List<Gamer> findAll() {
        return gamerRepository.findAll();
    }

    public Gamer findById(Integer id) {
        return gamerRepository.findById(id).orElse(null);
    }

    public Gamer save(Gamer gamer) {
        return gamerRepository.save(gamer);
    }

    public void deleteById(Integer id) {
        gamerRepository.deleteById(id);
    }

}
