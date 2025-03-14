package com.shop.service;


import com.shop.constant.ItemSellStatus;
import com.shop.dto.EventDto;
import com.shop.entity.ArtItem;
import com.shop.entity.Event;
import com.shop.repository.ArtItemRepository;
import com.shop.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtItemRepository artItemRepository;

    // 이벤트 목록 조회
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(event -> new EventDto(event.getId(), event.getTitle(), event.getStart(), event.getEnd(), event.getItemSellStatus()))
                .collect(Collectors.toList());
    }

    // 이벤트 생성
    public void addEvent(EventDto eventDto) {
        // Event 저장
        Event event = new Event();
        event.setTitle(eventDto.getTitle());
        event.setStart(eventDto.getStart());
        event.setEnd(eventDto.getEnd());
        event.setItemSellStatus(ItemSellStatus.SELL);

        eventRepository.save(event); // 먼저 이벤트를 저장하여 영속 상태로 만듬

        // ArtItem 업데이트
        List<ArtItem> artItems = artItemRepository.findAllByItemSellStatus(ItemSellStatus.SELL);
        for (ArtItem artItem : artItems) {
            if (artItem.getEventStart() == null && artItem.getEventEnd() == null) {
                // ArtItem에 event 설정
                artItem.setEvent(event);
                artItem.setEventStart(eventDto.getStart());
                artItem.setEventEnd(eventDto.getEnd());

                artItemRepository.save(artItem); // ArtItem 저장
            }
        }
    }

    // 이벤트 삭제
    @Transactional
    public void deleteEvent(Long eventId) {
        // 해당 event_id를 참조하는 ArtItem들의 eventStart와 eventEnd를 null로 설정
        artItemRepository.clearEventStartAndEndForEventId(eventId);

        // 이벤트 삭제
        eventRepository.deleteById(eventId);
    }
}
