package com.fitsharingapp.application;

import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.news.NewsService;
import com.fitsharingapp.domain.relationship.RelationshipService;
import com.fitsharingapp.domain.user.UserService;
import com.fitsharingapp.domain.user.dto.UpdateUserDTO;
import com.fitsharingapp.domain.user.repository.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RelationshipService relationshipService;
    private final NewsService newsService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PutMapping()
    public User updateUser(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @RequestBody UpdateUserDTO userUpdateDTO) {
        return userService.updateUser(fsUserId, userUpdateDTO);
    }

    @DeleteMapping()
    @Transactional
    public void deleteUser(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        newsService.deleteAllNews(fsUserId);
        relationshipService.deleteAllRelationships(fsUserId);
        userService.deleteUser(fsUserId);
    }

    @GetMapping("/{fsUserId}")
    public User getUserById(@PathVariable UUID fsUserId) {
        return userService.getUserById(fsUserId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }

    @GetMapping("/search")
    public List<User> getUserBySearchTermWithoutAuthenticated(@RequestParam String searchTerm) {
        return userService.searchByUsernameOrName(searchTerm);
    }

}