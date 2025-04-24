package com.esc.wmg.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.esc.wmg.entity.UserEntity;
import com.esc.wmg.repository.UserRepository;
import com.esc.wmg.service.EmailSenderService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailSenderService senderService;

    // 비밀번호 찾기 기능
    @PostMapping("/resetPw")
    public String resetPw(@RequestParam("email") String email,
            @RequestParam("pw") String pw,
            @RequestParam("pwCheck") String pwCheck,
            RedirectAttributes redirectAttributes, UserEntity entity) {
        // 1. 이메일로 사용자 조회
        UserEntity user = repository.findByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "존재하지 않는 사용자입니다.");
            return "redirect:/resetPw";
        }

        // 2. 비밀번호 일치 여부 확인
        if (!pw.equals(pwCheck)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/resetPw?";
        }

        String encodePw = passwordEncoder.encode(entity.getPw());
        user.setPw(encodePw);
        repository.save(user); // 비밀번호 업데이트 저장

        redirectAttributes.addFlashAttribute("success", "비밀번호가 성공적으로 변경되었습니다!");
        return "redirect:/login";
    }

    // 비밀번호 찾기 페이지
    @GetMapping("/ResetPassword")
    public String resetPassword() {
        return "ResetPassword";
    }

    // 비밀번호 찾기 - 인증 메일 발송
    @PostMapping("/sendEmail")
    public String findPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) throws MessagingException {

        if (repository.findByEmail(email) == null) {
            redirectAttributes.addFlashAttribute("error", "존재하지 않는 이메일 주소입니다.");
            return "redirect:/login";
        }
        String htmlContent = """
            <div style="max-width: 600px;">
              <div style="background-color: rgb(33, 47, 61); border-bottom: 2px solid rgb(86, 101, 115);  color: rgb(234, 236, 238); font-size: 28px; font-weight:bold; padding: 20px 30px;">
                ESC
              </div>
              <div style="line-height: 133%%; padding: 30px;">
                <span>안녕하세요! <b>고용상담 챗봇 플랫폼</b>입니다.</span><br><br>
                <span>해당 이메일 주소로 비밀번호 재설정을 요청하여 인증할 수 있는 링크를 발송하였습니다. 본인이 요청한 것이 맞다면 아래 링크를 클릭하여 이메일 인증을 완료한 뒤 비밀번호를 재설정해 주시기 바랍니다.</span><br><br>
                <a href="http://localhost:8087/ResetPassword"
                   style="background-color: rgb(33, 47, 61); border-radius: 0.375rem; color: rgb(234, 236, 238); display: inline-block; padding: 0.75rem 1rem; text-decoration: none; user-select: none"
                   target="_blank">인증하기</a><br><br>
                <span>혹시 본인이 요청한 적 없다면 이 이메일을 폐기해 주시기 바랍니다.</span><br><br>
                <span>감사합니다.</span>
              </div>
            </div>
        """;

        senderService.sendHtmlEmail(email, "[고용 상담 서비스] 인증 메일입니다.", htmlContent);
        redirectAttributes.addFlashAttribute("success", "비밀번호 재설정 메일 발송 완료!");
        return "redirect:/login";
    }

    // 로그인 기능
    @PostMapping("/userSelect")
    public String userSelect(UserEntity entity, HttpSession session,
            Model model
    ) {
        UserEntity loginUser = repository.findByEmail(entity.getEmail());

        if (loginUser != null && passwordEncoder.matches(entity.getPw(), loginUser.getPw())) {
            session.setAttribute("loginUser", loginUser);
            return "redirect:/";
        } else {
            return "login";
        }
    }

    // 로그아웃 기능
    @GetMapping("/logout")
    public String logout(HttpSession session
    ) {
        session.invalidate();
        return "redirect:/";
    }

    // 회원가입 기능
    @PostMapping("/userInsert")
    public String userInsert(UserEntity entity
    ) {
        String encodePw = passwordEncoder.encode(entity.getPw());
        entity.setPw(encodePw);
        repository.save(entity);
        return "redirect:/";
    }

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String goLogin() {
        return "login";
    }

    // 초기 메인 페이지
    @GetMapping("/")
    public String loginForm() {
        return "main";
    }

}
