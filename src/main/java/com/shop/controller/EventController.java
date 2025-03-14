package com.shop.controller;

import com.shop.dto.EventDto;
import com.shop.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    // 이벤트 목록 조회
    @GetMapping("/events/list")
    public List<EventDto> getEvents() {
        return eventService.getAllEvents();
    }

    // 이벤트 추가
    @PostMapping("/admin/events/add")
    public ResponseEntity<String> addEvent(@RequestBody EventDto eventDto) {
        eventService.addEvent(eventDto);
        return new ResponseEntity<>("Event added successfully", HttpStatus.CREATED);
    }

    // 이벤트 삭제
    @PostMapping("/admin/events/delete")
    public ResponseEntity<String> deleteEvent(@RequestBody EventDto eventDto) {
        eventService.deleteEvent(eventDto.getId());
        return new ResponseEntity<>("Event deleted successfully", HttpStatus.OK);
    }
}
