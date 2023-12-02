package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime current, Sort sort);

    List<Booking> findAllByBooker_IdAndStartIsAfter(Long userId, LocalDateTime current, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndEndIsAfterAndStartIsBefore(Long userId, LocalDateTime current,
                                                                  LocalDateTime current2, Sort sort);

    List<Booking> findAllByBooker_IdAndEndIsAfterAndStartIsBefore(Long userId, LocalDateTime current,
                                                                  LocalDateTime current2, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long userId, LocalDateTime current, Sort sort);

    List<Booking> findAllByBooker_IdAndEndIsBefore(Long userId, LocalDateTime current, PageRequest pageRequest);

    List<Booking> findAllByEndIsBefore(LocalDateTime current, Sort sort);

    List<Booking> findAllByEndIsBefore(LocalDateTime current, PageRequest pageRequest);

    List<Booking> findAllByStartIsAfter(LocalDateTime current, Sort sort);

    List<Booking> findAllByStartIsAfter(LocalDateTime current, PageRequest pageRequest);

    List<Booking> findAllByEndIsAfterAndStartIsBefore(LocalDateTime current,
                                                      LocalDateTime current2, Sort sort);

    List<Booking> findAllByEndIsAfterAndStartIsBefore(LocalDateTime current,
                                                      LocalDateTime current2, PageRequest pageRequest);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByBooker_IdAndStatus(Long bookerId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findAllByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findAllByBooker_Id(Long bookerId, PageRequest pageRequest);

    List<Booking> findAllByStatus(BookingStatus status);

    @Query(value = "select b.* from bookings as b left join items as t on b.item_id = t.id left join users as u on" +
            " t.owner_id = u.id where u.id = ?1", nativeQuery = true)
    List<Booking> takeAllByOwnerId(Long userId, PageRequest pageRequest);

    List<Booking> findAllByStatus(BookingStatus status, PageRequest pageRequest);

    @Query("SELECT b FROM Booking b WHERE b.item in ?1 AND b.status = 'APPROVED'")
    List<Booking> findApprovedBookingsFor(Collection<Item> items, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item = ?1 and b.start <= ?2 ORDER By b.start DESC")
    List<Booking> findLastBooking(Item item, LocalDateTime current);

    @Query("SELECT b FROM Booking b WHERE b.item = ?1 and b.start > ?2 ORDER By b.start asc")
    List<Booking> findFirstBooking(Item item, LocalDateTime current);
}
