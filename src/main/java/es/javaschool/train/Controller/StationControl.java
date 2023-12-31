package es.javaschool.train.Controller;

import es.javaschool.train.Entity.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import es.javaschool.train.Service.Impl.StationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class StationControl {
    @Autowired
    private StationServiceImpl stationServiceIMPL;

    @Autowired
    private es.javaschool.train.Service.Impl.ScheduleServiceImpl scheduleServiceIMPL;
    @Autowired
    private es.javaschool.train.Service.Impl.TrainServiceImpl trainServiceIMPL;

    @GetMapping("/stations")
    public String consultStation(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Pasa los roles al modelo
        model.addAttribute("userRoles", userRoles);
        List<Station> stations = this.stationServiceIMPL.consultStations();
        model.addAttribute("stations", stations);
        return "stations";
    }

    @GetMapping("/createStation")
    public String createAndUpdateStationForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        model.addAttribute("userRoles", userRoles);
        model.addAttribute("station", new Station());
        return "createAndUpdateStation";
    }

    @PostMapping("/stations")
    public String createAndUpdateStation(Station station) {
        this.stationServiceIMPL.createAndUpdateStation(station);
        return "redirect:/stations";
    }

    @GetMapping("/search3")
    public String searchStation(@RequestParam(value = "name") String nameStation, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Pasa los roles al modelo
        model.addAttribute("userRoles", userRoles);

        List<Station> stations = this.stationServiceIMPL.findStationsByName(nameStation);
        model.addAttribute("stations", stations);
        List<Station> allStations = this.stationServiceIMPL.consultStations();
        model.addAttribute("allStations", allStations);
        // Agrega este bloque para mostrar el mensaje cuando no hay resultados
        if (stations.isEmpty()) {
            model.addAttribute("noResults", true);
        } else {
            model.addAttribute("noResults", false);
        }

        return "stations";
    }



    @GetMapping("/edit2/{id}")
    public String modifyStationForm(@PathVariable int id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Pasa los roles al modelo
        model.addAttribute("userRoles", userRoles);
        Station station = this.stationServiceIMPL.consultStation(id);
        model.addAttribute("station", station);
        return "editStation"; // Debes crear esta vista en Thymeleaf
    }
    @PostMapping("/stations/{id}")
    public String modifyStation(@PathVariable int id, @ModelAttribute("station") Station station, Model model) {
        Station stationModify = this.stationServiceIMPL.consultStation(id);
        stationModify.setIdStation(id);
        stationModify.setNameStation(station.getNameStation());
        this.stationServiceIMPL.modifyStation(stationModify);
        return "redirect:/stations";
    }

    @GetMapping("/stations/{id}")
    public String deleteStation(@PathVariable int id) {
        this.stationServiceIMPL.deleteStation(id);
        return "redirect:/stations";
    }
}
