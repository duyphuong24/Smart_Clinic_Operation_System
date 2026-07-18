package com.smartclinic.masterdata.controller;

import com.smartclinic.masterdata.dto.RoomRequest;
import com.smartclinic.masterdata.dto.RoomResponse;
import com.smartclinic.masterdata.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public String list(Model model) {
        List<RoomResponse> rooms = roomService.findAll();
        model.addAttribute("rooms", rooms);
        model.addAttribute("title", "Master Data");
        return "admin/room-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("room", new RoomRequest());
        model.addAttribute("isEdit", false);
        model.addAttribute("title", "Master Data");
        return "admin/room-form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("room") RoomRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("title", "Master Data");
            return "admin/room-form";
        }
        roomService.create(request);
        return "redirect:/admin/rooms";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        RoomResponse room = roomService.getById(id);
        RoomRequest request = new RoomRequest();
        request.setRoomCode(room.getRoomCode());
        request.setName(room.getName());
        request.setFloor(room.getFloor());
        request.setActive(room.isActive());

        model.addAttribute("room", request);
        model.addAttribute("roomId", id);
        model.addAttribute("isEdit", true);
        model.addAttribute("title", "Master Data");
        return "admin/room-form";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("room") RoomRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roomId", id);
            model.addAttribute("isEdit", true);
            model.addAttribute("title", "Master Data");
            return "admin/room-form";
        }
        roomService.update(id, request);
        return "redirect:/admin/rooms";
    }
}
