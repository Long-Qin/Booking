package com.example.staybooking.service;

import com.example.staybooking.exception.StayNotExistException;
import com.example.staybooking.model.Stay;
import com.example.staybooking.model.StayImage;
import com.example.staybooking.model.User;
import com.example.staybooking.repository.StayRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StayService {

    private final StayRepository stayRepository;
    private final ImageStorageService imageStorageService;

    public StayService(StayRepository stayRepository, ImageStorageService imageStorageService) {

        this.stayRepository = stayRepository;
        this.imageStorageService = imageStorageService;
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

    public void add(Stay stay, MultipartFile[] images) {
        List<StayImage> stayImages = Arrays.stream(images)
                .filter(image -> !image.isEmpty())
                .parallel()
                .map(imageStorageService::save)
                .map(mediaLink -> new StayImage(mediaLink, stay))
                .collect(Collectors.toList());
        stay.setImages(stayImages);
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
