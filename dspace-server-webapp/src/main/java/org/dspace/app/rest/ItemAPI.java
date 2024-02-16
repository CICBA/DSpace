package org.dspace.app.rest;

import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;

import java.sql.SQLException;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Spliterator;
import java.util.Spliterators;

@RestController
@RequestMapping("/api/items")
public class ItemAPI {
    private final ItemService itemService;

    @Autowired
    public ItemAPI(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/total-count")
    public long getTotalItemCount() {
        try (Context context = new Context()) {
            Stream<Item> itemsStream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(itemService.findAll(context), Spliterator.ORDERED),
                false
            );
            return itemsStream.filter(item -> item.isArchived() && !item.isWithdrawn()).count();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener el conteo total de items", e);
        }
    }
}
