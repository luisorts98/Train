package es.javaschool.train.Controller;

import es.javaschool.train.Entity.Station;
import es.javaschool.train.Service.Impl.StationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import es.javaschool.train.Entity.Train;
import es.javaschool.train.Service.Impl.TrainServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/")
public class TrainControl {
    @Autowired
    private TrainServiceImpl trainServiceIMPL;

    @Autowired
    private StationServiceImpl stationServiceIMPL;
    @GetMapping("/trains")
    public String consultTrain(Model model, Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        model.addAttribute("userRoles", userRoles);
        List<Train> trains = this.trainServiceIMPL.consultTrains();
        List<Station> allStations = stationServiceIMPL.consultStations();
        model.addAttribute("trains", trains);
        model.addAttribute("allStations", allStations);
        return "trains";
    }

    @GetMapping("/search4")
    public String searchTrain(
            @RequestParam(value = "id_station", required = false) Integer idStation,
            @RequestParam(value = "id_station2", required = false) Integer idStation2,
            Model model,
            Authentication authentication
    ) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("userRoles", userRoles);

        List<Train> trains;
        if (idStation != null && idStation2 != null) {
            String station = stationServiceIMPL.consultStation(idStation).getNameStation();
            String station2 = stationServiceIMPL.consultStation(idStation2).getNameStation();
            trains = this.trainServiceIMPL.findTrainsByStationNameAndDestination(station, station2);
        } else if (idStation != null) {
            String station = stationServiceIMPL.consultStation(idStation).getNameStation();
            trains = this.trainServiceIMPL.findTrainsByStationName(station);
        } else if (idStation2 != null) {
            String station2 = stationServiceIMPL.consultStation(idStation2).getNameStation();
            trains = this.trainServiceIMPL.findTrainsByStationDestination(station2);
        } else {
            trains = this.trainServiceIMPL.consultTrains();
        }

        List<Station> allStations = stationServiceIMPL.consultStations();
        List<Train> allTrains = trainServiceIMPL.consultTrains();
        model.addAttribute("allStations", allStations);
        model.addAttribute("trains", trains);
        model.addAttribute("allTrains", allTrains);
        return "trains";
    }



    @GetMapping("/createTrain")
    public String createAndUpdateTrainForm(Model model, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("userRoles", userRoles);
        List<Station> stations = stationServiceIMPL.consultStations();
        model.addAttribute("allStations", stations);
        model.addAttribute("train", new Train());
        return "createAndUpdateTrain";
    }
    @PostMapping("/trains")
    public String createAndUpdateTrain(@RequestParam(value = "id_station") int idStation, @RequestParam(value = "id_station2") int idStation2, @ModelAttribute("train") Train train, Model model, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("userRoles", userRoles);
        Station station = stationServiceIMPL.consultStation(idStation);
        Station station2 = stationServiceIMPL.consultStation(idStation2);
        train.setStationOrigin(station2);
        train.setIdStation(station);
        this.trainServiceIMPL.createAndUpdateTrain(train);
        return "redirect:/trains";
    }


    @GetMapping("/edit/{id}")
    public String modifyTrainForm(@PathVariable int id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userRoles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        model.addAttribute("userRoles", userRoles);
        Train train = this.trainServiceIMPL.consultTrain(id);
        List<Station> stations = stationServiceIMPL.consultStations();
        model.addAttribute("allStations", stations);
        model.addAttribute("train", train);
        return "editTrain";
    }
    @PostMapping("/trains/{id}")
    public String modifyTrain(@PathVariable int id, @ModelAttribute("train") Train train, @RequestParam("idStation") int idPassenger, @RequestParam(value = "idStation2") int idStation2,Model model) {
        Train trainModify = this.trainServiceIMPL.consultTrain(id);
        trainModify.setIdTrain(id);
        trainModify.setStationOrigin(stationServiceIMPL.consultStation(idStation2));
        trainModify.setSeats(train.getSeats());
        trainModify.setStations(train.getStations());
        trainModify.setIdStation(stationServiceIMPL.consultStation(idPassenger));
        this.trainServiceIMPL.modifyTrain(trainModify);
        return "redirect:/trains";
    }

    @GetMapping("/trains/{id}")
    public String deleteTrain(@PathVariable int id) {
        this.trainServiceIMPL.deleteTrain(id);
        return "redirect:/trains";
    }


}
