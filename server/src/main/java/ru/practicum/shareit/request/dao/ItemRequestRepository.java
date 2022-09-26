package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor(long requestorId);

    @Query(value = "select r from ItemRequest r where r.requestor not in (?1)")
    Page<ItemRequest> findAllByRequestorIsNot(long requestorId, PageRequest pageRequest);

/*    @Query(value = "select * from requests where requestor_id not in (?1) " +
            "offset ?2", nativeQuery = true)*/
}
