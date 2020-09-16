package com.studyhahoho.settings;

import com.studyhahoho.WithAccount;
import com.studyhahoho.account.AccountRepository;
import com.studyhahoho.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("프로필 수정 폼")
    void 프로필_수정_폼() throws Exception {
        // given
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
        
        // when

        // then
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("프로필 수정_입력값 정상")
    void 프로필_수정_입력값_정상() throws Exception {
        // given
        String bio = "update bio";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
        // when
                .param("bio", bio)
                .with(csrf()))

        // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account hahoho = accountRepository.findByNickname("hahoho");
        assertEquals(bio, hahoho.getBio());
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("프로필 수정_입력값 에러")
    void 프로필_수정_입력값_에러() throws Exception {
        // given
        String bio = "too long too long too long too long too long too long too long too long too long too long too long too long too long too long too long ";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)

        // when
                .param("bio", bio)
                .with(csrf()))

        // then
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account hahoho = accountRepository.findByNickname("hahoho");
        assertNull(hahoho.getBio());
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("패스워드 수정 폼")
    void 패스워드_수정_폼() throws Exception {
        // given
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))

        // when

        // then
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("account"))
            .andExpect(model().attributeExists("passwordForm"));

    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("패스워드 수정_입력값 정상")
    void 패스워드_수정_입력값_정상() throws Exception {
        // given
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
        // when
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "12345678")
            .with(csrf()))
        // then
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
            .andExpect(flash().attributeExists("message"))
            .andDo(print());

        Account hahoho = accountRepository.findByNickname("hahoho");
        assertTrue(passwordEncoder.matches("12345678", hahoho.getPassword()));
    }

    @WithAccount("hahoho")
    @Test
    @DisplayName("패스워드 수정_입력값 에러_패스워드 불일치")
    void 패스워드_수정_입력값_에러_패스워드_불일치() throws Exception {
        // given
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
        // when
            .param("newPassword", "12345678")
            .param("newPasswordConfirm", "12345677")
            .with(csrf()))
        // then
            .andExpect(status().isOk())
            .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
            .andExpect(model().hasErrors())
            .andExpect(model().attributeExists("passwordForm"))
            .andExpect(model().attributeExists("account"))
            .andDo(print());
    }
}