package com.ebs.boardparadice.service.boards;

import com.ebs.boardparadice.model.Free;
import com.ebs.boardparadice.repository.boards.FreeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeService {

    private final FreeRepository freeRepository;

    public List<Free> findAll() {
        return freeRepository.findAll();
    }

    public Free findById(Integer id) {
        return freeRepository.findById(id).orElse(null);
    }

    public Free save(Free free) {
        return freeRepository.save(free);
    }

    public void deleteById(Integer id) {
        freeRepository.deleteById(id);
    }


}
