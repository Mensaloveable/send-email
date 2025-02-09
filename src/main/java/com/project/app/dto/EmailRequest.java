package com.project.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.app.enums.EmailActionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.File;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailRequest {

    @NotBlank
    @Email
    private String to;
    @NotBlank
    private String subject;
    @NotBlank
    private String body;
    private List<File> attachments;  // For emails with attachments
    private String sendAt; // For scheduled emails (ISO_LOCAL_DATE_TIME format)
    @NotNull
    private EmailActionType emailActionType;
    private List<String> cc;  // Carbon Copy recipients
    private List<String> bcc; // Blind Carbon Copy recipients
}
