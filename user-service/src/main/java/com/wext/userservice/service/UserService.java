package com.wext.userservice.service;

import com.wext.common.domain.UserInfoItem;
import com.wext.common.domain.exception.AuthorityLimitException;
import com.wext.common.domain.exception.InvalidOperationException;
import com.wext.common.domain.exception.NonExistentException;
import com.wext.userservice.domain.User;
import com.wext.userservice.domain.UserNode;
import com.wext.userservice.repository.UserNodeRepository;
import com.wext.userservice.repository.UserRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserNodeRepository userNodeRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserNodeRepository userNodeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userNodeRepository = userNodeRepository;
    }

    public UserDetails getUserDetail(@NonNull String userArg) {

        User user = this.getUserAutoChoose(userArg);
        List<String> roleList = Collections.singletonList("USER");

        List<GrantedAuthority> authorities = roleList.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new org.springframework.security.core.userdetails
                .User(userArg, user.getPassword(), authorities);
    }

    public User getUserAutoChoose(@NonNull String userArg) {

        Optional<User> user;
        String type;

        if (Pattern.matches("^\\d+$", userArg)) {
            user = this.userRepository.findById(Long.parseLong(userArg));
            type = "ID";
        }
        else if (userArg.indexOf("@") > 0) {
            user = this.userRepository.findByEmail(userArg);
            type = "Email";
        }
        else  {
            user = this.userRepository.findByScreenName(userArg);
            type = "ScreenName";
        }

        user.orElseThrow(() -> {
            log.debug(type + ": " + userArg + " not found.");
            return new NonExistentException(type + ": " + userArg + " not found.");
        });

        log.debug(type + ": " + userArg + " found.");
        return user.get();
    }

    public User getUserById(@NonNull Long id) throws NonExistentException {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new NonExistentException("ID: " + id + " not found."));
        log.debug("ID: " + id + " found.");
        return user;
    }

    public Map<Long, User> getUserByIds(@NonNull Collection<Long> ids) {
        List<User> users = this.userRepository.findByIdIn(ids);
        log.debug(users.toString());
        Map<Long, User> result = new HashMap<>(users.size());
        users.forEach(user -> result.put(user.getId(), user));
        log.debug(result.toString());
        return result;
    }

    @Transactional
    public User createUser(@NonNull String screenName, @NonNull String password, @NonNull String email, String nickname)
            throws AuthorityLimitException {
        screenName = screenName.toLowerCase();

        if (!Pattern.matches("^[a-zA-Z]\\w{3,17}$", screenName) &&    // 用户名由字母、数字和下划线构成
                !userRepository.findByScreenName(screenName).isPresent()) { // 用户已存在
            throw new InvalidOperationException("Username Error.Format wrong or username has used.");
        }

        if (!Pattern.matches("^(.+)@(.+)$", email) &&   // email格式简单认证
                !userRepository.findByEmail(email).isPresent()) {       // email已使用
            throw new InvalidOperationException("Email format wrong.");
        }

        if (nickname == null || nickname.isEmpty()) {   // 没有指定nickname时设置为用户名
            nickname = screenName;
        }

        // 保存到SQL数据库
        User user = userRepository.save(User.builder()
                .screenName(screenName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .build());
        // 保存到图形数据库
        userNodeRepository.save(UserNode.builder()
                .id(user.getId()).build());

        log.info("New user created: " + user);
        return user;
    }

    public UserInfoItem getUserInfo(@NonNull User user) {
//        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
//        // 基础信息
//        result.put("id", user.getId());
//        result.put("screenName", user.getScreenName());
//        result.put("nickname", user.getNickname());
//        result.put("email", user.getEmail());
//        result.put("profile", user.getProfile());
//        result.put("iconUrl", user.getIconFilename());
//        result.put("createdTime", user.getCreatedTime());
//
//        // 动态信息
//        Long id = user.getId();
//        result.put("followings", userNodeRepository.getFollowCount(id).orElse(0L));
//        result.put("followers", userNodeRepository.getFollowerCount(id).orElse(0L));
        UserInfoItem item = UserInfoItem.builder()
                .id(user.getId())
                .screenName(user.getScreenName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profile(user.getProfile())
                .iconUrl(user.getIconFilename())
                .createdTime(user.getCreatedTime())
                .followings(userNodeRepository.getFollowCount(user.getId()).orElse(0L))
                .followers(userNodeRepository.getFollowerCount(user.getId()).orElse(0L))
                .build();

        log.debug("Find user info: " + item);
        return item;
    }

    public UserInfoItem getUserInfo(@NonNull Long id) {
        User user = getUserById(id);
        return getUserInfo(user);
    }

    public Map<Long, UserInfoItem> getUsersInfo(@NonNull Collection<Long> ids) {
        var users = getUserByIds(ids);
        Map<Long, UserInfoItem> results = new HashMap<>(users.size());
        users.forEach((id, user) ->
                results.put(id, getUserInfo(user)));
        return results;
    }

    public List<Long> getUserFollowingIds(@NonNull Long id, Long page, Integer pageSize) {
        if (page == null || page < 1L) {
            page = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 30;

        }
        Long skip = pageSize * (page - 1);

        List<Long> userIds = userNodeRepository.findUserFollowings(id, skip, pageSize)
                .orElse(new ArrayList<>())
                .stream()
                .map(UserNode::getId)
                .collect(Collectors.toList());

        log.debug("Find user " + id + " followings: " + userIds);
        return userIds;
    }

    public List<Long> getUserFollowerIds(@NonNull Long id, Long page, Integer pageSize) {
        if (page == null || page < 1L) {
            page = 1L;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 30;

        }
        Long skip = pageSize * (page - 1);

        List<Long> userIds = userNodeRepository.findUserFollowers(id, skip, pageSize)
                .orElse(new ArrayList<>())
                .stream()
                .map(UserNode::getId)
                .collect(Collectors.toList());

        log.debug("Find user " + id + " followers: " + userIds);
        return userIds;
    }

    public User updateUserAttr(@NonNull Long id, @NonNull Map<String, String> newAttrs) {
        // 更新密码外的用户信息
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NonExistentException("Can't find by id " + id));

        for (Map.Entry<String, String> attr : newAttrs.entrySet()) {
            String key = attr.getKey();
            String value = attr.getValue();

            switch (key) {
                case "nickname":
                    if (value.length() > 32) {
                        throw new InvalidOperationException("Nickname is too Long");
                    }
                    user.setNickname(value);
                    break;
                case "profile":
                    if (value.length() > 255) {
                        throw new InvalidOperationException("Profile is too Long");
                    }
                    user.setProfile(value);
                    break;
                case "iconPath":
                    user.setIconFilename(value);
                    break;
            }
        }

        return userRepository.save(user);
    }

    public User updateUserPassword(@NonNull Long id, @NonNull String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NonExistentException("Can't find by id " + id));
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.saveAndFlush(user);
    }
}
