package com.fitsharingapp.application.user;

import com.fitsharingapp.application.user.dto.UpdateUserRequest;
import com.fitsharingapp.application.user.dto.UserResponse;
import com.fitsharingapp.domain.key.PublicKeyService;
import com.fitsharingapp.domain.news.NewsService;
import com.fitsharingapp.domain.relationship.RelationshipService;
import com.fitsharingapp.domain.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RelationshipService relationshipService;
    private final NewsService newsService;
    private final PublicKeyService publicKeyService;

    @PutMapping()
    @ResponseStatus(OK)
    public UserResponse updateUser(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @RequestBody UpdateUserRequest userUpdateDTO) {
        return userService.updateUser(fsUserId, userUpdateDTO);
    }

    @GetMapping("/me")
    public UserResponse getAuthenticatedUser(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        return userService.getAuthenticatedUser(fsUserId);
    }

    @GetMapping("/{fsUserId}")
    public UserResponse getUserById(@PathVariable UUID fsUserId) {
        return userService.getUserResponseById(fsUserId, USER_NOT_FOUND);
    }

    @GetMapping("/search")
    public List<UserResponse> getUserBySearchTermWithoutAuthenticated(
            @RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @RequestParam String searchTerm) {
        return userService.getUserBySearchTermWithoutAuthenticated(fsUserId, searchTerm);
    }

    @DeleteMapping()
    @ResponseStatus(NO_CONTENT)
    @Transactional
    public void deleteUser(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        newsService.deleteAllNews(fsUserId);
        relationshipService.deleteAllRelationships(fsUserId);
        publicKeyService.deleteKeys(fsUserId);
        userService.deleteUser(fsUserId);
    }

}