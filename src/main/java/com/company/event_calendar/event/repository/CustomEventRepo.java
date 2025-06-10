/* package com.company.event_calendar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.event_calendar.models.Event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class CustomEventRepo {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Event event) {
        entityManager.getTransaction().begin();
        entityManager.persist(event);
        entityManager.getTransaction().commit();
    }

    // batch
    private static final int BATCH_SIZE = 50;

    @Transactional
    public void saveEventsBatch(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            entityManager.persist(events.get(i));
            if (i % BATCH_SIZE == 0 && i > 0) {
                entityManager.flush();
                // clear to avoid memory issues
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }
}


























// 1. Define a custom interface
 interface CustomEventRepository {
    void customUpdate(Event Event);
}

// 2. Implement it (with @PersistenceContext)
@Repository
 class EventRepositoryImpl implements CustomEventRepository {
    
    @PersistenceContext
    private EntityManager em;

    @Override
        public
         void customUpdate(Event Event) {
            em.createNativeQuery("UPDATE Events SET ...").executeUpdate();
        }
}

// 3. Extend both in your main repository
 interface EventRepository extends JpaRepository<Event, Long>, CustomEventRepository {}
 */