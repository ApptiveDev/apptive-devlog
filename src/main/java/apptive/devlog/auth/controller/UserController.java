package apptive.devlog.auth.controller;


import apptive.devlog.auth.dto.UserLoginForm;
import apptive.devlog.auth.dto.UserSaveForm;
import apptive.devlog.auth.entity.User;
import apptive.devlog.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 로그인 시 진입하는 user home
    @GetMapping
    public String loginHome(@AuthenticationPrincipal UserDetails userDetails, Model model){
        model.addAttribute("user", userDetails);
        return "users/userHome";
    }

    // 회원가입
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserSaveForm());
        return "users/signup";
    }

    @PostMapping("/signup")
    public String signup(@Validated @ModelAttribute("user") UserSaveForm form, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);

            return "users/signup";
        }

        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setName(form.getName());
        user.setNickname(form.getNickname());
        user.setBirth(form.getBirth());
        user.setGender(form.getGender());
        userService.signup(user);

        return "redirect:/users/home";
    }

    // 로그인
    @GetMapping("/login")
    public String loginForm(Model model){
        model.addAttribute("user", new UserLoginForm());

        return "users/login";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("user") UserLoginForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);

            return "users/login";
        }

        return "redirect:/main";
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        return "redirect:/main";
    }
}
