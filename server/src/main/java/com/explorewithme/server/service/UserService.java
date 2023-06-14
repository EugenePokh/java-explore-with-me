package com.explorewithme.server.service;

import com.explorewithme.server.model.User;
import com.explorewithme.server.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;

    public Optional<User> findById(Integer userId) {
        return userRepository.findById(userId);
    }

    public void delete(User user) {
        logger.info("Delete user - " + user);
        userRepository.delete(user);
    }

    public Page<User> findAll(Pageable page) {
        return userRepository.findAll(page);
    }

    public Page<User> findAllByIds(List<Integer> ids, PageRequest pageable) {
        return userRepository.findAllByIdIn(ids, pageable);
    }

    public User save(User user) {
        logger.info("Save user - " + user);
        return userRepository.save(user);
    }
}
