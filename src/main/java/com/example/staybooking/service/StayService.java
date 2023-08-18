package com.example.staybooking.service;

import com.example.staybooking.exception.StayNotExistException;
import com.example.staybooking.model.Stay;
import com.example.staybooking.model.User;
import com.example.staybooking.repository.StayRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StayService {

    private StayRepository stayRepository;

    public StayService(StayRepository stayRepository) {
        this.stayRepository = stayRepository;
    }

    public List<Stay> listByUser(String username) {
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
    }

    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay Doesn't Exist");
        }

        return stay;
    }

    public void add(Stay stay) {
        stayRepository.save(stay);
    }

    public void delete(Long stayId, String username) throws StayNotExistException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());

        if (stay == null) {
            throw new StayNotExistException("Stay Doesn't Exist");
        }

        stayRepository.delete(stay);
    }

}
