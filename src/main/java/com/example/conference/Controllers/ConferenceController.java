package com.example.conference.Controllers;

import com.example.conference.Models.Conference;
import com.example.conference.Repository.ConferenceRepository;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ConferenceController {

    private final ConferenceRepository conferenceRepository;

    public ConferenceController(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная Страница");
        return "greeting";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Страница входа");
        return "login";
    }
    @GetMapping("/support")
    public String support() {
        return "support";
    }

    @GetMapping("/Conferences")
    public String conferences(Model model) {
        Iterable<Conference> conferences = conferenceRepository.findAll();
        model.addAttribute("title", "Страница конференций");
        model.addAttribute("Conferences", conferences);
        return "Plays"; // Используем оригинальное имя HTML-шаблона
    }

    @GetMapping("/addConference")
    public String addConference(Model model) {
        model.addAttribute("title", "Страница добавления конференции");
        return "addPlay"; // Используем оригинальное имя HTML-шаблона
    }

    @GetMapping("/Conferences/{id}")
    public String updateConference(@PathVariable long id, Model model) {
        if (!conferenceRepository.existsById(id)) {
            return "redirect:/Conferences";
        }
        Optional<Conference> conference = conferenceRepository.findById(id);
        ArrayList<Conference> res = new ArrayList<>();
        conference.ifPresent(res::add);
        model.addAttribute("Conference", res);
        model.addAttribute("title", "Страница редактирования");
        return "PlayDetails"; // Используем оригинальное имя HTML-шаблона
    }

    @GetMapping("/Conferences/filter")
    public String searchConferences(
            @RequestParam(required = false) String conferenceName,
            @RequestParam(required = false) String moderatorFullName,
            @RequestParam(required = false) String leadSpeakerFullName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            Model model) {

        Sort.Direction sortDirection = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortBy = Sort.by(sortDirection, "conferenceDate");

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<Conference> conferences;

        if (conferenceName != null || moderatorFullName != null || leadSpeakerFullName != null || startDate != null || endDate != null) {
            conferences = conferenceRepository.findByParams(conferenceName, moderatorFullName, leadSpeakerFullName, startDateTime, endDateTime, sortBy);
        } else {
            conferences = conferenceRepository.findAll(sortBy);
        }

        model.addAttribute("Conferences", conferences);
        return "Plays"; // Используем оригинальное имя HTML-шаблона
    }

    @GetMapping("/Conferences/stats")
    public String stats(Model model) {
        List<Object[]> stats = conferenceRepository.findConferenceStats();

        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (Object[] row : stats) {
            dates.add(row[0].toString());
            counts.add((Long) row[1]);
        }

        model.addAttribute("dates", dates);
        model.addAttribute("counts", counts);
        return "Play_stats"; // Используем оригинальное имя HTML-шаблона
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/addConference")
    public String addConference(
            @RequestParam String conferenceName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime conferenceDate,
            @RequestParam String moderatorFullName,
            @RequestParam String leadSpeakerFullName,
            Model model) {
        Conference conference = new Conference(conferenceName, conferenceDate, moderatorFullName, leadSpeakerFullName);
        conferenceRepository.save(conference);
        return "redirect:/Conferences";
    }

    @PostMapping("/Conferences/save")
    public String saveConference(
            @RequestParam("id") long id,
            @RequestParam String conferenceName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime conferenceDate,
            @RequestParam String moderatorFullName,
            @RequestParam String leadSpeakerFullName,
            Model model) {
        Conference conference = conferenceRepository.findById(id).orElseThrow();
        conference.setConferenceName(conferenceName);
        conference.setConferenceDate(conferenceDate);
        conference.setModeratorFullName(moderatorFullName);
        conference.setLeadSpeakerFullName(leadSpeakerFullName);
        conferenceRepository.save(conference);
        return "redirect:/Conferences";
    }

    @PostMapping("/Conferences/{id}/remove")
    public String removeConference(@PathVariable long id, Model model) {
        Conference conference = conferenceRepository.findById(id).orElseThrow();
        conferenceRepository.delete(conference);
        return "redirect:/Conferences";
    }

    @GetMapping("/Conferences/average")
    public String averageConferencesPerDay(Model model) {
        Double average = conferenceRepository.findAverageConferencesPerDay();
        model.addAttribute("average", average != null ? average : 0);
        return "average_conferences"; // Используем новый HTML шаблон
    }

}
