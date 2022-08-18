package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.CreatingException;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.exceptions.NotFoundParameterException;
import ru.practicum.shareit.exceptions.UpdateException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.get(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        return itemService.search(userId, text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) throws NotFoundParameterException, CreatingException {
        return itemService.addNew(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) throws IncorrectParameterException, UpdateException {
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        itemService.delete(userId, itemId);
    }
}