package com.explorewithme.server.service;

import com.explorewithme.server.dto.UserRequestDto;
import com.explorewithme.server.dto.UserResponseDto;
import com.explorewithme.server.exception.UserNotFoundException;
import com.explorewithme.server.mapper.UserMapper;
import com.explorewithme.server.model.User;
import com.explorewithme.server.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserResponseDto> findAllByIds(List<Integer> ids, Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size, Sort.by("created").ascending());
        Page<User> users;
        if (Objects.nonNull(ids) && !ids.isEmpty()) {
            users = userRepository.findByIdIn(ids, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(UserMapper::toDto);
    }

    @Transactional
    public UserResponseDto post(UserRequestDto userRequestDto) {
        User user = new User();
        user.setEmail(userRequestDto.getEmail());
        user.setName(userRequestDto.getName());
        user.setCreated(LocalDateTime.now());

        User created = userRepository.save(user);
        logger.info("Created user - " + created);

        return UserMapper.toDto(created);
    }

    @Transactional
    public void deleteById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No such user with id - " + userId));

        userRepository.delete(user);
        logger.info("Deleted user - " + user);
    }
}
