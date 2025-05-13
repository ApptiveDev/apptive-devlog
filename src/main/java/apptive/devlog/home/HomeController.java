package apptive.devlog.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

}
