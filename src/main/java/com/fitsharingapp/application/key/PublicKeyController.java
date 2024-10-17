package com.fitsharingapp.application.key;

import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.key.PublicKeyService;
import com.fitsharingapp.domain.key.repository.PublicKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        publicKeyService.savePublicKey(fsUserId, createPublicKeyRequest.publicKey());
    }

    @GetMapping("/{friendFsUserId}")
    public PublicKeyResponse getPublicKey(
            @RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @PathVariable UUID friendFsUserId) {
        return publicKeyService.getPublicKey(friendFsUserId)
                .map(PublicKey::getKey)
                .map(PublicKeyResponse::new)
                .orElseThrow(() -> new ServiceException(PUBLIC_KEY_NOT_FOUND));
    }

    @DeleteMapping
    public void deleteAllForUser(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        publicKeyService.deletePublicKey(fsUserId);
    }



}
