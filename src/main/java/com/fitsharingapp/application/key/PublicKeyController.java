package com.fitsharingapp.application.key;

import com.fitsharingapp.application.key.dto.CreatePublicKeyRequest;
import com.fitsharingapp.application.key.dto.PublicKeyResponse;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.key.PublicKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.fitsharingapp.common.Constants.FS_DEVICE_ID_HEADER;
import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.PUBLIC_KEY_NOT_FOUND;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(path = "/keys")
@RequiredArgsConstructor
public class PublicKeyController {

    private final PublicKeyService publicKeyService;

    @PostMapping
    @ResponseStatus(CREATED)
    public void createPublicKey(
            @RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @RequestBody CreatePublicKeyRequest createPublicKeyRequest) {
        publicKeyService.savePublicKey(fsUserId, createPublicKeyRequest);
    }

    @GetMapping("/my")
    public PublicKeyResponse getMyPublicKey(
            @RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @RequestHeader(value = FS_DEVICE_ID_HEADER) UUID deviceId) {
        return publicKeyService.getPublicKey(fsUserId, deviceId)
                .map(PublicKey::getKey)
                .map(PublicKeyResponse::new)
                .orElseThrow(() -> new ServiceException(PUBLIC_KEY_NOT_FOUND));
    }

    @DeleteMapping
    public void deleteAllForUser(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        publicKeyService.deleteKeys(fsUserId);
    }



}
