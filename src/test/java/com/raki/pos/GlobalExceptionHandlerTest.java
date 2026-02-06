package com.raki.pos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raki.pos.auth.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    public void handleAuthError_WhenAuthenticationException_ShouldReturn401AndErrorSchema() throws Exception {
        // API Spec: 401 returns ErrorResponse { "error": string }
        mockMvc.perform(get("/test/auth-exception"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication Failed"));
    }

    @Test
    public void handleValidationExceptions_WhenInvalidInput_ShouldReturn400AndValidationSchema() throws Exception {
        // API Spec: 400 returns ValidationError { "fieldName": "error message" }
        TestDto invalidDto = new TestDto("");

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.testField").value("must not be empty"));
    }

    @Test
    public void handleDatabaseErrors_WhenDbError_ShouldReturn500AndErrorSchema() throws Exception {
        // API Spec: 500 returns ErrorResponse { "error": string }
        mockMvc.perform(get("/test/database"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("A database error occurred. Please check your request or contact support."));
    }
}
@RestController
class TestController {
    @GetMapping("/test/auth-exception")
    public void throwAuth() throws BadCredentialsException {
        throw new BadCredentialsException("Authentication Failed");
    }

    @PostMapping("/test/validation")
    public void throwValidation(@Valid @RequestBody TestDto testDto) {
    }

    @GetMapping("/test/database")
    public void throwDatabase() {
        throw new DuplicateKeyException("SQL Error");
    }
}

class TestDto {
    @NotBlank(message = "must not be empty")
    public String testField;

    public TestDto() {
    }

    public TestDto(String testField) {
        this.testField = testField;
    }

    public String getTestField() {
        return testField;
    }
}