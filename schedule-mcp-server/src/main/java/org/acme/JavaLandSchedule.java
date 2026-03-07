package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class JavaLandSchedule implements PanacheRepository<Talk> {

    public List<Talk> findByTime(String day, String time) {
        return list("lower(day) = lower(:day) and time = :time",
                Parameters.with("day", day).and("time", time));
    }

    public List<Talk> findHappeningAt(String day, String time) {
        return list("lower(day) = lower(:day) and time <= :time and endTime > :time",
                Parameters.with("day", day).and("time", time));
    }

    public List<Talk> findBySpeaker(String speakerQuery) {
        return list("lower(speaker) like :q",
                Parameters.with("q", "%" + speakerQuery.toLowerCase() + "%"));
    }

    public List<Talk> findByTopic(String topicQuery) {
        String q = "%" + topicQuery.toLowerCase() + "%";
        return list("lower(title) like :q or lower(category) like :q or lower(speaker) like :q",
                Parameters.with("q", q));
    }

    public List<Talk> findByRoom(String room) {
        return list("lower(room) like :q",
                Parameters.with("q", "%" + room.toLowerCase() + "%"));
    }

    public List<Talk> getNextTalks(String day, String currentTime) {
        return find("lower(day) = lower(:day) and time > :currentTime order by time",
                Parameters.with("day", day).and("currentTime", currentTime))
                .page(0, 8).list();
    }

    public List<Talk> getAllTalks() {
        return listAll(Sort.by("day").and("time"));
    }

    static String formatTalks(List<Talk> talks) {
        if (talks.isEmpty()) return "No talks found.";
        return talks.stream()
                .map(t -> "%s %s-%s | %s — %s (%s)".formatted(t.day, t.time, t.endTime, t.title, t.speaker, t.room))
                .collect(Collectors.joining("\n"));
    }
}
