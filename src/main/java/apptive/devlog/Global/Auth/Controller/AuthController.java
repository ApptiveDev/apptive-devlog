package apptive.devlog.Global.Auth.Controller;

import apptive.devlog.Global.Auth.Dto.LoginRequestDto;
import apptive.devlog.Global.Auth.Service.AuthService;
import apptive.devlog.Global.Response.Result.ResultCode;
import apptive.devlog.Global.Response.Result.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<ResultResponse> login(@RequestBody LoginRequestDto loginRequestDto){
        authService.login(loginRequestDto);
        final ResultResponse response = new ResultResponse(ResultCode.LOGIN_SUCCESS,ResultCode.LOGIN_SUCCESS.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/logout")
    public ResponseEntity<ResultResponse> logout(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.replace("Bearer ", "");
        authService.logout(token);
        final ResultResponse response = new ResultResponse(ResultCode.LOGOUT_SUCCESS,ResultCode.LOGOUT_SUCCESS.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<ResultResponse> withdrawal(@RequestHeader("Authorization") String authorizationHeader){
        String token = authorizationHeader.replace("Bearer ", "");
        authService.withdrawal(token);
        final ResultResponse response = new ResultResponse(ResultCode.DELETE_SUCCESS,ResultCode.DELETE_SUCCESS.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
}
