package com.ankit.camunda.work.assignment;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ankit.camunda.work.assignment.repo.Image;

@Component
public class AnimalWorker {

  @Autowired
  private ImageService imageService;

  private final static Logger LOG = LoggerFactory.getLogger(AnimalWorker.class);

  //I was not able to find out a way to tie this with REST outbound connector 
  @JobWorker(type = "undefined")
  public void getImage(final ActivatedJob job) {
    final String animalType = (String) job.getVariablesAsMap().get("animalType");
    LOG.info("fetching animal images based on animalType {}", animalType);

    String apiUrl=null;

        switch (animalType) {
            case "cat":
                apiUrl = "https://placekitten.com/200/300";
                break;
            case "dog":
                apiUrl = "https://place.dog/200/300";
                break;
            case "bear":
                apiUrl = "https://placebear.com/200/300";
                break;
            default:
                throw new IllegalArgumentException("Unknown animal type: " + animalType);
        }

        try {
            byte[] pictureData = fetchPictureFromApi(apiUrl);
            Image image = new Image();
            image.setData(pictureData);
            imageService.savePicture(image);
  }catch (IOException e) {
    LOG.info("exception in persisting image");
}
  }
  private byte[] fetchPictureFromApi(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();

        return inputStream.readAllBytes();
    }

}