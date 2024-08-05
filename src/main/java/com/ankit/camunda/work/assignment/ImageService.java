package com.ankit.camunda.work.assignment;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ankit.camunda.work.assignment.repo.Image;
import com.ankit.camunda.work.assignment.repo.ImageRepository;

import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public byte[] getPicture(Long id) {
        Optional<Image> picture = imageRepository.findById(id);
        return picture.map(Image::getData).orElse(null);
    }

    public void savePicture(Image image) {
        imageRepository.save(image);
    }
}
