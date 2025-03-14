package com.fitsharingapp.application.news;

import com.fitsharingapp.application.common.validator.RequestValidator;
import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.CreateReferenceNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.news.News;
import com.fitsharingapp.domain.news.NewsRepository;
import com.fitsharingapp.domain.news.ReferenceNews;
import com.fitsharingapp.domain.news.ReferenceNewsRepository;
import com.fitsharingapp.application.relationship.RelationshipService;
import com.fitsharingapp.application.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.fitsharingapp.common.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final ReferenceNewsRepository referenceNewsRepository;
    private final NewsMapper newsMapper;
    private final UserService userService;
    private final RelationshipService relationshipService;
    private final RequestValidator requestValidator;
    private static final Sort SORT_BY_CREATED_AT = Sort.by(Sort.Order.desc("createdAt"));

    public News createNews(UUID fsUserId, CreateNewsRequest newsDTO) {
        requestValidator.validate(newsDTO);
        userService.validateUser(newsDTO.receiverFsUserId(), RECEIVER_NOT_FOUND);
        relationshipService.validateRelationship(fsUserId, newsDTO.receiverFsUserId());
        return newsRepository.save(newsMapper.toNewsEntity(newsDTO, fsUserId));
    }

    public ReferenceNews createReferenceNews(UUID fsUserId, UUID deviceId, CreateReferenceNewsRequest newsDTO) {
        requestValidator.validate(newsDTO);
        return referenceNewsRepository.save(newsMapper.toReferenceNewsEntity(newsDTO, fsUserId, deviceId));
    }

    public void likeNews(UUID fsUserId, UUID id) {
        newsRepository.findById(id)
                .ifPresent(news -> {
                    if (!news.getReceiverFsUserId().equals(fsUserId)) {
                        throw new ServiceException(USER_IS_NOT_RECEIVER);
                    }
                    news.setIsLiked(!news.getIsLiked());
                    newsRepository.save(news);
                    referenceNewsRepository.findById(news.getReferenceNewsId())
                            .ifPresent(referenceNews -> {
                                referenceNews.setLikes(news.getIsLiked() ? referenceNews.getLikes() + 1 : referenceNews.getLikes() - 1);
                                referenceNewsRepository.save(referenceNews);
                            });
                });
    }

    public List<NewsResponse> getAllPublishedNews(UUID fsUserId, UUID deviceId) {
        return referenceNewsRepository.findAllByPublisherFsUserIdAndDeviceId(fsUserId, deviceId, SORT_BY_CREATED_AT)
                .stream()
                .map(referenceNews -> newsMapper.toResponse(
                        referenceNews,
                        userService.getUserById(referenceNews.getPublisherFsUserId(), PUBLISHER_NOT_FOUND))
                    )
                .toList();
    }

    public List<NewsResponse> getAllReceivedNews(UUID fsUserId, UUID deviceId) {
        return newsRepository.findAllByReceiverFsUserIdAndReceiverDeviceId(fsUserId, deviceId, SORT_BY_CREATED_AT)
                .stream()
                .map(news -> newsMapper.toResponse(
                        news,
                        userService.getUserById(news.getPublisherFsUserId(), PUBLISHER_NOT_FOUND))
                    )
                .toList();
    }

    public List<NewsResponse> getAllReceivedNewsFromFriend(UUID fsUserId, UUID deviceId, UUID friendFsUserId) {
        relationshipService.validateRelationship(fsUserId, friendFsUserId);
        return newsRepository.findAllByPublisherFsUserIdAndReceiverFsUserIdAndReceiverDeviceId(
                        friendFsUserId, fsUserId, deviceId, SORT_BY_CREATED_AT)
                .stream()
                .map(news -> newsMapper.toResponse(
                        news,
                        userService.getUserById(news.getPublisherFsUserId(), PUBLISHER_NOT_FOUND))
                    )
                .toList();
    }

    public void deleteNews(UUID fsUserId, UUID id) {
        referenceNewsRepository.findById(id)
                .ifPresent(referenceNews -> {
                    if (!referenceNews.getPublisherFsUserId().equals(fsUserId)) {
                        throw new ServiceException(USER_IS_NOT_PUBLISHER);
                    }
                    referenceNewsRepository.deleteById(id);
                });
        newsRepository.deleteAll(newsRepository.findAllByReferenceNewsId(id));
    }

    public void deleteAllNews(UUID fsUserId) {
        referenceNewsRepository.deleteAllByPublisherFsUserId(fsUserId);
        newsRepository.deleteAllByPublisherFsUserId(fsUserId);
    }

    public void deleteNewsForPublisherAndReceiver(UUID sender, UUID recipient) {
        newsRepository.deleteByPublisherFsUserIdAndReceiverFsUserId(sender, recipient);
        newsRepository.deleteByPublisherFsUserIdAndReceiverFsUserId(recipient, sender);
    }

}
