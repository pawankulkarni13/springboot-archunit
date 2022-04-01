package com.stark.springbootarchunit.scheduling;

import com.stark.springbootarchunit.domain.User;
import com.stark.springbootarchunit.repository.UserRepository;
import com.stark.springbootarchunit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
public class UserSchedule {

    @Scheduled(fixedDelay = 10000l)
    public void callAPI(){
        ResponseEntity<List> response = new RestTemplate().getForEntity("http://localhost:8080/users", List.class, Optional.ofNullable(null));
        response.getBody().forEach(System.out::println);
    }

}
