package apptive.devlog.auth.controller;


import apptive.devlog.auth.dto.UserLoginForm;
import apptive.devlog.auth.dto.UserSaveForm;
import apptive.devlog.auth.entity.User;
import apptive.devlog.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * login 진입 페이지
     * @param model
     * @return
     */
    @GetMapping
    public String loginHome(Model model){
        //model.addAttribute("user", new User());

        return "users/home";
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping("/signup")
    public String signupForm(Model model){
        model.addAttribute("user", new User());
        return "users/signup";
    }

    /**
     *
     * @param form
     * @param bindingResult
     * @return
     */
    @PostMapping("/signup")
    public String signup(@Validated @ModelAttribute("user") UserSaveForm form, BindingResult bindingResult){
        // 이메일 중복 체크
        if(form.getEmail() != null){
            if(userService.isEmailDuplicated(form.getEmail())){
                bindingResult.rejectValue("email", "duplicated", "이미 가입된 이메일입니다.");
            }
        }

        // 닉네임 중복 체크
        if(form.getNickname() != null){
            if(userService.isNicknameDuplicated(form.getNickname())){
                bindingResult.rejectValue("nickname", "duplicated", "이미 사용 중인 닉네임입니다.");
            }
        }

        // Validation
        if(bindingResult.hasErrors()){
            log.info("errors = {}", bindingResult);

            return "/users/home";
        }

        // 성공 로직
        User user = new User(form);
        userService.signup(user);

        return "redirect:/users/home";
    }

    /**
     *
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String loginForm(Model model){
        model.addAttribute(new User());

        return "users/login";
    }

    /**
     *
     * @param form
     * @param bindingResult
     * @return
     */
    @PostMapping("login")
    public String login(@Validated @ModelAttribute("user") UserLoginForm form, BindingResult bindingResult){
        // 1차 유효성 검사
        if(bindingResult.hasErrors()){
            log.info("errors = {}", bindingResult);

            return "users/login";
        }

        User user = new User(form);

        //로그인 처리

        return "redirect:/main";
    }
}
