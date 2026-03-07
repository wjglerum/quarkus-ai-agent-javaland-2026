package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class JavaLandSchedule {

    @Inject
    EntityManager em;

    public List<Talk> findByTime(String day, String time) {
        return em.createQuery(
                "from Talk t where lower(t.day) = lower(:day) and t.time = :time", Talk.class)
                .setParameter("day", day)
                .setParameter("time", time)
                .getResultList();
    }

    public List<Talk> findHappeningAt(String day, String time) {
        return em.createQuery(
                "from Talk t where lower(t.day) = lower(:day) and t.time <= :time and t.endTime > :time", Talk.class)
                .setParameter("day", day)
                .setParameter("time", time)
                .getResultList();
    }

    public List<Talk> findBySpeaker(String speakerQuery) {
        return em.createQuery(
                "from Talk t where lower(t.speaker) like :q", Talk.class)
                .setParameter("q", "%" + speakerQuery.toLowerCase() + "%")
                .getResultList();
    }

    public List<Talk> findByTopic(String topicQuery) {
        String q = "%" + topicQuery.toLowerCase() + "%";
        return em.createQuery(
                "from Talk t where lower(t.title) like :q or lower(t.category) like :q or lower(t.speaker) like :q", Talk.class)
                .setParameter("q", q)
                .getResultList();
    }

    public List<Talk> findByRoom(String room) {
        return em.createQuery(
                "from Talk t where lower(t.room) like :q", Talk.class)
                .setParameter("q", "%" + room.toLowerCase() + "%")
                .getResultList();
    }

    public List<Talk> getNextTalks(String day, String currentTime) {
        return em.createQuery(
                "from Talk t where lower(t.day) = lower(:day) and t.time > :currentTime order by t.time", Talk.class)
                .setParameter("day", day)
                .setParameter("currentTime", currentTime)
                .setMaxResults(8)
                .getResultList();
    }

    public List<Talk> getAllTalks() {
        return em.createQuery("from Talk t order by t.day, t.time", Talk.class)
                .getResultList();
    }

    static String formatTalks(List<Talk> talks) {
        if (talks.isEmpty()) return "No talks found.";
        return talks.stream()
                .map(t -> "%s %s-%s | %s — %s (%s)".formatted(t.day, t.time, t.endTime, t.title, t.speaker, t.room))
                .collect(Collectors.joining("\n"));
    }
}
