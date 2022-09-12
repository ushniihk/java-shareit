package ru.practicum.shareit.requests.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestor(long requestorId);

    @Query(value = "select * from requests where requestor_id not in (?1) " +
            "offset ?2", nativeQuery = true)
    Page<ItemRequest> findAllByRequestorIsNot(long requestorId, int from, PageRequest pageRequest);

}
