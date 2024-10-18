package com.fitsharingapp.domain.news;

import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.CreateReferenceNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.news.repository.News;
import com.fitsharingapp.domain.news.repository.NewsRepository;
import com.fitsharingapp.domain.news.repository.ReferenceNews;
import com.fitsharingapp.domain.news.repository.ReferenceNewsRepository;
import com.fitsharingapp.domain.relationship.RelationshipService;
import com.fitsharingapp.domain.user.UserService;
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

    public News createNews(UUID fsUserId, CreateNewsRequest newsDTO) {
        userService.validateUser(newsDTO.receiverFsUserId(), RECEIVER_NOT_FOUND);
        relationshipService.validateRelationship(fsUserId, newsDTO.receiverFsUserId());
        return newsRepository.save(newsMapper.toNewsEntity(newsDTO, fsUserId));
    }

    public ReferenceNews createReferenceNews(UUID fsUserId, CreateReferenceNewsRequest newsDTO) {
        return referenceNewsRepository.save(newsMapper.toReferenceNewsEntity(newsDTO, fsUserId));
    }

    public List<NewsResponse> getAllPublishedNews(UUID fsUserId) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        return referenceNewsRepository.findAllByPublisherFsUserId(fsUserId, sort)
                .stream()
                .map(newsMapper::toResponse)
                .toList();
    }

    public List<NewsResponse> getAllReceivedNews(UUID fsUserId) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        return newsRepository.findAllByReceiverFsUserId(fsUserId, sort)
                .stream()
                .map(news -> newsMapper.toResponse(
                        news,
                        userService.getUsernameById(news.getPublisherFsUserId(), PUBLISHER_NOT_FOUND),
                        userService.getUsernameById(news.getReceiverFsUserId(), RECEIVER_NOT_FOUND))
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

    public List<NewsResponse> getAllReceivedNewsFromFriend(UUID fsUserId, UUID friendFsUserId) {
        relationshipService.validateRelationship(fsUserId, friendFsUserId);
        return newsRepository.findAllByPublisherFsUserIdAndReceiverFsUserId(friendFsUserId, fsUserId)
                .stream()
                .map(news -> newsMapper.toResponse(
                        news,
                        userService.getUsernameById(news.getPublisherFsUserId(), PUBLISHER_NOT_FOUND),
                        userService.getUsernameById(news.getReceiverFsUserId(), RECEIVER_NOT_FOUND))
                    )
                .toList();
    }

}
